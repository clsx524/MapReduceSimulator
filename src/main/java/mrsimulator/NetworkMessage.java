package mrsimulator;

public class NetworkMessage {

	private JobInfo.TaskInfo task = null;
	
	private static NetworkMessage nmsg = null;

	private boolean stopSign = false;

	private NetworkMessage() {

	}

	public static NetworkMessage getInstance() {
		if (nmsg == null)
			nmsg = return NetworkMessage();
		return nmsg;
	}

	public setMessage(JobInfo.TaskInfo t) {
		task = t;
	}

	public boolean stop() {
		return stopSign;
	}




}