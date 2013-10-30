package mrsimulator;

public class FIFOScheduler extends scheduler implements Runnable {

	private ArrivalTimeComparator implements Comparator<JobInfo> {
		public int compare(JobInfo j1, JobInfo j2) {
			return j1.getArrivalTime().compareTo(j2.getArrivalTime());
		}
	}

	private PriorityQueue<JobInfo> queue = null;
	private ArrivalTimeComparator atc = new ArrivalTimeComparator();

	private FIFOScheduler() {
		queue = new PriorityQueue(1);
	}

	public static scheduler getInstance() {
		super();
		if (instance == null) 
			instance = new FIFOScheduler();
		return instance;
	} 

	public int schedule(JobInfo job) {
		queue.add(job);

		JobInfo curr = queue.poll();
		Integer[] availableSlots = networkInstance.getAllAvailableSlots();
		ArrayList<Integer> prefs = curr.getPrefs(); // assume it not empty

		for (Integer i : prefs) {
			if (availableSlots[i] > 0)
				return
		}
	}

	public void run() {
		
	}

}