package mrsimulator;

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


}