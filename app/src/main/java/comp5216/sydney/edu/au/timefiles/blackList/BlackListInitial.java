package comp5216.sydney.edu.au.timefiles.blackList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.model.AppInfo;

public class BlackListInitial extends AppCompatActivity {

    private ArrayList<AppInfo> appChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_black_list_initial);

    }

    public void addAppToBlackList(View view) {
        Intent intent = new Intent(this, AddBlackListApp.class);
        startActivity(intent);
    }
}