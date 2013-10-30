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
}