package mrsimulator;

import java.util.ArrayList;
import java.lang.Math;

public class JobInfo {

	public class TaskInfo {

		public long duration = -1L;
		public Integer fileSize = -1; // bytes
		public Integer nodeIndex = -1;
		public long startTime = -1;
		public long endTime = -1;
		public boolean progress = false;
		public boolean taskType = true; // true MAP; false REDUCE

		public String toString() {
			return jobID + " " + duration + " " + startTime + " " + endTime + " " + taskType;
		}

		public Integer getMapNumber() {
			return mapNumber;
		}
		// public Double getReduceNumber() {
		// 	return reduceNumber;
		// }
		public Integer getMapProgress() {
			return mapProgress;
		}
		public Integer getReduceProgress() {
			return reduceProgress;
		}
		public void setMapProgress() {
			mapProgress++;
		}		
		public void setReduceProgress() {
			reduceProgress++;
		}	
		public TaskInfo[] getReduces() {
			return reduces;
		}	
		public JobInfo getJob() {
			return JobInfo.this;
		}
		public int getTotalTasksNumber() {
			return mapNumber + reduceNumber;
		}
		public void setJobEndTime() {
			JobInfo.this.endTime = endTime;
		}
		public boolean isFinished() {
			if (mapNumber == mapProgress && reduceNumber == reduceProgress)
				return true;
			return false;
		}
		public void setDuration(double d) {
			duration = (long) d;
		}
		public ArrayList<Integer> getMapPrefs() {
			return mapPrefs;
		}
		public ArrayList<Integer> getReducePrefs() {
			return reducePrefs;
		}
	}

	public Integer jobID = -1;
	public Long arrivalTime = -1L;
	public Integer gapTime = -1;
	public Long mapInputBytes = -1L;
	public Long shuffleBytes = -1L;
	public Long reduceOutputBytes = -1L;
	//public Long inputNode = -1L; 

	public Integer mapNumber;
	public Integer reduceNumber;

	public Integer mapProgress = 0;
	public Integer reduceProgress = 0;

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

		// if (strs.length > 6) 
		// 	inputNode = Long.parseLong(strs[6].substring(9));
	}

	public void initTasks(Long blockSize) {
		if (mapInputBytes % blockSize == 0)
			mapNumber = (int) (mapInputBytes / blockSize);
		else
			mapNumber = (int) (mapInputBytes / blockSize + 1);

		if (reduceOutputBytes % blockSize == 0)
			reduceNumber = (int) (reduceOutputBytes / blockSize);
		else
			reduceNumber = (int) (reduceOutputBytes / blockSize + 1);

		maps = new TaskInfo[mapNumber];
		reduces = new TaskInfo[reduceNumber];
		//System.out.println(mapNumber + " " + reduceNumber);
		Long mapBytes = mapInputBytes;
		for (int i = 0; i < mapNumber; i++) {
			maps[i] = this.new TaskInfo();
			maps[i].taskType = true;
			if (mapBytes > blockSize) {
				maps[i].setDuration((blockSize.doubleValue() / Configure.execSpeed + blockSize.doubleValue() / Configure.ioSpeed) * Math.pow(10.0, 6.0));
				maps[i].fileSize = blockSize.intValue();
				mapBytes -= blockSize;				
			} else {
				maps[i].setDuration((mapBytes.doubleValue() / Configure.execSpeed + mapBytes.doubleValue() / Configure.ioSpeed) * Math.pow(10.0, 6.0));
				maps[i].fileSize = mapBytes.intValue();				
			}
		}

		Long reduceBytes = reduceOutputBytes;
		for (int i = 0; i < reduceNumber; i++) {
			reduces[i] = this.new TaskInfo();
			reduces[i].taskType = false;
			if (reduceBytes > blockSize) {
				reduces[i].setDuration((blockSize.doubleValue() / Configure.execSpeed + shuffleBytes.doubleValue() / Configure.ioSpeed / reduceNumber) * Math.pow(10.0, 6.0));
				reduces[i].fileSize = blockSize.intValue();
				mapBytes -= blockSize;				
			} else {
				reduces[i].setDuration((reduceBytes.doubleValue() / Configure.execSpeed + shuffleBytes.doubleValue() / Configure.ioSpeed / reduceNumber) * Math.pow(10.0, 6.0));
				reduces[i].fileSize = reduceBytes.intValue();				
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

	// public long getDurationWithTaskID(String taskType, Integer tid) {
	// 	if (taskType.equals("MAP"))
	// 		return maps[tid].duration;
	// 	else if (taskType.equals("REDUCE"))
	// 		return reduces[tid].duration;
	// 	else
	// 		throw new IllegalArgumentException("Invalid task type");
	// } 

	// public Integer getNodeIndexWithTaskID(String taskType, Integer tid) {
	// 	if (taskType.equals("MAP"))
	// 		return maps[tid].nodeIndex;
	// 	else if (taskType.equals("REDUCE"))
	// 		return reduces[tid].nodeIndex;
	// 	else
	// 		throw new IllegalArgumentException("Invalid task type");
	// }

//	public double checkProgress() {
//		int number = 0;
//		for (int i = 0; i < maps.length; i++)
//			if (maps[i].progress)
//				number++;
//		for (int i = 0; i < reduces.length; i++)
//			if (reduces[i].getProgress())
//				number++;
//
//		return (double)number/((double)maps.length + (double)reduces.length);
//	}



	public String jobToString() {
		return "Job: " + jobID + " " + startTime + " " + endTime;
	}

	public String mapTaskToString(int i) {
		return "Task: " + maps[i].taskType + " " + maps[i].startTime + " " + maps[i].endTime + " " + maps[i].duration + " " + maps[i].nodeIndex + " " + maps[i].fileSize;
	}

	public String reduceTaskToString(int i) {
		return "	Task: " + reduces[i].taskType + " " + reduces[i].startTime + " " + reduces[i].endTime + " " + reduces[i].duration + " " + reduces[i].nodeIndex + " " + reduces[i].fileSize;
	}
}