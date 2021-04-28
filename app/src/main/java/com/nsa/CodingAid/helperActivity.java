package com.nsa.CodingAid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nsa.CodingAid.Adapter.CustomAdapter;
import com.nsa.CodingAid.Adapter.PlatformSpinnerAdapter;
import com.nsa.CodingAid.ExtraClasses.Firebase;
import com.nsa.CodingAid.ExtraClasses.clearALlCall;
import com.nsa.CodingAid.Model.PlatformInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.nsa.CodingAid.Model.FieldsModel;
import com.nsa.CodingAid.Model.FirebaseModel;
import com.google.firebase.database.ValueEventListener;

public class helperActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private TextView itemCounttxt;
    public ArrayList<String> selectedList = new ArrayList<>();
    private ArrayList<FieldsModel> adapterlist=new ArrayList<>();
    private CustomAdapter customAdapter;

    DatabaseReference reference_users,reference_fields;
    private FirebaseUser fuser;
    FloatingActionButton uploadButton,cancelButton;
    public int counter=0;

    LinearLayout platform_view;
    TextView platform_textview;
    EditText platform_edittext;

    boolean needEdit=false;
    boolean verifed;

    String selectedPlatformName="";

    EditText platformOtherEditText;
    RelativeLayout Pf_cancel_button;


    Spinner selectPFSpinner;
    ArrayList<String> platformsList;
    PlatformSpinnerAdapter spinnerAdapter;
    FirebaseModel model1;

    private String user_token="";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        fuser=FirebaseAuth.getInstance().getCurrentUser();

        reference_users=new Firebase().getReference_users();
        reference_fields=new Firebase().getReference_fields();
        getToken();






        uploadButton=findViewById(R.id.uploadBtn);
        cancelButton=findViewById(R.id.cancelbtn);




        recyclerView=findViewById(R.id.helperFieldRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemCounttxt=findViewById(R.id.countTextView);

        String[] fields=getResources().getStringArray(R.array.item_languages);

        Arrays.sort(fields);

        for (int i=0;i<fields.length;i++){
            FieldsModel field = new FieldsModel(fields[i].toUpperCase(),false);
            reference_fields.child(field.getName());
            adapterlist.add(field);

        }

        String edit = getIntent().getStringExtra("edit");
        String verification = getIntent().getStringExtra("verified");

        if(verification.equals("true")){
            verifed=true;
        }else{
            verifed=false;
        }
        if(edit.equals("true")){
            needEdit=true;
            cancelButton.setVisibility(View.VISIBLE);
            getDetails();
        }else{
            setAdapter();
        }
        new clearALlCall(getApplicationContext());
    }

    private void setAdapter() {
        customAdapter= new CustomAdapter(helperActivity.this,adapterlist);
        recyclerView.setAdapter(customAdapter);

    }

    private void getDetails() {

        reference_users.child(fuser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    model1 = dataSnapshot.getValue(FirebaseModel.class);
                    createChanges(model1);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createChanges(FirebaseModel model) {

        ArrayList<String> flist=model.getFields();
        selectedList=flist;
        for(int i=0;i<adapterlist.size();i++){
            if(flist.contains(adapterlist.get(i).getName())){
                adapterlist.get(i).setSelected(true);
            }
        }

        counter=flist.size();
        updateCounter(counter);
        setAdapter();

    }
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public  void upload(View view) {
         FirebaseModel model2;
        String name=fuser.getDisplayName();
        if(!user_token.equals("")) {
            String token = user_token;

            if (needEdit && verifed) {
                model2 = new FirebaseModel(name, model1.getPlatform(), "null",fuser.getUid(),model1.getDateOfJoining(),token, model1.isVerified(), selectedList);
            } else {
                String platformName = platform_edittext.getText().toString();
                if (platformName.length() < 2) {
                    Toast.makeText(this, "please enter a valid\n PlatForm Name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(selectedPlatformName.equals("4")){
                    selectedPlatformName=platformOtherEditText.getText().toString();
                    if(selectedPlatformName.length()<=2){
                        Toast.makeText(this, "Please Enter Valid Platform Name", Toast.LENGTH_SHORT).show();
                    }
                }
                PlatformInfo platformInfo = new PlatformInfo(selectedPlatformName, platformName);

                model2 = new FirebaseModel(name, platformInfo, "null",fuser.getUid(),getDateTime(),token,false, selectedList);
                if (!needEdit) {

                    DatabaseReference reference = new Firebase().getFirebaseDatabase().getReference("newhelpers");
                    reference.child(fuser.getUid()).setValue(name);
                }
            }

                reference_users.child(fuser.getUid()).setValue(model2);
                Toast.makeText(this, "Data Uploaded", Toast.LENGTH_SHORT).show();

            nextPage();

        }

    }




    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("Token", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        user_token = task.getResult();





                    }
                });
    }


    private void nextPage() {
        finish();
    }

    public void updateCounter(int counter) {
        if(counter==0){
            itemCounttxt.setText("0 Selected");
        }else{
            itemCounttxt.setText(counter+" Selected");
        }

    }



    public void uploadData(View view) {
        if(selectedList.size()==0){
            Toast.makeText(this, "Please Select Some Fields", Toast.LENGTH_SHORT).show();
        }else if(selectedList.size()>10){
            Toast.makeText(this, "You can add upto 10 fields in which you are smart at☺☺", Toast.LENGTH_LONG).show();
        }else if(needEdit&&verifed){

            upload(view);
        }
        else{
            setContentView(R.layout.platform_layout);

            selectPFSpinner= findViewById(R.id.selctPlatformSpinner);
            platformOtherEditText=findViewById(R.id.platformOtherEditText);
            Pf_cancel_button=findViewById(R.id.cancel_button_img);

            platform_view=findViewById(R.id.platFormView);
            platform_textview=findViewById(R.id.selectPlatFormTextView);
            platform_edittext=findViewById(R.id.platformEditText);

            getList();

            spinnerAdapter = new PlatformSpinnerAdapter(this, platformsList);
            selectPFSpinner.setAdapter(spinnerAdapter);
            if(needEdit){
                selectPFSpinner.setSelection(getIndex(model1.getPlatform().getPlatform_name()));
                platform_edittext.setText(model1.getPlatform().getUsername());

            }
            spinnerClick();




        }
    }

    private int getIndex(String platform_name) {
        for(int i=0;i<platformsList.size();i++){
            if(platformsList.get(i).equals(platform_name)){
                return i;
            }
        }
        platformOtherEditText.setText(platform_name);

        return 4;
    }


    private void spinnerClick() {
        selectPFSpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent,
                                               View view, int position, long id)
                    {


                        String a=selectedPlatformName;
                        selectedPlatformName = (String)parent.getItemAtPosition(position);

                        if(position==4){
                            selectedPlatformName=4+"";
                            otherPFVisible(true,view);
                        }


                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent)
                    {
                    }
                });
    }

    private void otherPFVisible(boolean b, View view) {
        if(b){
            selectPFSpinner.setVisibility(view.INVISIBLE);
            Pf_cancel_button.setVisibility(View.VISIBLE);
            platformOtherEditText.setVisibility(View.VISIBLE);
        }else{
            selectPFSpinner.setVisibility(View.VISIBLE);
            selectPFSpinner.setSelection(0);
            Pf_cancel_button.setVisibility(View.INVISIBLE);
            platformOtherEditText.setVisibility(View.INVISIBLE);
        }

    }

    private void getList() {

        platformsList = new ArrayList<>();

        platformsList.add("LinkedIn");
        platformsList.add("CodeChef");
        platformsList.add("GFG");
        platformsList.add("LeetCode");
        platformsList.add("Other");
    }


    public void cancelEdit(View view) {
        nextPage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void cancelOther(View view) {
        otherPFVisible(false, view);
    }
}