package com.bala.kriturs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    public FirebaseAuth mAuth;
    Button signUpButton;
    EditText signUpEmailTextInput;
    EditText signUpPasswordTextInput;
    CheckBox agreementCheckBox;
    TextView errorView;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference usersCollection = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        signUpEmailTextInput = findViewById(R.id.signUpEmailTextInput);
        signUpPasswordTextInput = findViewById(R.id.signUpPasswordTextInput);
        signUpButton = findViewById(R.id.signUpButton);
        agreementCheckBox = findViewById(R.id.agreementCheckbox);
        errorView = findViewById(R.id.signUpErrorView);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (signUpEmailTextInput.getText().toString().isEmpty()) {
                    errorView.setText("Email cannot be empty");
                } else if (signUpPasswordTextInput.getText().toString().isEmpty()) {
                    errorView.setText("Password cannot be empty");
                } else if (!agreementCheckBox.isChecked()) {
                    errorView.setText("Please agree to the terms and conditions");
                } else {
                    mAuth.createUserWithEmailAndPassword(signUpEmailTextInput.getText().toString(), signUpPasswordTextInput.getText().toString()).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                if (user != null) {
                                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "Email sent.");

                                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                                        SignUpActivity.this);

                                                // set title
                                                alertDialogBuilder.setTitle("Please Verify Your EmailID");

                                                // set dialog message
                                                alertDialogBuilder
                                                        .setMessage("A verification email is sent to your registered email address. Please click on the link and sign in again!")
                                                        .setCancelable(false)
                                                        .setPositiveButton("Sign In", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int id) {
                                                                Intent signInIntent = new Intent(SignUpActivity.this, SignInActivity.class);
                                                                SignUpActivity.this.finish();
                                                            }
                                                        });

                                                // create alert dialog
                                                AlertDialog alertDialog = alertDialogBuilder.create();

                                                // show it
                                                alertDialog.show();
                                            }
                                        }
                                    });

                                    // Add user ID to Firestore
                                    String userID = user.getUid();
                                    usersCollection.document(userID).set(new User(userID))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
//                                                    Log.d(TAG, "User ID added to Firestore: " + userID);
                                                    // Continue with your desired logic after adding the user ID to Firestore
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding user ID to Firestore", e);
                                                    // Handle the failure to add the user ID to Firestore
                                                }
                                            });
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                if (task.getException() != null) {
                                    errorView.setText(task.getException().getMessage());
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
