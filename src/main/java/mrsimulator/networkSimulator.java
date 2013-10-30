package mrsimulator;

public class networkSimulator extends  Thread {

	public class node {
		private final int index;
		private final int slotNumber;

		public node() {
			slotNumber = 0;
			index = -1;
		}

		public setSlotNumber(int id, int sn) {
			if (sn <= 0)
				throw new IllegalArgumentException(sn + " <= 0");
			slotNumber = sn;
			index = id;
		}

		public synchronized int getAvailableSlotsNumber() {
			return availableSlots[index];
		}

		public synchronized void increment() {
			if (availableSlots[index] + 1 > slotNumber)
				throw new IllegalArgumentException("Out of bound of total slots");
			availableSlots[index]++;
		}

		public synchronized void decrement() {
			if (availableSlots[index] - 1 < 0)
				throw new IllegalArgumentException("Out of free slots to use");
			availableSlots[index]--;			
		}
	}


	private static networkSimulator instance = null;
	private Arraylist<node> nodeInstances = new Arraylist<node>();
	private node[] nodes = null;
	private Integer[] availableSlots = null;

	private networkSimulator() {

	}

	public void setNode(Arraylist<Integer> nodeInfo) {
		nodes = new node[nodeInfo.size()];
		availableSlots = new Integer[nodeInfo.size()];
		for (int i = 0; i < nodeInfo.size(); i++) {
			nodes[i].setSlotNumber(i, nodeInfo.get(i));
			availableSlots[i] = nodeInfo.get(i);
		}	
	}

	public static getInstance() {
		if (instance == null)
			instance = new networkSimulator();
		return instance;
	}

	public Integer[] getAllAvailableSlots() {
		return availableSlots;
	}

	public Long getNodeNumber() {
		return ndoes.length;
	}
	


	
}