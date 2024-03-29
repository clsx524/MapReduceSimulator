package mrsimulator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class FIFOScheduler implements Scheduler  {

	private Queue <JobInfo.TaskInfo> queue = new LinkedBlockingQueue<JobInfo.TaskInfo>();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private Timer timer = null;

	private boolean stopSign = false;

	private JobInfo.TaskInfo curr = null;

	private RoutineSchedule routine = null;

	private int failure = -1;

	public void schedule(JobInfo.TaskInfo[] tasks) {
		if (tasks == null)
            throw new NullPointerException("job is null");

       	for (JobInfo.TaskInfo task : tasks) {
       		queue.offer(task);
       	} 	
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
			Set<Integer> prefs = null;
			PriorityBlockingQueue<SlotsLeft> queueLeft = null;
			while (!stopSign) {

				if (networkInstance.hasAvailableSlots() && queue.peek() != null) {
					prefs = null;
                	curr = queue.poll();
					if (curr.taskType == true)
						prefs = curr.getMapPrefs();
					else
					 	prefs = curr.getReducePrefs(); // assume it not empty

					queueLeft = curr.getRackLocality();

					boolean found = false;
					for (Integer i : prefs)
						if (networkInstance.checkSlotsAtNode(i)) {
							curr.nodeIndex = i;
							found = true;
							break;								
						}

					if (!found) {
						for (SlotsLeft i : queueLeft)
							if (networkInstance.checkSlotsAtNode(i.machineNumber)) {
								curr.nodeIndex = i.machineNumber;
								found = true;
								break;								
							}
					}
	
					if (!found) {
						int r = networkInstance.pickUpOneSlotRandom();
						curr.nodeIndex = r;
					}
					if (curr.taskType == true)
						curr.setReducePrefs();
					curr.updateDuration();
					timer.scheduleTask(curr);
				}
			}
		}
    }
}