package com.example.nickteo.voicetosms;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

/**
 * Created by Nick Teo on 12/23/2015.
 */
public class SMSService extends Service {

    private String phoneNumber;

    private PebbleKit.PebbleDataReceiver mDataReceiver;

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            phoneNumber = intent.getStringExtra("phoneNumber");
            Log.d("receiver", "Got phoneNumber: " + phoneNumber);
        }
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Register to receive messages.
        // We are registering an observer (mMessageReceiver) to receive Intents
        // with actions named "custom-event-name".
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("LocalBroadcasting"));

        // Set up data receiver handler
        if(mDataReceiver == null) {
            mDataReceiver = new PebbleKit.PebbleDataReceiver(Globals.APP_UUID) {

                @Override
                public void receiveData(Context context, int transactionId, PebbleDictionary dict) {
                    // Message received, over!
                    PebbleKit.sendAckToPebble(context, transactionId);
                    // Grab the transcription
                    String transcription = dict.getString(Globals.KEY_MESSAGE);
                    Log.i("something", "received message");
                    if (transcription != null) {
                        Log.i("receiveData", "Transcription: " + transcription);
                        sendMessage(phoneNumber, transcription);
                    }
                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }

        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        // Unregister since the activity is paused.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(
                mMessageReceiver);
        super.onDestroy();
    }


}
