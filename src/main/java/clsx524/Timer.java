package clsx524

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Timer extends Thread
{
	private int corePoolSize = 1000;
	private ScheduledThreadPoolExecutor slots = null;
	public  boolean runSlots = Flase;
	private 
	public Timer()
	{
		super();
		slots = new ScheduledThreadPoolExecutor(corePoolSize);
		start();
	} 

	public void run() {
        try {
        	int runHisCount = 0;
            while (true) {
                msg.wait();
                ScheduledFuture<Integer> Integer.toString(runHisCount++) = slots.schedule( new Runnable( ){	public void run(){
        	
     																									} }, 2, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {
        }
    }

	class slotRunning implements Runnable
	{

	}
}

