package mrsimulator;

<<<<<<< HEAD
interface Scheduler {

	public int schedule(JobInfo job);
=======
public class scheduler {

	private static scheduler instance = null;

	protected static scheduler() {

	}

	public static scheduler getInstance() {
		if (instance == null)
			instance = new scheduler();
		return instance;
	}

	public int schedule(String[] job, ArrayList<Integer> prefs) {
		Integer[] availableSlots = networkInstance.getAllAvailableSlots();
	}

>>>>>>> c2c3a20af3c8a2f9a5dd33fae134f5abecf002d5

}