package comp5216.sydney.edu.au.timefiles.blackList;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;
import comp5216.sydney.edu.au.timefiles.setting.LocalSettingInteract;

public class BlackList extends AppCompatActivity {

    ArrayList<AppInfo> appChecked;
    BlackListAdapter blackListAdapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView recyclerView;
    Button next;
    EditText max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list);

        recyclerView = findViewById(R.id.recyclerView2);
        next = findViewById(R.id.next_button);

        appChecked = MainActivity.settingsInfo.getBlackList();
        loadAppToList();

        max = (EditText) findViewById(R.id.max);
        if(MainActivity.settingsInfo.getTotalTime() > 0){
            // if previous settings exist, use it as default
            max.setText("" + MainActivity.settingsInfo.getTotalTime() / 60000);
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String maxString = max.getText().toString();
                    MainActivity.settingsInfo.setTotalTime(60000 * Integer.valueOf(maxString));
                }catch(Exception e){
                    Toast.makeText(BlackList.this, "Maximum using time is required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.e("settings", MainActivity.settingsInfo.toString());
                LocalSettingInteract.saveSetting(BlackList.this, MainActivity.settingsInfo);
                Intent intent = new Intent(BlackList.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadAppToList() {
        blackListAdapter = new BlackListAdapter(this, appChecked);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(blackListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void returnToAddBlackListApp(View view) {
        finish();
    }
}