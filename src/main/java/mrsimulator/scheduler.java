package mrsimulator;

interface Scheduler {

	public void schedule(JobInfo.TaskInfo[] tasks);

	public void threadStart();

	public void threadJoin();

	public void threadStop();

	public void setTimer();

	public int getQueueSize();

}