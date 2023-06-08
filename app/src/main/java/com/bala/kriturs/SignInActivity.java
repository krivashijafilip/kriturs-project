package com.bala.kriturs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity {

    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private EditText emailTextInput;
    private EditText passwordTextInput;
    private Button signInButton;
    private Button forgotPasswordButton;
    private Button signUpButton;
    private Button sendVerifyMailAgainButton;
    private Button anonymousSignInButton;
    private SignInButton signInWithGoogleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailTextInput = findViewById(R.id.signInEmailTextInput);
        passwordTextInput = findViewById(R.id.signInPasswordTextInput);
        signInButton = findViewById(R.id.signInButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        signUpButton = findViewById(R.id.signUpButton);
        sendVerifyMailAgainButton = findViewById(R.id.verifyEmailAgainButton);
        anonymousSignInButton = findViewById(R.id.anonymousSignInButton);
        signInWithGoogleButton = findViewById(R.id.signInWithGoogleButton);

        sendVerifyMailAgainButton.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithEmailAndPassword();
            }
        });

        forgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent forgotPasswordActivity = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(forgotPasswordActivity);
                SignInActivity.this.finish();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpActivity = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(signUpActivity);
                SignInActivity.this.finish();
            }
        });

        anonymousSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInAnonymously();
            }
        });

        signInWithGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "Google API client connection failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signInWithEmailAndPassword() {
        if (emailTextInput.getText().toString().equals("")) {
            Toast.makeText(SignInActivity.this, "Email cannot be empty!",
                    Toast.LENGTH_SHORT).show();
        } else if (passwordTextInput.getText().toString().equals("")) {
            Toast.makeText(SignInActivity.this, "Password cannot be empty.",
                    Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(emailTextInput.getText().toString(), passwordTextInput.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    if (user.isEmailVerified()) {
                                        Log.d(TAG, "Email Verified: " + user.isEmailVerified());
                                        Intent homeActivity = new Intent(SignInActivity.this, MainActivity.class);
                                        setResult(RESULT_OK, null);
                                        startActivity(homeActivity);
                                        SignInActivity.this.finish();
                                    } else {
                                        sendVerifyMailAgainButton.setVisibility(View.VISIBLE);
                                        Toast.makeText(SignInActivity.this, "Please verify your email and sign in!",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(SignInActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                if (task.getException() != null) {
                                    Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
        }
    }

    private void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent homeActivity = new Intent(SignInActivity.this, MainActivity.class);
                            setResult(RESULT_OK, null);
                            startActivity(homeActivity);
                            SignInActivity.this.finish();
                            // Proceed with your desired logic after successful sign-in
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Anonymous authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Log.d(TAG, "Google Sign-In failed");
                Toast.makeText(SignInActivity.this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Proceed with your desired logic after successful sign-in
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
