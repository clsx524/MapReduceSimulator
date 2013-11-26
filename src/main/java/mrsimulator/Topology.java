package mrsimulator;

abstract class Topology {

	public int machinesPerRack;
	public int racks;

	public abstract void genTop();

}