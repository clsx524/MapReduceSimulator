package mrsimulator;

public final class Configure {
	public final static String schedulerType = "FIFOScheduler";
	public final static String topologyType = "TreeTopology";
	public final static int corePoolSize = 60000;//Integer.MAX_VALUE;

	public final static Integer replica = 3;
	public final static Long blockSize = 65536L; // unit is byte

	public final static double execSpeed = 25600.0;
	public final static double ioSpeed = 5120.0;

	public final static Integer slotsPerNode = 100;

	public final static Double reduceStartPercentage = 1.0;

	public static long total;

	public final static int machinesPerRack = 20;
	public final static int racks = 30;

	public final static int sepmaphoreBound = 10;

	public static long initialTime;

	public final static int progressCheckPeriod = 10000;

}