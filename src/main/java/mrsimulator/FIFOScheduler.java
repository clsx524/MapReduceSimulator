package mrsimulator;

public class FIFOScheduler implements Scheduler extends Thread {

	// private class StartTimeComparator implements Comparator<JobInfo.TaskInfo> {
	// 	public int compare(JobInfo.TaskInfo j1, JobInfo.TaskInfo j2) {
	// 		return j1.getStartTime().compareTo(j2.getStartTime());
	// 	}
	// }

	private Queue <JobInfo.TaskInfo> queue = new LinkedBlockingQueue<JobInfo.TaskInfo>();
	// private StartTimeComparator atc = new StartTimeComparator();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private Timer timer = timer.getInstance();

	private boolean stopSign = false;

	private JobInfo.TaskInfo curr = null;

	public int schedule(JobInfo.TaskInfo[] tasks) {
		if (job == null)
            throw new NullPointerException("job is null");

       	for (JobInfo.TaskInfo task : tasks)
        	queue.put(task);
	}

	public void run() {
		while (true) {
			if (stopSign == true)
          		break;

			if (networkInstance.hasAvailableSlots() && queue.peek() != null)
                curr = queue.poll();
				Integer[] availableSlots = networkInstance.getAllAvailableSlots();
				ArrayList<Integer> prefs = curr.getPrefs(); // assume it not empty

				boolean found = false;
				for (Integer i : prefs)
					if (networkInstance.checkSlotsAtNode(i)) {
						curr.nodeIndex(i);
						found = true;
						break;								
					}
				if (!found) {
					int r = networkInstance.pickUpOneSlotRandom();
					curr.setNodeIndex(r);
				}
				timer.scheduleTask(curr);
			}
		}
	}

}