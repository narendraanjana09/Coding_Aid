package com.nsa.CodingAid;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.nsa.CodingAid.ExtraClasses.Firebase;

import com.nsa.CodingAid.ExtraClasses.ProgressBar;
import com.nsa.CodingAid.ExtraClasses.clearALlCall;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


import com.nsa.CodingAid.Model.firebaseModel;
import com.nsa.CodingAid.Services.NetworkChangeReceiver;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class WelcomeActivity extends AppCompatActivity  implements PopupMenu.OnMenuItemClickListener  {
    private GoogleSignInAccount acc;
    private CircleImageView profileIMg;
    private TextView infoText;
    Button helperbtn;

    ProgressBar progressBar;


    private List<AdView> adViewList;

    DatabaseReference reference_users;
    public boolean fieldExist;
    boolean verified=false;
    FirebaseUser fuser;
    private BroadcastReceiver NetworkChangeReceiver = null;

    String prevStarted = "verified";
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        adViewList=new ArrayList<>();
        adViewList.add(findViewById(R.id.adView1));
        adViewList.add(findViewById(R.id.adView2));
        adViewList.add(findViewById(R.id.adView3));


        AdRequest adRequest = new AdRequest.Builder().build();
        for(AdView adView:adViewList){
            adView.loadAd(adRequest);
        }


        profileIMg=findViewById(R.id.profile_image);
        infoText=findViewById(R.id.infotxt);
        helperbtn=findViewById(R.id.helperBtn);

        progressBar=new ProgressBar(this,"checking details...");


        acc=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        fuser= FirebaseAuth.getInstance().getCurrentUser();


        infoText.setText("Welcome!\n"+acc.getDisplayName());
        infoText.setPaintFlags(infoText.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        Uri imageUri=acc.getPhotoUrl();
        Picasso.get().load(imageUri).into(profileIMg);



        reference_users = new Firebase().getReference_users();


        new clearALlCall(getApplicationContext());
        NetworkChangeReceiver = new NetworkChangeReceiver();
        broadcastIntent();
         sharedpreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);

    }
    public void broadcastIntent() {
        registerReceiver(NetworkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    protected void onResume() {
        super.onResume();
        broadcastIntent();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(NetworkChangeReceiver);
    }

    public void signOut(View view) {

        GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();

        Toast.makeText(this, " SignOut Successfull ", Toast.LENGTH_LONG).show();
        Intent intent=new Intent(WelcomeActivity.this,SignInActivity.class);
        startActivity(intent);
        this.finish();

    }

    public void helperActivity(View view) {
        if (sharedpreferences.getBoolean(prevStarted, false)){
            goToMainActivity();
        }else {
            progressBar.show();
            check();
        }
    }

    private void check() {

        reference_users.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    firebaseModel model=dataSnapshot.getValue(firebaseModel.class);
                    fieldExist=true;
                    if(model.isVerified()){
                        setSharedPrefernces();
                        verified=true;
                    }else if(model.getName()==null){
                        fieldExist=false;
                    }else{
                        verified=false;
                    }

                }else {
                    fieldExist = false;

                }

                progressBar.hide();
                nextActvity(fieldExist);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setSharedPrefernces() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(prevStarted, Boolean.TRUE);
        editor.apply();
    }

    public void gotoHelper(boolean b){

        Intent intent=new Intent(WelcomeActivity.this,helperActivity.class);
        intent.putExtra("edit", b+"");
        intent.putExtra("verified", "false");
        startActivity(intent);
    }
    public void goToMainActivity(){
        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void nextActvity(boolean fieldExist){
        if(fieldExist){
            if(verified) {
               goToMainActivity();
            }else{
                notVerified();

            }
        }else{
            gotoHelper(false);
        }
    }

    private void notVerified() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(WelcomeActivity.this);
        builder1.setMessage("You Are Not Verified Yet!\nWants to change fields or platform?");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        gotoHelper(true);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void needHelpActivity(View view) {
        Intent intent=new Intent(WelcomeActivity.this,needHelpActivity.class);
        startActivity(intent);

    }
    public void devloperbtn() {
        Intent intent=new Intent(WelcomeActivity.this,infoActivity.class);
        startActivity(intent);
    }

    public void showpopup(View view) {
        PopupMenu popup=new PopupMenu(this,view);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.top_menu);
        popup.show();

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){

            case R.id.infobtn : devloperbtn();
                return true;
            case R.id.clearCache : new clearALlCall(getApplicationContext());
                Toast.makeText(this, "cache cleared", Toast.LENGTH_SHORT).show();
                return true;

            default: return false;
        }
    }


}