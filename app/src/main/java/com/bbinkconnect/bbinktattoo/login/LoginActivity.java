package com.bbinkconnect.bbinktattoo.login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bbinkconnect.bbinktattoo.home.HomeActivity;
import com.bbinkconnect.bbinktattoo.R;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.annotations.Nullable;

public class LoginActivity extends AppCompatActivity {

    // BB Ink
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;
    private ProgressBar mProgressBar;
    private AccessToken accessToken;

    private Context mContext = LoginActivity.this;

    //Facebook
    private CallbackManager mCallbackManager;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Google
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    //firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //find ID by TextBox
        mEmail = findViewById(R.id.input_email);
        mPassword = findViewById(R.id.input_password);

        //find ID by progress
        mProgressBar = findViewById(R.id.progressBar);
        mPleaseWait = findViewById(R.id.pleaseWait);

        //Firebase
        mAuth = FirebaseAuth.getInstance();
        setupFirebaseAuth();

        //PleaseWait and Progressbar Visible
        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        //Reg Link
        init();

        //facebook
        mCallbackManager = CallbackManager.Factory.create();

        //google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("701746104104-91g6ib8th3v23eknsuca5eqoehqneobh.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //LogIn with Email
        Button loginbtn = findViewById(R.id.btn_login);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if(isStringNull(email) || isStringNull(password)) {
                    Toast.makeText(getApplicationContext(), getString(R.string.text_null), Toast.LENGTH_SHORT).show();
                    mPleaseWait.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.GONE);
                }else{

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (!task.isSuccessful()){
                                        Toast.makeText(getApplicationContext(), getString(R.string.login_failed), Toast.LENGTH_SHORT).show();
                                        mPleaseWait.setVisibility(View.GONE);
                                        mProgressBar.setVisibility(View.GONE);
                                    }else {
                                        Intent intent1 = new Intent(LoginActivity.this, HomeActivity.class);//ACTIVITY_NUM = 0
                                        startActivity(intent1);
                                    }
                                }
                            });

                }

            }
        });

        //Facebook Login
        LoginButton fbButton = findViewById(R.id.login_button);
        fbButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);

                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        //Login with Google
        SignInButton signInButton = findViewById(R.id.google_login);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressBar.setVisibility(View.VISIBLE);
                mPleaseWait.setVisibility(View.VISIBLE);

                switch (v.getId()) {
                    case R.id.google_login:
                        signIn();
                        break;

                }
            }
        });

    }

    //Google
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    //Open Home site
    private void goMainScreen() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        //Google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    //Google - Login Result
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);

        } catch (ApiException e) {

        }
    }

    private void init(){
        //Reg Link
        TextView linkSignUp = findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
        }

    }

    private void handleFacebookAccessToken(AccessToken token) {

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            mPleaseWait.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            goMainScreen();
                        } else {
                            mPleaseWait.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                        }

                    }
                });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            mPleaseWait.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                            goMainScreen();
                        } else {
                            // If sign in fails, display a message to the user.
                            mPleaseWait.setVisibility(View.GONE);
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    //If string Null
    private boolean isStringNull(String string){

        return string.equals("");
        }

    @Override
    public void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        mPleaseWait.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

    }

    @Override
    public void onStop() {
        super.onStop();
       if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    private void setupFirebaseAuth(){

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //check if the user is logged in
                checkCurrentUser(user);

                if (user != null) {
                    // User is signed in

                } else {
                    // User is signed out

                }

            }
        };
    }

    private void checkCurrentUser(FirebaseUser user){

        if(user == null){
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
        }
    }

}
