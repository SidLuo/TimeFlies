package comp5216.sydney.edu.au.timefiles.overlay;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.usage.UsageStats;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.MonitorItem;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;

public class DetectService extends AccessibilityService {

    private final static String TAG = "DetectionService";

    private static DetectService mInstance = null;
    public static String mForegroundPackageName;
    public static HashMap<String, MonitorItem> monitorList;
    public static NotificationManager notificationManager;
    private AppInMachine appInMachine;

    public DetectService() {

    }
    @Override
    public void onCreate() {
        super.onCreate();
        appInMachine = new AppInMachine(this);
        monitorList = new HashMap<>();
        notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        MainActivity.isMonitor = true;
    }

    public static DetectService getInstance() {
        if (mInstance == null) {
            synchronized (DetectService.class) {
                if (mInstance == null) {
                    mInstance = new DetectService();
                }
            }
        }
        return mInstance;
    }

    // 此方法用来判断当前应用的辅助功能服务是否开启
    public static boolean isAccessibilitySettingsOn(Context context) {
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        if (accessibilityEnabled == 1) {
            String services = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (services != null) {
                return services.toLowerCase().contains(context.getPackageName().toLowerCase());
            }
        }

        return false;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return 0; // 根据需要返回不同的语义值
    }

    public boolean isShow(String mForegroundPackageName){
        if(!MainActivity.settingsInfo.getBlackListArray().contains(mForegroundPackageName)){
            return false;
        }

        long currentTime = appInMachine.getCurrentUsageTime(mForegroundPackageName, DetectService.this);
        if(monitorList.get(mForegroundPackageName).getEndTime() > currentTime){
            return false;
        }

        if(monitorList.get(mForegroundPackageName).isShow()){
            return false;
        }

        if(monitorList.get(mForegroundPackageName).isFinish){
            return false;
        }

        return true;
    }

