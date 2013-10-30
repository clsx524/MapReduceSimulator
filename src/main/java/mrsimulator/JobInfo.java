package mrsimulator;

public class JobInfo {

	private class taskInfo {

		public String type;
		public Integer taskID;
		public Integer duration;
		public Integer fileSize;
		public Integer nodeIndex;
	}

	private Integer jobID = -1;
	private Long arrivalTime = -1L;
	public Integer gapTime = -1L;
	public Long mapInputBytes = -1L;
	public Long shuffleBytes = -1L;
	public Long reduceOutputBytes = -1L;
	public Long inputNode = -1L; 

	private Integer mapNumber;
	private Integer reduceNumber;

	private taskInfo[] maps = null; // how to make sure map scheduled before reduce
	private taskInfo[] reduces = null;

	private ArrayList<Integer> prefs = null;


	public JobInfo(String[] strs) {
		jobID = Integer.parseInt(strs[0].substring(3));
		arrivalTime = Long.parseLong(strs[1]);
		gapTime = Integer.parseInt(strs[2]);
		mapInputBytes = Long.parseLong(strs[3]);
		shuffleBytes = Long.parseLong(strs[4]);
		reduceOutputBytes = Long.parseLong(strs[5]);

		if (strs.length > 6) 
			inputNode = Long.parseLong(strs[6].substring(9));
	}

	public Integer getJobID() {
		return jobID;
	}

	public Long getArrivalTime() {
		return arrivalTime;
	}

	public void setInputNode(Long i) {
		inputNode = i;
	}

	public void setPrefs(ArrayList<Integer> p) {
		prefs = p;
	}

	public ArrayList<Integer> getPrefs() {
		return prefs;
	}

	public Integer getDurationWithTaskID(String taskType, Integer tid) {
		if (taskType.equals("MAP"))
			return maps[tid].duration;
		else if (taskType.equals("REDUCE"))
			return reduces[tid].duration;
		else
			throw new IllegalArgumentException("Invalid task type");
	} 

	public Integer getNodeIndexWithTaskID(String taskType, Integer tid) {
		if (taskType.equals("MAP"))
			return maps[tid].nodeIndex;
		else if (taskType.equals("REDUCE"))
			return reduces[tid].nodeIndex;
		else
			throw new IllegalArgumentException("Invalid task type");
	}


}