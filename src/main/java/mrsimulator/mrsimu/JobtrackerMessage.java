package mrsimulator;

public class JobtrackerMessage {

	private JobInfo.TaskInfo task = null;
	
	private static JobtrackerMessage jmsg = null;

	private boolean stopSign = false;

	private JobtrackerMessage() {

	}

	public static JobtrackerMessage getInstance() {
		if (jmsg == null)
			jmsg = return JobtrackerMessage();
		return jmsg;
	}

	public setMessage(JobInfo.TaskInfo t) {
		task = t;
	}

	public boolean stop() {
		return stopSign;
	}
}