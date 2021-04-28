package com.nsa.CodingAid.ExtraClasses;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.nsa.CodingAid.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;

    private boolean isShowing;

    public LoadingDialog(Activity activity) {
        this.activity = activity;


    }
    public void startLoadingDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater=activity.getLayoutInflater();

        builder.setView(layoutInflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(true);

        dialog=builder.create();
        dialog.show();
        isShowing=true;
        }
       public void dismissDialog(){
        dialog.dismiss();
        isShowing=false;
        }
        public boolean isShowing(){
        return isShowing;
        }
    }

