package com.t9hacks.check_in;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;

import android.app.ProgressDialog;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/*
TO-DO (not in order/for the googles):
Create persistent data stuff
Implement checks of persistent data storage
Send server input for checkin/badge info
Get nfc badge unique id

Design model for synchronizing local DB and online DB

 */

public class MainActivity extends AppCompatActivity {

    String db_url;
    String token;
    NfcAdapter nfcAdapter;

    Context ctx;
    DbOps DOP;
    Cursor cursor;
    //Need to deprecate; This is from old NFC tutorial
    ToggleButton toggleReadWrite;
    //EditText textTagContent;

    public static ArrayList<String> nameSet = new ArrayList<String>();

    AutoCompleteTextView nameSearch;
    String [] registeredNames = {}; //Need to populate this with names from the database

    TextView email;
    TextView key;
    TextView type;
    TextView shirt;
    TextView phone;
    TextView bracelet;
    CheckBox forceful;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize nfc adapter
        //Check if DB exists
        //Check if submission queue file exists

        ctx = MainActivity.this;
        DOP = new DbOps(ctx);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        //Attach nameSearch variable to nameSearchView
        nameSearch = (AutoCompleteTextView)findViewById(R.id.nameSearchView);
        email = (TextView)findViewById(R.id.emailField);
        key = (TextView)findViewById(R.id.keyField);
        type = (TextView)findViewById(R.id.typeField);
        shirt = (TextView)findViewById(R.id.shirtField);
        phone = (TextView)findViewById(R.id.phoneField);
        bracelet = (TextView)findViewById(R.id.braceletIDField);
        forceful = (CheckBox)findViewById(R.id.forceBox);

        db_url = "http://t9hacks.org/api/users.all.modified.php?token=";
        token = getString(R.string.tokenValue);
        //Get token credential from resource file credentials.xml

        pullDBData();

        nameSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Populate nameSearch with values from DB

                cursor = DOP.getRowWithName(DOP, nameSearch.getText().toString());

                type.setText(cursor.getString(0));
                key.setText(cursor.getString(2));
                email.setText(cursor.getString(4));
                phone.setText(cursor.getString(5));
                shirt.setText(cursor.getString(6));
                Log.d("Db accessing", type.getText().toString());
                Log.d("Db accessing", key.getText().toString());
                Log.d("Db accessing", email.getText().toString());
                Log.d("Db accessing", phone.getText().toString());
                Log.d("Db accessing", shirt.getText().toString());




                //nameSet.add(cursor.getString(3));


//                email.setText(nameSearch.getText().toString());
//                key.setText(nameSearch.getText().toString());
            }
        });
    }

    @Override
    protected void onStart(){
        //Check network access(wifi/cellular)
        //Update DB values

        super.onStart();
        pullDBData();
    }

    @Override
    protected void onResume() {
        //Enable foregroundDispatch

        super.onResume();
        enableForegroundDispatchSystem();
    }

    @Override
    protected void onPause() {
        //Disable foregroundDispatch

        super.onPause();
        disableForegroundDispatchSystem();
    }

    @Override
    protected void onDestroy(){
        //Delete global variables

        super.onDestroy();
        //Stub
    }

    // Converting byte[] to hex string:
    private String ByteArrayToHexString(byte [] inarray) {
        int i, j, in;
        String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
        String out= "";
        for(j = 0 ; j < inarray.length ; ++j)
        {
            in = (int) inarray[j] & 0xff;
            i = (in >> 4) & 0x0f;
            out += hex[i];
            i = in & 0x0f;
            out += hex[i];
        }
        return out;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(intent.hasExtra(NfcAdapter.EXTRA_TAG)){
            //Toast.makeText(this, "NFC Intent", Toast.LENGTH_LONG).show();

            //if(toggleReadWrite.isChecked()){
                bracelet.setText(ByteArrayToHexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID)));
                Parcelable[] parcelables = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
                if(parcelables != null && parcelables.length > 0){
                    readTextFromMessage((NdefMessage)parcelables[0]);
                }//else{
                    //Stub
                    //Toast.makeText(this, "No NDEF message found", Toast.LENGTH_SHORT).show();
                //}
