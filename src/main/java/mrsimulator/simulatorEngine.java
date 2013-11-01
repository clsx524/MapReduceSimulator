package mrsimulator;

public class SimulatorEngine {

	private String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first50jobs.tsv";
	private BufferedReader inputReader = null;
	private ArrayList<String> allJobs = new ArrayList<String>();
	private Iterable<String> jobIterator = null;
	private Jobtracker jobtrackerInstance = null;
	private Scheduler schedulerInstance = null;
	private NetworkSimulator networkInstance = null;
	private Timer timer = null;
	private Configure config = null;
	private TimerMessage tmsg = null;
	private Random rd = new Random(System.currentTimeMillis());
	//public simulatorEngine() {
		//inputReader = new BufferedReader(new FileReader(inputPath));
	public SimulatorEngine() {
		init();
	}
	public SimulatorEngine(String path) {
		inputPath = path;
		init();
	}
	private void init() {
		inputReader = new BufferedReader(new FileReader(inputPath));
		readInputFile();
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




	}
	private void readInputFile() {
		String line = null;
		while ((line = inputReader.readLine()) != null) {
           	allJobs.add(line);
       	}
        inputReader.close();
        jobIterator = allJobs.iterator();
	}
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
		
	}

    public static void main( String[] args) {
    	String[] job = null;
    	while ((job = getOneJob()) != null) {
    		tmsg.setJobMsg(job, "JOB");
    		tmsg.notify();
    		Thread.sleep(2000);
    	}
    	// find all threads finish, stop them
    	schedulerInstance.join();
    	networkInstance.join();
    	timer.join();
    }	

    
}