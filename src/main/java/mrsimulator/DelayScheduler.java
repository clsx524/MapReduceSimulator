package mrsimulator;

public class DelayScheduler implements scheduler extends Thread {

	private class StartTimeComparator implements Comparator<JobInfo> {
		public int compare(JobInfo j1, JobInfo j2) {
	 		if (j1.getMaps().length() + j1.getReduce().length() <= j2.getMaps.length()+j2.getReduce().length())
	 			return 1;
	 		else
	 			return -1;
	 	}
	}

	private Queue <JobInfo.TaskInfo> queue = null;
	// private StartTimeComparator atc = new StartTimeComparator();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private NetworkMessage nmsg = NetworkMessage.getInstance();

	private static Scheduler instance = null;

	private boolean stopSign = false;

	private DelayScheduler() {
		queue = new LinkedBlockingQueue <JobInfo>();
	}

	public static scheduler getInstance() {
		super();
		if (instance == null) 
			instance = new DelayScheduler();
		return instance;
	} 

	public int schedule(JobInfo job, String type) {
		if (job == null)
            throw new NullPointerException("job is null");

        queue.put(job);
        // if (type.equals("MAP"))
        // 	for (JobInfo.TaskInfo task : job.getMaps())
        // 		queue.put(task);
				
	}

	public void run() {
		while (true) {
            synchronized(this) {
                try {
                	if (stopSign == true)
                		break;
                	if (networkInstance.hasAvailableSlots() && queue.peek() != null)
                		JobInfo currJob = queue.poll();
                		for(int k: currJob.getMaps().length()){
                			
                		}
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