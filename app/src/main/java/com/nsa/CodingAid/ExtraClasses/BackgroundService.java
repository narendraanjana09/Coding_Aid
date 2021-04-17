package com.nsa.CodingAid.ExtraClasses;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.nsa.CodingAid.needHelpActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BackgroundService extends Service {
      FirebaseUser fuser=FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        new needHelpActivity().check();
        new Firebase().getReference_users().child(fuser.getUid()).child("online").setValue(false);
        new clearALlCall(getApplicationContext());

        new needHelpActivity().check();

        Log.e("ClearFromRecentService", "END");
        //Code here
        stopSelf();
    }
}