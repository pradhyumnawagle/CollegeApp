package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/*

 NAME: GroupChatActivity - This is the class that controls the Group Chat page for the user

 DESCRIPTION: When the Group chat page is opened by the user, this class gets the sent and received
              messages and displays them using a recycler view.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */
public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton mSendMessageBtn;
    private EditText mUserMessageInput;
    private ScrollView mScrollView;
    private TextInputLayout mGroupChatSearch;
    private TextView mDisplayMessages;
    private Button mSearchInChat;

    private String mCurrentUserId, mCurrUserName, mCurrDate, mCurrTime;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDbRef,mGroupDbRef,mGroupMessageKeyDbRef,mDbRef ;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView mMessagesListRecyclerView;

    private String mCurrGroupName;
    private String mCollegeName;


    /*

     NAME: GroupChatActivity::onCreate() - Initializes member variables of the class and
           sets the onclick listener for send message button.

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the group chat page loads, this method is called.
                   The member variables are initialized, including the page layout inflator and the
                   recycler view. The function retrieves data needed to display in the conversations
                    page. This function also handles the search in chat feature. This function also
                    retrieves messages from the firebase database and displays them using the recycler view
                    after search.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        mCurrGroupName = getIntent().getExtras().get("groupName").toString();
        Toast.makeText(this, mCurrGroupName, Toast.LENGTH_SHORT).show();

        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference();
        mUsersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");


        mGroupDbRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        mSearchInChat = (Button) findViewById(R.id.search);
        mGroupChatSearch = (TextInputLayout) findViewById(R.id.search_group_chat);

        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mCurrGroupName);

        mSendMessageBtn = (ImageButton) findViewById(R.id.group_chat_send_button);
        mUserMessageInput = (EditText) findViewById(R.id.input_group_message);
       // mDisplayMessages = (TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.group_chat_scroll_view);

        getUserInfo();


        messageAdapter = new MessageAdapter(messagesList);
        mMessagesListRecyclerView  = (RecyclerView) findViewById(R.id.group_conversation_list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        mMessagesListRecyclerView.setLayoutManager(linearLayoutManager);
        mMessagesListRecyclerView.setAdapter(messageAdapter);

        mSendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mUsersDbRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child("ucollege").exists()){
                            String collegeName = dataSnapshot.child("ucollege").getValue().toString();
                            StoreMessageInDb(collegeName);
                            mUserMessageInput.setText("");
                            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

        mSearchInChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messagesList.clear();
                 final String searchText = mGroupChatSearch.getEditText().getText().toString().toLowerCase();
                if(!searchText.isEmpty()) {
                    mUsersDbRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("ucollege").exists()){
                                String collegeName = dataSnapshot.child("ucollege").getValue().toString();
                                mGroupDbRef.child(collegeName).child(mCurrGroupName).addChildEventListener(new ChildEventListener() {
                                    @Override
                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        if (dataSnapshot.exists()) {
                                            Messages messages = dataSnapshot.getValue(Messages.class);
                                            if(messages.getMessage().contains(searchText)) {
                                                messagesList.add(messages);
                                                messageAdapter.notifyDataSetChanged();
                                                mMessagesListRecyclerView.smoothScrollToPosition(mMessagesListRecyclerView.getAdapter().getItemCount());
                                            }
                                        }
                                    }

                                    @Override
                                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                        Messages messages = dataSnapshot.getValue(Messages.class);
                                        if(messages.getMessage().contains(searchText)) {
                                            messagesList.add(messages);
                                            messageAdapter.notifyDataSetChanged();
                                            mMessagesListRecyclerView.smoothScrollToPosition(mMessagesListRecyclerView.getAdapter().getItemCount());
                                        }
                                    }

                                    @Override
                                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

                }else{
                    onStart();
                }
            }
        });

    }

    /*

     NAME: GroupChatActivity::onStart() - Gets called when the page loads after onCreate() and is visible.

     SYNOPSIS: protected void onStart()

     DESCRIPTION: When the group chat page loads, this method is called.
                   When the page starts getting visible to the user, first the onCreate method is
                   called followed by onStart(). This function also retrieves messages from the
                   firebase database and displays them using the recycler view.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onStart() {
        super.onStart();

        final String searchText = mGroupChatSearch.getEditText().getText().toString();
        mUsersDbRef.child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("ucollege").exists()) {
                    mCollegeName = dataSnapshot.child("ucollege").getValue().toString();
                    mGroupDbRef.child(mCollegeName).child(mCurrGroupName).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            if (dataSnapshot.exists()) {
                                Messages messages = dataSnapshot.getValue(Messages.class);
                                //System.out.println(messages.getMessage());
                                messagesList.add(messages);
                                messageAdapter.notifyDataSetChanged();
                                mMessagesListRecyclerView.smoothScrollToPosition(mMessagesListRecyclerView.getAdapter().getItemCount());
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            Messages messages = dataSnapshot.getValue(Messages.class);
                            //System.out.println("2 " + messages.getMessage());
                            messagesList.add(messages);
                            messageAdapter.notifyDataSetChanged();
                            mMessagesListRecyclerView.smoothScrollToPosition(mMessagesListRecyclerView.getAdapter().getItemCount());
                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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

        }


/*

     NAME: GroupChatActivity::displayMessages() - Displays messages from the database into a text view.

     SYNOPSIS: private void displayMessages(DataSnapshot dataSnapshot)
                dataSnapshot: The data received from firebase database is reveived as a DataSnapshot.
                              Values can be received using dataSnapshot.

     DESCRIPTION: This function retrieves all the messages from a group chat after a data snapshot
                    is sent to it as the parameter. It accesses firebase through the DataSnapshot
                    object and retrieves the data and prints it to a list view.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */

    private void displayMessages(DataSnapshot dataSnapshot) {
        Iterator it = dataSnapshot.getChildren().iterator();

        while(it.hasNext()){
            String chatDate = (String) ((DataSnapshot)it.next()).getValue();
            String chatMsg = (String) ((DataSnapshot)it.next()).getValue();
            String chatTime = (String) ((DataSnapshot)it.next()).getValue();
            String chatUsername = (String) ((DataSnapshot)it.next()).getValue();

            mDisplayMessages.append(chatUsername + " :\n" + chatMsg + "\n" + chatTime + "   " + chatDate + "\n\n\n");

            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    /*

     NAME: ChatActivity::StoreMessageInDb() - Stores the typed message in the firebase database

     SYNOPSIS: private void StoreMessageInDb(String college_name)
                college_name: The name of the college that group is under. It is because a chat group
                                is a child of a college name.

     DESCRIPTION: The function receives the message from user input and stores it in the database
                    after putting the message and related information like date, time and key into
                    a HashMap.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    private void StoreMessageInDb(String college_name) {

        final String message = mUserMessageInput.getText().toString();
        String msgKey = mGroupDbRef.child(mCurrGroupName).child(college_name).push().getKey();
        System.out.println("Bottom" + message);

        if(message.equals(null)){
            Toast.makeText(GroupChatActivity.this, "Please enter a message first!!!", Toast.LENGTH_SHORT).show();
        } else{
            Calendar currDate = Calendar.getInstance();
            SimpleDateFormat currDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            mCurrDate = currDateFormat.format(currDate.getTime());

            Calendar currTime = Calendar.getInstance();
            SimpleDateFormat currTimeFormat = new SimpleDateFormat("hh:mm a");
            mCurrTime = currTimeFormat.format(currTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            mGroupDbRef.child(college_name).child(mCurrGroupName).updateChildren(groupMessageKey);


            mGroupMessageKeyDbRef = mGroupDbRef.child(college_name).child(mCurrGroupName).child(msgKey);

            Map messageText = new HashMap();
            messageText.put("message",message);
            messageText.put("type","text");
            messageText.put("from",mCurrentUserId);
            messageText.put("to","");
            messageText.put("messageID",msgKey);
            messageText.put("time",mCurrTime);
            messageText.put("date",mCurrDate);

            mGroupMessageKeyDbRef.updateChildren(messageText);
        }


    }

    /*

     NAME: GroupChatActivity::getUserInfo() - Gets the user's information and stores username in
                               mCurrUserName.

     SYNOPSIS:private void getUserInfo()

     DESCRIPTION: The function gets the user's name through a addValueListenerEvent on the current user's
                    id. Then, the username is stores in the class variable mCurrUserName.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    private void getUserInfo() {
        mUsersDbRef.child(mCurrentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    mCurrUserName = dataSnapshot.child("uname").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}

