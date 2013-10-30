package mrsimulator;

public class SimulatorEngine {

	private String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first50jobs.tsv";
	private BufferedReader inputReader = null;
	private ArrayList<String> allJobs = new ArrayList<String>();
	private Iterable<String> jobIterator = null;

	private Scheduler schedulerInstance = null;

	private NetworkSimulator networkInstance = null;

	private Timer timer = null;

	private Configure config = null;

	private Random rd = new Random(System.currentTimeMillis());

	private TimerMessage tmsg = null;

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
		networkInstance.setNode(config.get("nodes"));
		networkInstance.setTopology(config.get("topology"));

		schedulerInstance = SchedulerFactory.newInstance(config.get("schedulerType"));

		timer = Timer.getInstance(config.get("corePoolSize"));

		networkInstance.start();
		schedulerInstance.start();
		timer.start();		
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
			job.setInputNode(inputNode);
			ArrayList<Integer> prefs = getPreference(inputNode);
			job.setPrefs(prefs);
			return job;
		}
		return null;
	}



	private ArrayList<Integer> getPreference(Long inputNode) { //  the input is obtained by our will
		
	}


    public static void main( String[] args) {
    	JobInfo job = null;
    	while ((job = getOneJob()) != null) {
    		tmsg.setJobMsg(job, "JOB");
    		tmsg.notify();
    	}

    	schedulerInstance.join();
    	networkInstance.join();
    	timer.join();
    }	

    
}