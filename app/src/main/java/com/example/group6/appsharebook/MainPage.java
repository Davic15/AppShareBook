package com.example.group6.appsharebook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
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

    private ProgressDialog dialog;

    CallbackManager mCallBackManager;
    private static final String TAG = "FACELOG";
    private Button buttonRegister;
    private Button login;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        dialog = new ProgressDialog(this);
        buttonRegister = findViewById(R.id.register);
        login = findViewById(R.id.login);

        //Facebook login
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            Intent i = new Intent(this, ShowProfile.class);
            startActivity(i);

        }

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
        finish();
        startActivity(showProfile);
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

        public void onClick (View view) {
            if (view == buttonRegister) {
                clickRegister();
            }

            if (view == login) {
                clickLogin();
            }
        }

    public void clickRegister () {
        Intent intent = new Intent(this, Registration.class);
        this.startActivity(intent);
    }



      public void clickLogin() {

                EditText passwd = (EditText) findViewById(R.id.userPassword);
                EditText userwd = (EditText) findViewById(R.id.loginUsername);
                String pass11 = passwd.getText().toString();
                String user11 = userwd.getText().toString();
                //this ID gets the id of the session (Example if user franklin logs into the app all
                //the update are reflected without create a new register or new user on the data base
                if (TextUtils.isEmpty(user11)) {
                    // email is empty;
                    Toast.makeText(MainPage.this,"Please enter email",Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(pass11)) {
                    // password is empty;
                    Toast.makeText(MainPage.this,"Please enter Password",Toast.LENGTH_SHORT).show();
                    return;

                }

                mAuth.signInWithEmailAndPassword(user11,pass11)
                        .addOnCompleteListener(MainPage.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                dialog.dismiss();

                                if (task.isSuccessful()) {

                                    Intent intent = new Intent(MainPage.this,ShowProfile.class) ;
                                    finish();
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(MainPage.this,"Not logged in. Retry",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                //using a clase to send all data getter and setter methods
              //  User user = new User (pass11, user11);
              //  mDatabase.child("user").child(id).setValue(user);

            }
      //  });


 //   }

}
