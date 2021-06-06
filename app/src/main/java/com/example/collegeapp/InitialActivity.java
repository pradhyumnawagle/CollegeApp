package com.example.collegeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/*

 NAME: InitialActivity - This is the class that controls the Initial page when the app loads.

 DESCRIPTION: When the application first starts, this class is called. It displays the logo of the
              app along with the options for login and sign up.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class InitialActivity extends AppCompatActivity {

    private Button mSignUpBtn;
    private Button mLoginBtn;

    /*

     NAME: InitialActivity::onCreate() - Initializes member variables of the class and
           sets the onclick listener for login and sign up buttons

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the application starts at first, this function is called and it initializes
                  all member variables like buttons, displays the logo and sets the event
                  setOnCLickListener for the signup and login buttons.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        mSignUpBtn = (Button) findViewById(R.id.initial_signup_btn);
        mLoginBtn = (Button) findViewById(R.id.initial_login_btn);

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(InitialActivity.this, SignupActivity.class);
                startActivity(signupIntent);
                finish();
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(InitialActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}
