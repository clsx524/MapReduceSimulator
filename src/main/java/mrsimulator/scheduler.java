package mrsimulator;

interface Scheduler {

	public void schedule(JobInfo.TaskInfo[] tasks);

	public void start();

	public void join();

	public void stop();

	public void setTimer();

	public int getQueueSize();

	public boolean isAlive();

	public boolean isInterrupted();

}