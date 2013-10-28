package clsx524;

public class workingSlot implements Runnable {
	 
	private Thread slot;
	private int time;

   	public workingSlot(String name, int t) {
      	// Create a new, second thread
      	slot = new Thread(this, name);
      	time = t;
      	slot.start(); // Start the thread
    }
   
   // This is the entry point for the second thread.
   public void run() {
      	try {
            // Let the thread sleep for a while.
            Thread.sleep(time);
     	} catch (InterruptedException e) {
         	System.out.println("Child interrupted.");
     }
     
   }
}