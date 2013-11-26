package mrsimulator;

public class SchedulerFactory {

	private Scheduler schedulerInstance = null;

	public static Scheduler newInstance(String type) {
		if (type.equals("FIFOScheduler"))
			schedulerInstance = new FIFOScheduler();
		else if (type.equals(""))
		else
			throw new IllegalArgumentException("Invalid Scheduler Type");
		return schedulerInstance;
	}

	public static Scheduler getInstance() {
		if (schedulerInstance == null)
			throw new NullPointerException("schedulerInstance is null");
		return schedulerInstance;
	}
}