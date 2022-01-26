package comp5216.sydney.edu.au.timefiles.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import comp5216.sydney.edu.au.timefiles.AccessPermission;
import comp5216.sydney.edu.au.timefiles.LogIn;
import comp5216.sydney.edu.au.timefiles.MainActivity;
import comp5216.sydney.edu.au.timefiles.setting.LocalSettingInteract;


public class AuthenticationManagement{
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private Activity activity;
    private String userEmailAddress = "";

    public AuthenticationManagement(Activity activity) {
        this.activity = activity;

        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Log.e("Logging", currentUser.getEmail().toString());
            userEmailAddress = currentUser.getEmail().toString();
            decideIfNeedGoSettings(false);
        }else{
            Log.e("Logging", "do not log in");
            Intent intent = new Intent(activity, LogIn.class);
            activity.startActivity(intent);
        }
    }

    public void decideIfNeedGoSettings(Boolean ifBack){
        if(!AccessPermission.ifNeedPermission(activity)) {
            if(ifBack){
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
            }
        }else{
            Intent intent = new Intent(activity, AccessPermission.class);
            activity.startActivity(intent);
        }
    }

    /**
     * Creates a new User with an email and password
     * @param email
     * @param password
     */
    public void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                                updateUI(user);
                            decideIfNeedGoSettings(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "createUserWithEmail:failure", task.getException());
//                                updateUI(null);
                            Toast.makeText(activity, "Register failed, please try complex password or change an email address.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signIn(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            userEmailAddress = user.getEmail();
                            Log.e(TAG, "signInWithEmail:success" + userEmailAddress);
                            decideIfNeedGoSettings(true);
//
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signOut(){
        mAuth.signOut();
        this.userEmailAddress = "";
        Intent intent = new Intent(activity, LogIn.class);
        activity.startActivity(intent);
    }

    public void getUser(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            Log.e("current user", name.toString() + email.toString());
            Toast.makeText(activity, "Sign in successed",
                    Toast.LENGTH_SHORT).show();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
        }
    }

    public boolean ifLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            return true;
        }else{
            return false;
        }
    }

    public String getUserEmailAddress() {
        return userEmailAddress;
    }



}
