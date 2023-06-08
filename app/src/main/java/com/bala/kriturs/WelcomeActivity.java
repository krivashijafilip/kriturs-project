package com.bala.kriturs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private static final int REQUEST_EXIT = 9;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private Button signUpButton;
    private Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        mAuth = FirebaseAuth.getInstance();

        signUpButton = findViewById(R.id.welcomeSignUpButton);
        signInButton = findViewById(R.id.welcomeSignInButton);

        signInButton.setVisibility(View.INVISIBLE);
        signUpButton.setVisibility(View.INVISIBLE);

        if (mAuth.getCurrentUser() != null) {
            mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    currentUser = mAuth.getCurrentUser();

                    if (currentUser != null && currentUser.isEmailVerified()) {
                        Intent MainActivity = new Intent(WelcomeActivity.this, MainActivity.class);
                        startActivity(MainActivity);
                        finish();
                    } else {
                        showButtons();
                    }
                }
            });
        } else {
            showButtons();
        }

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signUpIntent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = new Intent(WelcomeActivity.this, SignInActivity.class);
                startActivityForResult(signInIntent, REQUEST_EXIT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_EXIT && resultCode == RESULT_OK) {
            finish();
        }
    }

    private void showButtons() {
        signInButton.setVisibility(View.VISIBLE);
        signUpButton.setVisibility(View.VISIBLE);
    }
}
