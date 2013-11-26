package mrsimulator;

public static class Configure {
	public static String schedulerType = "FIFOScheduler";
	public static String topologyType = "TreeTopology";
	public static Long corePoolSize = 100000L;

	public static Integer replica = 3;
	public static Long blockSize = 64L; // unit is byte

	public static Double execSpeed = 50.0;
	public static Double ioSpeed = 10.0;

	public static Integer slotsPerNode = 500;

	public static Double reduceStartPercentage = 1.0;
}