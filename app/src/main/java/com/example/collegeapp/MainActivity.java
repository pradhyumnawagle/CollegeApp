package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/*

 NAME: MainActivity - This is the main class that handles the dashboard of the app

 DESCRIPTION: When the user logs in, the main dashboard that includes all fragments and menu
              options is controlled by the class.

 AUTHOR: Pradhyumna Wagle

 DATE 9/27/2020

 */
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    private Toolbar mtoolBar;
    private String mCurrentUserId;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    private TabsAccessorAdapter mTabsAccessorAdapter;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavView;

    /*

      NAME: MainActivity::mainPageIntent() - Sets the intent to the initial page of the
                                             application

      SYNOPSIS: private void mainPageIntent()

      DESCRIPTION:  The function sets the intent to initial activity so whenever the function is called,
                     the initial page of the application loads where the user is not logged in

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */
    private void mainPageIntent() {
        Intent initialIntent = new Intent(MainActivity.this, InitialActivity.class);
       // initialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(initialIntent);

        //Leaving the finish results in the program closing when the back button in the toolbar is pressed
        //finish();
    }


    /*

      NAME: MainActivity::settingsPageIntent() - Sets the intent to the account settings page of the
                                                application

      SYNOPSIS: private void settingsPageIntent()

      DESCRIPTION:  The function sets the intent to account settings activity so whenever the function
                    is called, the user can see account settings page.

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */
    private void settingsPageIntent(){
        Intent settingsIntent = new Intent(MainActivity.this, AccountSettingsActivity.class);
       // settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        //finish();
    }


    /*

     NAME: MainActivity::onCreate() - Initializes member variables of the class sets the views in place.

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the user is logged in, the dashboard loads and this function is called. It
                   initializes all member variables including navigation views and menu items and fragments.
                   Also, the function listens to items selected from the navigation bar.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDbRef = FirebaseDatabase.getInstance().getReference();
        if(mAuth.getCurrentUser() != null) {
            mCurrentUserId = mAuth.getCurrentUser().getUid();
        }

        mtoolBar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setTitle("My College");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //--------------
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        //----------------------------------

        mViewPager = (ViewPager) findViewById(R.id.main_tabs_pager);
        mTabsAccessorAdapter = new TabsAccessorAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mTabsAccessorAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mNavView = (NavigationView) findViewById(R.id.nav_view);
        mNavView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id){
                    case R.id.main_page_search_user:
                        searchFriendsIntent();
                        break;
                    case R.id.main_page_connection_requests:
                        ShowConnectionRequests();
                        break;
                    case R.id.main_page_create_group:
                        CreateNewGroup();
                        break;
                }
                return false;
            }
        });

    }

    /*

     NAME: MainActivity::onPostCreate() - Called when activity start-up is complete

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: This function is called when activity start-up is complete. It intends to do the
                   final initialization after application code runs.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mToggle.syncState();
    }


    /*
    NAME: MainActivity::onStart() - Checks the status of the user everytime main page loads

    SYNOPSIS: protected void onStart()

    DESCRIPTION: The function checks the status of the user. If user is logged in and is a valid
                 user, it loads main page. Else, it will validate the user and send back to login
                 or initial page.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null){
           mainPageIntent();
        }
        else{
            updateUserBio("online");
            ValidateUser();
        }
    }

    /*
    NAME: MainActivity::onStop() - Called when the activity is no longer visible to the user

    SYNOPSIS: protected void onStop()

    DESCRIPTION: The function is called when the activity is no longer visible to the user. If any other
                  activity loads, this will change the status of the user to offline.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();


        if(currentUser!=null){
            updateUserBio("offline");
        }

    }

    /*
    NAME: MainActivity::onDestroy() - Called before the activity is destroyed

    SYNOPSIS: protected void onDestroy()

    DESCRIPTION: The function is called before the activity is destroyed. This can happen if the
                  activity is finishing or getting temporarily destroyed.

    RETURNS: None

    AUTHOR: Pradhyumna Wagle

    DATE 9/27/2020

    */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            updateUserBio("offline");
        }
    }

    /*
   NAME: MainActivity::ValidateUser() - Checks if the user has valid username and image

   SYNOPSIS: private void ValidateUser()

   DESCRIPTION: The function is called when the user logs in for the first time. It checks if the user
                   has a valid username, bio and a disolay picture. If not, this will send back to
                   account settings page.

   RETURNS: None

   AUTHOR: Pradhyumna Wagle

   DATE 9/27/2020

   */
    private void ValidateUser() {
        String currUserId = mAuth.getCurrentUser().getUid();
        mDbRef.child("Users").child(currUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("uname").exists())){
                    String name = dataSnapshot.child("uname").getValue().toString();
                    getSupportActionBar().setTitle("Hi " +name);

                    if(dataSnapshot.child("uimage").exists()) {
                        String userImage = dataSnapshot.child("uimage").getValue().toString();
                        View headerView = mNavView.getHeaderView(0);
                        Picasso.get().load(userImage).placeholder(R.drawable.profile).into((ImageView) headerView.findViewById(R.id.navigation_user_image));
                    }

                } else{
                    settingsPageIntent();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    /*
   NAME: MainActivity::onCreateOptionsMenu() - Initializes the contents of the activity's standard menu

   SYNOPSIS: public boolean onCreateOptionsMenu(Menu menu)
            menu: The options menu where menu items are placed

   DESCRIPTION: The function initializes the contents of the activity's standard menu. After the menu
                items have been passed through the Menu parameter, the contents are initialized.

   RETURNS: Boolean True when after the options menu is created.

   AUTHOR: Pradhyumna Wagle

   DATE 9/27/2020

   */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    /*
   NAME: MainActivity::onOptionsItemSelected() - Sets what to do when menu items are selected.

   SYNOPSIS: public boolean onOptionsItemSelected(@NonNull MenuItem item)
            item: Each item on the menu list

   DESCRIPTION: The function is called when an item is selected from the menu. After an item is selected,
                it sets the intent for each menu item and sends to the declared intent.

   RETURNS: Boolean True when after an item is selected and intent is loaded.

   AUTHOR: Pradhyumna Wagle

   DATE 9/27/2020

   */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_page_logout_btn){

            updateUserBio("offline");
            FirebaseAuth.getInstance().signOut();
            mainPageIntent();
        }

        if(item.getItemId() == R.id.main_page_account_settings_btn ){
            settingsPageIntent();
        }
        return true;
    }

    /*

      NAME: MainActivity::ShowConnectionRequests() - Sets the intent to the connection requests  page of the
                                                application

      SYNOPSIS: private void ShowConnectionRequests()

      DESCRIPTION:  The function sets the intent to connection requests page of the application.

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */
    private void ShowConnectionRequests() {
        Intent searchIntent = new Intent(MainActivity.this, ConnectionRequestsActivity.class);
        startActivity(searchIntent);
    }


    /*

      NAME: MainActivity::searchFriendsIntent() - Sets the intent to the search friends page of the
                                                application

      SYNOPSIS: private void searchFriendsIntent()

      DESCRIPTION:  The function sets the intent to the search friends page of the application.

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */
    private void searchFriendsIntent() {
        Intent searchIntent = new Intent(MainActivity.this, SearchFriendsActivity.class);
        startActivity(searchIntent);
    }


    /*

      NAME: MainActivity::CreateNewGroup() - Creates a new group

      SYNOPSIS: private void CreateNewGroup()

      DESCRIPTION:  The function is called when Create GFroup is selected by the user from the
                    navigation window. The function asks group name as input in a dialog box and if
                    valid, adds the group into the firebase database.

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */
    private void CreateNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle("Group name: ");

        final EditText groupNameField = new EditText(MainActivity.this);
        groupNameField.setHint("e.g Ramapo College");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String group_name = groupNameField.getText().toString();
                if(TextUtils.isEmpty(group_name)){
                    Toast.makeText(MainActivity.this, "Please enter a Group Name!!!", Toast.LENGTH_SHORT).show();
                } else{
                    mDbRef.child("Users").child(mCurrentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String collegeName = dataSnapshot.child("ucollege").getValue().toString();
                            CreateGroup(group_name,collegeName);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.cancel();
            }
        });

        builder.show();

    }

    /*

      NAME: MainActivity::CreateGroup() - Creates a new group in the database

      SYNOPSIS: private void CreateGroup(final String group_name, final String college_name)
                group_name: The name of the group
                college_name: The name of the college in which a group is created

      DESCRIPTION:  The function is called inside the CreateNewGroup function. It takes the name
                    of the group and name of the college as argument and stores the
                    group as a child of the college under Groups in the firebase database.

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */

    private void CreateGroup(final String group_name, final String college_name) {
        mDbRef.child("Groups").child(college_name).child(group_name).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()){
                   Toast.makeText(MainActivity.this, group_name + " has been created successfully!!!", Toast.LENGTH_SHORT).show();
               }
            }
        });
    }

    /*

      NAME: MainActivity::updateUserBio() - Updates the status of the user

      SYNOPSIS: updateUserBio(String state)
                state: It is the state of the user (online/offline) as a string value

      DESCRIPTION:  The function updates the status of the user. When teh status changes from
                    online to offline or vice versa, it is passed as a parameter. The function then
                    updates in database the status along with date and time as timestamp.

      RETURNS: None

      AUTHOR: Pradhyumna Wagle

      DATE 9/27/2020

      */
    private void updateUserBio(String state){
        String currentTime, currentDate;

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
        currentDate = date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm a");
        currentTime = time.format(calendar.getTime());

        HashMap<String,Object> onlineState = new HashMap<>();
        onlineState.put("time",currentTime);
        onlineState.put("date",currentDate);
        onlineState.put("state",state);

        mCurrentUserId = mAuth.getCurrentUser().getUid();

        mDbRef.child("Users").child(mCurrentUserId).child("userState").updateChildren(onlineState);

    }
}
