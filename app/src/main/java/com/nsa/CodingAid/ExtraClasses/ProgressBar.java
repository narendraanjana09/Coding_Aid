package com.nsa.CodingAid.ExtraClasses;

import android.app.ProgressDialog;
import android.content.Context;

public class ProgressBar {
    private ProgressDialog progressDialog;
    public ProgressBar(Context context,String content) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(content);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
    }
    public void show(){
        progressDialog.show();
    }
    public void hide(){
        progressDialog.dismiss();
    }

}
