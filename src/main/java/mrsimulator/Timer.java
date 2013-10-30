package mrsimulator;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

<<<<<<< HEAD
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
=======
public class Timer extends Thread
{
	private int corePoolSize = 1000;
	private ScheduledThreadPoolExecutor slots = null;
	public  boolean runSlots = Flase;
	private Messgae msg;
	private  node nodeInstance = null;
	public Timer()
	{
		super();
		slots = new ScheduledThreadPoolExecutor(corePoolSize);
		start();
	} 

	public void getMessage(Messgae msg) {
		this.msg = msg;
	}

// wait until notify by networksimulator
	public void run() {
        try {
        	int runHisCount = 0;
            while (true) {
                synchronized(msg) {
                try {
                	msg.wait();
                	}catch(InterruptedException e) {
                	e.printStackTrace();
                	}
                }
        					
                ScheduledFuture<Integer> Integer.toString(runHisCount++) = slots.schedule( new Runnable( ) {	
                	public void run() {
                	 nodeInstance = msg.getMessage();

     				} 
     			}, 2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
        }
    }

	class slotRunning implements Runnable
	{

>>>>>>> c2c3a20af3c8a2f9a5dd33fae134f5abecf002d5
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

