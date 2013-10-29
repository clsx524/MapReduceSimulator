package clsx524;

public class networkSimulator extends  Thread {

	private static networkSimulator instance = null;
	private Arraylist<node> nodeInstances = new Arraylist<node>();
	private networkSimulator() {

	}

	public static getInstance() {
		if (instance == null)
			instance = new networkSimulator();
		return instance;
	}
	


	
}