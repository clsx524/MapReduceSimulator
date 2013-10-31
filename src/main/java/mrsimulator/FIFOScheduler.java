package mrsimulator;

public class FIFOScheduler implements scheduler extends Thread {

	// private class StartTimeComparator implements Comparator<JobInfo.TaskInfo> {
	// 	public int compare(JobInfo.TaskInfo j1, JobInfo.TaskInfo j2) {
	// 		return j1.getStartTime().compareTo(j2.getStartTime());
	// 	}
	// }

	private Queue <JobInfo.TaskInfo> queue = null;
	// private StartTimeComparator atc = new StartTimeComparator();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private NetworkMessage nmsg = NetworkMessage.getInstance();

	private static Scheduler instance = null;

	private boolean stopSign = false;

	private FIFOScheduler() {
		queue = new LinkedBlockingQueue <JobInfo.TaskInfo>();
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
        	for (JobInfo.TaskInfo task : job.getMaps())
        		queue.put(task);
				
	}

	public void run() {
		while (true) {
            synchronized(this) {
                try {
                	if (stopSign == true)
                		break;
                	if (networkInstance.hasAvailableSlots() && queue.peek() != null)
                		JobInfo.TaskInfo curr = queue.poll();
						Integer[] availableSlots = networkInstance.getAllAvailableSlots();
						ArrayList<Integer> prefs = curr.getPrefs(); // assume it not empty

						for (Integer i : prefs)
							if (availableSlots[i] > 0) {
								curr.setNodeIndex(i);
								nmsg.setMessage(curr);
								nmsg.notify();
							}
                	else 
            	   		this.wait();
                } catch (InterruptedException e) {
            	   e.printStackTrace();
                }
            }       
        }	
	}

}