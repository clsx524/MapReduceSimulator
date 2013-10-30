package mrsimulator;

public class FIFOScheduler implements scheduler extends Thread {

	private class ArrivalTimeComparator implements Comparator<JobInfo> {
		public int compare(JobInfo j1, JobInfo j2) {
			return j1.getArrivalTime().compareTo(j2.getArrivalTime());
		}
	}

	private PriorityQueue<JobInfo> queue = null;
	private ArrivalTimeComparator atc = new ArrivalTimeComparator();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private static Scheduler instance = null;

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
		if (job == null)
            throw new NullPointerException("job is null");
		queue.add(job);
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