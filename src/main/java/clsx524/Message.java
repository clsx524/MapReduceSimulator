package clsx524


public class Message {
    private node  nodeInstace = null;
    public Message(node nodeInstance){
        This.nodeInstance = nodeInstance;
    }

    public node getMsg() {
        return this.nodeInstance;
    }
    public void setMsg(node nodeInstance) {
        this.nodeInstance = nodeInstance;
    }

}