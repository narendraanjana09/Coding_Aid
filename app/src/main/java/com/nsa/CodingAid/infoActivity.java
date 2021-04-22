package com.nsa.CodingAid;

import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.nsa.CodingAid.ExtraClasses.clearALlCall;

import java.util.ArrayList;
import java.util.List;

public class infoActivity extends AppCompatActivity {
    TextView textView;
    private List<AdView> adViewList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        textView=findViewById(R.id.nameTextView);
        textView.setPaintFlags(textView.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        new clearALlCall(getApplicationContext());
        adViewList=new ArrayList<>();
        getAds();
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



        AdRequest adRequest = new AdRequest.Builder().build();
        for(AdView adView:adViewList){
            adView.loadAd(adRequest);
        }
    }



    public void linkLinkedIn(View view) {
        Uri uri = Uri.parse("https://www.linkedin.com/in/narendra-singh-aanjna-454bb6190");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void linkInstagram(View view) {
        Uri uri = Uri.parse("https://www.instagram.com/narendra_aanjna_09");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
    public void linkTwitter(View view) {
        Uri uri = Uri.parse("https://twitter.com/AanjanaNarendr");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}