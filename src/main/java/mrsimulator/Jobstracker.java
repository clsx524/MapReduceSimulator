package mrsimulator;

public class Jobtracker extends Thread {
	
	private static Jobtracker instance = null;
	private ArrayList<String> allJobs = null;
	private scheduler schedulerInstance;
	private final JobtrackerMessage jmsg;

	private Jobtracker(ArrayList<String> alls) {
		allJobs = alls;
		jmsg = JobtrackerMessage.getInstance();
		scheduler = SchedulerFactory.getInstance();
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
            synchronized(jmsg) {
                try {
            	   jmsg.wait();
                } catch (InterruptedException e) {
            	   e.printStackTrace();
                }
                scheduler.notify();
            }
        }
	}
}