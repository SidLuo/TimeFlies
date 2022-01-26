package comp5216.sydney.edu.au.timefiles.whiteList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;
import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.blackList.BlackListInitial;
import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;

public class WhiteList extends AppCompatActivity {

    ArrayList<AppInfo> appChecked;
    WhiteListAdapter whiteListAdapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView recyclerView;
    Button next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list);
        recyclerView = findViewById(R.id.recyclerView2);
        next = findViewById(R.id.next_button);

        appChecked = MainActivity.settingsInfo.getWhiteList();

        Log.e("appChecked", appChecked.toString());
        loadAppToList();

        // allow button to be clicked once colour is changed
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Starts Block List activity when start button is clicked
                Intent intent = new Intent(WhiteList.this, BlackListInitial.class);

                startActivity(intent);
            }
        });
    }

    private void loadAppToList() {
        whiteListAdapter = new WhiteListAdapter(this, appChecked);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setAdapter(whiteListAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void returnToAddWhiteListApp(View view) {
        finish();
    }
}