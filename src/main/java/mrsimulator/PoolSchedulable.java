package mrsimulator;

import java.util.Queue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class PoolSchedulable implements Comparable<PoolSchedulable> {
	
	public long runningTasks;
	public Queue <JobInfo.TaskInfo> jobQueue = null;
	public boolean type;

	public Set<Integer> getPrefs() {
		if (runningTasks == 0)
			throw new IllegalArgumentException("PoolSchedulable is empty");
		if (type)
			return jobQueue.peek().getMapPrefs();
		else
			return jobQueue.peek().getReducePrefs();
	}

	public PriorityBlockingQueue<SlotsLeft> getRackLocality() {
		return jobQueue.peek().getRackLocality();
	}
	
	public PoolSchedulable() {
		runningTasks = 0;
		jobQueue = new LinkedBlockingQueue<JobInfo.TaskInfo>();
	}
	
	public boolean isEmpty(){
		return (0 == runningTasks);
	}
	
	public void addTask(JobInfo.TaskInfo t){
		jobQueue.offer(t);
		runningTasks++;
	}
	
	public JobInfo.TaskInfo getTask(){
		runningTasks--;
		return jobQueue.poll();
	}	

	@Override
    public int compareTo(PoolSchedulable other){
       	return (runningTasks > other.runningTasks ? 1 : (runningTasks == other.runningTasks ? 0 : -1));
    }	
}