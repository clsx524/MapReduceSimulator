package clsx524;

public class networkSimulator {

	private static networkSimulator instance = null;
	private networkSimulator() {

	}

	public static getInstance() {
		if (instance == null)
			instance = new networkSimulator();
		return instance;
	}
	


	public Integer[] getAllAvailableSlots() {

	}
}