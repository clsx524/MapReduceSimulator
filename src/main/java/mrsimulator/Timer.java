package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
            corePoolSize = 10000;
        corePoolSize = size;
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

    public static Timer newInstance(Long size, Semaphore ns) {
        if (timer == null)
            timer = new Timer(size, ns);
        return timer;        
    }

	public void scheduleJob(JobInfo job, String type) {
        timerQueue.schedule(new JobAfterTimerDone(job, type), job.arrivalTime, TimeUnit.SECONDS);
    }

    public void scheduleTask(JobInfo.TaskInfo task) {
        networksimulator.occupyOneSlotAtNode(task.nodeIndex);
        task.startTime = System.currentTimeMillis();
        timerQueue.schedule(new TaskAfterTimerDone(task), task.duration, TimeUnit.SECONDS);
    }

    public boolean join() {
        timerQueue.shutdown();
        while(!timerQueue.isTerminated()){
            //wait for all tasks to finish
        }
        System.out.println("Finished all threads");
    }

	class JobAfterTimerDone implements Runnable {

        private JobInfo job;
        private String type;

        public JobAfterTimerDone(JobInfo j, String t) {
            job = j;
            type = t;
        }

        public void run() {
            scheduler.schedule(job, type);
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
            task.endTime = System.currentTimeMillis();
            if (task.taskType == true) 
                task.mapProgress += 1;
            else {
                task.reduceProgress += 1;
                task.setJobEndTime();
            }
                
            netSemaphore.take();
        }
    }

}

