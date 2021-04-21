package com.nsa.CodingAid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.nsa.CodingAid.Adapter.NeedHelpAdapter;
import com.nsa.CodingAid.Services.BackgroundService;
import com.nsa.CodingAid.ExtraClasses.Firebase;
import com.nsa.CodingAid.ExtraClasses.LoadingDialog;
import com.nsa.CodingAid.ExtraClasses.clearALlCall;
import com.nsa.CodingAid.Model.availableFieldModel;
import com.nsa.CodingAid.Model.firebaseModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class needHelpActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    TextView infoTxt;
    DatabaseReference reference_fields=new Firebase().getReference_fields()
            ,reference_users=new Firebase().getReference_users();
    firebaseModel model1,model2;
    NeedHelpAdapter needHelpAdapter;
    List<availableFieldModel> list=new ArrayList<>();
    List<String> allreadyConnList=new ArrayList<>();//All Ready Connected teacher list
    boolean teacherOnline=false;

    FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();;

    int count=0;

    List<AlertDialog> alertList=new ArrayList<>();
    LoadingDialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);

        infoTxt=findViewById(R.id.infoTextView);
        reference_users.child(fuser.getUid()).child("connectedTeacher").setValue("null");



        recyclerView=findViewById(R.id.need_help_rv);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        needHelpAdapter= new NeedHelpAdapter(needHelpActivity.this,list);
        recyclerView.setAdapter(needHelpAdapter);

        getAvailableList();
        try {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        new Firebase().getReference_users().child(fuser.getUid()).child("online").setValue(true);

        new clearALlCall(getApplicationContext());
        startService(new Intent(getBaseContext(), BackgroundService.class));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void getAvailableList() {

        reference_fields.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    list.clear();
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        List<String> users=new ArrayList<>();
                        for(DataSnapshot snap : childSnapshot.getChildren()){
                            users.add(snap.getKey());
                        }
                        availableFieldModel model=new availableFieldModel(childSnapshot.getKey(),users);
                        list.add(model);
                        infoTxt.setText("😄 Teacher Available");
                    }

                }else{
                    infoTxt.setText("☹ No Teacher Available");
                    list.clear();

                }
                needHelpAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public  void searchField(availableFieldModel subjectName, Context context) {
        loadingDialog=new LoadingDialog((Activity) context);
        loadingDialog.startLoadingDialog();
        getAlreadyConnecTeacherList(subjectName,context,loadingDialog);

    }
    private void connectField(availableFieldModel subjectName, Context context) {
        loadingDialog= new LoadingDialog((Activity)context);
        loadingDialog.startLoadingDialog();
        boolean connected=false;

        for(int i=0;i<subjectName.getAvaiableList().size();i++){
            String key=subjectName.getAvaiableList().get(i);
            if(allreadyConnList.contains(key)){
                connected=false;
                continue;
            }else{
                loadingDialog.dismissDialog();
                connected=true;
                connectToJitsi(key, context);
                break;
            }
        }
        if(!connected){

            for(int i=0;i<subjectName.getAvaiableList().size();i++){
                String key=subjectName.getAvaiableList().get(i);
                if(allreadyConnList.contains(key)){

                    loadingDialog.dismissDialog();
                    wanttoConnect(context,key);

                }
            }
        }

    }

    private void wanttoConnect(Context context, String key) {


        count++;

        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage("Would You Like to Connect To \n Same Teacher You Connected earlier          " + count);
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        connectToJitsi(key, context);
                        count--;
                        dialog.cancel();
                        dismissAll();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        count--;
                        dialog.cancel();

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
        alertList.add(alert11);

    }

    private void dismissAll() {
        count=0;
        for(AlertDialog alertDialog:alertList){
            alertDialog.dismiss();
        }
    }


    private void getAlreadyConnecTeacherList(availableFieldModel subjectName, Context context, LoadingDialog loadingDialog) {
        reference_users.child(fuser.getUid()).child("connectedWith").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String key="";

                    for (DataSnapshot childDataSnapshot : snapshot.getChildren()) {
                        key = childDataSnapshot.getKey();
                        allreadyConnList.add(key);
                    }


                }else{

                    allreadyConnList.clear();
                }
                loadingDialog.dismissDialog();
                connectField(subjectName,context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




    @Override
    protected void onResume() {
        super.onResume();
        new clearALlCall(getApplicationContext());
        check();
    }

    public void check() {
        reference_users.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    model1 = dataSnapshot.getValue(firebaseModel.class);
                    if(!model1.getConnectedTeacher().equals("null")){
                        checkOnline(model1.getConnectedTeacher());
                        getList(model1.getConnectedTeacher(),false);

                    }


                }else{
                    model1=null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void checkOnline(String connectedTeacher) {
        reference_users.child(connectedTeacher).child("online").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    teacherOnline= (boolean) snapshot.getValue();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createChanges(boolean delete,firebaseModel model) {
        if(!delete) {
            reference_users.child(fuser.getUid()).child("connectedTeacher").setValue("null");
        }
        for(int i=0;i<model.getFields().size();i++){
            if(!delete){
                if(teacherOnline) {
                    reference_fields.child(model.getFields().get(i)).child(model.getId()).setValue("available");
                }}else{
                if(!teacherOnline){
                    reference_fields.child(model.getFields().get(i)).child(model.getId()).removeValue();
                }}
        }

    }
    private void connectToJitsi(String key, Context context) {



        reference_users.child(fuser.getUid()).child("connectedWith").child(key).setValue(true);
        reference_users.child(fuser.getUid()).child("connectedTeacher").setValue(key);
        getList(key,true);


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
                        .setRoom(key)
                        .build();
        JitsiMeetActivity.launch(context,options);





    }

    private void getList(String key,boolean delete) {
        reference_users.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    model2 = dataSnapshot.getValue(firebaseModel.class);
                    createChanges(delete,model2);


                }else{
                    model2=null;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



}