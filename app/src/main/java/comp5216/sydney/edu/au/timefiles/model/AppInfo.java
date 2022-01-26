package comp5216.sydney.edu.au.timefiles.model;

import android.graphics.drawable.Drawable;

import java.io.Serializable;

public class AppInfo implements Serializable {
    /**
     * Stores the app name and icon of each app
     */

    String appName;
    String packageName;
    Boolean isBlack = false;
    Boolean isWhite = false;

    public AppInfo() {
        this.appName = "";
        this.packageName = "";
        this.isBlack = false;
        this.isWhite = false;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }



    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public Boolean getBlack() {
        return isBlack;
    }

    public void setBlack(Boolean black) {
        isBlack = black;
    }

    public Boolean getWhite() {
        return isWhite;
    }

    public void setWhite(Boolean white) {
        isWhite = white;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", isBlack=" + isBlack +
                ", isWhite=" + isWhite +
                '}';
    }
}
