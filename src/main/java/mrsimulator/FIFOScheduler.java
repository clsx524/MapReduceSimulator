package mrsimulator;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class FIFOScheduler extends Thread implements Scheduler  {

	// private class StartTimeComparator implements Comparator<JobInfo.TaskInfo> {
	// 	public int compare(JobInfo.TaskInfo j1, JobInfo.TaskInfo j2) {
	// 		return j1.getStartTime().compareTo(j2.getStartTime());
	// 	}
	// }

	private Queue <JobInfo.TaskInfo> queue = new LinkedBlockingQueue<JobInfo.TaskInfo>();
	// private StartTimeComparator atc = new StartTimeComparator();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private Timer timer = Timer.getInstance();

	private boolean stopSign = false;

	private JobInfo.TaskInfo curr = null;

	public void schedule(JobInfo.TaskInfo[] tasks) {
		if (tasks == null)
            throw new NullPointerException("job is null");

       	for (JobInfo.TaskInfo task : tasks)
        	queue.offer(task);
	}

	public void threadStart() {
		this.start();
	}

	public void threadJoin() {
		try {
			this.join();
		} catch (InterruptedException ie) {
    		System.out.println("Exception thrown  :" + ie);
    	}
	}

	public void threadStop() {
		stopSign = true;
	}

	public void run() {
		ArrayList<Integer> prefs = null;
		while (!stopSign) {
			if (networkInstance.hasAvailableSlots() && queue.peek() != null) {
                curr = queue.poll();
				if (curr.taskType == true)
					prefs = curr.getMapPrefs();
				else
				 	prefs = curr.getReducePrefs(); // assume it not empty

				boolean found = false;
				for (Integer i : prefs)
					if (networkInstance.checkSlotsAtNode(i)) {
						curr.nodeIndex = i;
						found = true;
						break;								
					}
				if (!found) {
					int r = networkInstance.pickUpOneSlotRandom();
					curr.nodeIndex = r;
				}
				timer.scheduleTask(curr);
			}
		}
	}
}