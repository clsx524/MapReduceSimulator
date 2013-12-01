package mrsimulator;

public class SlotsLeft implements Comparable<SlotsLeft> {
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

    public String toString() {
    	return "(" + machineNumber + "," + left + ")";
    }

	@Override  
    public boolean equals(Object o) {
   		if (o == null)  
            return false;  
        if (this == o)  
            return true;  
        if (o instanceof SlotsLeft) {  
            SlotsLeft r = (SlotsLeft)o;  
            return (r.left == left && machineNumber == r.machineNumber);  
        } else {  
            return false;  
        }  
    } 
}