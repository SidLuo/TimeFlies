package comp5216.sydney.edu.au.timefiles.whiteList;

import android.app.usage.UsageStats;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;
import comp5216.sydney.edu.au.timefiles.utils.AppInMachine;

public class AddWhiteListApp extends AppCompatActivity {
    AppInMachine appInMachine;
    List<PackageInfo> apps;

    ArrayList<AppInfo> appInfoExtracted;
    ArrayList<String> appInfoExtractedArray;
    RecyclerView recyclerView;

    ChoseWhiteListAdapter addListAdapter;
    LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_white_list_app);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        getAppInformation();
        loadAppToList();

        appInMachine = MainActivity.appInMachine;

    }

    private void getAppInformation() {
        PackageManager pm = this.getPackageManager();
        final long USAGE_STATS_PERIOD = 1000 * 60 * 60 * 24 * 7; // period is a day
        long endTime = System.currentTimeMillis();
        long startTime = endTime - USAGE_STATS_PERIOD;

        //Get the name and icon of each app

        appInfoExtracted = new ArrayList<AppInfo>();
        appInfoExtractedArray = new ArrayList<String>();

        if(MainActivity.settingsInfo.getAppList() != null && (MainActivity.settingsInfo.getAppList().size() > 0)){
            // if previous settings exist, use them as the default settings

            appInfoExtracted = MainActivity.settingsInfo.getAppList();
            appInfoExtractedArray = MainActivity.settingsInfo.getAppListArray();

            Map<String, UsageStats>  appUasge = AppInMachine.getUsageList(this, startTime, endTime);
            for (Map.Entry<String, UsageStats> entry : appUasge.entrySet()) {
                String packageName = entry.getKey();

                if(!appInfoExtractedArray.contains(packageName)){
                    // support newly installed apps
                    if (!AppInMachine.isSystemApp(this, packageName)){
                        AppInfo newInfo = new AppInfo();
                        String appName;
                        try{
                            appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.e("failed in get app name", e.toString());
                            e.printStackTrace();
                            appName = packageName;
                        }
                        newInfo.setAppName(appName);
                        newInfo.setPackageName(packageName);
                        appInfoExtracted.add(newInfo);
                        appInfoExtractedArray.add(packageName);
                    }
                }
            }
        } else {
            // if previous settings not exist, get the whole list
            Map<String, UsageStats>  appUasge = AppInMachine.getUsageList(this, startTime, endTime);
            for (Map.Entry<String, UsageStats> entry : appUasge.entrySet()) {
                String packageName = entry.getKey();

                if (!AppInMachine.isSystemApp(this, packageName)){
                    AppInfo newInfo = new AppInfo();
                    String appName;
                    try{
                        appName = pm.getApplicationLabel(pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e("failed in get app name", e.toString());
                        e.printStackTrace();
                        appName = packageName;
                    }
                    newInfo.setAppName(appName);
                    newInfo.setPackageName(packageName);
                    appInfoExtracted.add(newInfo);
                    appInfoExtractedArray.add(packageName);
                }
            }
        }
    }

    private void loadAppToList() {
        // Loads all apps available to recyclerView
        addListAdapter = new ChoseWhiteListAdapter(this, appInfoExtracted);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(addListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    public void addToWhiteList(View view) {

        // Gets a list of apps checked for white list by user

        ArrayList<AppInfo> appChecked = new ArrayList<AppInfo>();
        ArrayList<String> appCheckedArray = new ArrayList<String>();
        for(AppInfo app: appInfoExtracted){
            if(app.getWhite() == true){
                Log.e("white", app.getAppName());
                appChecked.add(app);
                appCheckedArray.add(app.getPackageName());
            }
        }

        MainActivity.settingsInfo.setAppList(appInfoExtracted);
        MainActivity.settingsInfo.setAppListArray(appInfoExtractedArray);
        MainActivity.settingsInfo.setWhiteList(appChecked);
        MainActivity.settingsInfo.setWhiteListArray(appCheckedArray);
        Intent intent = new Intent(this, WhiteList.class);
        startActivity(intent);

    }
}