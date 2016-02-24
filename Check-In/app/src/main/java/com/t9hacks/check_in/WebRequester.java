package com.t9hacks.check_in;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

/**
 * Created by Michael V. Swisher and Jessica Albarian on 2/20/2016.
 */

public abstract class WebRequester extends AsyncTask<String, String, Void> {

    protected ProgressDialog progressDialog = null;
    InputStream inputStream = null;
    String result = "";


    /**
     * onPreExecute shows "Getting person info"
     * before data is processed
     */
    protected void onPreExecute() {
        progressDialog.setMessage("Getting person info...");
        progressDialog.show();
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface arg0) {
                WebRequester.this.cancel(true);
            }
        });
    }



    /**
     * Opening connection to URL, reading data from URL,
     * convert inputStream to string, then closes inputStream
     */
    @Override
    protected Void doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            URLConnection urlConnection = url.openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            result = new String(ByteStreams.toByteArray(inputStream), Charsets.UTF_8); //using Guava ByteStreams
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException ignored) {
            }
        }
        return null;
    }
}