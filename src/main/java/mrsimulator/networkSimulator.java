package mrsimulator;

import java.util.Queue;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.PriorityBlockingQueue;

public class NetworkSimulator extends Thread {

	private static NetworkSimulator instance = null;

	public ConcurrentMap<Integer, SlotsLeft> node2Left = new ConcurrentHashMap<Integer, SlotsLeft>();

	private int machinesPerRack;
	private int racks;
	private int slotsPerNode;
	private int machines;

	private long finished = 0L;

	private Scheduler SchedulerInstance = null;

	private Random rd = new Random(System.currentTimeMillis());

	private Queue<SlotsLeft> queue = new PriorityBlockingQueue<SlotsLeft>();

	private Deque<JobInfo> checkProgress = new LinkedBlockingDeque<JobInfo>();

	private Profiler profile = new Profiler("/Users/eric/Google Drive/GitHub/MapReduceSimulator/Results/", "Tasks");

	private JobInfo curr = null;

	private boolean stopSign = false;

	private BoundedSemaphore netSemaphore = null;

	//private Queue<SlotsLeft>[] rackLeft = null;

	private NetworkSimulator(BoundedSemaphore net) {
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
		//rackLeft = new PriorityBlockingQueue<SlotsLeft>[racks];
		// totalSlots = machines * slotsPerNode;
		// availableSlots = totalSlots;
		for (int i = 0; i < machines; i++) {
			SlotsLeft sl = new SlotsLeft(i, slotsPerNode);
			node2Left.put(i, sl);
			queue.offer(sl);
			//rackLeft[i].offer(sl);
		}
	}
	public static NetworkSimulator getInstance() {
		if (instance == null)
			throw new NullPointerException("network is null");
		return instance;
	}

	public static NetworkSimulator newInstance(BoundedSemaphore net) {
		if (instance == null)
			instance = new NetworkSimulator(net);
		return instance;
	}

	public synchronized int getNodeNumber() {
		return machinesPerRack * racks;
	}

	public boolean notifyFinish(JobInfo.TaskInfo task) {
		if (!checkProgress.contains(task.getJob())) {
			checkProgress.offer(task.getJob());
			return true;
		}
		return false;
	}

	public void run() {

        while (!stopSign) {
        	try {
            	netSemaphore.release();
        	} catch (InterruptedException ie) {
                System.out.println("Exception thrown  :" + ie);
            }
            while (checkProgress.size() > 0) {
             	curr = checkProgress.poll();

  				if (curr == null)
  					continue;
  				System.out.println("network enter again: " + curr.jobID);
            	if (curr.isFinished()) {
            		System.out.println("Job finished: " + curr.jobID);
            		finished++;
            		profile.print(curr);
            		if (finished == Configure.total) {
            			SchedulerInstance.threadStop();
            			stopSign = true;
            		}
            	} else if (curr.reduceStarted == false && curr.prog() >= Configure.reduceStartPercentage) {
            		System.out.println("Map almost finished: " + curr.jobID);
            		SchedulerInstance.schedule(curr.reduces);
            		curr.reduceStarted = true;
            	}           	
            }
        }
	}

	public synchronized int pickUpOneSlotRandom() {
		if (queue.peek() != null && queue.peek().left > 0)
			return queue.peek().machineNumber;
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
		//System.out.println(queue.peek() == null);
		if (queue.peek() != null && queue.peek().left > 0)
			return true;
		return false;
	}
}