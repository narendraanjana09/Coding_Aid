package com.nsa.CodingAid.ExtraClasses;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBar {
    private ProgressDialog progressDialog;
    public ProgressBar(Context context,String message) {
        progressDialog = new ProgressDialog(context,ProgressDialog.THEME_DEVICE_DEFAULT_DARK);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }
    public void setMessage(String message){
        progressDialog.setMessage(message);
    }
    public void show(){
        progressDialog.show();
    }
    public void hide(){
        progressDialog.dismiss();
    }

}
