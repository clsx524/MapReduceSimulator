package mrsimulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DistributedFileSystem {
	
	private Integer replicaNumber;
	private Long blockSize; // unit is byte

	private static DistributedFileSystem dfs = null;

	private NetworkSimulator networkInstance = NetworkSimulator.getInstance();

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
				job.mapRackLocality.offer(networkInstance.node2Left.get(i+machinesPerRack*rack));
			}
		}
	}

	private void replicationPlacement(JobInfo job, int rack) {
		int i = 0;
		while (i < replicaNumber-1) {
			int j = rd.nextInt(machinesPerRack) + rack * machinesPerRack;
			boolean res = job.mapPrefs.add(j);
			if (res == true)
				i++;
		}
		int j = rack;
		while (j == rack) {
			j = rd.nextInt(racks);
		}
		boolean res = job.mapPrefs.add(rd.nextInt(machinesPerRack) + j * machinesPerRack);
	}

	public void updateTaskNumber(ArrayList<JobInfo> alljobs) {
		for (JobInfo job : alljobs)
			job.initTasks(blockSize);
	}


}