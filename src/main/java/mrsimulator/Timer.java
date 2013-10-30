package mrsimulator;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Timer extends Thread {
	private int corePoolSize = 10000;
	private ScheduledThreadPoolExecutor slots = null;
	private timerMessage msg;
	private  node nodeInstance = null;
	
    public Timer(int size, Messgae m) {
		super();
        corePoolSize = size;
        msg = m;
		slots = new ScheduledThreadPoolExecutor(corePoolSize);
		start();
	} 

// wait until notify by networksimulator
	public void run() {

        while (true) {
            synchronized(msg) {
                try {
            	   msg.wait();
                } catch (InterruptedException e) {
            	   e.printStackTrace();
                }
            }	
        msg.getNode().decrement();	
        slots.schedule(new slotRunning(msg.getNode()), msg.getDuration(), TimeUnit.NANOSECONDS);
        }
    }

	class slotRunning implements Runnable {
        private node nodeInstance = null;

        public slotRunning(node ni) {
            nodeInstance = ni;
        }

        public void run() {
            if (nodeInstance == null)
                throw new NullPointerException("nodeInstance is null");
            nodeInstance.increment();
        }
	}
}

