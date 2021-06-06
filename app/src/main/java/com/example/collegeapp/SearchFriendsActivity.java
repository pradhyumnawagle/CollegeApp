package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.util.ScopeUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/*

 NAME: SearchFriendsActivity - This is the class that controls the Search Friends page

 DESCRIPTION: When the user selects Search Friends, this class displays the list of users who are
              available to connect with the user.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */

public class SearchFriendsActivity extends AppCompatActivity {

    private Toolbar mToolBar;
    private RecyclerView mSearchFriendsRecyclerList;
    private DatabaseReference mUsersDbRef;

    private FirebaseAuth mAuth;
    private String mCurrUserId;

    /*

     NAME: SearchFriendsActivity::onCreate() - Initializes member variables of the class

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the search friends page loads, this function is called. It initializes all member
                  variables in the class.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_friends);

        mUsersDbRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mSearchFriendsRecyclerList = (RecyclerView) findViewById(R.id.search_friends_recycler_list);
        mSearchFriendsRecyclerList.setLayoutManager(new LinearLayoutManager(this));

        mAuth = FirebaseAuth.getInstance();
        mCurrUserId = mAuth.getCurrentUser().getUid();

        mToolBar = (Toolbar) findViewById(R.id.search_friends_toolbar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search Users");

    }

    /*
    NAME: SearchFriendsActivity::onStart() - Retrieves the data of available users and displays in a
                                            view when the page is visible

    SYNOPSIS: protected void onStart()

    DESCRIPTION: When the Search Friends page is visible to the user, this function retrieves the
                 data from all users who are able to connect i.e. from the same college and displays
                 them in the Recycler list using a model class and a view holder. This function also
                 declares the setOnClickListener event on the items displayed in the list.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(mUsersDbRef, Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts, SearchFriendViewHolder> adapter = new FirebaseRecyclerAdapter<Contacts, SearchFriendViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final SearchFriendViewHolder holder, final int position, @NonNull final Contacts model) {

                holder.userName.setVisibility(View.GONE);
                holder.userBio.setVisibility(View.GONE);
                holder.userProfileImage.setVisibility(View.GONE);

               mUsersDbRef.child(mCurrUserId).addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       if(dataSnapshot.hasChild("ucollege")) {
                           String college = dataSnapshot.child("ucollege").getValue().toString();
                         //  System.out.println(model.getUname() + " OutCollege " + model.getUcollege() + " Current:" + college);
                        //   Log.d("User and College", model.getUname()+" "+model.getUcollege());
                           if(model.getUcollege().equals(college)){
                               //System.out.println(model.getUname() + " InCollege " + model.getUcollege() + " Current:" + college);
                               holder.userName.setVisibility(View.VISIBLE);
                               holder.userBio.setVisibility(View.VISIBLE);
                               holder.userProfileImage.setVisibility(View.VISIBLE);

                               holder.userName.setText(model.getUname());
                               holder.userBio.setText(model.getUbio());
                               Picasso.get().load(model.getUimage()).placeholder(R.drawable.profile).into(holder.userProfileImage);

                               holder.itemView.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       String profile_user_id = getRef(position).getKey();

                                       Intent profileIntent = new Intent(SearchFriendsActivity.this, ProfileActivity.class);
                                       profileIntent.putExtra("profile_user_id", profile_user_id);
                                       startActivity(profileIntent);
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
            public SearchFriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_users_layout, parent, false);
                SearchFriendViewHolder viewHolder = new SearchFriendViewHolder(view);
                return viewHolder;
            }
        };

        mSearchFriendsRecyclerList.setAdapter(adapter);
        adapter.startListening();
    }


    /*

 NAME: SearchFriendViewHolder - This is the holder class renders the Recycler view of the users

 DESCRIPTION: This class renders the list view that displays all users. It instantiates the
                fields from Recycler view that displays the users.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
    private class SearchFriendViewHolder extends RecyclerView.ViewHolder {

        TextView userName, userBio;
        CircleImageView userProfileImage;

         /*

     NAME: SearchFriendViewHolder::SearchFriendViewHolder() - Initializes member variables of the class

     SYNOPSIS: public SearchFriendViewHolder(@NonNull View itemView)
               itemView: an individual item from the view

     DESCRIPTION: This function is a constructor for the SearchFriendViewHolder class. It
                   initializes all member variables

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */

        public SearchFriendViewHolder(@NonNull View itemView) {

            super(itemView);
            userName = itemView.findViewById(R.id.search_user_name);
            userBio = itemView.findViewById(R.id.search_user_bio);
            userProfileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
