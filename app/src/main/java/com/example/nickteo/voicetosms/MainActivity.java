package com.example.nickteo.voicetosms;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.UUID;

public class MainActivity extends ActionBarActivity {

    private static final int KEY_PHONE_NUMBER = 0;
    private static final int KEY_MESSAGE = 1;
    private static final UUID APP_UUID = UUID.fromString("5f8e15dd-acad-4d8b-9f01-1869ef95b57e");

    private String phoneNumber;

    private PagerAdapter mPagerAdapter;

    private PebbleKit.PebbleDataReceiver mDataReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), SMSService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks the Save Number button */
    public void saveNumber(View view) {
        EditText phoneNumberET = (EditText) findViewById(R.id.phone_number);
        phoneNumber = phoneNumberET.getText().toString();
        Toast.makeText(getApplicationContext(),
                String.format("Saved phone number: %s!", phoneNumber),
                Toast.LENGTH_LONG).show();
        sendMessageToService("phoneNumber", phoneNumber);
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(String phoneNumber, String message) {
        if (phoneNumber != null && message != null){
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(getApplicationContext(),
                        "SMS sent!",
                        Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again later!",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Missing either message or phone number",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Use LocalBroadcastManager to send phone number to service
     * Used antarix's example here: https://gist.github.com/Antarix/8131277
     */
    private void sendMessageToService(String extra,String message) {
        Log.i("sending", "Sending message to service");
        Intent intent = new Intent("LocalBroadcasting");
        intent.putExtra(extra, message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("tag", "onResume");

        // Set up data receiver handler
        if(mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    // Message received, over!
                    PebbleKit.sendAckToPebble(context, transactionId);
                    // Grab the transcription
                    String transcription = dict.getString(KEY_MESSAGE);
                    Log.i("something", "received message");
                    if (transcription != null) {
                        Log.i("receiveData", "Transcription: " + transcription);
                        sendMessage(phoneNumber, transcription);
                    }
                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }
    }

}
