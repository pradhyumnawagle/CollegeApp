package com.example.collegeapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/*

 NAME: AccountSettingsActivity - This is the class that controls the Account Settings Page

 DESCRIPTION: When the Account settings page loads after the user opens the page, this class
               displays the input fields for user image, user name and user bio

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */

public class AccountSettingsActivity extends AppCompatActivity {

    private Button mUpdate;
    private EditText mUserName, mUserBio;
    private CircleImageView mProfileImage;
    private Spinner collegeName;
    private TextView collegeString;
    private  String college;
    private static final int galleryPic = 1;

    private String mCurrUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRef;
    private StorageReference mUserProfileImageRef;
    private ProgressDialog mProgressBar;
    private String userImageUrl;

    private Toolbar mtoolBar;


    /*

     NAME: AccountSettingsActivity::onCreate() - Initializes member variables of the class and
           sets the onclick listener for update account

     SYNOPSIS: protected void onCreate(Bundle savedInstanceState)
                savedInstanceState: This bundle restores the previous state of the data stored

     DESCRIPTION: When the Account Settings page loads, this method is called.
                   The member variables are initialized, including the page layout toolbar.
                   The function also restores the users data into the input fields and sets
                   the onClick event listener for the image field.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mtoolBar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(mtoolBar);
        getSupportActionBar().setTitle("Account Settings");

        //Shows the up button whiuch links to main activity from the manifest file
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUpdate = (Button) findViewById(R.id.settings_update_btn);
        mUserName = (EditText) findViewById(R.id.settings_user_name);
        mUserBio = (EditText) findViewById(R.id.settings_user_bio);
        mProgressBar = new ProgressDialog(this);

        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        mUserProfileImageRef = FirebaseStorage.getInstance().getReference().child("Profile Images");

        mAuth = FirebaseAuth.getInstance();
        mCurrUserId = mAuth.getCurrentUser().getUid();
        mDbRef = FirebaseDatabase.getInstance().getReference();


        collegeName = (Spinner) findViewById(R.id.college_name);
        collegeString = (TextView) findViewById(R.id.college);

        collegeName.setVisibility(View.INVISIBLE);
        collegeString.setVisibility(View.INVISIBLE);

        List<String> collegeNames = new ArrayList<>();
        collegeNames.add("Ramapo");
        collegeNames.add("Rutgers");
        collegeNames.add("Montclair");
        collegeNames.add("Stevens");
        collegeNames.add("NJIT");

        ArrayAdapter<String> collegeAdapter = new ArrayAdapter<String>(AccountSettingsActivity.this,
                android.R.layout.simple_list_item_1,
                /*R.array.college_list*/
                collegeNames);

        collegeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        collegeName.setAdapter(collegeAdapter);

        mDbRef.child("Users").child(mCurrUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("ucollege").exists()){
                    college = dataSnapshot.child("ucollege").getValue().toString();
                    System.out.println(college);
                }
                else{
                    collegeName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(final AdapterView<?> parent, View view, int position, long id) {
                            college = (String) parent.getSelectedItem().toString();
                            // Log.i("College Name",college);
                            // Toast.makeText(AccountSettingsActivity.this, "College:" + college, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAccount();
            }
        });

        RestoreUserInfo();//Retrieve user data and fill the fields


        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, galleryPic);
            }
        });
    }

   /*

         NAME: AccountSettingsActivity::RestoreUserInfo() - Restores the user's data on the input
                     fields in the Account Settings page

         SYNOPSIS: private void RestoreUserInfo()


         DESCRIPTION:  The function gets the user's data from firebase database and restores the data
                        into the input fields in the account settings page.

         RETURNS: None

         AUTHOR: Pradhyumna Wagle

         DATE 9/26/2020

         */
    private void RestoreUserInfo() {
        mDbRef.child("Users").child(mCurrUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("uname") && dataSnapshot.hasChild("uimage")) ){
                    String restoredUsername = dataSnapshot.child("uname").getValue().toString();
                    String restoredBio = dataSnapshot.child("ubio").getValue().toString();
                    String restoredImage = dataSnapshot.child("uimage").getValue().toString();
                    userImageUrl = restoredImage;

                    mUserName.setText(restoredUsername);
                    mUserBio.setText(restoredBio);

                    Picasso.get().load(restoredImage).into(mProfileImage);
                    //Glide.with(AccountSettingsActivity.this).load(userImageUrl).into(mProfileImage);


                } else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("uname"))) {
                    String restoredUsername = dataSnapshot.child("uname").getValue().toString();
                    String restoredBio = dataSnapshot.child("ubio").getValue().toString();

                    mUserName.setText(restoredUsername);
                    mUserBio.setText(restoredBio);
                }else if(dataSnapshot.exists() && dataSnapshot.hasChild("uimage")){
                    String username = mUserName.getText().toString();
                    String userBio = mUserBio.getText().toString();

                    mUserName.setText(username);
                    mUserBio.setText(userBio);
                    collegeName.setVisibility(View.VISIBLE);
                    collegeString.setVisibility(View.VISIBLE);


                    String restoredImage = dataSnapshot.child("uimage").getValue().toString();
                    userImageUrl = restoredImage;
                    Picasso.get().load(restoredImage).into(mProfileImage);

                } else {
                    collegeName.setVisibility(View.VISIBLE);
                    collegeString.setVisibility(View.VISIBLE);
                   // Toast.makeText(AccountSettingsActivity.this, "Please update your profile information!!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /*

     NAME: AccountSettingsActivity::UpdateAccount() - Updates the users information in the database

     SYNOPSIS: private void UpdateAccount()


     DESCRIPTION:  The function gets the user's data from input fields and updates the data
                    into the firebase database.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    private void UpdateAccount() {
        //---------------------------
        // -------------------------

        String username = mUserName.getText().toString();
        String userBio = mUserBio.getText().toString();
       // System.out.println(college);

        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, "Please add a username...", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(userBio)){
            Toast.makeText(this, "Please add a bio..", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> userProfile = new HashMap<>();
            userProfile.put("uid", mCurrUserId);
            userProfile.put("uname", username);
            userProfile.put("ucollege",college);
            userProfile.put("ubio", userBio);
            userProfile.put("uimage",userImageUrl);
            mDbRef.child("Users").child(mCurrUserId).updateChildren(userProfile).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       mainActivityIntent();
                       Toast.makeText(AccountSettingsActivity.this, "Update Successful!!!" + college, Toast.LENGTH_SHORT).show();
                    } else{
                       String message = task.getException().toString();
                       Toast.makeText(AccountSettingsActivity.this, "Update Error: " + message, Toast.LENGTH_SHORT).show();
                   }
                }
            });
        }
    }



    /*

     NAME: AccountSettingsActivity::mainActivityIntent() - Sets the intent to the main page of the
            application

     SYNOPSIS: private void mainActivityIntent()

     DESCRIPTION:  The function sets the intent to main activity so whenever the function is called,
                    the main page of the application loads.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    private void mainActivityIntent(){
        Intent mainIntent = new Intent(AccountSettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    /*

     NAME: AccountSettingsActivity::onActivityResult() - when user wants to select an image from
            gallery, this function allows them to choose an image

     SYNOPSIS: protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
                requestCode: A unique request code, an integer value, that is sent from
                            startActivityForResult() inside the onCreate() method.
                resultCode: A unique result code, returned through setResult() by child activity
                data: Intent to the Gallery, which returns the image data

     DESCRIPTION:  The function gets called when an image is added or updated by the user. The gallery
                    from phone is accessed and image can be selected after options to edit the image.

     RETURNS: None

     AUTHOR: Pradhyumna Wagle

     DATE 9/26/2020

     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==galleryPic && resultCode==RESULT_OK && data!= null){
            Uri ImageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){
                Uri resultUri = result.getUri();
                //mProgressBar.setTitle("Profile picture upload");
                //mProgressBar.setMessage("Profile image is uploading...");
                mProgressBar.setCanceledOnTouchOutside(false);
                mProgressBar.show();


                StorageReference imagePath = mUserProfileImageRef.child(mCurrUserId + ".jpg");
                imagePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(AccountSettingsActivity.this, "Profile Image uploaded Successfully!!!", Toast.LENGTH_SHORT).show();

                            Task<Uri> result = task.getResult().getStorage().getDownloadUrl();
                            result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String imageUrl = uri.toString();
                                    mDbRef.child("Users").child(mCurrUserId).child("uimage").setValue(imageUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                mProgressBar.dismiss();
                                                Toast.makeText(AccountSettingsActivity.this, "Image stored successfully!!", Toast.LENGTH_SHORT).show();
                                                userImageUrl = imageUrl;
                                                //RestoreUserInfo();
                                            } else{
                                                String message = task.getException().toString();
                                                Toast.makeText(AccountSettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                mProgressBar.dismiss();
                                            }
                                        }
                                    });

                                }
                            });

                        } else{
                            String message = task.getException().toString();
                            Toast.makeText(AccountSettingsActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            mProgressBar.dismiss();
                        }
                    }
                });
            }
        }

    }
}
