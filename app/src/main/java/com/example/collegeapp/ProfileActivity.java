package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

/*

 NAME: ProfileActivity - This is the class that controls the User Profiles page

 DESCRIPTION: When the Profile page loads after the user selects a user on the Search Users page, this class
               displays the user image, user name and user bio and button for request

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class ProfileActivity extends AppCompatActivity {


    String mReceiverUserId;
    private CircleImageView mProfileUserImage;
    private TextView mProfileUserName, mProfileUserBio;
    private Button mSendMessageRequestBtn, mDeclineRequestBtn;
    private Toolbar mToolBar;

    private DatabaseReference mUsersdDbRef, mNotificationsDbRef;
    private String mCurrState, mCurrUserId;

    private FirebaseAuth mAuth;
    private DatabaseReference mChatRequestDbRef, mDbConnectionsRef;

    /*

     NAME: ProfileActivity::onCreate() - Initializes member variables of the class and
                                        retrieves user info

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the profile page loads, this function is called. It initializes all member
                  variables used and retrieves user info to display the user's information on the page.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /*mToolBar = (Toolbar) findViewById(R.id.pro)
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("User Profile");

        //Shows the up button which links to main activity from the manifest file
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        mReceiverUserId = getIntent().getExtras().get("profile_user_id").toString();

        mProfileUserImage = (CircleImageView) findViewById(R.id.profile_user_image);
        mProfileUserName = (TextView) findViewById(R.id.profile_user_name);
        mProfileUserBio = (TextView) findViewById(R.id.profile_user_bio);

        mSendMessageRequestBtn = (Button) findViewById(R.id.profile_send_message_button);
        mDeclineRequestBtn = (Button) findViewById(R.id.profile_reject_message_request_btn);

        mUsersdDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mCurrState = "new";//means two users are new to each other
        mAuth = FirebaseAuth.getInstance();
        mCurrUserId = mAuth.getCurrentUser().getUid();
        mChatRequestDbRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        mDbConnectionsRef = FirebaseDatabase.getInstance().getReference().child("Connections");
        mNotificationsDbRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        RetrieveUserInfo();

    }

    /*

     NAME: ProfileActivity::RetrieveUserInfo() - Retrieves user info

     SYNOPSIS: private void RetrieveUserInfo()

     DESCRIPTION: When the profile page loads, this function is called when it is needed to retrieve
                  the information of the user whose profile is opened. This function sets the values
                  retrieved from the database to the fields in the layout file.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    private void RetrieveUserInfo() {
        mUsersdDbRef.child(mReceiverUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("uimage"))){
                    String userImage = dataSnapshot.child("uimage").getValue().toString();
                    String userName = dataSnapshot.child("uname").getValue().toString();
                    String userBio = dataSnapshot.child("ubio").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.profile).into(mProfileUserImage);
                    mProfileUserName.setText(userName);
                    mProfileUserBio.setText(userBio);

                    ManageChatRequests();
                } else{
                    String userName = dataSnapshot.child("uname").getValue().toString();
                    String userBio = dataSnapshot.child("ubio").getValue().toString();
                    mProfileUserName.setText(userName);
                    mProfileUserBio.setText(userBio);

                    ManageChatRequests();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*

     NAME: ProfileActivity::ManageChatRequests() - Accepts or Cancels chat requests

     SYNOPSIS: private void ManageChatRequests()

     DESCRIPTION: When the profile page loads, this function helps to manage the connection request
                  based on what the visiting user wants. If a connection request is sent, it is stored
                  in the database that the request has been sent and the options change to Accept/Reject
                  or Send a connection request on the basis of connection request status.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    private void ManageChatRequests() {

        mChatRequestDbRef.child(mCurrUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(mReceiverUserId)){
                    String request_type = dataSnapshot.child(mReceiverUserId).child("request_type").getValue().toString();
                    if(request_type.equals("sent")){
                        mCurrState = "request_sent";
                        mSendMessageRequestBtn.setText("Cancel Request");
                    } else if(request_type.equals("received")){
                        mCurrState = "request_received";
                        mSendMessageRequestBtn.setText("Accept Request");

                        mDeclineRequestBtn.setVisibility(View.VISIBLE);
                        mDeclineRequestBtn.setEnabled(true);

                        mDeclineRequestBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelRequest();
                            }
                        });
                    }
                }
                else{
                    mDbConnectionsRef.child(mCurrUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(mReceiverUserId)){
                                mCurrState = "connected";
                                mSendMessageRequestBtn.setText("Remove connection");
                                mSendMessageRequestBtn.setBackgroundColor(getColor(R.color.primary_buttons));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(!mCurrUserId.equals(mReceiverUserId)){
            mSendMessageRequestBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSendMessageRequestBtn.setEnabled(false);
                    if(mCurrState.equals("new")){
                        SendChatRequest();
                    }
                    if(mCurrState.equals("request_sent")){
                        CancelRequest();
                    }
                    if(mCurrState.equals("request_received")){
                        AcceptRequest();
                    }
                    if(mCurrState.equals("connected")){
                        RemoveConnection();
                    }
                }
            });
        } else{
            mSendMessageRequestBtn.setVisibility(View.INVISIBLE);
        }
    }

    /*

     NAME: ProfileActivity::RemoveConnection() - Called when a connection request is removed

     SYNOPSIS: private void RemoveConnection()

     DESCRIPTION: When the profile page loads, if the connection request has been rejected or has not been sent yet,
                  this function is called. It provides an option to send a message request.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    private void RemoveConnection() {
        mDbConnectionsRef.child(mCurrUserId).child(mReceiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDbConnectionsRef.child(mReceiverUserId).child(mCurrUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mSendMessageRequestBtn.setEnabled(true);
                                mCurrState = "new";
                                mSendMessageRequestBtn.setText("Send Message");
                                mSendMessageRequestBtn.setBackgroundColor(getColor(R.color.colorPrimary));

                                mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });

    }

    /*

     NAME: ProfileActivity::AcceptRequest() - Called when a connection request is accepted

     SYNOPSIS: private void AcceptRequest()

     DESCRIPTION: When the profile page loads, if the connection request has been accepted, this
                  function updates the database inside saved connections. It also displays an option
                  to remove the connection if the visiting user wants to.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    private void AcceptRequest() {
        mDbConnectionsRef.child(mCurrUserId).child(mReceiverUserId).child("Connections").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDbConnectionsRef.child(mReceiverUserId).child(mCurrUserId).child("Connections").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mChatRequestDbRef.child(mCurrUserId).child(mReceiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            mChatRequestDbRef.child(mReceiverUserId).child(mCurrUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @RequiresApi(api = Build.VERSION_CODES.M)
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                        mSendMessageRequestBtn.setEnabled(true);
                                                        mCurrState = "connected";
                                                        mSendMessageRequestBtn.setText("Remove Connection");
                                                        mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                                        mSendMessageRequestBtn.setBackgroundColor(getColor(R.color.primary_buttons));
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        });
    }

    /*

     NAME: ProfileActivity::CancelRequest() - Called when a connection request is cancelled

     SYNOPSIS: private void CancelRequest()

     DESCRIPTION: When the profile page loads, if the connection request has been sent but the user
                  wants to cancel the sent request, this function is called. It updates the database
                  where the request_type value is set as sent changing it to new.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    private void CancelRequest() {
        mChatRequestDbRef.child(mCurrUserId).child(mReceiverUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mChatRequestDbRef.child(mReceiverUserId).child(mCurrUserId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                mSendMessageRequestBtn.setEnabled(true);
                                mCurrState = "new";
                                mSendMessageRequestBtn.setText("Send Message");

                                mDeclineRequestBtn.setVisibility(View.INVISIBLE);
                                mDeclineRequestBtn.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });


    }

    /*

     NAME: ProfileActivity::SendChatRequest() - Called when a connection request is sent

     SYNOPSIS: private void SendChatRequest()

     DESCRIPTION: When the profile page loads, if the connection request has been sent, this function
                  is called. It updates the request_type value in the database as sent and at the
                  sender's end, it gives the option to cancel request.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    private void SendChatRequest() {
        mChatRequestDbRef.child(mCurrUserId).child(mReceiverUserId).child("request_type").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                   mChatRequestDbRef.child(mReceiverUserId).child(mCurrUserId).child("request_type").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               mSendMessageRequestBtn.setEnabled(true);
                               mCurrState = "request_sent";
                               mSendMessageRequestBtn.setText("Cancel request");

                           }
                       }
                   }) ;
                }
            }
        });
    }
}
