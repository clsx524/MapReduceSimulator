package mrsimulator;

public class JobInfo {

	public class taskInfo {

		
	}



	private Integer jobID = -1;
	private Long arrivalTime = -1L;
	public Integer gapTime = -1L;
	public Long mapInputBytes = -1L;
	public Long shuffleBytes = -1L;
	public Long reduceOutputBytes = -1L;
	public Long inputNode = -1L;

	private ArrayList<Integer> prefs = null;

	public Integer getJobID() {
		return jobID;
	}

	public Long getArrivalTime() {
		return arrivalTime;
	}

	public void setInputNode(Long i) {
		inputNode = i;
	}

	public void setPrefs(ArrayList<Integer> p) {
		prefs = p;
	}

	public ArrayList<Integer> getPrefs() {
		return prefs;
	}


	public JobInfo(String[] strs) {
		jobID = Integer.parseInt(strs[0].substring(3));
		arrivalTime = Long.parseLong(strs[1]);
		gapTime = Integer.parseInt(strs[2]);
		mapInputBytes = Long.parseLong(strs[3]);
		shuffleBytes = Long.parseLong(strs[4]);
		reduceOutputBytes = Long.parseLong(strs[5]);

		if (strs.length > 6) 
			inputNode = Long.parseLong(strs[6].substring(9));
	}

}