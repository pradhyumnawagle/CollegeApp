package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
/*

 NAME: ChatActivity - This is the class that controls the Chat page for the user

 DESCRIPTION: When the conversations page is opened by the user, this class gets the sent and received
              messages and displays them using a recycler view.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */
public class ChatActivity extends AppCompatActivity {

    private String mReceiverId , mCurrUserId;
    private String mReceiverName, mReceiverImage;

    private TextView mUserName;
    private CircleImageView mUserImage;

    private ImageButton mSendMessageButton, mSendImageMessageButton;
    private EditText mMessageInputText;

    private Toolbar mToolBar;
    private DatabaseReference mDbRef;

    private FirebaseAuth mAuth;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView mMessagesListRecyclerView;

    private String mCurrentTime, mCurrentDate;
    private Uri imageURI;
    private String imageURL="";
    private StorageTask uploadTask;

    /*

     NAME: ChatActivity::onCreate() - Initializes member variables of the class and
           sets the onclick listener for send message button

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the personal conversation i.e. chat page loads, this method is called.
                   The member variables are initialized, including the page layout inflator.
                   The function retrieves data needed to display in the conversations page.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mCurrUserId = mAuth.getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference();

        mReceiverId = getIntent().getExtras().get("selected_user_id").toString();
        mReceiverName = getIntent().getExtras().get("selected_user_name").toString();
        mReceiverImage = getIntent().getExtras().get("selected_user_image").toString();


        mToolBar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setTitle("");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_conversation_toolbar, null);
        actionBar.setCustomView(actionBarView);

        mUserImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        mUserName = (TextView) findViewById(R.id.custom_user_name);
        mSendMessageButton = (ImageButton) findViewById(R.id.send_message_btn);
        mSendImageMessageButton = (ImageButton) findViewById(R.id.send_image_message_btn);
        mMessageInputText = (EditText) findViewById(R.id.write_message_input_text);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        mCurrentDate = date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
        mCurrentTime = time.format(calendar.getTime());

        messageAdapter = new MessageAdapter(messagesList);
        mMessagesListRecyclerView  = (RecyclerView) findViewById(R.id.private_conversation_list_users);
        linearLayoutManager = new LinearLayoutManager(this);
        mMessagesListRecyclerView.setLayoutManager(linearLayoutManager);
        mMessagesListRecyclerView.setAdapter(messageAdapter);



        mUserName.setText(mReceiverName);
        if (mReceiverImage.isEmpty()) {
            Picasso.get().load(R.drawable.profile).placeholder(R.drawable.profile).into(mUserImage);
        } else{
            Picasso.get().load(mReceiverImage).placeholder(R.drawable.profile).into(mUserImage);
        }

        mSendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               SendMessage();
            }
        });

        mSendImageMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select an Image"), 677);
            }
        });
        mDbRef.child("Messages").child(mCurrUserId).child(mReceiverId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);

                messagesList.add(messages);
                messageAdapter.notifyDataSetChanged();

                mMessagesListRecyclerView.smoothScrollToPosition(mMessagesListRecyclerView.getAdapter().getItemCount());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

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


    /*

     NAME: ChatActivity::onStart() - Gets called when the page loads after onCreate() and is visible.

     SYNOPSIS: protected void onStart()

     DESCRIPTION: When the personal conversation i.e. chat page loads, this method is called.
                   When the page starts getting visible to the user, first the onCreate method is
                   called followed by onStart().

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */

    @Override
    protected void onStart() {
        super.onStart();
    }


    /*

     NAME: ChatActivity::onActivityResult() - when user wants to select an image from
            gallery, this function allows them to choose an image

     SYNOPSIS: protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
                requestCode: A unique request code, an integer value, that is sent from
                            startActivityForResult() inside the onCreate() method.
                resultCode: A unique result code, returned through setResult() by child activity
                data: Intent to the Gallery, which returns the image data

     DESCRIPTION:  The function gets called when a user wants to send picture message. The gallery
                    from phone is accessed and image can be selected after options to edit the image.
                    Also, the function stores the image's info in the database.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(677==requestCode && resultCode==RESULT_OK && data!=null && data.getData()!=null) {
            imageURI = data.getData();
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image Files");
            final String messageSenderRef = "Messages/" + mCurrUserId + "/" + mReceiverId;
            final String messageReceiverRef = "Messages/" + mReceiverId + "/" + mCurrUserId;

            DatabaseReference userMsgKeyRef = mDbRef.child("Messages").child(mCurrUserId).child(mReceiverId).push();

            final String messageKey = userMsgKeyRef.getKey();

            final StorageReference filePath = storageReference.child(messageKey + ".jpg");
            uploadTask = filePath.putFile(imageURI);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = (Uri) task.getResult();
                        imageURL = downloadUri.toString();

                        Map messageText = new HashMap();
                        messageText.put("message",imageURL);
                        messageText.put("name",imageURI.getLastPathSegment());
                        messageText.put("type","image");
                        messageText.put("from",mCurrUserId);
                        messageText.put("to",mReceiverId);
                        messageText.put("messageID",messageKey);
                        messageText.put("time",mCurrentTime);
                        messageText.put("date",mCurrentDate);

                        Map messageBody = new HashMap();
                        messageBody.put(messageSenderRef + "/" + messageKey, messageText);
                        messageBody.put(messageReceiverRef + "/" + messageKey, messageText);

                        mDbRef.updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(ChatActivity.this, "Message sent!!!", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(ChatActivity.this, "Error in sending message!!!", Toast.LENGTH_SHORT).show();
                                }
                                mMessageInputText.setText(null);
                            }
                        });

                    }
                }
            });

        }
    }

    /*

     NAME: ChatActivity::SendMessage() - This function gets user's message from the input field and
                        stores the message into the database.

     SYNOPSIS: private void SendMessage()

     DESCRIPTION:  The function gets called the user sends the message. It stores the messages
                    into the firebase database using keys generated from the sender and receiver
                       user ids.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    private void SendMessage(){
        String inputMessage = mMessageInputText.getText().toString();
        if(TextUtils.isEmpty(inputMessage)){
            Toast.makeText(this, "Please write a message first!!!", Toast.LENGTH_SHORT).show();
        } else{
            String messageSenderRef = "Messages/" + mCurrUserId + "/" + mReceiverId;
            String messageReceiverRef = "Messages/" + mReceiverId + "/" + mCurrUserId;

            DatabaseReference userMsgKeyRef = mDbRef.child("Messages").child(mCurrUserId).child(mReceiverId).push();

            String messageKey = userMsgKeyRef.getKey();

            Map messageText = new HashMap();
            messageText.put("message",inputMessage);
            messageText.put("type","text");
            messageText.put("from",mCurrUserId);
            messageText.put("to",mReceiverId);
            messageText.put("messageID",messageKey);
            messageText.put("time",mCurrentTime);
            messageText.put("date",mCurrentDate);

            Map messageBody = new HashMap();
            messageBody.put(messageSenderRef + "/" + messageKey, messageText);
            messageBody.put(messageReceiverRef + "/" + messageKey, messageText);

            mDbRef.updateChildren(messageBody).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Message sent!!!", Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(ChatActivity.this, "Error in sending message!!!", Toast.LENGTH_SHORT).show();
                    }
                    mMessageInputText.setText(null);
                }
            });
        }
    }
}
