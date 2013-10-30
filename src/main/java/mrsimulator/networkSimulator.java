package mrsimulator;

public class NetworkSimulator extends Thread {

	private static NetworkSimulator instance = null;

	private Integer[] availableSlots = null;
	private Integer[] nodeInfo = null;

	private final TimerMessage msg;

	private NetworkSimulator() {
		msg = TimerMessage.getInstance();
	}

	public void setNode(Arraylist<Integer> nodes) {
		nodeInfo = (Integer[]) nodes.toArray();
		availableSlots = (Integer[]) nodes.toArray();
	}

	public void setTopology(Map<>) {
		
	}

	public static getInstance() {
		if (instance == null)
			instance = new NetworkSimulator();
		return instance;
	}

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
	


	
}