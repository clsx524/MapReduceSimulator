package clsx524


import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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

	}
}

