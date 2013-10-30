package mrsimulator;

public class FIFOScheduler implements scheduler extends Thread {

	private class StartTimeComparator implements Comparator<JobInfo.TaskInfo> {
		public int compare(JobInfo.TaskInfo j1, JobInfo.TaskInfo j2) {
			return j1.getStartTime().compareTo(j2.getStartTime());
		}
	}

	private PriorityBlockingQueue<JobInfo.TaskInfo> queue = null;
	private StartTimeComparator atc = new StartTimeComparator();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private static Scheduler instance = null;

	private FIFOScheduler() {
		queue = new PriorityBlockingQueue<JobInfo.TaskInfo>(1, atc);
	}

	public static scheduler getInstance() {
		super();
		if (instance == null) 
			instance = new FIFOScheduler();
		return instance;
	} 

	public int schedule(JobInfo job, String type) {
		if (job == null)
            throw new NullPointerException("job is null");

        if (type.equals("MAP"))
        	for (JobInfo.TaskInfo task : job.getMaps()) {
        		task.setStartTime(System.currentTimeMillis());
        		queue.add(task);
        	}
				
	}

	public void run() {
		while (true) {
			JobInfo curr = queue.poll();
			if (curr == null)


		}
		
		Integer[] availableSlots = networkInstance.getAllAvailableSlots();
		ArrayList<Integer> prefs = curr.getPrefs(); // assume it not empty

		for (Integer i : prefs) {
			if (availableSlots[i] > 0)
				return i;
		}
	}

}