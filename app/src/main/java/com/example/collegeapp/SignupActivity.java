package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/*

 NAME: LoginActivity - This is the class that controls the signup page.

 DESCRIPTION: When the signup page is called, this class loads the page, takes input values and
              deals with successful and unsuccessful signup attempts.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class SignupActivity extends AppCompatActivity {


    private TextInputLayout firstName;
    private TextInputLayout lastName;
    private TextInputLayout email;
    private TextInputLayout password;

    private Button signUp;
    private TextView mAlreadyHaveAccount;

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;

    private Toolbar mtoolBar;

    private ProgressDialog mSignupProgress;


    /*

     NAME: SignupActivity::onCreate() - Initializes member variables of the class and
           sets the onclick listener for signup button.

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the signup page loads, this function is called. The function initializes
                  the member variables, and calls the event setOnClickListener for the signup button
                  and other options like already have an account?.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mtoolBar = (Toolbar) findViewById(R.id.signup_toolbar);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setTitle("Sign Up");

        //Shows the up button whiuch links to main activity from the manifest file
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSignupProgress = new ProgressDialog(this);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference();

        firstName = (TextInputLayout) findViewById(R.id.signup_firstName);
        lastName = (TextInputLayout) findViewById(R.id.signup_lastName);
        email = (TextInputLayout) findViewById(R.id.signup_email);
        password = (TextInputLayout) findViewById(R.id.signup_password);


        signUp = (Button) findViewById(R.id.signup_btn);
        mAlreadyHaveAccount = (TextView) findViewById(R.id.signup_already_have_account);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String first_name = firstName.getEditText().getText().toString();
                String last_name = lastName.getEditText().getText().toString();
                String email_add = email.getEditText().getText().toString();
                String passWord = password.getEditText().getText().toString();

                if (TextUtils.isEmpty(first_name)){
                    Toast.makeText(SignupActivity.this, "Please enter your First Name!!", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(last_name)){
                    Toast.makeText(SignupActivity.this, "Please enter your Last Name!!", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(email_add)){
                    Toast.makeText(SignupActivity.this, "Please enter your Email!!", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(passWord)){
                    Toast.makeText(SignupActivity.this, "Please enter a password!!", Toast.LENGTH_SHORT).show();
                }

                if(!TextUtils.isEmpty(first_name) && !TextUtils.isEmpty(last_name)  && !TextUtils.isEmpty(email_add) && !TextUtils.isEmpty(passWord) ){

                    mSignupProgress.setTitle("User Registration in progress..");
                    mSignupProgress.setMessage("Please verify your email address!!");
                    mSignupProgress.setCanceledOnTouchOutside(true);
                    mSignupProgress.show();
                    register_user(first_name,last_name,email_add,passWord);
                }

            }
        });

        mAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    /*

    NAME: SignupActivity::register_user() - Deals with the registration process using user input attributes.

    SYNOPSIS: register_user(String firstName, String lastName, final String email, String password)
               firstName: First name of the user
               lastName: Last name of the user
               email: The email used to register
               password: The password used to register

    DESCRIPTION: The function deals with registration process. Once input fields are checked,
                  if all input values are valid, an email verification is sent. The user is then
                  directed to login.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    private void register_user(String firstName, String lastName, final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        mSignupProgress.dismiss();
                                        // Sign in success, update UI with the signed-in user's information
                                        String currUserId = mAuth.getCurrentUser().getUid();
                                        mDbRef.child("Users").child(currUserId).setValue("");

                                        Intent mainIntent = new Intent(SignupActivity.this, LoginActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            mSignupProgress.hide();
                            // If sign in fails, display a message to the user.
                            String message = task.getException().toString();
                            Toast.makeText(SignupActivity.this, "Invalid attempt!!!  " + message, Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }
}
