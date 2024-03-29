package mrsimulator;

public class TopologyFactory {

	private static Topology topologyInstance = null;

	public static Topology newInstance(String type) {
		if (type.equals("TreeTopology"))
			topologyInstance = new TreeTopology();
		else if (type.equals("VL2Topology"))
			topologyInstance = new VL2Topology();
		else if (type.equals("FatTreeTopology"))
			topologyInstance = new FatTreeTopology();
		else
			throw new IllegalArgumentException("Invalid Topology Type");
		return topologyInstance;
	}

	public static Topology getInstance() {
		if (topologyInstance == null)
			throw new NullPointerException("topologyInstance is null");
		return topologyInstance;
	}
}