package comp5216.sydney.edu.au.timefiles;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import comp5216.sydney.edu.au.timefiles.utils.AuthenticationManagement;

public class Register extends AppCompatActivity {

    AuthenticationManagement authenticationManagement;
    EditText email;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);

        authenticationManagement = MainActivity.authentication;
    }


    public void confirm(View view) {

        authenticationManagement.createUser(email.getText().toString(), password.getText().toString());

        //Go to request permissions page
//        Intent intent = new Intent(this, AccessPermission.class);
//        startActivity(intent);
    }

    public void logIn(View view) {
        Intent intent = new Intent(this, LogIn.class);
        startActivity(intent);
    }
}