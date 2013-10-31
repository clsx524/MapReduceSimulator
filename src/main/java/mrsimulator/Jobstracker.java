package mrsimulator;

public class Jobtracker extends Thread {
	
	private static Jobtracker instance = null;
	private ArrayList<String> allJobs = null;

	private Jobtracker(ArrayList<String> alls) {
		allJobs = alls;
	}

	public static Jobtracker newInstance(ArrayList<String> alls) {
		if (instance == null)
			instance = new Jobtracker(alls);
		return instance;
	}

	public static Jobtracker getInstance() {
		if (instance == null)
			throw new NullPointerException("Jobstracker is null");
		return instance;
	}

 
	public void run() {
		while (true) {
            synchronized(tmsg) {
                try {
            	   tmsg.wait();
                } catch (InterruptedException e) {
            	   e.printStackTrace();
                }
            }
            if (tmsg.getType().equals("JOB"))
                slots.schedule(new JobAfterTimerDone(tmsg.getJobInfo()), tmsg.getDuration(), TimeUnit.SECONDS);
            else if (tmsg.getType().equals("TASK")) {
                Integer nodeIndex = tmsg.getNodeIndex();
                networksimulator.occupyOneSlotAtNode(nodeIndex);
                slots.schedule(new TaskAfterTimerDone(nodeIndex), tmsg.getDuration(), TimeUnit.SECONDS);
            } else 
                throw new IllegalArgumentException("Invalid TimerMessage Type");
                
        }
	}
}