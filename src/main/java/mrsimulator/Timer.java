package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class Timer {
    private int corePoolSize = Configure.corePoolSize;
    public ScheduledThreadPoolExecutor jobQueue = null;
    public ScheduledThreadPoolExecutor taskQueue = null;
    // public ScheduledThreadPoolExecutor taskQueueBackup = null;

    public ScheduledThreadPoolExecutor currQueue = null;
    // public ScheduledThreadPoolExecutor backupQueue = null;

    private static Timer timer = null;

    private final NetworkSimulator networksimulator;

    private BoundedSemaphore netSemaphore = null;
    
    private final Scheduler scheduler;

    public int totalTimes = 0;
    public int oldExecutor = 0;
    public int currQueueLength = 0;
    
    private Timer(BoundedSemaphore ns) {
        jobQueue = new ScheduledThreadPoolExecutor(Configure.total);
        taskQueue = new ScheduledThreadPoolExecutor(corePoolSize); 
        networksimulator = NetworkSimulator.getInstance();  
        netSemaphore = ns;
        scheduler = SchedulerFactory.getInstance();  
        currQueue = taskQueue;
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

    // public int getBackupSize() {
    //     if (backupQueue == null)
    //         return 0;
    //     else 
    //         return backupQueue.getQueue().size();
    // }

    // private void requireSwitch() {
    //     if (currQueueLength >= Configure.timerLimit) {
    //         Thread re = new Thread(new RetireExecutor(currQueue));
    //         if (taskQueueBackup == null) {
    //             taskQueueBackup = new ScheduledThreadPoolExecutor(corePoolSize);
    //             backupQueue = currQueue;
    //             currQueue = taskQueueBackup;                
    //         } else if (taskQueue == null){
    //             taskQueue = new ScheduledThreadPoolExecutor(corePoolSize);
    //             backupQueue = currQueue;
    //             currQueue = taskQueue;
    //         } else {
    //             System.out.println("Timer one of queue is not null");
    //         }
    //         currQueueLength = 0;
    //         re.start();
    //     }
    // }

    public void scheduleJob(JobInfo job) {
        jobQueue.schedule(new JobAfterTimerDone(job), job.arrivalTime, TimeUnit.SECONDS);
        totalTimes++;
        networksimulator.jobStarted(job);
    }

    public void scheduleTask(JobInfo.TaskInfo task) {
        //requireSwitch();
        networksimulator.occupyOneSlotAtNode(task.nodeIndex);
        task.startTime = System.currentTimeMillis() - Configure.initialTime;
        currQueue.schedule(new TaskAfterTimerDone(task), task.duration, TimeUnit.MICROSECONDS);
        currQueueLength++;
        totalTimes++;
    }

    public void join() {
        currQueue.shutdown();
        while(!currQueue.isTerminated()){
            //wait for all tasks to finish
        }
        System.out.println("Timer has terminated safely");
    }

    public void join(ScheduledThreadPoolExecutor stpe) {
        stpe.shutdown();
        while(!stpe.isTerminated()){
            //wait for all tasks to finish
        }
        stpe = null;
        oldExecutor++;
        System.out.println("Old executor terminated: " + (stpe == null) + (currQueue == null));
    }

    class JobAfterTimerDone implements Runnable {

        private JobInfo job;

        public JobAfterTimerDone(JobInfo j) {
            job = j;
        }

        public void run() {
            //System.out.println("job start schedule: " + job.jobToString());
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
            // if (task.taskType == true)
            //     task.setMapProgress();
            // else {
            //     task.setReduceProgress();
            // }
            //System.out.println("task finished: " + task.toString());  
            //networksimulator.notifyFinish(task); 
            // try {
            //     netSemaphore.acquire();
            // } catch (InterruptedException ie) {
            //     System.out.println("Exception thrown  :" + ie);
            // }
            
        }
    }

    class RetireExecutor implements Runnable {

        private ScheduledThreadPoolExecutor stpe;

        public RetireExecutor(ScheduledThreadPoolExecutor s) {
            stpe = s;
        }

        public void run() {
            join(stpe);
        }
    }

}

