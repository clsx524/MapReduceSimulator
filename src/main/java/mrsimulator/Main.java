package mrsimulator;

public class Main {
    public static void main( String[] args) {
    	SimulatorEngine se = new SimulatorEngine();
    	se.scheduleAllJobs();
    	se.join();
    }	
}