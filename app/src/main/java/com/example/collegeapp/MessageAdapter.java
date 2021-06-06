package com.example.collegeapp;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*

 NAME: MessageAdapter - This is the adapter class for personal and group chat messages

 DESCRIPTION: The class is an adapter for personal and group chat messages. It provides an interface
              of the page where conversations are stored and updates the messages sent and
              received into the views.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUsersDbRef;


    /*

     NAME: MessageAdapter::MessageAdapter() - Constructor of the class MessageAdapter

     SYNOPSIS: public MessageAdapter(List<Messages> userMessagesList)
               userMessagesList: List of all messages between users or in a group

     DESCRIPTION:  The function is a constructor of the MessageAdapter class. It initializes the
                    member variable userMessageList.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    public MessageAdapter(List<Messages> userMessagesList){
        this.userMessagesList = userMessagesList;
    }


    /*

     NAME: MessageAdapter::onCreateViewHolder() - Creates a view to display the conversations

     SYNOPSIS: public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
               parent: it is the parent viewGroup
               viewType: it is the int value of the position where data is displayed in the view

     DESCRIPTION:  The function creates a view where conversations are displayed. From the xml resource file
                    associated, the layout is inflated and the created view is returned.

     RETURNS: The created view.

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_messages_layout, parent, false);
        mAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);
    }

    /*

    NAME: MessageAdapter::onBindViewHolder() - Called by the RecyclerView to display the data at the
                                               position specified.

    SYNOPSIS: public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position)
              holder: Holder which represents the contents of the item at a given position.
              position: The position of item within the adapter's data

    DESCRIPTION:  The function is called by the RecyclerView to display the data at the position
                  specified. THis function updates the contents of the View Holder.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {
        String messageSenderId = mAuth.getCurrentUser().getUid();
        Messages messages = userMessagesList.get(position);

        String receiverUserId = messages.getFrom();
        String receivedMessageType = messages.getType();

        if(receiverUserId != "") {
            mUsersDbRef = FirebaseDatabase.getInstance().getReference().child("Users").child(receiverUserId);
            mUsersDbRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("uimage")) {
                        String userImage = dataSnapshot.child("uimage").getValue().toString();
                        Picasso.get().load(userImage).placeholder(R.drawable.profile).into(holder.receiverProfileImage);
                    }
                    if(dataSnapshot.hasChild("uname")) {
                        holder.receiverName.setText(dataSnapshot.child("uname").getValue().toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);
        holder.receiverName.setVisibility(View.GONE);
        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderPicture.setVisibility(View.GONE);
        holder.receiverPicture.setVisibility(View.GONE);


        if(receivedMessageType.equals("text")){

            if(receiverUserId.equals(messageSenderId)){
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                holder.senderMessageText.setText(messages.getMessage() + "\n\n" + messages.getTime() + "  " + messages.getDate());
            }
            else{
                holder.receiverProfileImage.setVisibility(View.VISIBLE);
                holder.receiverName.setVisibility(View.VISIBLE);
                //holder.receiverName.setText();
                holder.receiverMessageText.setVisibility(View.VISIBLE);
                holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                holder.receiverMessageText.setText(messages.getMessage() + "\n\n" + messages.getTime() + "  " + messages.getDate());

            }
        } else if(receivedMessageType.equals("image")){
            if(receiverUserId.equals(messageSenderId)){
                holder.senderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.senderPicture);
            }
            else{
                holder.receiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(holder.receiverPicture);

            }
        }
    }


    /*

   NAME: MessageAdapter::getItemCount() - Returns the count of the messages in the message list.

   SYNOPSIS: public int getItemCount()

   DESCRIPTION:  The function returns the count of the messages in userMessagesList

   RETURNS: count of the messages in userMessagesList

   AUTHOR: Pradhyumna Wagle

   DATE 9/27/2020

   */
    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }

    /*

 NAME: MessageViewHolder - This is the class that renders the view that displays the messages.

 DESCRIPTION: This class renders the view that displays messages. It instantiates the
                fields from Recycler view in the page where messages are displayed.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText, receiverName;
        public CircleImageView receiverProfileImage;
        public ImageView senderPicture, receiverPicture;

        /*

     NAME: MessageViewHolder::MessageViewHolder() - Constructor of the class MessageViewHolder

     SYNOPSIS:  public MessageViewHolder(@NonNull View itemView)
               itemView: an individual item from the view

     DESCRIPTION: This function is a constructor for the MessageViewHolder class. It
                   initializes all member variables

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverName = (TextView) itemView.findViewById(R.id.sender_name);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.message_profile_image);
            senderPicture = (ImageView) itemView.findViewById(R.id.sender_message_image);
            receiverPicture = (ImageView) itemView.findViewById(R.id.receiver_message_image);

        }
    }
}
