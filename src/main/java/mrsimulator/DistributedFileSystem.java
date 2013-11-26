package mrsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedFileSystem {
	
	private Integer replicaNumber;
	private Long blockSize; // unit is byte

	private static DistributedFileSystem dfs = null;

	private int machinesPerRack;
	private int racks;

	private Random rd = new Random(System.currentTimeMillis());

	private DistributedFileSystem(Integer replica, Long size){
		replicaNumber = replica;
		blockSize = size;

		Topology topology = TopologyFactory.getInstance();
		machinesPerRack = topology.machinesPerRack;
		racks = topology.racks;	
	}

	public static DistributedFileSystem newInstance(Integer replica, Long size) {
		if (dfs == null)
			dfs = new DistributedFileSystem(replica, size);
			
		return dfs;
	}
	
	public static DistributedFileSystem getInstance() {
		return dfs;
	}

	public void updatePrefs(ArrayList<JobInfo> alljobs) {
		int machines = machinesPerRack * racks;
		for (JobInfo job : alljobs) {
			int rack = rd.nextInt(racks);
			replicationPlacement(job, rack);

			for (int i = 0; i < machinesPerRack; i++) {
				if (!job.mapPrefs.contains(i))
					job.mapPrefs.add(i);
			}
		}
	}

	private void replicationPlacement(JobInfo job, int rack) {
		Map<Integer, Boolean> ex = new HashMap<Integer, Boolean>();
		int i = 0;
		while (i < replicaNumber) {
			int j = rd.nextInt(machinesPerRack) + rack * machinesPerRack;
			if (!ex.containsKey(j)) {
				job.mapPrefs.add(j);
				ex.put(j, true);
				i++;
			}
		}
	}

	public void updateTaskNumber(ArrayList<JobInfo> alljobs) {
		for (JobInfo job : alljobs)
			job.initTasks(blockSize);
	}


}