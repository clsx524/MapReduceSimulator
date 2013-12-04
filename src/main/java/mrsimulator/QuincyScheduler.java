package mrsimulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class QuincyScheduler implements Scheduler  {

	private Map<Integer, PriorityBlockingQueue<PoolSchedulable>> mapPool = new LinkedHashMap<Integer, PriorityBlockingQueue<PoolSchedulable>>();
	
	private Queue<PoolSchedulable> queuePool = new LinkedBlockingQueue<PoolSchedulable>();

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

	private Timer timer = null;

	private boolean stopSign = false;

	private PoolSchedulable curr = null;

	private RoutineSchedule routine = null;

	private int failure = -1;

	public void schedule(JobInfo.TaskInfo[] tasks) {
		
		if (tasks == null)
            throw new NullPointerException("job is null");

        PoolSchedulable ps = new PoolSchedulable();
		for(JobInfo.TaskInfo task: tasks){
			ps.addTask(task);
		}
		ps.type = tasks[0].taskType;

        queuePool.offer(ps);
	}

	public void join() {
		try {
			routine.join();
		} catch (InterruptedException ie) {
    		System.out.println("Exception thrown  :" + ie);
    	}
	}

	public void stop() {
		stopSign = true;
	}

	public void setTimer() {
		timer = Timer.getInstance();
	}

	public int getQueueSize() {
		return queuePool.size();
	}

	public boolean isAlive() {
		return routine.isAlive();
	}

	public boolean isInterrupted() {
		return routine.isInterrupted();
	}

	public int failureTimes() {
		return failure;
	}

	public void start() {
		failure++;
		routine = new RoutineSchedule();
		routine.start();
	}

	class RoutineSchedule extends Thread {
		
		public void run() {
			Set<Integer> prefs = null;
			PriorityBlockingQueue<SlotsLeft> queueLeft = null;
			Integer tempFirstPref = null;
			int node = -1;
			while (!stopSign) {

				if (networkInstance.hasAvailableSlots() && queuePool.peek() != null) {

					mapPool.clear();
					for (PoolSchedulable ps : queuePool) {
						boolean found = false;
						prefs = ps.getPrefs();
						for (Integer i : prefs) {
							if (networkInstance.checkSlotsAtNode(i)) {
								node = i;
								found = true;
								break;
							}
						}
						if (!found) {
							queueLeft = ps.getRackLocality();
                			for (SlotsLeft j : queueLeft)
                				if (networkInstance.checkSlotsAtNode(j.machineNumber)) {
                					node = j.machineNumber;
                					break;	          							
                				}						
						}

                		if (!found)
                			node = networkInstance.pickUpOneSlotRandom();

						if (mapPool.containsKey(node))
							mapPool.get(node).add(ps);
						else {
							PriorityBlockingQueue<PoolSchedulable> ll = new PriorityBlockingQueue<PoolSchedulable>();
							ll.offer(ps);
							mapPool.put(node, ll);
						}
					}

					for (Integer i : mapPool.keySet()) {
						PriorityBlockingQueue<PoolSchedulable> ll = mapPool.get(i);
						
						while (ll.size() > 0) {
							curr = ll.poll();
							int left = networkInstance.node2Left.get(i).left / (ll.size()+1);
							long part = left < curr.runningTasks ? left : curr.runningTasks;
							for (long j = 0L; j < part; j++) {
								if (curr.runningTasks > 0) {
									JobInfo.TaskInfo task = curr.getTask();
									task.nodeIndex = i;
									if (task.taskType == true)
										task.setReducePrefs();
									task.updateDuration();
									timer.scheduleTask(task);
								}
								if (curr.runningTasks == 0) {
									queuePool.remove(curr);
									break;
								}
							}
						}
					}								
				}
			}
		}
    }
}