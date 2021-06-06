package com.example.collegeapp;

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

import de.hdodenhof.circleimageview.CircleImageView;

/*

 NAME: ConnectionsFragment - This is the class that controls the fragment that holds connections list

 DESCRIPTION: When the connection tab is selected in the main page, this class controls the
                list of connections displayed.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */


public class ConnectionsFragment extends Fragment {

    private View mConnectionsView;
    private RecyclerView mConnectionsRecyclerView;

    private DatabaseReference mDbConnectionsRef, mDbUsersRef;
    private FirebaseAuth mAuth;
    private String mCurrUserId;

    /*

         NAME: ConnectionsFragment::ConnectionsFragment() - Required empty public constructor

         SYNOPSIS: public ConnectionsFragment()

         DESCRIPTION: This function is an empty constructor for the ConnectionsFragment class.

         RETURNS: None

         AUTHOR: Pradhyumna Wagle

         DATE 9/26/2020

         */
    public ConnectionsFragment() {
        // Required empty public constructor
    }


    /*

     NAME: ConnectionsFragment::onCreateView() - Instantiates the user interface view in the Connections fragment.

     SYNOPSIS: public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
                inflater: The LayoutInflater object that can be used to inflate any views in the fragment
                container: this is the parent view that the fragment's UI should be attached to
                savedInstanceState: fragment is being re-constructed from a previous saved state as given here

     DESCRIPTION: The user's interface of the Connections fragment is instantiated in this function. The recycler view and the
                firebase user authentication is initialized here

     RETURNS: View that contains the layout i.e. mConnectionsView

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mConnectionsView =  inflater.inflate(R.layout.fragment_connections, container, false);
        mConnectionsRecyclerView = (RecyclerView) mConnectionsView.findViewById(R.id.connections_list);
        mConnectionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAuth = FirebaseAuth.getInstance();
        mCurrUserId = mAuth.getCurrentUser().getUid();

        mDbConnectionsRef = FirebaseDatabase.getInstance().getReference().child("Connections").child(mCurrUserId);
        mDbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        return mConnectionsView;
    }

    /*
    NAME: ConnectionsFragment::onStart() - Called when the fragment is visible to the user

    SYNOPSIS: public void onStart()

    DESCRIPTION: Gets the data from the list of connections and displays the users inside the inflated
                    view on the Connections fragment page under connections tab.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/26/2020

    */
    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(mDbConnectionsRef,Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,ConnectionsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ConnectionsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConnectionsViewHolder holder, int position, @NonNull Contacts model) {
                String userIds = getRef(position).getKey();
                mDbUsersRef.child(userIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("uimage")) {
                            String userImage = dataSnapshot.child("uimage").getValue().toString();
                            Picasso.get().load(userImage).placeholder(R.drawable.profile).into(holder.userProfileImage);
                        }
                            String userProfileName = dataSnapshot.child("uname").getValue().toString();
                            String userProfileBio = dataSnapshot.child("ubio").getValue().toString();

                            holder.userName.setText(userProfileName);
                            holder.userBio.setText(userProfileBio);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ConnectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_users_layout, parent,false);
                ConnectionsViewHolder connViewHolder = new ConnectionsViewHolder(view);
                return connViewHolder;
            }
        };

        mConnectionsRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    /*

 NAME: ConnectionsViewHolder - This is the class that renders the view that displays
                                      connections.

 DESCRIPTION: This class renders the list view that displays connections. It instantiates the
                fields from Recycler view in the connections fragment.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */

    private class ConnectionsViewHolder extends RecyclerView.ViewHolder  {

        TextView userName, userBio;
        CircleImageView userProfileImage;

        /*

     NAME: ConnectionsViewHolder::ConnectionsViewHolder() - Initializes member variables of the class

     SYNOPSIS: public ConnectionsViewHolder(@NonNull View itemView)
               itemView: an individual item from the view

     DESCRIPTION: This function is a constructor for the ConnectionsViewHolder class. It
                   initializes all member variables

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
        public ConnectionsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.search_user_name);
            userBio = itemView.findViewById(R.id.search_user_bio);
            userProfileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
