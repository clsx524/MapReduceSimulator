package mrsimulator;

public class SimulatorEngine {

	private String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first50jobs.tsv";
	private BufferedReader inputReader = null;
<<<<<<< HEAD

	private ArrayList<JobInfo> allJobs = new ArrayList<JobInfo>();
	private Iterable<JobInfo> jobIterator = null;

=======
	private ArrayList<String> allJobs = new ArrayList<String>();
	private Iterable<String> jobIterator = null;
>>>>>>> 795b53dfe06357640b44203031dc0e2e1650ce93
	private Jobtracker jobtrackerInstance = null;
	private Scheduler schedulerInstance = null;
	private NetworkSimulator networkInstance = null;
	private Timer timer = null;
<<<<<<< HEAD

	private Topology topology = null;

	private DistributedFileSystem dfs = null;

	private Random rd = new Random(System.currentTimeMillis());

	private Semaphore netSemaphore = null;

=======
	private Configure config = null;
	private TimerMessage tmsg = null;
	private Random rd = new Random(System.currentTimeMillis());
	//public simulatorEngine() {
		//inputReader = new BufferedReader(new FileReader(inputPath));
>>>>>>> 795b53dfe06357640b44203031dc0e2e1650ce93
	public SimulatorEngine() {
		init();
	}
	public SimulatorEngine(String path) {
		inputPath = path;
		init();
	}
	private void init() {
		/************* Read input file *************/
		inputReader = new BufferedReader(new FileReader(inputPath));
		readInputFile();
<<<<<<< HEAD

		/************* Init Semaphore *************/
		netSemaphore = new Semaphore(1);

		/************* Init scheduler *************/
		schedulerInstance = SchedulerFactory.newInstance(Configure.schedulerType);

		/************* Init topology *************/
		topology = TopologyFactory.newInstance(Configure.topologyType);

		/************* Init network *************/
		networkInstance = NetworkSimulator.newInstance(netSemaphore);
		networkInstance.setTopology(topology, Configure.slotsPerNode);

		/************* Init distributed file system *************/
		dfs = DistributedFileSystem.newInstance(Configure.replica, Configure.blockSize, Configure.execSpeed, Configure.ioSpeed);
		dfs.updatePrefs(allJobs);
		dfs.updateTaskNumber(allJobs);

		/************* Init timer *************/
		timer = Timer.getInstance(Configure.corePoolSize);

		/************* Init all services *************/
		networkInstance.start();
		schedulerInstance.start();
=======
		readConfig();
		tmsg = TimerMessage.getInstance();
		networkInstance = NetworkSimulator.getInstance();
		// the number of maps and reduces
		networkInstance.setNode(config.get("nodes"));
		networkInstance.setTopology(config.get("topology"));
		schedulerInstance = SchedulerFactory.newInstance(config.get("schedulerType"));
		timer = Timer.getInstance(config.get("corePoolSize"));
		jobtrackerInstance = Jobtracker.newInstance(allJobs);
		networkInstance.start();
		schedulerInstance.start();
		timer.start();	
		jobtrackerInstance.start();	
	}
	private void readConfig() {




>>>>>>> 795b53dfe06357640b44203031dc0e2e1650ce93
	}
	private void readInputFile() {
		String line = null;
		while ((line = inputReader.readLine()) != null)
           	allJobs.add(parseJob(line));
       	
        inputReader.close();
        jobIterator = allJobs.iterator();
	}
<<<<<<< HEAD

	private JobInfo parseJob(String str) {
		String[] strs = str.split("\\t");
		JobInfo job = new JobInfo(strs);
		Long inputNode = -1L;
		if (strs.length <= 6)
			inputNode = rd.nextLong() % networkInstance.getNodeNumber();
		else
			inputNode = job.getInputNode() % networkInstance.getNodeNumber();
		job.setInputNode(inputNode);
		// ArrayList<Integer> prefs = getPreference(inputNode);
		// job.setPrefs(prefs);
		return job;
=======
	private JobInfo getOneJob() {
		if (jobIterator.hasNext()) {
			String[] strs = jobIterator.next().split("\\t");
			JobInfo job = new JobInfo(strs);
			Long inputNode = -1L;
			if (strs.length <= 6)
				inputNode = rd.nextLong() % networkInstance.getNodeNumber();
			else
				inputNode = job.getInputNode() % networkInstance.getNodeNumber();
			// file repilca
			job.setInputNode(inputNode);
			ArrayList<Integer> prefs = getPreference(inputNode);
			job.setPrefs(prefs);
			return job;
		}
	private String[] getOneJob() {
		if (jobIterator.hasNext())
			return jobIterator.next().split("\\t");
		return null;
	}


	private ArrayList<Integer> getPreference(String[] job) {
		
>>>>>>> 795b53dfe06357640b44203031dc0e2e1650ce93
	}

    public static void main( String[] args) {
    	while (jobIterator.hasNext()) {
    		timer.scheduleJob(job.next().maps, "M");
    		Thread.sleep(20000);
    	}
    	// find all threads finish, stop them
<<<<<<< HEAD
    	timer.join();

=======
>>>>>>> 795b53dfe06357640b44203031dc0e2e1650ce93
    	schedulerInstance.join();
    	networkInstance.join();
    	
    }	

    
}