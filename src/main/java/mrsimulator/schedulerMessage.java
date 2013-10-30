package mrsimulator;


public class SchedulerMessage {

	private static SchedulerMessage schedulerMessage = null;

	private JobInfo job = null;

	private SchedulerMessage() {

	}

	public static SchedulerMessage getInstance() {
		if (schedulerMessage == null)
			schedulerMessage = new SchedulerMessage();
		return schedulerMessage;
	}

	public void setJob(JobInfo j) {
		job = j;
	}


}