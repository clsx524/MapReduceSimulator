package mrsimulator;

public class simulatorEngine {

	private String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first50jobs.tsv";
	private BufferedReader inputReader = null;
	private ArrayList<String> allJobs = new ArrayList<String>();
	private Iterable<String> jobIterator = null;

	private scheduler schedulerInstance = null;

	private networkSimulator networkInstance = null;

	private Random rd = new Random(System.currentTimeMillis());

	public simulatorEngine() {
		inputReader = new BufferedReader(new FileReader(inputPath));

		schedulerInstance = scheduler.getInstance();
		networkInstance = networkSimulator.getInstance();

		readInputFile();
		readConfig();
	}

	public simulatorEngine(String path) {
		inputPath = path;
		inputReader = new BufferedReader(new FileReader(inputPath));

		schedulerInstance = scheduler.getInstance();
		networkInstance = networkSimulator.getInstance();

		readInputFile();
		readConfig();
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
			Long inputNode = -1;
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
    		schedulerInstance.schedule(job);
    	}
        
    }	





}