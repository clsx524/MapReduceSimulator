package mrsimulator;

public class NetworkSimulator extends Thread {

	public class SlotsLeft implements Comparable<SlotsLeft>{
		public int slotNumber;
		public int left;

		public SlotsLeft(int sn, int l) {
			slotNumber = sn;
			left = l;
		}

		@Override
    	public int compareTo(SlotsLeft other){
        	return (left > other.left ? -1 : (left == other.left ? 0 : 1));
    	}
	}

	private static NetworkSimulator instance = null;

	private NetworkMessage nmsg = NetworkMessage.getInstance();

	//private Map<Integer, Set<Integer>> node2Task = new HashMap<Integer, Set<Integer>>();
	private ConcurrentMap<Integer, SlotsLeft> node2Left = new ConcurrentHashMap<Integer, SlotsLeft>();

	private int machinesPerRack;
	private int racks;
	private int slotsPerNode;
	private int machines;

	private Scheduler SchedulerInstance = SchedulerFactory.getInstance();

	private Random rd = new Random(System.currentTimeMillis());

	private Queue<SlotsLeft> queue = new PriorityBlockingQueue<SlotsLeft>();

	private Queue<JobInfo.TaskInfo> checkProgress = new LinkedBlockingQueue<JobInfo.TaskInfo>();

	private Profiler profile = new Profiler("/Users/eric/Google Drive/GitHub/MapReduceSimulator/Results/", "Tasks");

	private Semaphore netSemaphore = null;

	private JobInfo.TaskInfo curr = null;

	private NetworkSimulator(Semaphore net) {
		netSemaphore = net;
	}

	public void setTopology(Topology topology, Integer spn) {
		machinesPerRack = topology.machinesPerRack;
		racks = topology.racks;
		slotsPerNode = spn;
		machines = machinesPerRack * nodeInfo
		// totalSlots = machines * slotsPerNode;
		// availableSlots = totalSlots;
		for (int i = 0; i < machines; i++) {
			SlotsLeft sl = new SlotsLeft(i, slotsPerNode);
			node2Left.put(i, sl);
		}
	}

	public static getInstance() {
		if (instance == null)
			throw new NullPointerException("network is null");
		return instance;
	}

	public static newInstance(Semaphore net) {
		if (instance == null)
			instance = new NetworkSimulator(net);
		return instance;
	}

	public synchronized Long getNodeNumber() {
		return machinesPerRack * racks;
	}

	public void notifyFinish(JobInfo.TaskInfo task) {
		checkProgress.put(task);
	}

	public void run() {
        while (true) {
            netSemaphore.release();

            curr = checkProgress.poll();
            if (curr.isFinished())
            	profile.print(curr.getJob());
           	else if (curr.getMapProgress() / curr.getMapNumber() > Configure.reduceStartPercentage)
           		SchedulerInstance.schedule(curr.getReduces());
        }
	}

	public synchronized int pickUpOneSlotRandom() {
		if (!queue.peek() && queue.peek().left > 0)
			return queue.peek.slotNumber;
		return -1;
	}

	public synchronized boolean checkSlotsAtNode(int index) {
		if (node2Left.get(index).left > 0)
			return true;
		return false;
	}

	public synchronized void occupyOneSlotAtNode(Integer nodeIndex) {
		if (node2Left.get(index).left < 1)
			throw new IllegalArgumentException("Out of free slots to use");
		node2Left.get(index).left--;			
	}

	public synchronized void addOneSlotAtNode(Integer nodeIndex) {
		if (node2Left.get(index).left + 1 > slotsPerNode)
			throw new IllegalArgumentException("Out of bound of total slots");
		node2Left.get(index).left++;
	}

	public boolean hasAvailableSlots() {
		if (!queue.peek() && queue.peek().left > 0)
			return true;
		return false;
	}
	
}