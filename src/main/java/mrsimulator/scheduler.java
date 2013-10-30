package mrsimulator;

public abstract class scheduler {

	protected static scheduler instance = null;

	protected static scheduler() {

	}

	public abstract int schedule(JobInfo job);

}