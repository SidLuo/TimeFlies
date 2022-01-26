package comp5216.sydney.edu.au.timefiles.blackList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;

import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;

public class AddBlackListApp extends AppCompatActivity {

    private ArrayList<AppInfo> appList;
    private ArrayList<AppInfo> remainAppList;
    private RecyclerView recyclerView;

    ChoseBlackListAdapter addListAdapter;
    LinearLayoutManager linearLayoutManager;

    ArrayList<AppInfo> appChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_black_list_app);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Gets the list of apps not checked for white list
        appList = MainActivity.settingsInfo.getAppList();
        remainAppList = new ArrayList<AppInfo>();
        for(AppInfo item : MainActivity.settingsInfo.getAppList()){
            if(!MainActivity.settingsInfo.getWhiteListArray().contains(item.getPackageName())){
                remainAppList.add(item);
            }
        }
        Iterator<AppInfo> i = remainAppList.iterator();
        while (i.hasNext()) {
            AppInfo item = i.next();
            if (item.getWhite()) {
                remainAppList.remove(item);
            }
        }

        loadAppToList();
    }

    private void loadAppToList() {
        // Loads all apps available to recyclerView
        addListAdapter = new ChoseBlackListAdapter(this, remainAppList);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(addListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void onClickSelectAll(View view) {
        addListAdapter.selectAll();
    }

    public void addToBlackList(View view) {

        // Gets a list of apps checked for black list by user
        ArrayList<AppInfo> appChecked = new ArrayList<AppInfo>();
        ArrayList<String> appCheckedArray = new ArrayList<String>();
        for(AppInfo app: appList){
            if(app.getBlack()==true){
                Log.e("black", app.getAppName());
                appChecked.add(app);
                appCheckedArray.add(app.getPackageName());
            }
        }

        MainActivity.settingsInfo.setBlackList(appChecked);
        MainActivity.settingsInfo.setBlackListArray(appCheckedArray);
        Intent intent = new Intent(this, BlackList.class);

        startActivity(intent);
    }
}