    public int getRemains(){
        int total = 0;

        long endTime = System.currentTimeMillis();
        long startTime = AppInMachine.startOfDay(new Timestamp(System.currentTimeMillis()));

        Map<String, UsageStats> usageMap = appInMachine.getUsageList(DetectService.this, startTime, endTime);
        for (Map.Entry<String, UsageStats> entry : usageMap.entrySet()) {
            UsageStats stats = entry.getValue();
            if(MainActivity.settingsInfo.getBlackListArray().contains(entry.getKey())){
                total += stats.getTotalTimeInForeground();
            }
        }
        return Math.round((MainActivity.settingsInfo.getTotalTime() - total) / 60000);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e(TAG, "open" + String.valueOf(event.getPackageName()));
        mForegroundPackageName = event.getPackageName().toString();
        String packageName = event.getPackageName().toString();
        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ) {
            try{
                long currentTime = appInMachine.getCurrentUsageTime(packageName, DetectService.this);

                if(!monitorList.containsKey(packageName) && MainActivity.settingsInfo.getBlackListArray().contains(packageName)){
                    MonitorItem monitorItem = new MonitorItem(packageName, currentTime);
                    monitorList.put(packageName, monitorItem);
                }
                MonitorItem monitorItem = monitorList.get(packageName);

                Log.e("compare", packageName + "  " + currentTime + "  " + monitorItem.getEndTime() + "   " + monitorItem.isFinish());
                if(monitorItem.getEndTime() != 0 && monitorItem.getEndTime() < currentTime && !monitorItem.isFinish()){
                    showFinishDialog(packageName);
                }

                if(isShow(packageName)) {
                    showDialog(packageName);
                }

                checkForNotification(packageName);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void showFinishDialog(String packageName){
        monitorList.get(packageName).setFinish(true);
        @SuppressLint("WrongConstant") final WindowManager wm = (WindowManager) getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams para = new WindowManager.LayoutParams();

        para.height = WindowManager.LayoutParams.WRAP_CONTENT;
        para.width = WindowManager.LayoutParams.WRAP_CONTENT;
        para.format = 1;
        para.dimAmount = 0.6f;
        para.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            para.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        final View mView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.finish_overlay, null);

        String goals = DetectService.monitorList.get(packageName).getGoal();
        if(goals != null && !"".equals(goals)){
            TextView goalsArea = (TextView) mView.findViewById(R.id.goals_area);
            goalsArea.setText(goals);
        }

        mView.findViewById(R.id.know).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(mView);
                DetectService.monitorList.get(packageName).onFinish();
                showDialog(packageName);
            }
        });
        try{
            wm.addView(mView, para);
        }catch (Exception e){
            DetectService.monitorList.get(packageName).setFinish(false);
            Log.e(TAG, String.valueOf(e.getStackTrace()));
        }
    }

    public void showDialog(String packageName){
        monitorList.get(packageName).setShow(true);
        @SuppressLint("WrongConstant") final WindowManager wm = (WindowManager) getApplicationContext().getSystemService("window");
        WindowManager.LayoutParams para = new WindowManager.LayoutParams();

        para.height = WindowManager.LayoutParams.WRAP_CONTENT;
        para.width = WindowManager.LayoutParams.WRAP_CONTENT;
        para.format = 1;
        para.dimAmount = 0.6f;
        para.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            para.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            para.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }
        final View mView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.setting_time_overlay, null);
        EditText mins = (EditText) mView.findViewById(R.id.mins);
        EditText goalsInput = (EditText) mView.findViewById(R.id.goals);
        TextView remainsRemind = (TextView) mView.findViewById(R.id.timeRemainsRemind);
        remainsRemind.setText("Reminder: " + getRemains() + " minutes remaining for your daily screen time limit");
        mView.findViewById(R.id.five).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mins.setText("5");
            }
        });
        mView.findViewById(R.id.ten).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mins.setText("10");
            }
        });
        mView.findViewById(R.id.fivteen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mins.setText("15");
            }
        });
        mView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wm.removeView(mView);
                DetectService.this.monitorList.get(packageName).setShow(false);
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        });
        mView.findViewById(R.id.confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Pattern pattern = Pattern.compile("[0-9]*");
                String timeInput = String.valueOf(mins.getText());
                Matcher isNum = pattern.matcher(timeInput);
                if(timeInput != null && !"".equals(timeInput) && isNum.matches() && Integer.valueOf(timeInput) > 0) {
                    int time = Integer.valueOf(timeInput);
                    long currentTime = appInMachine.getCurrentUsageTime(packageName, DetectService.this);
                    long endTime = Math.round(currentTime + time * 60000);
                    DetectService.this.monitorList.get(packageName).updateNewTimer(currentTime, endTime, String.valueOf(goalsInput.getText()));
                    wm.removeView(mView);
                }else{
                    Toast.makeText(DetectService.this, "invalid input time", Toast.LENGTH_SHORT).show();
                }
            }
        });
        try{
            wm.addView(mView, para);
        }catch (Exception e){
            DetectService.this.monitorList.get(packageName).setShow(false);
            Log.e(TAG, String.valueOf(e.getStackTrace()));
        }
    }

    public void checkForNotification(String currentPackageName){
        try{
            long currentTime = AppInMachine.getCurrentUsageTime(currentPackageName, DetectService.this);
            MonitorItem monitorItem = DetectService.monitorList.get(currentPackageName);
            long notifyTime = monitorItem.getNotifyTime();
            long endTime = monitorItem.getEndTime();
            boolean isNotify = monitorItem.isNotify();

            if(!isNotify && notifyTime < currentTime){
                createNotification(currentPackageName, endTime - currentTime);
                DetectService.monitorList.get(currentPackageName).setNotify(true);
            }
        }catch (Exception e){
            DetectService.monitorList.get(currentPackageName).setNotify(false);
            e.printStackTrace();
        }

    }

    public void backgroundChecking(){
        new Timer().schedule(new TimerTask() {
            @SuppressLint("MissingPermission")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                String currentPackageName = DetectService.mForegroundPackageName;
                Log.e("checkTime", currentPackageName + "");
                if(currentPackageName == null || !MainActivity.settingsInfo.getBlackListArray().contains(currentPackageName)) return;

                long currentTime = AppInMachine.getCurrentUsageTime(currentPackageName, DetectService.this);

                MonitorItem monitorItem = DetectService.monitorList.get(currentPackageName);
                long notifyTime = monitorItem.getNotifyTime();
                long endTime = monitorItem.getEndTime();
                boolean isNotify = monitorItem.isNotify();

                Log.e("checkTime", currentPackageName + "  " + currentTime + "  " + notifyTime + "  "  + endTime + "  " + isNotify);

                if(!isNotify && notifyTime < currentTime){
                    createNotification(currentPackageName, endTime - currentTime);
                    DetectService.monitorList.get(currentPackageName).setNotify(true);
                }

                if(currentTime > endTime){
                    showDialog(currentPackageName);
                }
            }
        }, 0, 1000);
    }


    public void createNotification(String packageName, long remains){
        String appName = appInMachine.getAppName(packageName, DetectService.this);
        int remainMins = Math.round(remains / 60000);

        String reminder = remainMins + " minutes left of " + appName;
        if(remainMins < 0) reminder = "Time is up for " + appName;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Context context = getApplicationContext();
            String channelId = "timeFliesNormal";
            Notification notification = new Notification.Builder(context)
                    .setChannelId(channelId)
                    .setContentTitle("Times remaining")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText(reminder)
                    .build();
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "timeFliesNormal",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            notificationManager.notify(1, notification);
        }else{
            NotificationCompat.Builder builder= new NotificationCompat.Builder(this);
            builder.setContentTitle("Times remaining");
            builder.setContentText(remainMins + "minutes left of " + appName);
            builder.setPriority(2);
            notificationManager.notify(1, builder.build());
        }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    protected  void onServiceConnected() {
        super.onServiceConnected();
    }

}
