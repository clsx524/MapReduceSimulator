package mrsimulator;

public class JobInfo {

	public class TaskInfo {

		public Double duration = -1.0;
		public Integer fileSize = -1; // bytes
		public Integer nodeIndex = -1;
		public long startTime = -1;
		public long endTime = -1;
		public boolean progress = false;
		public boolean taskType = true; // true MAP; false REDUCE

		public Double getMapNumber() {
			return mapNumber;
		}
		public Double getReduceNumber() {
			return reduceNumber;
		}
		public Double getMapProgress() {
			return mapProgress;
		}
		public Double getReduceProgress() {
			return reduceProgress;
		}	
		public JobInfo.TaskInfo getReduces() {
			return reduces;
		}	
		public JobInfo getJob() {
			return JobInfo.this;
		}
		public void setJobEndTime() {
			return JobInfo.this.endTime = endTime;
		}
	}

	public Integer jobID = -1;
	public Long arrivalTime = -1L;
	public Integer gapTime = -1L;
	public Long mapInputBytes = -1L;
	public Long shuffleBytes = -1L;
	public Long reduceOutputBytes = -1L;
	public Long inputNode = -1L; 

	public Double mapNumber;
	public Double reduceNumber;

	public Double mapProgress = 0;
	public Double reduceProgress = 0;

	public long startTime;
	public long endTime;

	//private ArrayList<Long> replica = new ArrayList<Long>();

	public JobInfo.TaskInfo[] maps = null; // how to make sure map scheduled before reduce
	public JobInfo.TaskInfo[] reduces = null;

	public ArrayList<Integer> mapPrefs = new ArrayList<Integer>();
	public ArrayList<Integer> reducePrefs = new ArrayList<Integer>();

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

	public void initTasks(Integer blockSize) {
		if (mapNumber % blockSize == 0)
			mapNumber = mapInputBytes / blockSize;
		else
			mapNumber = mapInputBytes / blockSize + 1L;

		if (reduceOutputBytes % blockSize == 0)
			reduceOutputBytes = reduceOutputBytes / blockSize;
		else
			reduceOutputBytes = reduceOutputBytes / blockSize + 1L;

		maps = new JobInfo.TaskInfo[mapNumber];
		reduces = new JobInfo.TaskInfo[reduceNumber];

		int mapBytes = mapInputBytes;
		for (int i = 0; i < mapNumber; i++) {
			maps[i].taskType = true;
			if (mapBytes > blockSize) {
				maps[i].duration = blockSize.toDouble() / Configure.execSpeed + blockSize.toDouble() / Configure.ioSpeed;
				maps[i].fileSize = blockSize;
				mapBytes -= blockSize;				
			} else {
				maps[i].duration = mapBytes.toDouble() / Configure.execSpeed + mapBytes.toDouble() / Configure.ioSpeed;
				maps[i].fileSize = mapBytes;				
			}
		}

		int reduceBytes = reduceOutputBytes;
		for (int i = 0; i < mapNumber; i++) {
			reduces[i].taskType = false;
			if (reduceBytes > blockSize) {
				reduces[i].duration = blockSize.toDouble() / Configure.execSpeed + shuffleBytes.toDouble() / Configure.ioSpeed / reduceNumber;
				reduces[i].fileSize = blockSize;
				mapBytes -= blockSize;				
			} else {
				reduces[i].duration = reduceBytes.toDouble() / Configure.execSpeed + shuffleBytes.toDouble() / Configure.ioSpeed / reduceNumber;
				reduces[i].fileSize = reduceBytes;				
			}
		}
	}

	public JobInfo.TaskInfo getTaskAt(Integer tid, String taskType) {
		if (taskType.equals("MAP"))
			return maps[tid];
		else if (taskType.equals("REDUCE"))
			return reduces[tid];
		else
			throw new IllegalArgumentException("Invalid task type");
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

	public boolean isFinished() {
		if (mapNumber == mapProgress && reduceNumber == reduceProgress)
			return true;
		return false;
	}

	public String jobToString() {
		return "Job: " + job.jobID + " " + startTime + " " + endTime;
	}

	public String mapTaskToString(int i) {
		return "Task: " + maps[i].taskType + " " + maps[i].startTime + " " + maps[i].endTime + " " + maps[i].duration + " " + maps[i].nodeIndex + " " + maps[i].fileSize;
	}

	public String reduceTaskToString(int i) {
		return "	Task: " + reduces[i].taskType + " " + reduces[i].startTime + " " + reduces[i].endTime + " " + reduces[i].duration + " " + reduces[i].nodeIndex + " " + reduces[i].fileSize;
	}
}