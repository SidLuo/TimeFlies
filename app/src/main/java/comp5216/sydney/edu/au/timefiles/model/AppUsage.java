package comp5216.sydney.edu.au.timefiles.model;

public class AppUsage extends AppInfo {
    /**
     * Stores the app name, icon and usage of each app
     */

    private String usage;
    private long totalTime;

    public AppUsage() {
        super();
        this.usage = "";
        this.totalTime = 1;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

}