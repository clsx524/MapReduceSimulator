package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Timer {
    private int corePoolSize = Configure.corePoolSize;
    public ScheduledThreadPoolExecutor jobQueue = null;
    public ScheduledThreadPoolExecutor taskQueue = null;

    private static Timer timer = null;

    private final NetworkSimulator networksimulator;
    
    private final Scheduler scheduler;

    public int totalTimes = 0;
    public int oldExecutor = 0;
    public int currQueueLength = 0;
    
    private Timer() {
        jobQueue = new ScheduledThreadPoolExecutor(Configure.total);
        taskQueue = new ScheduledThreadPoolExecutor(corePoolSize); 
        networksimulator = NetworkSimulator.getInstance();  
        scheduler = SchedulerFactory.getInstance();  
    }

    public static Timer getInstance() {
        if (timer == null)
            throw new NullPointerException("timer is null");
        return timer;
    } 

    public static Timer newInstance() {
        if (timer == null)
            timer = new Timer();
        return timer;        
    }

    public void scheduleJob(JobInfo job) {
        jobQueue.schedule(new JobAfterTimerDone(job), job.arrivalTime, TimeUnit.SECONDS);
        totalTimes++;
        networksimulator.jobStarted(job);
    }

    public void scheduleTask(JobInfo.TaskInfo task) {
        networksimulator.occupyOneSlotAtNode(task.nodeIndex);
        task.startTime = System.currentTimeMillis() - Configure.initialTime;
        taskQueue.schedule(new TaskAfterTimerDone(task), task.duration, TimeUnit.MICROSECONDS);
        currQueueLength++;
        totalTimes++;
    }

    public void join() {
        taskQueue.shutdown();
        while(!taskQueue.isTerminated()){
            //wait for all tasks to finish
        }
        System.out.println("Timer has terminated safely");
    }

    class JobAfterTimerDone implements Runnable {

        private JobInfo job;

        public JobAfterTimerDone(JobInfo j) {
            job = j;
        }

        public void run() {
            scheduler.schedule(job.maps);
        }
    }

    class TaskAfterTimerDone implements Runnable {

        private JobInfo.TaskInfo task;

        public TaskAfterTimerDone(JobInfo.TaskInfo t) {
            task = t;
        }

        public void run() {
            networksimulator.addOneSlotAtNode(task.nodeIndex);
            task.progress = true;
            task.endTime = System.currentTimeMillis() - Configure.initialTime;
        }
    }

}

