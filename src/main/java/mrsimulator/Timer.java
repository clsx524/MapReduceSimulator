package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


public class Timer {
    private final Long corePoolSize;
    private final ScheduledThreadPoolExecutor timerQueue;
    private static Timer timer = null;

    private final NetworkSimulator networksimulator;

    private Semaphore netSemaphore = null;
    
    private final Scheduler scheduler;
    
    private Timer(Long size, Semaphore ns) {
        if (size < 0L)
            corePoolSize = 10000L;
        else
            corePoolSize = size;
        timerQueue = new ScheduledThreadPoolExecutor(corePoolSize.intValue()); 
        networksimulator = NetworkSimulator.getInstance();  
        netSemaphore = ns;
        scheduler = SchedulerFactory.getInstance();  
    }

    public static Timer getInstance() {
        if (timer == null)
            throw new NullPointerException("timer is null");
        return timer;
    } 

    public static Timer newInstance(Long size, Semaphore ns) {
        if (timer == null)
            timer = new Timer(size, ns);
        return timer;        
    }

    public void scheduleJob(JobInfo job) {
        System.out.println(job.jobToString());
        timerQueue.schedule(new JobAfterTimerDone(job), job.arrivalTime, TimeUnit.SECONDS);
    }

    public void scheduleTask(JobInfo.TaskInfo task) {
        networksimulator.occupyOneSlotAtNode(task.nodeIndex);
        task.startTime = System.currentTimeMillis();
        timerQueue.schedule(new TaskAfterTimerDone(task), task.duration, TimeUnit.SECONDS);
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
            System.out.println("task finished: " + task.toString());
            networksimulator.addOneSlotAtNode(task.nodeIndex);
            task.progress = true;
            task.endTime = System.currentTimeMillis();
            if (task.taskType == true) 
                task.setMapProgress();
            else {
                task.setReduceProgress();
                task.setJobEndTime();
            }
                
            try {
                netSemaphore.acquire();
            } catch (InterruptedException ie) {
                System.out.println("Exception thrown  :" + ie);
            }
            
        }
    }

}

