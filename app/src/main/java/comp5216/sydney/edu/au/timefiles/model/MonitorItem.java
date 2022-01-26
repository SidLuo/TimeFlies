package comp5216.sydney.edu.au.timefiles.model;


import android.util.Log;

public class MonitorItem {

    public String name;
    public long startTime;
    public long endTime;
    public long notifyTime;
    public boolean isNotify;
    public boolean isShow;
    public boolean isFinish;
    public String goal;



    public MonitorItem(String name, long startTime) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = 0;
        this.isShow = false;
        this.isNotify = true;
        this.isFinish = false;
        this.notifyTime = 0;
        this.goal = "";
    }

    public void updateNewTimer(long startTime, long endTime, String goal){
        this.startTime = startTime;
        this.endTime = endTime;
        this.goal = goal;
        this.isShow = false;
        this.isNotify = false;
        this.isFinish = false;
        long period = endTime - startTime;
        if(period > 300000){
            this.notifyTime = endTime - 300000;
        }else{
            this.notifyTime = Math.round(startTime + ( period * 0.7));
        }
        Log.e("checkTime", String.valueOf(this.notifyTime) + "  " + this.startTime + "  " + this.endTime );
    }

    public void onFinish(){
        this.isFinish = false;
        this.endTime = 0;
    }

    public boolean isFinish() {
        return isFinish;
    }

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public boolean isShow() {
        return isShow;
    }
    public String getGoal() {
        return goal;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    public long getNotifyTime() {
        return notifyTime;
    }

    public void setNotifyTime(long notifyTime) {
        this.notifyTime = notifyTime;
    }

    public boolean isNotify() {
        return isNotify;
    }

    public void setNotify(boolean notify) {
        isNotify = notify;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }


    @Override
    public String toString() {
        return "MonitorItem{" +
                "name='" + name + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", notifyTime=" + notifyTime +
                ", isNotify=" + isNotify +
                ", isShow=" + isShow +
                ", isFinish=" + isFinish +
                ", goal='" + goal + '\'' +
                '}';
    }
}
