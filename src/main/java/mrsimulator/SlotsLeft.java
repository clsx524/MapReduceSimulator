package mrsimulator;

public class SlotsLeft implements Comparable<SlotsLeft>{
	public int machineNumber;
	public int left;

	public SlotsLeft(int sn, int l) {
		machineNumber = sn;
		left = l;
	}

	@Override
    public int compareTo(SlotsLeft other){
       	return (left > other.left ? -1 : (left == other.left ? 0 : 1));
    }
}