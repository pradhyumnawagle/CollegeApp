package com.example.collegeapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/*

 NAME: GroupsFragment - This is the class that controls the fragment that holds chat groups list

 DESCRIPTION: When the groups tab is selected in the main page, this class controls the
                list of groups displayed.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */

public class GroupsFragment extends Fragment {


    private View groupsView;
    private ListView list_view;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> array_of_groups = new ArrayList<>();

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrUser;
    private DatabaseReference mDbRef;
    private DatabaseReference groupDbRef;
    private String mCollegeName;

    private String mCurrUserId;


    /*

        NAME: GroupsFragment::GroupsFragment() - Required empty public constructor

        SYNOPSIS: public GroupsFragment()

        DESCRIPTION: This function is an empty constructor for the GroupsFragment class.

        RETURNS: None

        AUTHOR: Pradhyumna Wagle

        DATE 9/26/2020

        */
    public GroupsFragment() {
        // Required empty public constructor
    }



    /*

     NAME: GroupsFragment::onCreateView() - Instantiates the user interface view in the Groups fragment.

     SYNOPSIS: public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
                inflater: The LayoutInflater object that can be used to inflate any views in the fragment
                container: this is the parent view that the fragment's UI should be attached to
                savedInstanceState: fragment is being re-constructed from a previous saved state as given here

     DESCRIPTION: The user's interface of the Groups fragment is instantiated in this function. The recycler view and the
                firebase user authentication is initialized here. The setOnItemClickListener event for
                each group in the list is also declared here.

     RETURNS: View that contains the layout. i.e. groupView.

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        groupsView =  inflater.inflate(R.layout.fragment_groups, container, false);

        groupDbRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        list_view = (ListView) groupsView.findViewById(R.id.groups_list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,array_of_groups);
        list_view.setAdapter(arrayAdapter);

        mAuth = FirebaseAuth.getInstance();
        mCurrUser = mAuth.getCurrentUser();
        if(mCurrUser!=null){
            mCurrUserId = mCurrUser.getUid();
            mDbRef = FirebaseDatabase.getInstance().getReference();
            ShowGroups();
        }






        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currGroupName = parent.getItemAtPosition(position).toString();
                Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                groupChatIntent.putExtra("groupName", currGroupName);
                startActivity(groupChatIntent);
            }
        });
        return groupsView;
    }

    /*

     NAME: GroupsFragment::ShowGroups() - Displays the list of groups in the group fragment.

     SYNOPSIS: private void ShowGroups()

     DESCRIPTION: The function displays the list of groups on the basis of the user's college name. It is
                    because each group is a child of a college name on which the user is present.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */

    private void ShowGroups() {

        mDbRef.child("Users").child(mCurrUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("ucollege").exists()) {
                    mCollegeName = dataSnapshot.child("ucollege").getValue().toString();
                    groupDbRef.child(mCollegeName).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Set<String> set = new HashSet<>();
                            Iterator iterator = dataSnapshot.getChildren().iterator();

                            while (iterator.hasNext()) {
                                set.add(((DataSnapshot) iterator.next()).getKey());
                            }

                            array_of_groups.clear();
                            array_of_groups.addAll(set);
                            arrayAdapter.notifyDataSetChanged();
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
}
