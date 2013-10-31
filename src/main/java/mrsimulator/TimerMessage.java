package mrsimulator;

public class TimerMessage {

    private JobInfo job;
    private String type; // job or task
    private Integer taskID;
    private String taskType; // map or reduce

    private boolean stopSign = false;

    private static TimerMessage msg = null;

    private TimerMessage() {
        job = null;
    }

    public static TimerMessage getInstance() {
        if (msg == null)
            msg = new TimerMessage();
        return msg;
    }

    public void setJobMsg(JobInfo j, String t) {
        job = j;
        type = t;
    }

    public void setTaskMsg(JobInfo j, String t, Integer tid, String ty) {
        job = j;
        type = t;
        taskID = tid;
        taskType = ty;
    }

    public String getType() {
        return type;
    }

    public JobInfo getJobInfo() {
        return job;
    }

    public long getDuration() {
        if (type.equals("JOB"))
            return job.getArrivalTime();
        else if (type.equals("TASK"))
            return job.getDurationWithTaskID(taskType, taskID).longValue();
    }

    public Integer getNodeIndex() {
        job.getNodeIndexWithTaskID(taskType, taskID);
    }

    public boolean stop() {
        return stopSign;
    }

}