package mrsimulator;

import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public class DelayScheduler implements Scheduler  {

	private class StartTimeComparator implements Comparator<JobInfo.TaskInfo> {
		public int compare(JobInfo.TaskInfo j1, JobInfo.TaskInfo j2) {
	 		if (j1.getTotalTasksNumber() <= j2.getTotalTasksNumber())
	 			return 1;
	 		else
	 			return -1;
	 	}
	}

	private Queue<JobInfo.TaskInfo> queue = null;

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private Timer timer = null;

	private boolean stopSign = false;

	private RoutineSchedule routine = null;

	private int failure = -1;

	public DelayScheduler() {
		StartTimeComparator atc = new StartTimeComparator();
		queue = new PriorityBlockingQueue<JobInfo.TaskInfo>(1,atc);
	}

	public void schedule(JobInfo.TaskInfo[] tasks) {
		if (tasks == null)
            throw new NullPointerException("tasks is null");

        for (JobInfo.TaskInfo task : tasks)
        	queue.offer(task);
	}
	
	public void join() {
		try {
			routine.join();
		} catch (InterruptedException ie) {
    		System.out.println("Exception thrown  :" + ie);
    	}
	}

	public void stop() {
		stopSign = true;
	}

	public void setTimer() {
		timer = Timer.getInstance();
	}

	public int getQueueSize() {
		return queue.size();
	}

	public boolean isAlive() {
		return routine.isAlive();
	}

	public boolean isInterrupted() {
		return routine.isInterrupted();
	}

	public int failureTimes() {
		return failure;
	}

	public void start() {
		failure++;
		routine = new RoutineSchedule();
		routine.start();
	}

	class RoutineSchedule extends Thread {
		public void run() {

		}
	}

}