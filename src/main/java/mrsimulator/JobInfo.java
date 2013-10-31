package mrsimulator;

public class JobInfo {

	public class TaskInfo {

		private Integer taskID;
		private Integer duration;
		private Integer fileSize;
		private Integer nodeIndex;
		private Integer startTime;
		private boolean progress = false;

		public void setNodeIndex(Integer i) {
			nodeIndex = i;
		}

		public boolean getProgress() {
			return progress;
		}

		public void setFinished() {
			progress = true;
		}

		public Integer getJobID() {
			return jobID;
		}

		public Integer getStartTime() {
			return startTime;
		}

		public void setStartTime(Integer t) {
			startTime = t;
		}
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

	private TaskInfo[] maps = null; // how to make sure map scheduled before reduce
	private TaskInfo[] reduces = null;

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

	public TaskInfo[] getMaps() {
		return maps;
	}

	public TaskInfo[] getReduces() {
		return reduces;
	}

	public Integer getJobID() {
		return jobID;
	}

	public JobInfo.TaskInfo getTaskAt(Integer tid, String taskType) {
		if (taskType.equals("MAP"))
			return maps[tid];
		else if (taskType.equals("REDUCE"))
			return reduces[tid];
		else
			throw new IllegalArgumentException("Invalid task type");
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

	public double checkProgress() {
		int number = 0;
		for (int i = 0; i < maps.length; i++)
			if (maps[i].getProgress())
				number++;
		for (int i = 0; i < reduces.length; i++)
			if (reduces[i].getProgress())
				number++;

		return (double)number/((double)maps.length + (double)reduces.length);
	}


}