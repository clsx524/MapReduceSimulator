package mrsimulator;

public class NetworkSimulator extends Thread {
	public class SlotThread extends Thread {

		private JobInfo.TaskInfo task;

		public SlotThread(JobInfo.TaskInfo t) {
			super();
			task = t;
		}
		public void run() {
			
		}

	}
	private static NetworkSimulator instance = null;
	private NetworkMessage nmsg = NetworkMessage.getInstance();
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
	public synchronized Integer[] getAllAvailableSlots() {
		return availableSlots;
	}

	public synchronized Long getNodeNumber() {
		return nodeInfo.length;
	}

	public void run() {
        while (true) {
            synchronized(nmsg) {
                try {
            	   nmsg.wait();
                } catch (InterruptedException e) {
            	   e.printStackTrace();
                }
            }
            if (nmsg.stop() == true)
            	break;
            SlotThread st = new SlotThread(nmsg.getTaskInfo());
            st.start();
                
        }
	}

	public synchronized void occupyOneSlotAtNode(Integer nodeIndex) {
		if (availableSlots[nodeIndex] - 1 < 0)
			throw new IllegalArgumentException("Out of free slots to use");
		availableSlots[nodeIndex]--;			
	}

	public synchronized void addOneSlotAtNode(Integer nodeIndex) {
		if (availableSlots[nodeIndex] + 1 > nodeInfo[nodeIndex])
			throw new IllegalArgumentException("Out of bound of total slots");
		availableSlots[nodeIndex]++;
	}

	public boolean hasAvailableSlots() {
		for(Integer nodeSlot: availableSlots) {
			if(nodeSlot > 0)
				return true;
		}
		return false;
	}
	
	
}