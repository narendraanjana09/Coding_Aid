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
import android.widget.Toast;

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
import java.util.concurrent.ThreadLocalRandom;

public class needHelpActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    TextView infoTxt;
    DatabaseReference reference_fields=new Firebase().getReference_fields()
            ,reference_users=new Firebase().getReference_users();
    firebaseModel model1,model2;
    NeedHelpAdapter needHelpAdapter;
    List<availableFieldModel> list=new ArrayList<>();

    boolean teacherOnline=false;

    FirebaseUser fuser= FirebaseAuth.getInstance().getCurrentUser();;


    LoadingDialog loadingDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_need_help);

        infoTxt=findViewById(R.id.infoTextView);
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

    public  void searchField(availableFieldModel model, Context context) {
        loadingDialog=new LoadingDialog((Activity) context);
        loadingDialog.startLoadingDialog();
        connnectToRandom(model,context,loadingDialog);


    }
    public int getRandomValue(int Max)
    {
        return ThreadLocalRandom
                .current()
                .nextInt(0, Max + 1);
    }
    private void connnectToRandom(availableFieldModel model, Context context, LoadingDialog loadingDialog) {
        int random=getRandomValue(model.getAvaiableList().size()-1);
        String key=model.getAvaiableList().get(random);
        loadingDialog.dismissDialog();
        connectToJitsi(key,context);

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
                    if(!(model1.getConnectedTeacher() ==null) &&!model1.getConnectedTeacher().equals("null")){
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
        reference_users.child(connectedTeacher).child("online").addListenerForSingleValueEvent(new ValueEventListener() {
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

        for(int i=0;i<model.getFields().size();i++){
            if(!delete){
                if(teacherOnline) {
                    reference_fields.child(model.getFields().get(i)).child(model.getId()).setValue(model.getName());
                }}else{
                if(!teacherOnline){
                    reference_fields.child(model.getFields().get(i)).child(model.getId()).removeValue();
                }}
        }


    }
    private void connectToJitsi(String key, Context context) {



        getList(key,true);
        reference_users.child(fuser.getUid()).child("connectedTeacher").setValue(key);

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