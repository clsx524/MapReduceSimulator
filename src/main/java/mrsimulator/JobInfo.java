package mrsimulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.lang.Math;

import org.apache.commons.math3.distribution.*;

public class JobInfo {

	public class TaskInfo {

		public int taskID = -1;
		public long duration = 0L;
		public Integer fileSize = -1; // bytes
		public Integer nodeIndex = -1;
		public long startTime = -1;
		public long endTime = -1;
		public boolean progress = false;
		public boolean taskType = true; // true MAP; false REDUCE
		public double netTime = 0.0;
		public boolean isRunning = false;
		public double progressRate = 0.0;
		public double estimateCompletion = 0.0;

		public String toString() {
			return jobID + " " + taskID + " " + duration + " " + fileSize + " " + nodeIndex + " " + startTime + " " + endTime + " " + taskType;
		}

		public Integer getMapNumber() {
			return mapNumber;
		}
		public Integer getMapProgress() {
			return mapProgress;
		}
		public Integer getReduceProgress() {
			return reduceProgress;
		}
		public TaskInfo[] getReduces() {
			return reduces;
		}	
		public JobInfo getJob() {
			return JobInfo.this;
		}
		public Integer getJobID() {
			return jobID;
		}
		public int getTotalTasksNumber() {
			return mapNumber + reduceNumber;
		}
		public void setDuration(double d) {
			duration += (long) d;
		}
		public Set<Integer> getMapPrefs() {
			return mapPrefs;
		}
		public Set<Integer> getReducePrefs() {
			return reducePrefs;
		}
		public void updateDuration() {
			if (taskType) {
				if (!mapPrefs.contains(nodeIndex))
					setDuration(netTime);
			} else {
				if (!reducePrefs.contains(nodeIndex))
					setDuration(netTime);
			}
			progressRate = 1.0/duration;
		}
		public void setReducePrefs() {
			if (taskType == true) {
				boolean res = reducePrefs.add(nodeIndex);
				if (res) {
					int rack = nodeIndex / Configure.machinesPerRack;
					for (int i = 0; i < Configure.machinesPerRack; i++) {
						reduceRackLocality.offer(NetworkSimulator.getInstance().node2Left.get(i + Configure.machinesPerRack * rack));
					}
				}
			}
		}
		public PriorityBlockingQueue<SlotsLeft> getRackLocality() {
			if (taskType == true)
				return mapRackLocality;
			else
				return reduceRackLocality;
		}
	}

	public Integer jobID = -1;
	public Long arrivalTime = -1L;
	public Integer gapTime = -1;
	public Long mapInputBytes = -1L;
	public Long shuffleBytes = -1L;
	public Long reduceOutputBytes = -1L;
	//public Long inputNode = -1L; 

	public Integer mapNumber = 0;
	public Integer reduceNumber = 0;

	public Integer mapProgress = 0;
	public Integer reduceProgress = 0;

	public long startTime;
	public long endTime;

	public boolean reduceStarted = false;
	public boolean finished = false;

	public PoolSchedulable ps = null;

	//private ArrayList<Long> replica = new ArrayList<Long>();

	public JobInfo.TaskInfo[] maps = null; // how to make sure map scheduled before reduce
	public JobInfo.TaskInfo[] reduces = null;

	public Set<Integer> mapPrefs = new HashSet<Integer>();
	public Set<Integer> reducePrefs = new HashSet<Integer>();

	public PriorityBlockingQueue<SlotsLeft> mapRackLocality = new PriorityBlockingQueue<SlotsLeft>();
	public PriorityBlockingQueue<SlotsLeft> reduceRackLocality = new PriorityBlockingQueue<SlotsLeft>();

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
		double temp;
		Long mapBytes = mapInputBytes;
		for (int i = 0; i < mapNumber; i++) {
			maps[i] = this.new TaskInfo();
			maps[i].taskID = i;
			maps[i].taskType = true;
			
			if (mapBytes > blockSize) {
				temp = blockSize.doubleValue() / Configure.execSpeed * Math.pow(10.0, 6.0);
				maps[i].setDuration(temp + rpareto(temp, Configure.paretoShape));
				maps[i].fileSize = blockSize.intValue();
				mapBytes -= blockSize;				
			} else {
				temp = mapBytes.doubleValue() / Configure.execSpeed * Math.pow(10.0, 6.0);
				maps[i].setDuration(temp + rpareto(temp, Configure.paretoShape));
				maps[i].fileSize = mapBytes.intValue();				
			}
			maps[i].netTime = maps[i].fileSize.doubleValue() / Configure.ioSpeed * Math.pow(10.0, 6.0);
		}

		Long reduceBytes = reduceOutputBytes;
		for (int i = 0; i < reduceNumber; i++) {
			reduces[i] = this.new TaskInfo();
			reduces[i].taskID = i;
			reduces[i].taskType = false;
			reduces[i].netTime = shuffleBytes.doubleValue() / Configure.ioSpeed / reduceNumber * Math.pow(10.0, 6.0);

			if (reduceBytes > blockSize) {
				temp = blockSize.doubleValue() / Configure.execSpeed * Math.pow(10.0, 6.0);
				reduces[i].setDuration(temp + rpareto(temp, Configure.paretoShape));
				reduces[i].fileSize = blockSize.intValue();
				mapBytes -= blockSize;				
			} else {
				temp = reduceBytes.doubleValue() / Configure.execSpeed * Math.pow(10.0, 6.0);
				reduces[i].setDuration(temp + rpareto(temp, Configure.paretoShape));
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

	public boolean isFinished() {
		if (finished == true)
			return true;
		if (mapNumber.equals(mapProgress) && reduceNumber.equals(reduceProgress)) {
			finished = true;
			return true;
		}
		return false;
	}

	public void updateProgress() {
		int mp = 0;
		int rp = 0;
		for (int i = 0; i < mapNumber; i++) {
			if (maps[i].progress == true)
				mp++;
			maps[i].estimateCompletion = maps[i].duration + maps[i].startTime - System.currentTimeMillis();			
		}

		for (int i = 0; i < reduceNumber; i++) {
			if (reduces[i].progress == true)
				rp++;	
			reduces[i].estimateCompletion = reduces[i].duration + reduces[i].startTime - System.currentTimeMillis();			
		}

		mapProgress = mp;
		reduceProgress = rp;	
	}

	public double prog() {
		return (mapProgress.doubleValue() / mapNumber.doubleValue());
	}

	public String jobToString() {
		return "Job: " + jobID + " " + arrivalTime + " " + startTime + " " + endTime + " " + mapInputBytes + " " + shuffleBytes + " " + reduceOutputBytes + " " + mapPrefs.size() + " " + reducePrefs.size() + " " + Arrays.toString(mapPrefs.toArray()) + " " + Arrays.toString(reducePrefs.toArray());
	}

	public String mapTaskToString(int i) {
		return "	Task: " + maps[i].toString();
	}

	public String reduceTaskToString(int i) {
		return "	Task: " + reduces[i].toString();
	}
	public void setJobEndTime() {
		endTime = System.currentTimeMillis() - Configure.initialTime;
	}

	public double rpareto(double scale, double shape) {
		//ParetoDistribution pd = new ParetoDistribution(scale, shape);
		//return pd.sample();
		return 0.0;
	}
}