package mrsimulator;

public class SchedulerFactory {

	private Scheduler schedulerInstance = null;

	public static Scheduler newInstance(String type) {
		if (type.equals("FIFOScheduler"))
			schedulerInstance = FIFOScheduler.getInstance();
		else if (type.equals(""))


		else
			throw new IllegalArgumentException("Invalid Scheduler Type");
	}

	public static Scheduler getInstance() {
		return schedulerInstance;
	}
}