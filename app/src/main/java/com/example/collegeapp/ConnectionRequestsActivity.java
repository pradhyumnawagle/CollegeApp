package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
/*

 NAME: ConnectionRequestsActivity - This is the class that controls Connection Requests page

 DESCRIPTION: When the connection requests page is opened by the user, this class gets the
               information from users that have sent connection requests
                and displays them using a recycler view.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */
public class ConnectionRequestsActivity extends AppCompatActivity {


    private Toolbar mToolBar;
    private RecyclerView mConnectionsRecyclerList;
    private DatabaseReference mUsersDbRef, mConnectionsDbRef, mConnectionsRequestRef;

    private FirebaseAuth mAuth;
    private String mCurrUserId;

    private String mCurrUserName;


    /*

     NAME: ConnectionRequestsActivity::onCreate() - Initializes member variables of the class

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the connection requests page is loaded, this method is called.
                   The member variables are initialized, including the page recycler view.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection_requests);


        mAuth = FirebaseAuth.getInstance();
        mUsersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mConnectionsDbRef = FirebaseDatabase.getInstance().getReference().child("Connections");
        mConnectionsRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");

        mConnectionsRecyclerList = (RecyclerView) findViewById(R.id.connection_requests_recycler_list);
        mConnectionsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mCurrUserId = mAuth.getCurrentUser().getUid();

        mToolBar = (Toolbar) findViewById(R.id.connection_requests_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Connection Requests");

    }

    /*
    NAME: ConnectionRequestsActivity::onStart() - Gets the data from connection requests sent and
            received and displays the users with the request state accordingly.

    SYNOPSIS: protected void onStart()

    DESCRIPTION: From the list of all users, this function gets the information regarding request
                   sent or received  from the firebase database and displays the request status.
                   This method also handles the onCLick listener for each item in the view.
                   If connection request accepted, this function adds the user to the database as a
                   friend.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/26/2020

    */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(mConnectionsRequestRef
                        .child(mCurrUserId),Contacts.class)
                            .build();

        FirebaseRecyclerAdapter<Contacts, ConnectionRequestsViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ConnectionRequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ConnectionRequestsViewHolder holder, int position, @NonNull Contacts model) {


                //Toast.makeText(ConnectionRequestsActivity.this, "Cut address", Toast.LENGTH_SHORT).show();

                final String list_user_id = getRef(position).getKey();

                DatabaseReference typeDbRef = getRef(position).child("request_type").getRef();
                typeDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            String type = dataSnapshot.getValue().toString();
                            holder.itemView.findViewById(R.id.search_user_name).setVisibility(View.GONE);
                            holder.itemView.findViewById(R.id.search_user_bio).setVisibility(View.GONE);
                            holder.itemView.findViewById(R.id.users_profile_image).setVisibility(View.GONE);
                            if(type.equals("received")){
                                holder.itemView.findViewById(R.id.search_user_name).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.search_user_bio).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.users_profile_image).setVisibility(View.VISIBLE);
                                holder.itemView.findViewById(R.id.user_accept).setVisibility(View.VISIBLE);
                                mUsersDbRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("uimage")) {
                                            String userImage = dataSnapshot.child("uimage").getValue().toString();

                                            Picasso.get().load(userImage).placeholder(R.drawable.profile).into(holder.userProfileImage);
                                        }

                                        if (dataSnapshot.hasChild("uname")) {
                                            final String userProfileName = dataSnapshot.child("uname").getValue().toString();
                                            String userProfileBio = dataSnapshot.child("ubio").getValue().toString();

                                            // mCurrUserName = userProfileName;
                                            holder.userName.setText(userProfileName);
                                            holder.userBio.setText(userProfileBio);

                                            //}
                                            holder.acceptConnection.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    CharSequence options[] = new CharSequence[]{
                                                            "Accept", "Reject"
                                                    };

                                                    AlertDialog.Builder builder = new AlertDialog.Builder(ConnectionRequestsActivity.this);
                                                    builder.setTitle("Connect with " + userProfileName + " ?");
                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (0 == which) {
                                                                mConnectionsDbRef.child(mCurrUserId).
                                                                        child(list_user_id).
                                                                        child("Connections").
                                                                        setValue("Saved").
                                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    mConnectionsDbRef.child(list_user_id).
                                                                                            child(mCurrUserId).
                                                                                            child("Connections").
                                                                                            setValue("Saved").
                                                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        mConnectionsRequestRef.
                                                                                                                child(mCurrUserId).
                                                                                                                child(list_user_id).
                                                                                                                removeValue().
                                                                                                                addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                    @Override
                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                        if (task.isSuccessful()) {
                                                                                                                            mConnectionsRequestRef.child(list_user_id).
                                                                                                                                    child(mCurrUserId).
                                                                                                                                    removeValue().
                                                                                                                                    addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                        @Override
                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                Toast.makeText(ConnectionRequestsActivity.this, "Connection added to database!!!", Toast.LENGTH_SHORT).show();
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
                                                                            }
                                                                        });
                                                            }
                                                            if (1 == which) {
                                                                mConnectionsRequestRef.child(mCurrUserId).
                                                                        child(list_user_id).
                                                                        removeValue().
                                                                        addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    mConnectionsRequestRef.child(list_user_id).
                                                                                            child(mCurrUserId).
                                                                                            removeValue().
                                                                                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        Toast.makeText(ConnectionRequestsActivity.this, "Connection request removed!!!", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });

                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ConnectionRequestsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_users_layout,parent,false);
                ConnectionRequestsViewHolder holder = new ConnectionRequestsViewHolder(view);
                return holder;
            }
        };
        mConnectionsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }

    /*

 NAME: ConnectionRequestsViewHolder - This is the class that renders the view that displays
                                      connection requests.

 DESCRIPTION: This class renders the list view that displays connection requests. It instantiates the
                fields from Recycler view in the connections requests page.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */

    private class ConnectionRequestsViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userBio;
        CircleImageView userProfileImage;
        Button acceptConnection;
        /*

     NAME: ConnectionRequestsViewHolder::ConnectionRequestsViewHolder() - Initializes member variables of the class

     SYNOPSIS: public ConnectionRequestsViewHolder(@NonNull View itemView)
               itemView: an individual item from the view

     DESCRIPTION: This function is a constructor for the ConnectionRequestsViewHolder class. It
                   initializes all member variables

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */

        public ConnectionRequestsViewHolder(@NonNull View itemView) {

            super(itemView);
            userName = itemView.findViewById(R.id.search_user_name);
            userBio = itemView.findViewById(R.id.search_user_bio);
            userProfileImage = itemView.findViewById(R.id.users_profile_image);
            acceptConnection = itemView.findViewById(R.id.user_accept);

        }
    }
}
