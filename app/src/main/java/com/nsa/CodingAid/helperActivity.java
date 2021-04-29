package com.nsa.CodingAid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nsa.CodingAid.Adapter.CustomAdapter;
import com.nsa.CodingAid.Adapter.PlatformSpinnerAdapter;
import com.nsa.CodingAid.ExtraClasses.Firebase;
import com.nsa.CodingAid.ExtraClasses.ProgressBar;
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
import java.util.UUID;

import com.nsa.CodingAid.Model.FieldsModel;
import com.nsa.CodingAid.Model.FirebaseModel;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

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
    TextView platform_textview,resumeTextView;
    EditText platform_edittext;

    boolean needEdit=false;
    boolean verifed;

    String selectedPlatformName="";

    EditText platformOtherEditText;
    RelativeLayout Pf_cancel_button;


    Spinner selectPFSpinner;
    ArrayList<String> platformsList;
    PlatformSpinnerAdapter spinnerAdapter;
    FirebaseModel model1=null;

    private String user_token="";

    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri resumePath=null;
    String resumeLink=null;
    boolean resumeChoosen=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helper);

        fuser=FirebaseAuth.getInstance().getCurrentUser();

        reference_users=new Firebase().getReference_users();
        reference_fields=new Firebase().getReference_fields();
        getToken();





        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
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
                model2 = new FirebaseModel(name, model1.getPlatform(), "null",fuser.getUid(),model1.getDateOfJoining(),token, model1.isVerified(), selectedList,model1.getResumeLink());
                uploadModel(model2);
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
                        return;
                    }
                }

                PlatformInfo platformInfo = new PlatformInfo(selectedPlatformName, platformName);
                model2 = new FirebaseModel(name, platformInfo, "null",fuser.getUid(),getDateTime(),token,false, selectedList,resumeLink);

                if(resumePath==null){
                    Toast.makeText(this, "Please Select Your Resume?", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(model1!=null){
                        if(resumeChoosen) {

                            uploadResume(model2);
                        }else{
                            uploadModel(model2);
                            }
                    }else {
                        uploadResume(model2);
                    }
                }


            }



        }

    }

    private void uploadModel(FirebaseModel model2) {
        reference_users.child(fuser.getUid()).setValue(model2);
        Toast.makeText(this, "Data Uploaded", Toast.LENGTH_SHORT).show();

        nextPage();
    }

    private void uploadOtherData(FirebaseModel model2) {
        if (!needEdit) {

            DatabaseReference reference = new Firebase().getFirebaseDatabase().getReference("newhelpers");
            reference.child(fuser.getUid()).setValue(model2.getName());
        }
        uploadModel(model2);
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
            resumeTextView=findViewById(R.id.resumeTextView);
            platform_edittext=findViewById(R.id.platformEditText);
            TextView noteTV=findViewById(R.id.noteTextView);
            setVibrationAnim(noteTV);
            getList();

            spinnerAdapter = new PlatformSpinnerAdapter(this, platformsList);
            selectPFSpinner.setAdapter(spinnerAdapter);
            if(needEdit){
                selectPFSpinner.setSelection(getIndex(model1.getPlatform().getPlatform_name()));
                platform_edittext.setText(model1.getPlatform().getUsername());
                resumeTextView.setText("resume.pdf");
                resumePath= Uri.parse(model1.getResumeLink());

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

    private static final int PICK_PDF_FILE = 2;

    public void selectDocument(View view) {
        if(resumePath==null){
        chooseResume();
        }else{
            getPdfAction();
        }
    }

    private void chooseResume() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.
     //   intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

        startActivityForResult(intent, PICK_PDF_FILE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode ==PICK_PDF_FILE
                && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.

            if (resultData != null) {
                resumePath=resultData.getData();
                getPdfName(resumePath);
                resumeChoosen=true;

                // Perform operations on the document using its URI.
            }
        }
    }
    public void getPdfAction(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this,AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder1.setMessage("What You Wanna Do?");
        builder1.setCancelable(false);
        builder1.setPositiveButton(
                "Open",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                        openPdf();
                    }
                });

        builder1.setNegativeButton(
                "Choose Another",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        chooseResume();


                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void openPdf() {
        Intent intent=new Intent(helperActivity.this,View_Pdf_Activity.class);
        if(!resumeChoosen){
            intent.putExtra("link", model1.getResumeLink());
        }else{
            intent.putExtra("uri",resumePath.toString());
        }


        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    private void setVibrationAnim(TextView noteTV) {

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try{
                    YoYo.with(Techniques.Flash).duration(200).playOn(noteTV);
                }
                catch (Exception e) {
                    // TODO: handle exception
                }
                finally{
                    //also call the same runnable to call it at regular interval
                    handler.postDelayed(this, 2000);
                }
            }
        };
        handler.postDelayed(runnable, 2000);
    }

    public void getPdfName(Uri uri) {

        // The query, because it only applies to a single document, returns only
        // one row. There's no need to filter, sort, or select fields,
        // because we want all fields for one document.
        Cursor cursor = this.getContentResolver()
                .query(uri, null, null, null, null, null);

        try {
            // moveToFirst() returns false if the cursor has 0 rows. Very handy for
            // "if there's anything to look at, look at it" conditionals.
            if (cursor != null && cursor.moveToFirst()) {

                // Note it's called "Display Name". This is
                // provider-specific, and might not necessarily be the file name.
                String displayName = cursor.getString(
                        cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                resumeTextView.setText(displayName);
               }
        } finally {
            cursor.close();
        }
    }
    private void uploadResume(FirebaseModel model2)
    {
        if (resumePath != null) {

            // Code for showing progressDialog while uploading
            ProgressBar progressBar=new ProgressBar(this,"Uploading");
           progressBar.show();

            // Defining the child of storageReference
            StorageReference ref
                    = storageReference
                    .child(
                            "resumes/"
                                    + fuser.getUid());

            UploadTask uploadTask = ref.putFile(resumePath);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress
                            = (100.0
                            * taskSnapshot.getBytesTransferred()
                            / taskSnapshot.getTotalByteCount());

                    progressBar.setMessage(
                            "Uploaded "
                                    + (int)progress + "%");
                }
            });
            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    progressBar.hide();

                    if (task.isSuccessful()) {

                        Toast.makeText(helperActivity.this, "Resume Uploaded", Toast.LENGTH_SHORT).show();
                        Uri downloadUri = task.getResult();
                        resumeLink=downloadUri.toString();
                        model2.setResumeLink(resumeLink);
                        uploadOtherData(model2);
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });


        }
    }


}