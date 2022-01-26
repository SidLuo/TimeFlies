package comp5216.sydney.edu.au.timefiles.utils;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import comp5216.sydney.edu.au.timefiles.MainActivity;

public class OverlayUtils extends Activity{
    public Context context;
    public Activity activity;
    private String TAG = "overlay";
    public OverlayUtils(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        checkPermission();
    }

    public void checkPermission(){
        if (!Settings.canDrawOverlays(context)) {
            Toast.makeText(activity, "no permission", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
            context.startActivity(intent);
        } else {
//            startService(new Intent(MainActivity.this, FloatingService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(context)) {
                Log.e(TAG, "peimission failed");
            } else {
                Log.e(TAG, "permisioon success");
//                startService(new Intent(MainActivity.this, FloatingService.class));
            }
        }
    }


}
