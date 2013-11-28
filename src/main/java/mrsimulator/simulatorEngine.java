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

	private BoundedSemaphore netSemaphore = null;

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

		/************* Init Semaphore *************/
		netSemaphore = new BoundedSemaphore(Configure.sepmaphoreBound);

		/************* Init topology *************/
		topology = TopologyFactory.newInstance(Configure.topologyType);
		topology.genTop();

		/************* Init network *************/
		networkInstance = NetworkSimulator.newInstance(netSemaphore);
		networkInstance.setTopology(topology, Configure.slotsPerNode);

		/************* Init scheduler *************/
		schedulerInstance = SchedulerFactory.newInstance(Configure.schedulerType);
		networkInstance.setScheduler();

		/************* Init distributed file system *************/
		dfs = DistributedFileSystem.newInstance(Configure.replica, Configure.blockSize);
		dfs.updatePrefs(allJobs);
		dfs.updateTaskNumber(allJobs);

		/************* Init timer *************/
		timer = Timer.newInstance(netSemaphore);
		schedulerInstance.setTimer();

		/************* Init all services *************/
		networkInstance.start();
		schedulerInstance.threadStart();
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
		// Long inputNode = -1L;
		// if (strs.length <= 6)
		// 	inputNode = rd.nextLong() % networkInstance.getNodeNumber();
		// else
		// 	inputNode = job.inputNode % networkInstance.getNodeNumber();
		// job.setInputNode(inputNode);
		// ArrayList<Integer> prefs = getPreference(inputNode);
		// job.setPrefs(prefs);
		return job;
	}

	public void scheduleAllJobs() {
		Configure.initialTime = System.currentTimeMillis();
		try {
    		for (JobInfo job : allJobs) {
    			timer.scheduleJob(job);	
    			profile.print(job);
    		}
    		Thread.sleep(36000000);
    	} catch (InterruptedException ie) {
    		System.out.println("Exception thrown  :" + ie);
    	}
	}

	public void join() {
		try {
    	// find all threads finish, stop them
			//while (Configure.total != networkInstance.finished) { Thread.sleep(); }
    		timer.join();
    		schedulerInstance.threadJoin();
    		networkInstance.join();
    	} catch (InterruptedException ie) {
    		System.out.println("Exception thrown  :" + ie);
    	}
	}	   
}