package comp5216.sydney.edu.au.timefiles.ranking;

import java.util.Date;

public class RankingModel {
    private String userName;
    private long planned_time;
    private long actual_time;
    private float ratio;
    private long date;
    private int rank;

    public RankingModel() {
        this.userName = "";
        this.planned_time = 0;
        this.actual_time = 0;
        this.ratio = 1;
        this.rank = 1;
        this.date = new Date().getTime();
    }

    public RankingModel(String userName, long planned_time, long actual_time, float ratio) {
        this.userName = userName;
        this.planned_time = planned_time;
        this.actual_time = actual_time;
        this.ratio = ratio;
        this.date = new Date().getTime();
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getPlanned_time() {
        return planned_time;
    }

    public void setPlanned_time(long planned_time) {
        this.planned_time = planned_time;
    }

    public long getActual_time() {
        return actual_time;
    }

    public void setActual_time(long actual_time) {
        this.actual_time = actual_time;
    }

    public float getRatio() {
        return ratio;
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RankingModel{" +
                "userName='" + userName + '\'' +
                ", planned_time=" + planned_time / 60000 +
                ", actual_time=" + actual_time / 60000 +
                ", ratio=" + ratio +
                ", date=" + new Date(date).toString() +
                '}';
    }

}
