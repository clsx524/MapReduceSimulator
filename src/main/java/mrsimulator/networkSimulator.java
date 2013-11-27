package mrsimulator;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

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

	//private Map<Integer, Set<Integer>> node2Task = new HashMap<Integer, Set<Integer>>();
	private ConcurrentMap<Integer, SlotsLeft> node2Left = new ConcurrentHashMap<Integer, SlotsLeft>();

	private int machinesPerRack;
	private int racks;
	private int slotsPerNode;
	private int machines;

	private long finished = 0L;

	private Scheduler SchedulerInstance = null;

	private Random rd = new Random(System.currentTimeMillis());

	private Queue<SlotsLeft> queue = new PriorityBlockingQueue<SlotsLeft>();

	private Queue<JobInfo.TaskInfo> checkProgress = new LinkedBlockingQueue<JobInfo.TaskInfo>();

	private Profiler profile = new Profiler("/Users/eric/Google Drive/GitHub/MapReduceSimulator/Results/", "Tasks");

	private Semaphore netSemaphore = null;

	private JobInfo.TaskInfo curr = null;

	private boolean stopSign = false;

	private NetworkSimulator(Semaphore net) {
		netSemaphore = net;
	}

	public void setScheduler() {
		SchedulerInstance = SchedulerFactory.getInstance();
	}

	public void setTopology(Topology topology, Integer spn) {
		machinesPerRack = topology.machinesPerRack;
		racks = topology.racks;
		slotsPerNode = spn;
		machines = machinesPerRack * racks;
		// totalSlots = machines * slotsPerNode;
		// availableSlots = totalSlots;
		for (int i = 0; i < machines; i++) {
			SlotsLeft sl = new SlotsLeft(i, slotsPerNode);
			node2Left.put(i, sl);
		}
	}
	public static NetworkSimulator getInstance() {
		if (instance == null)
			throw new NullPointerException("network is null");
		return instance;
	}

	public static NetworkSimulator newInstance(Semaphore net) {
		if (instance == null)
			instance = new NetworkSimulator(net);
		return instance;
	}

	public synchronized int getNodeNumber() {
		return machinesPerRack * racks;
	}

	public void notifyFinish(JobInfo.TaskInfo task) {
		checkProgress.offer(task);
	}

	public void run() {
        while (!stopSign) {
            netSemaphore.release();

            curr = checkProgress.poll();
            if (curr == null) {
            	continue;
            }
            if (curr.isFinished()) {
            	finished++;
            	profile.print(curr.getJob());
            	if (finished == Configure.total) {
            		SchedulerInstance.threadStop();
            		stopSign = true;
            	}
            } else if (curr.getMapProgress() / curr.getMapNumber() > Configure.reduceStartPercentage)
           		SchedulerInstance.schedule(curr.getReduces());
        }
	}

	public synchronized int pickUpOneSlotRandom() {
		if (queue.peek() != null && queue.peek().left > 0)
			return queue.peek().slotNumber;
		return -1;
	}

	public synchronized boolean checkSlotsAtNode(int index) {
		if (node2Left.get(index).left > 0)
			return true;
		return false;
	}

	public synchronized void occupyOneSlotAtNode(Integer index) {
		if (node2Left.get(index).left < 1)
			throw new IllegalArgumentException("Out of free slots to use");
		node2Left.get(index).left--;			
	}

	public synchronized void addOneSlotAtNode(Integer index) {
		if (node2Left.get(index).left + 1 > slotsPerNode)
			throw new IllegalArgumentException("Out of bound of total slots");
		node2Left.get(index).left++;
	}

	public boolean hasAvailableSlots() {
		if (queue.peek() != null && queue.peek().left > 0)
			return true;
		return false;
	}
}