//            }else {
//                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//                NdefMessage ndefMessage = createNdefMessage(textTagContent.getText()+"");
//                writeNdefMessage(tag, ndefMessage);
//            }
        }
    }

    public void submitData(View view){
        //stub
        if(key.getText()==""){
            Toast.makeText(this, "Requires valid user", Toast.LENGTH_SHORT).show();
        }
        else if(bracelet.getText()=="" && forceful.isChecked()==false){
            Toast.makeText(this, "Please scan an NFC badge", Toast.LENGTH_SHORT).show();
        }
        else{
            //Submit checkin

            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
            //Clear all fields
            nameSearch.setText("");
            email.setText("");
            key.setText("");
            type.setText("");
            shirt.setText("");
            phone.setText("");
            bracelet.setText("");
        }

    }

    public void refreshDB(View view){
       pullDBData();
    }

    public void pullDBData(){

        new WebRequesterDownload().execute(db_url+token);

        //Populate nameSearch with values from DB
        cursor = DOP.getData(DOP);
        cursor.moveToFirst();
        do{
            nameSet.add(cursor.getString(3));

        }while(cursor.moveToNext());
        //Collections.sort(nameSet);
        HashSet holder = new HashSet();
        holder.addAll(nameSet);
        nameSet.clear();
        nameSet.addAll(holder);
        holder = null;

        Collections.sort(nameSet);
        registeredNames = nameSet.toArray(new String[nameSet.size()]);

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, registeredNames);
        nameSearch.setAdapter(nameAdapter);
    }
    public class WebRequesterDownload extends WebRequester {

        public WebRequesterDownload() {
            progressDialog = new ProgressDialog(MainActivity.this);
        }

        /**
         * onPostExecute shows website data
         */
        protected void onPostExecute(Void v) {

            progressDialog.dismiss();
            try {
                //DbOps DB = new DbOps(ctx);
                JSONObject jObject = new JSONObject(result);
                JSONArray jArray = jObject.getJSONArray("data");
                for (int i = 0; i < jArray.length(); i++) {
                    JSONObject person = jArray.getJSONObject(i);
                    DOP.insertData(DOP, person);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void enableForegroundDispatchSystem(){
        Intent intent = new Intent(this, MainActivity.class).addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent, 0);
        IntentFilter[] intentFilters = new IntentFilter[]{};
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    private void disableForegroundDispatchSystem(){
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage){
        try{

            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if(ndefFormatable == null){
                Toast.makeText(this, "Tag is not ndef formatable", Toast.LENGTH_LONG).show();
                return;
            }

            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();

            Toast.makeText(this, "Tag successfully written",Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Log.e("formatTag", e.getMessage());
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage){
        try{
            if(tag==null){
                Toast.makeText(this,"Tag object cannot be null", Toast.LENGTH_LONG).show();
                return;
            }

            Ndef ndef = Ndef.get(tag);

            if(ndef == null){
                //Format tag with ndef formatting and write message to the tag
                formatTag(tag, ndefMessage);
            }else{
                ndef.connect();
                if(!ndef.isWritable()){
                    Toast.makeText(this,"Tag is not writable", Toast.LENGTH_LONG).show();
                    ndef.close();
                    return;
                }

                ndef.writeNdefMessage(ndefMessage);
                ndef.close();
                Toast.makeText(this,"Tag successfully written", Toast.LENGTH_LONG).show();
            }
        }catch(Exception e){
            Log.e("formatTag", e.getMessage());
        }
    }

    private NdefRecord createTextRecord(String content){
        try {
            byte[] language;
            language = Locale.getDefault().getLanguage().getBytes("UTF-8");

            final byte[] text = content.getBytes("UTF-8");
            final int languageSize = language.length;
            final int textLength = text.length;
            final ByteArrayOutputStream payload = new ByteArrayOutputStream(1 + languageSize + textLength);

            payload.write((byte) (languageSize & 0x1F));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);

            return new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        }catch (UnsupportedEncodingException e){
            Log.e("createTextRecord", e.getMessage());
        }
        return null;
    }

    private NdefMessage createNdefMessage(String content){
        NdefRecord ndefRecord = createTextRecord(content);
        NdefMessage ndefMessage = new NdefMessage(new NdefRecord[]{ ndefRecord});

        return ndefMessage;
    }

//   public void toggleReadWriteOnClick(View view){
//        textTagContent.setText("");
//    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord){
        String tagContent = null;

        try {
            String textEncoding = null;
            byte[] payload = ndefRecord.getPayload();
            //String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            if ((payload[0] & 128) == 0){
                textEncoding = "UTF-8";
            }
            else{
                textEncoding = "UTF-16";
            }
            int languageSize = payload[0] & 0063;
            tagContent = new String(payload, languageSize + 1, payload.length - languageSize - 1, textEncoding);
        } catch (UnsupportedEncodingException e){
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
        }

        return tagContent;
    }

    private void readTextFromMessage(NdefMessage ndefMessage){
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if(ndefRecords != null && ndefRecords.length>0){
            NdefRecord ndefRecord = ndefRecords[0];
            String tagContent = getTextFromNdefRecord(ndefRecord);
            //bracelet.setText(tagContent);
        }else{
            //Stub
            Toast.makeText(this, "No NDEF records found", Toast.LENGTH_SHORT).show();
        }
    }
}
