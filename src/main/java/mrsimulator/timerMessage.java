package mrsimulator;

public class timerMessage {
	private node nodeInstace = null;
    private int duration = 0;

    private static timerMessage msg = null;

    private timerMessage() {
        
    }

    public static timerMessage getInstance() {
        if (msg == null)
            msg = new timerMessage();
        return msg;
    }

    public node getNode() {
        return this.nodeInstance;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setMsg(node nodeInstance, int dur) {
        this.nodeInstance = nodeInstance;
        duration = dur;
    }
}