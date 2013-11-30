package mrsimulator;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class SimulatorEngine {

	private String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first50jobs.tsv";
	private BufferedReader inputReader = null;

	private ArrayList<JobInfo> allJobs = new ArrayList<JobInfo>();

	private Scheduler schedulerInstance = null;
	private NetworkSimulator networkInstance = null;
	private Timer timer = null;

	private Topology topology = null;

	private DistributedFileSystem dfs = null;

	private Random rd = new Random(System.currentTimeMillis());

	private Profiler profile = new Profiler("/Users/eric/Google Drive/GitHub/MapReduceSimulator/", "TasksPreview"); 

	public SimulatorEngine() {
		init();
	}
	public SimulatorEngine(String path) {
		inputPath = path;
		init();
	}
	private void init() {
		/************* Read input file *************/
		try {
			inputReader = new BufferedReader(new FileReader(inputPath));
		} catch (FileNotFoundException io) {
			System.out.println("Exception thrown  :" + io);
		}
		readInputFile();

		/************* Init topology *************/
		topology = TopologyFactory.newInstance(Configure.topologyType);
		topology.genTop();

		/************* Init network *************/
		networkInstance = NetworkSimulator.newInstance();
		networkInstance.setTopology(topology, Configure.slotsPerNode);

		/************* Init scheduler *************/
		schedulerInstance = SchedulerFactory.newInstance(Configure.schedulerType);
		networkInstance.setScheduler();

		/************* Init distributed file system *************/
		dfs = DistributedFileSystem.newInstance(Configure.replica, Configure.blockSize);
		dfs.updatePrefs(allJobs);
		dfs.updateTaskNumber(allJobs);

		/************* Init timer *************/
		timer = Timer.newInstance();
		schedulerInstance.setTimer();

		/************* Init all services *************/
		networkInstance.start();
		schedulerInstance.start();

		System.out.println("Initializing finished ... All services started ...");
	}
	private void readInputFile() {
		String line = null;
		try {
			while ((line = inputReader.readLine()) != null)
           		allJobs.add(parseJob(line));
        	inputReader.close();
        	Configure.total = allJobs.size();
		} catch (IOException io) {
			System.out.println("Exception thrown  :" + io);
		}

	}

	private JobInfo parseJob(String str) {
		String[] strs = str.split("\\t");
		JobInfo job = new JobInfo(strs);
		return job;
	}

	public void scheduleAllJobs() {
		Configure.initialTime = System.currentTimeMillis();
    	for (JobInfo job : allJobs) {
    		timer.scheduleJob(job);	
    		profile.print(job);
    	}
	}

	public void join() {
		try {
    	// find all threads finish, stop them
			int i = 0;
			while (Configure.total != networkInstance.finished) { 
				System.out.println("************ Current Status Summary ************");
				System.out.println("Current time: " + Configure.progressCheckPeriod*i);
				System.out.println("Scheduler alive: " + schedulerInstance.isAlive() + "; Interrupted: " + schedulerInstance.isInterrupted());
				System.out.println("NetworkSimulator alive: " + networkInstance.isAlive() + "; Interrupted: " + networkInstance.isInterrupted());
				System.out.println("Total Finished Jobs: " + networkInstance.finished + " Total Number of Jobs: " + Configure.total);
				System.out.println("Timer queue size: " + timer.jobQueue.getQueue().size() + " # " + timer.taskQueue.getQueue().size() + "; Scheduler queue size: " + schedulerInstance.getQueueSize());
				System.out.println("Slots available: " + networkInstance.hasAvailableSlots());
				System.out.println("Scheduler failure times: " + schedulerInstance.failureTimes());
				System.out.println("Timer schedule times: " + timer.totalTimes);
				System.out.println("CheckProgress size: " + networkInstance.checkProgressSize());
				System.out.println("************************************************");
				if (!schedulerInstance.isAlive())
					schedulerInstance.start();

				Thread.sleep(Configure.progressCheckPeriod);
				i++;
			}
			System.out.println("All Jobs finished");
    		timer.join();
    		schedulerInstance.join();
    		networkInstance.join();
    	} catch (InterruptedException ie) {
    		System.out.println("Exception thrown  :" + ie);
    	}
	}	   
}