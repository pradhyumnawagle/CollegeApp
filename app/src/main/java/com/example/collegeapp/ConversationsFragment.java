package com.example.collegeapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/*

 NAME: ConversationsFragment - This is the class that controls the fragment that holds all
                               private conversations

 DESCRIPTION: When the Chats tab is selected in the main page, this class controls the
                list of conversations displayed.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */
public class ConversationsFragment extends Fragment {

    private View mPrivateConversationsView;
    private RecyclerView mConversationsRecyclerView;

    private DatabaseReference mDbConnectionsRef, mDbUsersRef, mDbConversationsRef;
    private FirebaseAuth mAuth;
    private String mCurrUserId;


    /*

        NAME: ConversationsFragment::ConversationsFragment() - Required empty public constructor

        SYNOPSIS: public ConversationsFragment()

        DESCRIPTION: This function is an empty constructor for the ConversationsFragment class.

        RETURNS: None

        AUTHOR: Pradhyumna Wagle

        DATE 9/26/2020

        */
    public ConversationsFragment() {
        // Required empty public constructor
    }


    /*

     NAME: ConversationsFragment::onCreateView() - Instantiates the user interface view in the Chats fragment.

     SYNOPSIS: public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
                inflater: The LayoutInflater object that can be used to inflate any views in the fragment
                container: this is the parent view that the fragment's UI should be attached to
                savedInstanceState: fragment is being re-constructed from a previous saved state as given here

     DESCRIPTION: The user's interface of the Chats fragment is instantiated in this function. The recycler view and the
                firebase user authentication is initialized here

     RETURNS: View that contains the layout i.e. mConversationsView

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mPrivateConversationsView  = inflater.inflate(R.layout.fragment_conversations, container, false);

        mConversationsRecyclerView = (RecyclerView) mPrivateConversationsView.findViewById(R.id.conversations_list);
        mConversationsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            mCurrUserId = mAuth.getCurrentUser().getUid();
        }

        mDbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return mPrivateConversationsView;
    }

    /*
    NAME: ConversationsFragment::onStart() - Called when the fragment is visible to the user

    SYNOPSIS: public void onStart()

    DESCRIPTION: Gets the data from the list of private conversations and displays the users inside
                 the inflated view on the Conversations fragment page under Chat tab in the dashboard.
                 the function also updates if the each user in the list is online or offline and starts
                 conversationIntent when a user is selected.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/26/2020

    */
    @Override
    public void onStart() {
        super.onStart();



        //mDbConversationsRef = FirebaseDatabase.getInstance().getReference().child("Connections").child();
        if(mCurrUserId!=null) {
            mDbConnectionsRef = FirebaseDatabase.getInstance().getReference().child("Connections").child(mCurrUserId);
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(mDbConnectionsRef, Contacts.class).build();

            FirebaseRecyclerAdapter<Contacts, ConversationsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ConversationsViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final ConversationsViewHolder holder, int position, @NonNull Contacts model) {
                    final String userIds = getRef(position).getKey();
                    final String[] mUserImage = {""};
                    mDbUsersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (dataSnapshot.hasChild("uimage")) {
                                    mUserImage[0] = dataSnapshot.child("uimage").getValue().toString();
                                    Picasso.get().load(mUserImage[0]).placeholder(R.drawable.profile).into(holder.userProfileImage);
                                }

                                final String userProfileName = dataSnapshot.child("uname").getValue().toString();
                                String userProfileBio = dataSnapshot.child("ubio").getValue().toString();

                                holder.userName.setText(userProfileName);


                                if (dataSnapshot.child("userState").hasChild("state")) {
                                    String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                    String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                    String date = dataSnapshot.child("userState").child("date").getValue().toString();

                                    if (state.equals("online")) {
                                        holder.userBio.setText("active");
                                    } else if (state.equals("offline")) {
                                        holder.userBio.setTextSize(10);
                                        holder.userBio.setText(date + " " + time);
                                    }

                                } else {
                                    holder.userBio.setText("Offline");
                                }


                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent conversationIntent = new Intent(getContext(), ChatActivity.class);
                                        conversationIntent.putExtra("selected_user_id", userIds);
                                        conversationIntent.putExtra("selected_user_name", userProfileName);
                                        conversationIntent.putExtra("selected_user_image", mUserImage[0]);
                                        startActivity(conversationIntent);
                                    }
                                });


                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                @NonNull
                @Override
                public ConversationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_users_layout, parent, false);
                    ConversationsViewHolder conversationsViewHolder = new ConversationsViewHolder(view);
                    return conversationsViewHolder;
                }
            };

            mConversationsRecyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    /*

 NAME: ConnectionsViewHolder - This is the class that renders the view that displays
                                      private conversations.

 DESCRIPTION: This class renders the list view that displays private conversations. It instantiates the
                fields from Recycler view in the conversations fragment.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */
    private class ConversationsViewHolder extends RecyclerView.ViewHolder  {

        TextView userName, userBio;
        CircleImageView userProfileImage;

         /*

     NAME: ConversationsViewHolder::ConversationsViewHolder() - Initializes member variables of the class

     SYNOPSIS: public ConversationsViewHolder(@NonNull View itemView)
               itemView: an individual item from the view

     DESCRIPTION: This function is a constructor for the ConversationsViewHolder class. It
                   initializes all member variables

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */

        public ConversationsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.search_user_name);
            userBio = itemView.findViewById(R.id.search_user_bio);
            userProfileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
