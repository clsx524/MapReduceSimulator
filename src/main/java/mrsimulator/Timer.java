package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Timer {
    private int corePoolSize = Configure.corePoolSize;
    public ScheduledThreadPoolExecutor timerQueue;
    private static Timer timer = null;

    private final NetworkSimulator networksimulator;

    private BoundedSemaphore netSemaphore = null;
    
    private final Scheduler scheduler;
    
    private Timer(BoundedSemaphore ns) {
        timerQueue = new ScheduledThreadPoolExecutor(corePoolSize); 
        networksimulator = NetworkSimulator.getInstance();  
        netSemaphore = ns;
        scheduler = SchedulerFactory.getInstance();  
    }

    public static Timer getInstance() {
        if (timer == null)
            throw new NullPointerException("timer is null");
        return timer;
    } 

    public static Timer newInstance(BoundedSemaphore ns) {
        if (timer == null)
            timer = new Timer(ns);
        return timer;        
    }

    public void scheduleJob(JobInfo job) {
        timerQueue.schedule(new JobAfterTimerDone(job), job.arrivalTime, TimeUnit.SECONDS);
    }

    public void scheduleTask(JobInfo.TaskInfo task) {
        networksimulator.occupyOneSlotAtNode(task.nodeIndex);
        task.startTime = System.currentTimeMillis() - Configure.initialTime;
        timerQueue.schedule(new TaskAfterTimerDone(task), task.duration, TimeUnit.MICROSECONDS);
    }

    public void join() {
        timerQueue.shutdown();
        while(!timerQueue.isTerminated()){
            //wait for all tasks to finish
        }
        System.out.println("Finished all threads");
    }

    class JobAfterTimerDone implements Runnable {

        private JobInfo job;

        public JobAfterTimerDone(JobInfo j) {
            job = j;
        }

        public void run() {
            System.out.println("job start schedule: " + job.jobToString());
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
            if (task.taskType == true)
                task.setMapProgress();
            else {
                task.setReduceProgress();
                task.setJobEndTime();
            }
            System.out.println("task finished: " + task.toString());   
            try {
                if (networksimulator.notifyFinish(task))
                    netSemaphore.acquire();
            } catch (InterruptedException ie) {
                System.out.println("Exception thrown  :" + ie);
            }
            
        }
    }

}

