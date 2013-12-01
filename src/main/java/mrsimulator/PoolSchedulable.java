package mrsimulator;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class PoolSchedulable implements Comparable<PoolSchedulable> {
	
	public long runningTasks;
	public Queue <JobInfo.TaskInfo> jobQueue = null;
	
	public PoolSchedulable (){
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