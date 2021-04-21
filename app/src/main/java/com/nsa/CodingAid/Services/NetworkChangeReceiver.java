package com.nsa.CodingAid.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.nsa.CodingAid.ExtraClasses.NetworkUtil;

public class NetworkChangeReceiver  extends BroadcastReceiver {

       boolean connected=false;
    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean status = NetworkUtil.getConnectivityStatusString(context);


        if(!status) {
           connected=true;

            Toast.makeText(context, "No Internet Connection!🥱", Toast.LENGTH_LONG).show();
        }else{
        if(connected){

            Toast.makeText(context, "Connected!😊", Toast.LENGTH_LONG).show();
            connected=false;
        }

    }}
}