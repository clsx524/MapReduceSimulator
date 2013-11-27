package mrsimulator;

class TreeTopology extends Topology {
	// public int machinesPerRack;
	// public int racks;
	public int racksPerL2;
	public int l2Switch;
	public int l2PerAggr;
	public int aggrSwitch;
	public int l3Access;
	public int l3Core;

	public int rackSpeed; // Unit Gbps
	public int linkSpeed; // Unit Gbps

	private Profiler profile = null;

	public TreeTopology() {
		machinesPerRack = 20;
		racks = 30;
		racksPerL2 = 3;
		l2Switch = 20;
		l2PerAggr = 4;
		aggrSwitch = 10;
		l3Access = 10;
		l3Core = 2;

		rackSpeed = 1;
		linkSpeed = 10;

		profile = new Profiler("/Users/eric/Google Drive/GitHub/MapReduceSimulator/Results/", "TreeTopology");
	}

	public void genTop() {
		profile.println(machinesPerRack + " " + racks + " " + racksPerL2 + " " +  l2Switch + " " + l2PerAggr + " " + aggrSwitch + " " + l3Access + " " + l3Core);
		profile.print2ln(rackSpeed, linkSpeed);
		profile.nextLine();
		for (int i = 0; i < racks*machinesPerRack; i++) {
			profile.print2ln("mach" + i, "rack" + (i/racks));
		}
		for (int i = 0; i < racks; i++) {
			int group = i/racksPerL2;
			for (int j = 0; j < 2; j++)
				profile.print2ln("rack" + i, "L2Switch" + (group*racksPerL2+j));
		}
		for (int i = 0; i < l2Switch; i++) {
			int group = i/l2PerAggr;
			for (int j = 0; j < 2; j++)
				profile.print2ln("l2Switch" + i, "aggrSwitch" + (group*l2PerAggr+j));
		}

		for (int i = 0; i < aggrSwitch; i++) {
			int group = i/2;
			for (int j = 0; j < 2; j++)
				profile.print2ln("aggrSwitch" + i, "l3Access" + (group*2+j));
		}		

		for (int i = 0; i < l3Access; i++) {
			for (int j = 0; j < 2; j++)
				profile.print2ln("l3Access" + i, "l3Core" + j);
		}
	}



}