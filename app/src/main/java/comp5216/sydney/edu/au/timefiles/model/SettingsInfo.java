package comp5216.sydney.edu.au.timefiles.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SettingsInfo implements Serializable {
    public enum PageMode {
        ONLY, TOTAL
    }

    public ArrayList<AppInfo> whiteList;
    public ArrayList<AppInfo> blackList;
    public ArrayList<AppInfo> appList;
    public ArrayList<String> whiteListArray;
    public ArrayList<String> blackListArray;
    public ArrayList<String> appListArray;
    public int totalTime;
    public PageMode mainPageMode;


    public SettingsInfo() {
        super();
        this.whiteList = new ArrayList<AppInfo>();
        this.blackList = new ArrayList<AppInfo>();
        this.appList = new ArrayList<AppInfo>();
        this.whiteListArray = new ArrayList<String>();
        this.blackListArray = new ArrayList<String>();
        this.appListArray = new ArrayList<String>();
        this.totalTime = 0;
        this.mainPageMode = PageMode.ONLY;
    }

    public PageMode getMainPageMode() {
        return mainPageMode;
    }

    public void setMainPageMode(PageMode mainPageMode) {
        this.mainPageMode = mainPageMode;
    }

    public ArrayList<AppInfo> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(ArrayList<AppInfo> whiteList) {
        this.whiteList = whiteList;
    }

    public ArrayList<AppInfo> getBlackList() {
        return blackList;
    }

    public void setBlackList(ArrayList<AppInfo> blackList) {
        this.blackList = blackList;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public ArrayList<AppInfo> getAppList() {
        return appList;
    }

    public void setAppList(ArrayList<AppInfo> appList) {
        this.appList = appList;
    }

    public ArrayList<String> getWhiteListArray() {
        return whiteListArray;
    }

    public void setWhiteListArray(ArrayList<String> whiteListArray) {
        this.whiteListArray = whiteListArray;
    }

    public ArrayList<String> getBlackListArray() {
        return blackListArray;
    }

    public void setBlackListArray(ArrayList<String> blackListArray) {
        this.blackListArray = blackListArray;
    }

    public ArrayList<String> getAppListArray() {
        return appListArray;
    }

    public void setAppListArray(ArrayList<String> appListArray) {
        this.appListArray = appListArray;
    }

    @Override
    public String toString() {
        return "SettingsInfo{" +
                "whiteList=" + whiteList +
                ", blackList=" + blackList +
//                ", appList=" + appList +
                ", totalTime=" + totalTime +
                '}';
    }
}
