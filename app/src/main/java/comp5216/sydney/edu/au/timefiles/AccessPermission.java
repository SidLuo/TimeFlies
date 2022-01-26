package comp5216.sydney.edu.au.timefiles;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import comp5216.sydney.edu.au.timefiles.overlay.DetectService;
import comp5216.sydney.edu.au.timefiles.setting.LocalSettingInteract;
import comp5216.sydney.edu.au.timefiles.whiteList.WhiteListInitial;

public class AccessPermission extends AppCompatActivity {

    Button appUsage;
    Boolean appUsageIsChecked = false;

    Button overlayDialog;
    Boolean overlayDialogIsChecked = false;

    Button appLaunch;
    Boolean appLaunchIsChecked = false;

    Button start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_permission);

        appUsage = findViewById(R.id.switch1);
        overlayDialog = findViewById(R.id.switch2);
        appLaunch = findViewById(R.id.switch3);

        start = (Button) findViewById(R.id.button);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean isAccesiblityService = DetectService.isAccessibilitySettingsOn(AccessPermission.this);
                Boolean isUsage = checkUsagePermission(AccessPermission.this);
                Boolean isOverlay = Settings.canDrawOverlays(AccessPermission.this);

                if(!isAccesiblityService){
                    Toast.makeText(AccessPermission.this, "Accessibility Service is not authorized", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!isUsage){
                    Toast.makeText(AccessPermission.this, "Usage data access permission is not authorized", Toast.LENGTH_LONG).show();
                    return;
                }

                if(!isOverlay){
                    Toast.makeText(AccessPermission.this, "Overlay permission is not authorized", Toast.LENGTH_LONG).show();
                    return;
                }

                if(LocalSettingInteract.getSetting(AccessPermission.this) != null) {
                    Intent intent = new Intent(AccessPermission.this, MainActivity.class);
                    AccessPermission.this.startActivity(intent);
                }else{
                    Intent intent = new Intent(AccessPermission.this, WhiteListInitial.class);
                    AccessPermission.this.startActivity(intent);
                }
            }
        });


        appUsage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                startActivity(intent);
                appUsageIsChecked = true;
            }
        });

        overlayDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                startActivity(intent);
                overlayDialogIsChecked = true;

            }
        });

        appLaunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request permission to be notified when other apps are launched
                Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                appLaunchIsChecked = true;
            }
        });
    }

    public static boolean checkUsagePermission(Context context){
        // Check permission to get access to usage statistics
        long ts = System.currentTimeMillis();
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getApplicationContext().getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_BEST, 0, ts);
        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
          return false;
        }
        return true;
    }

    public static boolean ifNeedPermission(Context context){
//        Boolean isAccesiblityService = DetectService.isAccessibilitySettingsOn(context);
        Boolean isAccesiblityService = true;
        Boolean isUsage = checkUsagePermission(context);
        Boolean isOverlay = Settings.canDrawOverlays(context);

        if(!isUsage || !isOverlay || !isAccesiblityService){
            return true;
        }
        return false;
    }
}