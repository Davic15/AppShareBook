package com.example.group6.appsharebook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.util.Arrays;

public class MainPage extends AppCompatActivity {
    private DatabaseReference mDatabase;


    CallbackManager mCallBackManager;
    LoginButton loginButton;
    private static final String TAG = "FACELOG";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Facebook login
        mAuth = FirebaseAuth.getInstance();
        mCallBackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess: "+loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel: ");

            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"facebook:onError", error);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            updateUI();
        }
    }
    private void updateUI(){
        Toast.makeText(MainPage.this, "You are Logged in", Toast.LENGTH_LONG).show();
        //if the user is logged in, i will send him/her to other activity
        Intent showProfile = new Intent(MainPage.this, ShowProfile.class);
        startActivity(showProfile);
        finish();
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser userFireBase = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainPage.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallBackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void clickRegister (View v) {
        Intent intent = new Intent(this, Registration.class);
        this.startActivity(intent);
    }

    public void clickLogin (View v){
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        Button btn = (Button)findViewById(R.id.login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText passwd = (EditText) findViewById(R.id.userPassword);
                EditText userwd = (EditText) findViewById(R.id.loginUsername);
                String pass11 = passwd.getText().toString();
                String user11 = userwd.getText().toString();
                //this ID gets the id of the session (Example if user franklin logs into the app all
                //the update are reflected without create a new register or new user on the data base
                String id = mDatabase.push().getKey();
                //using a clase to send all data getter and setter methods
                User user = new User (pass11, user11);
                mDatabase.child("user").child(id).setValue(user);

            }
        });


    }

}
