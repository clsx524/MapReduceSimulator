package clsx524


public class Message {
    private int[] slotInfo = new int[2];
    
    public Message(int node, int slot){
        this.slotInfo[0]=node;
        this.slotInfo[1] = slot;
    }

    public int[] getMsg() {
        return this.slotInfo;
    }
    public 
    public void setMsg(int node, int slot) {
        this.slotInfo[0] = node;
        this.slotInfo[1] = slot;
    }

}