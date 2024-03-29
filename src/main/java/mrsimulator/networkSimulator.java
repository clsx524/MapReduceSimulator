package mrsimulator;

import java.util.Iterator;
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

	private Scheduler schedulerInstance = null;

	private Random rd = new Random(System.currentTimeMillis());

	private Queue<SlotsLeft> queue = new PriorityBlockingQueue<SlotsLeft>();

	private Deque<JobInfo> checkProgress = new LinkedBlockingDeque<JobInfo>();

	public Profiler profile = new Profiler(Configure.resultFileName);

	private JobInfo curr = null;

	private boolean stopSign = false;

	public long finished = 0L;

	private NetworkSimulator() {
	}

	public void setScheduler() {
		schedulerInstance = SchedulerFactory.getInstance();
	}

	public void setTopology(Topology topology, Integer spn) {
		machinesPerRack = topology.machinesPerRack;
		racks = topology.racks;
		slotsPerNode = spn;
		machines = machinesPerRack * racks;
		for (int i = 0; i < machines; i++) {
			SlotsLeft sl = new SlotsLeft(i, slotsPerNode);
			node2Left.put(i, sl);
			queue.offer(sl);
		}
	}
	public static NetworkSimulator getInstance() {
		if (instance == null)
			throw new NullPointerException("network is null");
		return instance;
	}

	public static NetworkSimulator newInstance() {
		if (instance == null)
			instance = new NetworkSimulator();
		return instance;
	}

	public synchronized int getNodeNumber() {
		return machinesPerRack * racks;
	}

	public int checkProgressSize() {
		return checkProgress.size();
	}

	public void jobStarted(JobInfo job) {
		checkProgress.offer(job);
	}

	public void run() {

        while (!stopSign) {
            while (checkProgress.size() > 0) {
            	Iterator<JobInfo> it = checkProgress.iterator();
            	while(it.hasNext()) {
            		JobInfo curr = it.next();
            		curr.updateProgress();




            		if (curr.isFinished()) {
            			System.out.println("Job finished: " + curr.jobID);
            			finished++;
            			checkProgress.remove(curr);
	            		curr.setJobEndTime();
    	        		profile.print(curr);
        	    		if (finished == Configure.total)
            				schedulerInstance.stop();
	            	} else if (curr.finished == false && curr.reduceStarted == false && curr.prog() >= Configure.reduceStartPercentage) {
        	    		schedulerInstance.schedule(curr.reduces);
            			curr.reduceStarted = true;
            		}
            	}       	
            }
        }
        profile.close();
        System.out.println("NetworkSimulator has terminated safely");
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
		SlotsLeft sr = node2Left.get(index);
		if (sr.left < 1)
			throw new IllegalArgumentException("Out of free slots to use");
		queue.remove(sr);
		sr.left--;
		queue.offer(sr);		
	}

	public synchronized void addOneSlotAtNode(Integer index) {
		SlotsLeft sr = node2Left.get(index);
		if (sr.left + 1 > slotsPerNode)
			throw new IllegalArgumentException("Out of bound of total slots");
		queue.remove(sr);
		sr.left++;
		queue.offer(sr);
	}

	public synchronized boolean hasAvailableSlots() {
		if (queue.peek() != null && queue.peek().left > 0)
			return true;
		return false;
	}

	public String getQueue() {
		return queue.toString();
	}

	public void stopThread() {
		stopSign = true;
	}
}