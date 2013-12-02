package mrsimulator;

class TreeTopology extends Topology {
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
		machinesPerRack = Configure.machinesPerRack;
		racks = Configure.racks;
		racksPerL2 = 5;
		l2Switch = 6;
		l2PerAggr = 3;
		aggrSwitch = 2;
		l3Core = 1;

		rackSpeed = 1;
		linkSpeed = 10;

		profile = new Profiler("TreeTopology");
	}

	public void genTop() {
		profile.println(machinesPerRack + " " + racks + " " + racksPerL2 + " " +  l2Switch + " " + l2PerAggr + " " + aggrSwitch + " " + l3Core);
		profile.print2ln(rackSpeed, linkSpeed);
		profile.nextLine();
		for (int i = 0; i < racks*machinesPerRack; i++) {
			profile.print2ln("mach" + i, "rack" + (i/machinesPerRack));
		}
		for (int i = 0; i < racks; i++) {
			profile.print2ln("rack" + i, "L2Switch" + (i/racksPerL2));
		}
		for (int i = 0; i < l2Switch; i++) {
			profile.print2ln("l2Switch" + i, "aggrSwitch" + (i/l2PerAggr));
		}

		for (int i = 0; i < aggrSwitch; i++) {
			profile.print2ln("aggrSwitch" + i, "l3Core0");
		}		

	}
}