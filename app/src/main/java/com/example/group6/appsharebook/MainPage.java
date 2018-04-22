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
import android.widget.TextView;
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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;


import java.util.Arrays;

public class MainPage extends AppCompatActivity {

    private ProgressDialog dialog;

    CallbackManager mCallBackManager;
    private static final String TAG = "FACELOG";
    private TextView register;
    private Button login;
    private static final String TAG1 = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private SignInButton signInButton;

    private Button phoneAuth;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        //mDatabase = FirebaseDatabase.getInstance().getReference();
        dialog = new ProgressDialog(this);
        register = findViewById(R.id.textregister);
        login = findViewById(R.id.login);
        phoneAuth = findViewById(R.id.phonebutton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Facebook login
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            finish();
            Intent i = new Intent(this, ShowProfile.class);
            startActivity(i);

        }

        signInButton = findViewById(R.id.google_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

          mCallBackManager = CallbackManager.Factory.create();
          LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
          loginButton.setReadPermissions("email", "public_profile");
          loginButton.registerCallback(mCallBackManager, new FacebookCallback<LoginResult>() {

              @Override
            public void onSuccess(LoginResult loginResult) {
                  Log.d(TAG, "facebook:onSuccess: " + loginResult);
                  handleFacebookAccessToken(loginResult.getAccessToken());
              }

              @Override
              public void onCancel () {
            Log.d(TAG, "facebook:onCancel: ");

        }

               @Override
              public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
             }
         });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null)
            updateUI();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI() {

        startActivity(new Intent(MainPage.this,ShowProfile.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                //updateUI();
                // [END_EXCLUDE]
            }
       //     else {
       //         mCallBackManager.onActivityResult(requestCode, resultCode, data);
       //     }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }

                        // ...
                    }
                });
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


        public void onClick (View view) {
            if (view == register) {
                clickRegister();
            }

            if (view == login) {
                clickLogin();
            }

            if (view == phoneAuth) {
                Intent intent = new Intent (MainPage.this,PhoneAuth.class);
                startActivity(intent);
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


            }


}
