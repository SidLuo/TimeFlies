package comp5216.sydney.edu.au.timefiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import comp5216.sydney.edu.au.timefiles.overlay.DetectService;
import comp5216.sydney.edu.au.timefiles.utils.AuthenticationManagement;

public class LogIn extends AppCompatActivity {

    AuthenticationManagement authenticationManagement;
    DetectService detectService;

    EditText email;
    EditText password;

    Button logIn;
    Button register;
    Button confirm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        logIn = findViewById(R.id.logIn_button);
        confirm = findViewById(R.id.confirm_button);

        authenticationManagement = MainActivity.authentication;

    }

    public void confirm(View view) {
        authenticationManagement.signIn(email.getText().toString(), password.getText().toString());
    }

    public void register(View view) {
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }
}