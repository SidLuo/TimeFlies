package comp5216.sydney.edu.au.timefiles.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.timefiles.MainActivity;

/**
 * This class gets the usage time of apps
 */
public class AppInMachine {
    public Context context;
    private static final String TAG = "authentication";
    public Map<String, UsageStats> usageList;

    public AppInMachine(Context context){
        this.context = context;
    }

    public Map<String, UsageStats> getUsageList() {
        return usageList;
    }

    public void setUsageList(Map<String, UsageStats> usageList) {
        this.usageList = usageList;
    }


    public static List<PackageInfo> getAppList(Context context){
        // Gets the list of apps
        List<PackageInfo> packages = new ArrayList<>();
        try {
            List<PackageInfo> packageInfos = context.getPackageManager().getInstalledPackages(0);
            PackageInfo timeFlies = context.getPackageManager().getPackageInfo("comp5216.sydney.edu.au.timefiles",0);
            packageInfos.remove(timeFlies);
            for (PackageInfo info : packageInfos) {
                if((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0){
                    packages.add(info);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();;
        }
        return packages;
    }

    public static Map<String, UsageStats> getUsageList(Context context, long startTime, long endTime) {

        Log.e(TAG,"Range start:" + startTime);
        Log.e(TAG,"Range end:" + endTime);

        ArrayList<UsageStats> list = new ArrayList<>();

        UsageStatsManager mUsmManager = (UsageStatsManager) context.getSystemService(context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> map = mUsmManager.queryAndAggregateUsageStats(startTime, endTime);
        map.remove("comp5216.sydney.edu.au.timefiles");
        Log.e(TAG, map.entrySet().toString());
        for (Map.Entry<String, UsageStats> entry : map.entrySet()) {
            UsageStats stats = entry.getValue();

            if(stats.getTotalTimeInForeground() > 0){
                list.add(stats);
           }
        }

        return map;
    }

    public static String getAppName(String packageName, Context context){
        // currently only used in an log e tag (will delete this comment later)
        PackageManager pm = context.getPackageManager();
        String Name;
        try{
            Name = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("failed in get app name", e.toString());
            e.printStackTrace();
            Name = packageName;
        }
        return Name;
    }

    public static boolean isSystemApp(Context context, String pkgName) {
        boolean isSystemApp = false;
        PackageInfo pi = null;
        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(pkgName, 0);
        } catch (Throwable t) {
            Log.e(TAG, t.getMessage(), t);
        }
        if (pi != null) {
            boolean isSysApp = (pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1;
            boolean isSysUpd = (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1;
            isSystemApp = isSysApp || isSysUpd;
        }
        return isSystemApp;
    }

    public static long getCurrentUsageTime(String packName, Context context){
        long endTime = System.currentTimeMillis();
        long startTime = AppInMachine.startOfDay(new Timestamp(System.currentTimeMillis()));
        long currentTime = AppInMachine.getUsageList(context, startTime, endTime).get(packName).getTotalTimeInForeground();
        return currentTime;
    }

    public static Drawable getIcon(String packName, Context context){
        Drawable icon = null;

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(packName, 0);
            icon = info.loadIcon(pm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return icon;
    }

    public static long startOfDay(Timestamp time) {
        Calendar cal = Calendar.getInstance();
        // use current time zone
        cal.setTimeInMillis(time.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0); //set hours to zero
        cal.set(Calendar.MINUTE, 0); // set minutes to zero
        cal.set(Calendar.SECOND, 0); //set seconds to zero
        cal.set(Calendar.MILLISECOND, 0);
        Log.i("Time", cal.getTime().toString());
        return cal.getTimeInMillis();
    }

    public static int getTotalTime(Context context){
        int total = 0;

        long endTime = System.currentTimeMillis();
        long startTime = AppInMachine.startOfDay(new Timestamp(System.currentTimeMillis()));

        Map<String, UsageStats> usageMap = AppInMachine.getUsageList(context, startTime, endTime);
        Log.e("total", MainActivity.settingsInfo.getBlackListArray().toString());
        for (Map.Entry<String, UsageStats> entry : usageMap.entrySet()) {
            UsageStats stats = entry.getValue();
            if(MainActivity.settingsInfo.getBlackListArray().contains(entry.getKey())){
                Log.e("total", entry.getKey() + "  " + stats.getTotalTimeInForeground());
                total += stats.getTotalTimeInForeground();
            }
        }
        return total;
    }

}
