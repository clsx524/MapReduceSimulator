package mrsimulator;

public final class Configure {
	public final static String schedulerType = "FIFOScheduler";
	public final static String topologyType = "TreeTopology";
	public final static Long corePoolSize = 100000L;

	public final static Integer replica = 3;
	public final static Long blockSize = 65536L; // unit is byte

	public final static double execSpeed = 50.0;
	public final static double ioSpeed = 10.0;

	public final static Integer slotsPerNode = 500;

	public final static Double reduceStartPercentage = 1.0;

	public static long total;

}