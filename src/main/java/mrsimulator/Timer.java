package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Timer extends Thread {
	private final int corePoolSize = 10000;
	private final ScheduledThreadPoolExecutor slots;
	private final TimerMessage tmsg;
    private final NetworkSimulator networksimulator;
    private final Scheduler scheduler;
	private static Timer timer = null;

    private Timer(int size) {
        corePoolSize = size;
        slots = new ScheduledThreadPoolExecutor(corePoolSize);
        tmsg = TimerMessage.getInstance();  
        networksimulator = NetworkSimulator.getInstance();  
        scheduler = SchedulerFactory.getInstance();    
    }

    public static Timer getInstance(int size) {
        if (timer == null)
            timer = new Timer(int size);
        return timer;
    } 

    // wait until notify by networksimulator
	public void run() {
        while (true) {
            synchronized(tmsg) {
                try {
            	   tmsg.wait();
                } catch (InterruptedException e) {
            	   e.printStackTrace();
                }
            }
            if (tmsg.getType().equals("JOB"))
                slots.schedule(new JobAfterTimerDone(tmsg.getJobInfo()), tmsg.getDuration(), TimeUnit.SECONDS);
            else if (tmsg.getType().equals("TASK")) {
                Integer nodeIndex = tmsg.getNodeIndex();
                networksimulator.occupyOneSlotAtNode(nodeIndex);
                slots.schedule(new TaskAfterTimerDone(nodeIndex), tmsg.getDuration(), TimeUnit.SECONDS);
            } else 
                throw new IllegalArgumentException("Invalid TimerMessage Type");
                
        }
    }

	class JobAfterTimerDone implements Runnable {

        private JobInfo job;

        public AfterTimerDone(JobInfo j) {
            job = j;
        }

        public void run() {
            scheduler.schedule(job);
        }
	}

    class TaskAfterTimerDone implements Runnable {

        private Integer nodeIndex;

        public AfterTimerDone(Integer ni) {
            nodeIndex = ni;
        }

        public void run() {
            networksimulator.addOneSlotAtNode(nodeIndex);
        }
    }

}

