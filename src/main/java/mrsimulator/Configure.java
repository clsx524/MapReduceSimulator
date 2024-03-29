package mrsimulator;

public final class Configure {
	public final static String schedulerType = "FIFOScheduler";//"QuincyScheduler"; // "FIFOScheduler"; // "DelayScheduler";
	public final static String topologyType = "TreeTopology";//"TreeTopology"; // "FatTreeTopology";// "VL2Topology";  // "TreeTopology";
	public final static String netPath = "FIFOTreeResult";
	public final static String resultFileName = "FIFOTreeTasks";

	public final static int corePoolSize = 12000;//Integer.MAX_VALUE;
	public final static int timerLimit = 12000;

	public final static String inputPath = "datasets/FB-2009_samples_24_times_1hr_0_first43jobs.tsv";

	public final static Integer replica = 3;
	public final static Long blockSize = 262144L; // unit is byte

	public final static double execSpeed = 102400.0;
	public final static double ioSpeed = 51200.0;

	public final static Integer slotsPerNode = 20;

	public final static Double reduceStartPercentage = 0.9;

	public static int total;

	public final static int machinesPerRack = 20;
	public final static int racks = 30;

	public final static int sepmaphoreBound = 10;

	public static long initialTime;

	public final static int progressCheckPeriod = 10000;

	public static int replicaBudget;
	public static int replicaDelta = 2;

	public static boolean secondPhase = false;

	public static double paretoShape = 5.0;
	public static int speculativeCap = 60;
	public static double slowTaskThreshold = 0.25;
	public static double slowNodeThreshold = 0.25;

}