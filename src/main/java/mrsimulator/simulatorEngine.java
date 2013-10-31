package mrsimulator;

public class simulatorEngine {

	private String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first50jobs.tsv";
	private BufferedReader inputReader = null;
	private ArrayList<String> allJobs = new ArrayList<String>();
	private Iterable<String> jobIterator = null;

	private scheduler schedulerInstance = null;

	private networkSimulator networkInstance = null;

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
    		ArrayList<Integer> prefs = getPreference(job);
    		schedulerInstance.schedule(job, prefs);
    	}
        
    }	





}