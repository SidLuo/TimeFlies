package comp5216.sydney.edu.au.timefiles.whiteList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import comp5216.sydney.edu.au.timefiles.R;
import comp5216.sydney.edu.au.timefiles.blackList.BlackListInitial;

public class WhiteListInitial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_white_list_initial);
    }

    public void addApp(View view) {
        Intent intent = new Intent(this, AddWhiteListApp.class);
        startActivity(intent);
    }

    public void jump(View view){
        Intent intent = new Intent(this, BlackListInitial.class);
        startActivity(intent);
    }
}