package mrsimulator;

class FatTreeTopology extends Topology {
	public int racksPerAggr;
	public int aggrSwitch;
	public int l3Core;

	public int rackSpeed; // Unit Gbps
	public int linkSpeed; // Unit Gbps

	private Profiler profile = null;

	public FatTreeTopology() {
		machinesPerRack = Configure.machinesPerRack;
		racks = Configure.racks;
		racksPerAggr = 2;
		aggrSwitch = racks;
		l3Core = 20;

		rackSpeed = 1;
		linkSpeed = 10;

		profile = new Profiler("FatTreeTopology");
	}

	public void genTop() {
		profile.println(machinesPerRack + " " + racks + " " + racksPerAggr + " " + aggrSwitch + " " + l3Core);
		profile.print2ln(rackSpeed, linkSpeed);
		profile.nextLine();
		for (int i = 0; i < racks*machinesPerRack; i++) {
			profile.print2ln("mach" + i, "rack" + (i/machinesPerRack));
		}

		for (int i = 0; i < racks; i++) {
			int group = i/racksPerAggr;
			for (int j = 0; j < 2; j++)
				profile.print2ln("rack" + i, "aggrSwitch" + (group*racksPerAggr+j));
		}

		for (int i = 0; i < aggrSwitch; i++) {
			int group = i % 2;
			for (int j = 0; j < l3Core / 2; j++)
				profile.print2ln("aggrSwitch" + i, "l3Core" + (group*l3Core/2 + j));
		}	
		profile.close();	
	}



}