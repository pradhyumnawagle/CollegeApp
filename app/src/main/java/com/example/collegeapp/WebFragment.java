package com.example.collegeapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*

 NAME: WebFragment - This is the class that controls the fragment that holds the college's website

 DESCRIPTION: When the website tab is selected in the main page, this class controls the
                website displayed.

 AUTHOR: Pradhyumna Wagle

 DATE 9/26/2020

 */

public class WebFragment extends Fragment {


    private WebView mWebView;
    private View webView;
    private DatabaseReference mDbConnectionsRef, mDbUsersRef, mDbConversationsRef;
    private FirebaseAuth mAuth;
    private String mCurrUserId;
    private String mUrl;


    /*

        NAME: WebFragment::WebFragment() - Required empty public constructor

        SYNOPSIS: public WebFragment()

        DESCRIPTION: This function is an empty constructor for the WebFragment class.

        RETURNS: None

        AUTHOR: Pradhyumna Wagle

        DATE 9/27/2020

        */
    public WebFragment(){

    }

    /*

     NAME: WebFragment::onCreateView() - Instantiates the user interface view in the Web fragment.

     SYNOPSIS: public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
                inflater: The LayoutInflater object that can be used to inflate any views in the fragment
                container: this is the parent view that the fragment's UI should be attached to
                savedInstanceState: fragment is being re-constructed from a previous saved state as given here

     DESCRIPTION: The user's interface of the Web fragment is instantiated in this function. The recycler view and the
                firebase user authentication is initialized here. The function basically displays the
                official website of teh college associated with the user.

     RETURNS: View that contains the layout. i.e. webView.

     AUTHOR: Pradhyumna Wagle

     DATE 9/27/2020

     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        webView =  inflater.inflate(R.layout.fragment_web, container, false);

        mWebView = (WebView) webView.findViewById(R.id.website_view);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null) {
            mCurrUserId = mAuth.getCurrentUser().getUid();
        }

        mDbUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mDbUsersRef.child(mCurrUserId).child("ucollege").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String college = dataSnapshot.getValue().toString();
                college  = college.toLowerCase();
                mUrl = "https://www." + college + ".edu";
                System.out.println(mUrl);

                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setLoadWithOverviewMode(true);
                mWebView.getSettings().setUseWideViewPort(true);
                mWebView.getSettings().setBuiltInZoomControls(true);
                mWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                mWebView.setWebViewClient(new WebViewClient());

                mWebView.loadUrl(mUrl);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ;

        return webView;
    }
}
