package mrsimulator;

<<<<<<< HEAD
public class NetworkSimulator extends Thread {

	private static NetworkSimulator instance = null;

	private Integer[] availableSlots = null;
	private Integer[] nodeInfo = null;

	private final TimerMessage msg;

	private NetworkSimulator() {
		msg = TimerMessage.getInstance();
	}
=======
public class networkSimulator extends  Thread {

	private static networkSimulator instance = null;
	private Arraylist<node> nodeInstances = new Arraylist<node>();
	private networkSimulator() {
>>>>>>> c2c3a20af3c8a2f9a5dd33fae134f5abecf002d5

	public void setNode(Arraylist<Integer> nodes) {
		nodeInfo = (Integer[]) nodes.toArray();
		availableSlots = (Integer[]) nodes.toArray();
	}

<<<<<<< HEAD
	public void setTopology(Map<>) {
		
	}

=======
>>>>>>> c2c3a20af3c8a2f9a5dd33fae134f5abecf002d5
	public static getInstance() {
		if (instance == null)
			instance = new NetworkSimulator();
		return instance;
	}
<<<<<<< HEAD

	public Integer[] getAllAvailableSlots() {
		return availableSlots;
	}

	public Long getNodeNumber() {
		return nodeInfo.length;
	}

	public void run() {

	}

	public void occupyOneSlotAtNode(Integer nodeIndex) {
		if (availableSlots[nodeIndex] - 1 < 0)
			throw new IllegalArgumentException("Out of free slots to use");
		availableSlots[nodeIndex]--;			
	}

	public void addOneSlotAtNode(Integer nodeIndex) {
		if (availableSlots[nodeIndex] + 1 > nodeInfo[nodeIndex])
			throw new IllegalArgumentException("Out of bound of total slots");
		availableSlots[nodeIndex]++;
	}
=======
>>>>>>> c2c3a20af3c8a2f9a5dd33fae134f5abecf002d5
	


	
}