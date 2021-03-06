package com.nsa.CodingAid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nsa.CodingAid.Services.BackgroundService;
import com.nsa.CodingAid.ExtraClasses.Firebase;
import com.nsa.CodingAid.ExtraClasses.clearALlCall;
import com.nsa.CodingAid.Model.FirebaseModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nsa.CodingAid.Services.NetworkChangeReceiver;

import org.jitsi.meet.sdk.BroadcastIntentHelper;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    String sessionId;
    FirebaseUser fuser;
    FirebaseModel model;
    DatabaseReference reference_users,reference_fields ;
    String prevStarted = "prevStarted";
    private BroadcastReceiver NetworkChangeReceiver = null;

    private List<AdView> adViewList;
    private ReviewManager reviewManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reviewManager = ReviewManagerFactory.create(MainActivity.this);

        adViewList=new ArrayList<>();
         getAds();
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        reference_users=new Firebase().getReference_users();
        reference_fields=new Firebase().getReference_fields();
        sessionId = fuser.getUid();
        getlist();

        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new clearALlCall(getApplicationContext());
        startService(new Intent(getBaseContext(), BackgroundService.class));
        NetworkChangeReceiver = new NetworkChangeReceiver();
        broadcastIntent();

        SharedPreferences sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        if (!sharedpreferences.getBoolean(prevStarted, false)) {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(prevStarted, Boolean.TRUE);
            editor.apply();
            subscribeUserForNotification();
        }
    }



    public void showRateApp(View view) {
        com.google.android.play.core.tasks.Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = reviewManager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, log or handle the error code.
            }
        });
    }

    private void getAds() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        adViewList.add(findViewById(R.id.adView1));
        adViewList.add(findViewById(R.id.adView2));
        adViewList.add(findViewById(R.id.adView3));
        adViewList.add(findViewById(R.id.adView4));
        adViewList.add(findViewById(R.id.adView5));
        adViewList.add(findViewById(R.id.adView6));



        AdRequest adRequest = new AdRequest.Builder().build();
        for(AdView adView:adViewList){
            adView.loadAd(adRequest);
        }
    }

    private void subscribeUserForNotification() {
        FirebaseMessaging.getInstance().subscribeToTopic("IAmAHelper")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed To Notifications";
                        if (!task.isSuccessful()) {
                            msg = "Can't To Notifications";
                        }


                    }
                });
    }
    public void broadcastIntent() {
        registerReceiver(NetworkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    private void getlist() {
        reference_users.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    model = dataSnapshot.getValue(FirebaseModel.class);


                }else{
                    model=null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    @Override
    public void onResume() {
        super.onResume();
        broadcastIntent();
        reference_users.child(fuser.getUid()).child("online").setValue(false);
        Intent hangupBroadcastIntent = BroadcastIntentHelper.buildHangUpIntent();
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(hangupBroadcastIntent);
        if(model!=null){
            createChanges(false);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(NetworkChangeReceiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(model!=null){
            createChanges(false);
        }
    }

    private void createChanges(boolean b) {

        for(int i=0;i<model.getFields().size();i++){
            if(b){
                reference_fields.child(model.getFields().get(i)).child(model.getUid()).setValue(model.getName());
            }else{
                reference_fields.child(model.getFields().get(i)).child(model.getUid()).removeValue();
            }
        }

    }


    public void connect(View view) {

        if(sessionId.length()>0){

            createChanges(true);
            JitsiMeetUserInfo userInfo=new JitsiMeetUserInfo();
            Uri uri=fuser.getPhotoUrl();
            URL url= null;
            try {
                url = new URL(uri.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            userInfo.setAvatar(url);
            userInfo.setDisplayName(fuser.getDisplayName());

            JitsiMeetConferenceOptions options=
                    new JitsiMeetConferenceOptions.Builder()
                            .setUserInfo(userInfo)
                            .setRoom(sessionId)
                            .build();
            reference_users.child(fuser.getUid()).child("online").setValue(true);
            JitsiMeetActivity.launch(this,options);
        }

    }

    public void editFields(View view) {
        Intent intent = new Intent(MainActivity.this, helperActivity.class);
        intent.putExtra("edit", "true");
        intent.putExtra("verified", "true");
        startActivity(intent);
    }

}