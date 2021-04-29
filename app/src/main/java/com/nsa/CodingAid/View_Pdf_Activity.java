package com.nsa.CodingAid;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.nsa.CodingAid.ExtraClasses.ProgressBar;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class View_Pdf_Activity extends AppCompatActivity {
    PDFView pdfView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__pdf_);
        pdfView =findViewById(R.id.pdfView);
        progressBar=new ProgressBar(View_Pdf_Activity.this,"Loading...");
        String link="";
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            if(extras.containsKey("uri")) {

                link=extras.getString("uri");
                pdfView.fromUri(Uri.parse(link)).load();

            }
            if(extras.containsKey("link")){
                progressBar.show();
                link=extras.getString("link");
                new RetrivePDFfromUrl().execute(link);
            }

        }



    }
    // create an async task class for loading pdf file from URL.
    class RetrivePDFfromUrl extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... strings) {
            // we are using inputstream
            // for getting out PDF.
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                // below is the step where we are
                // creating our connection.
                HttpURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    // response is success.
                    // we are getting input stream from url
                    // and storing it in our variable.
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
                }

            } catch (IOException e) {
                // this is the method
                // to handle errors.
                e.printStackTrace();
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            // after the execution of our async
            // task we are loading our pdf in our pdf view.
            pdfView.fromStream(inputStream).load();
            progressBar.hide();
        }
    }
    public void backToPlatformLayout(View view) {
        finish();
    }
}