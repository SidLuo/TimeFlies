package comp5216.sydney.edu.au.timefiles;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import comp5216.sydney.edu.au.timefiles.model.AppUsage;
import comp5216.sydney.edu.au.timefiles.model.SettingsInfo;
import comp5216.sydney.edu.au.timefiles.overlay.DetectService;
import comp5216.sydney.edu.au.timefiles.ranking.RankingActivity;
import comp5216.sydney.edu.au.timefiles.setting.LocalSettingInteract;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;
import comp5216.sydney.edu.au.timefiles.utils.AuthenticationManagement;
import comp5216.sydney.edu.au.timefiles.whiteList.WhiteListInitial;

public class MainActivity extends AppCompatActivity {
    public final static long ONE_SECOND = 1000;
    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public static AuthenticationManagement authentication;
    public static AppInMachine appInMachine;
    public static SettingsInfo settingsInfo;
    public static boolean isMonitor;
    public DetectService detectService;

    // Define variables
    RecyclerView recyclerView;
    TreeMap<Long, String> apps; // TreeMap sorts apps based on usage, usage saved in milliseconds
    ArrayList<AppUsage> list; // A list for RecyclerView
    MainActivityAdapter mainActivityAdapter;

    private Button monitorBtn;
    private TextView infoText;
    private TextView totalText;
    private Switch showTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settingsInfo = new SettingsInfo();
        authentication = new AuthenticationManagement(this);
        appInMachine = new AppInMachine(this);
        isMonitor = false;

        // Get previous setting data
        loadPreviousSetting();

        // Reference the "listView" variable to the id "lstView" in xml
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Setup the RecyclerView and the adapter
        setupAdapter();

        monitorBtn = (Button) findViewById(R.id.monitor_btn);
        infoText = (TextView) findViewById(R.id.info_text);
        totalText = (TextView) findViewById(R.id.total_text);
        infoText.setText((MainActivity.settingsInfo.getTotalTime() - AppInMachine.getTotalTime(MainActivity.this)) / 60000 + "");
        totalText.setText(" / " + MainActivity.settingsInfo.getTotalTime() / 60000 + " mins left");
        initSwitch();
    }

    public void initSwitch(){
        showTotal = (Switch) findViewById(R.id.showTotal);
        showTotal.setChecked(settingsInfo.getMainPageMode() == SettingsInfo.PageMode.TOTAL);
        showTotal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                settingsInfo.setMainPageMode( b ? SettingsInfo.PageMode.TOTAL : SettingsInfo.PageMode.ONLY);
                LocalSettingInteract.saveSetting(MainActivity.this, settingsInfo);
                mainActivityAdapter.notifyDataSetChanged();
                setupAdapter();
            }
        });
    }

    private void loadPreviousSetting(){
        if(LocalSettingInteract.getSetting(this) != null) {
            MainActivity.settingsInfo = LocalSettingInteract.getSetting(this);
        }
    }

    public void onResume() {
        super.onResume();
        setupAdapter();
    }

    public void startRanking(View view){
        Intent intent = new Intent(this, RankingActivity.class);
        startActivity(intent);
    }

    private void setupAdapter() {
        list = new ArrayList<>();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Map<String, UsageStats> usages = appInMachine.getUsageList(getApplicationContext(),
                                                                    AppInMachine.startOfDay(timestamp),
                                                                    System.currentTimeMillis());
        Log.i("dataRows:", "dataRows:" + usages.keySet() + usages.values());

        apps = new TreeMap<>();

        ArrayList blackList = settingsInfo.getBlackListArray();
        ArrayList whiteList = settingsInfo.getWhiteListArray();
        SettingsInfo.PageMode mode = settingsInfo.getMainPageMode();

        // Put usages in TreeMap to sort
        Iterator<Map.Entry<String, UsageStats>> i = usages.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry<String, UsageStats> it = i.next();

            if(mode == null || mode == SettingsInfo.PageMode.ONLY){
                if (!blackList.contains(it.getKey())) {
                    continue;
                }
            } else {
                if (whiteList.contains(it.getKey())) {
                    continue;
                }
            }
            if (appInMachine.isSystemApp(getApplicationContext(), it.getKey())) {
                continue;
            }

            String appName = it.getKey();
            apps.put(-it.getValue().getTotalTimeInForeground(), appName);
        }

        Iterator<Map.Entry<Long, String>> itr = apps.entrySet().iterator();

        while (itr.hasNext()) {
            AppUsage appUsage = new AppUsage();
            Map.Entry<Long, String> item = itr.next();
            // Put app name
            appUsage.setAppName(appInMachine.getAppName(item.getValue(), getApplicationContext()));
            appUsage.setPackageName(item.getValue());
            appUsage.setBlack(true);
            // Calculate usage time
            long usage = -item.getKey();
            String usageText = "";
            long temp = 0;
            if (usage >= ONE_MINUTE) {
                temp = usage / ONE_HOUR;
                if (temp > 0) {
                    usageText = usageText + temp + "h";
                    usage -= temp * ONE_HOUR;
                    if (usage > ONE_MINUTE) {
                        usageText += " ";
                    }
                }
                temp = usage / ONE_MINUTE;
                if (temp > 0) {
                    usageText = usageText + temp + "min";
                }
            } else if (usage >= ONE_SECOND) {
                temp = usage / ONE_SECOND;
                usageText = usageText + temp + "s";
            } else {
                usageText = "<1s";
            }
            usageText += '.';
            appUsage.setTotalTime(usage);
            appUsage.setUsage(usageText);
            list.add(appUsage);
        }

        mainActivityAdapter = new MainActivityAdapter(this, list);

        // Connect the recyclerView and the adapter
        recyclerView.setAdapter(mainActivityAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public void startAccessibilityService(Context context) {
        if (DetectService.isAccessibilitySettingsOn(context) == true) {
            detectService = DetectService.getInstance();
            isMonitor = true;
        } else {
            Intent intent = new Intent(this, AccessPermission.class);
            context.startActivity(intent);
            Toast.makeText(context, "No permission to accessibility", Toast.LENGTH_SHORT).show();
        }
    }

    public void settingAgain(View view){
        if(AccessPermission.ifNeedPermission(MainActivity.this)){
            Intent intent = new Intent(this, AccessPermission.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, WhiteListInitial.class);
            startActivity(intent);
        }
    }

    public void onClickUser(View view){
        if(authentication.getUserEmailAddress() == null || authentication.getUserEmailAddress().length() == 0){
            Intent intent = new Intent(this, LogIn.class);
            startActivity(intent);
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("User Center");
            builder.setMessage("Current User: \n" + authentication.getUserEmailAddress());
            builder.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    authentication.signOut();
                }
            });
            builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.create().show();
        }
    }

    public void startMonitor(View view){
        if(!isMonitor){
            if(settingsInfo.getTotalTime() <= 0){
                Toast.makeText(this, "Please set maximize screen time first!", Toast.LENGTH_LONG).show();
            } else {
                startAccessibilityService(this);
                Toast.makeText(this, "Screen time restriction service started!", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "Screen time restriction service already started!", Toast.LENGTH_LONG).show();
        }
    }
}