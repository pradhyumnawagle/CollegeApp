package com.example.collegeapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/*

 NAME: LoginActivity - This is the class that controls the login page.

 DESCRIPTION: When the login page is called, this class loads the page, takes input values and
              deals with successful and unsuccessful login attempts.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */

public class LoginActivity extends AppCompatActivity {

    private Toolbar mtoolBar;

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;

    private Button mLoginBtn;
    private TextView mForgotPassword, mLoginUsingPhone, mNewAccount;

    private ProgressDialog mLoginProgress;

    private FirebaseAuth mAuth;


    /*

     NAME: LoginActivity::onCreate() - Initializes member variables of the class and
           sets the onclick listener for login button.

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the login page loads, this function is called. The function initializes
                  the member variables, and calls the event setOnClickListener for the login button.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mtoolBar = (Toolbar) findViewById(R.id.login_toolbar);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");

        mLoginProgress = new ProgressDialog(this);

        mLoginEmail = (TextInputLayout) findViewById(R.id.login_email);
        mLoginPassword = (TextInputLayout) findViewById(R.id.login_password);

        mLoginBtn = findViewById(R.id.login_btn);

        mForgotPassword = findViewById(R.id.login_forgot_password);
        mLoginUsingPhone = findViewById(R.id.login_using_phone);
        mNewAccount = findViewById(R.id.login_need_new_account);


        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getEditText().getText().toString();
                String password = mLoginPassword.getEditText().getText().toString();

                if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
                    mLoginProgress.setTitle("Login in Progress...");
                    mLoginProgress.setMessage("Please wait while we check your credentials!!!");
                    mLoginProgress.setCanceledOnTouchOutside(true);
                    mLoginProgress.show();

                    loginUser(email,password);
                }
            }
        });

        mNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(mainIntent);
            }
        });

    }


    /*

    NAME: LoginActivity::loginUser() - Deals with the sign in process using email and password.

    SYNOPSIS: private void loginUser(String email, String password)
               email: The email used to login
               password: The password used to login

    DESCRIPTION: The function deals with login process. Once email and password are checked, the
                  function makes sure that the email is verified. Then, if all conditions holds true,
                  the user is able to login.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        String currentUserId = mAuth.getCurrentUser().getUid();
                        // String deviceUniqueToken = FirebaseInstance

                        mLoginProgress.dismiss();
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    } else{
                        Toast.makeText(LoginActivity.this, "User not verified!!!", Toast.LENGTH_SHORT).show();
                    }

                } else{

                    mLoginProgress.hide();
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, "Invalid attempt!!! Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
