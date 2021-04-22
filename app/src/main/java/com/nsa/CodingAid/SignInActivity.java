package com.nsa.CodingAid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.nsa.CodingAid.ExtraClasses.ProgressBar;
import com.nsa.CodingAid.ExtraClasses.clearALlCall;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;


public class SignInActivity extends AppCompatActivity {


     private GoogleSignInClient mGoogleSignInClient;
     private String TAG="SignInActivity";
     private FirebaseAuth mfirebaseAuth;
    private int  RC_SIGN_IN=1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

       mfirebaseAuth=FirebaseAuth.getInstance();
       progressBar=new ProgressBar(SignInActivity.this,"connecting...");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        new clearALlCall(getApplicationContext());
    }
    public void signInToGoogle(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.

        updateUI();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            progressBar.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(SignInActivity.this, "Google SignIn SuccessFull", Toast.LENGTH_SHORT).show();
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show();
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mfirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(SignInActivity.this, "SuccessFull", Toast.LENGTH_SHORT).show();

                    updateUI();
                }else{
                    Toast.makeText(SignInActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI() {
        progressBar.hide();
        GoogleSignInAccount account=GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null){
            String name=account.getDisplayName();
            Intent intent=new Intent(SignInActivity.this,WelcomeActivity.class);
            startActivity(intent);
            finish();


        }
    }


}