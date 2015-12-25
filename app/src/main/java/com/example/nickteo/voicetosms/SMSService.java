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

import java.util.ArrayList;

/**
 * Created by Nick Teo on 12/23/2015.
 */
public class SMSService extends Service {

    private String phoneNumber;
    private ArrayList<Contact> favorites;

    private PebbleKit.PebbleDataReceiver mDataReceiver;

    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "LocalBroadcasting" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            // Get extra data included in the Intent
            if (intent.hasExtra("phoneNumber")) {
                phoneNumber = intent.getStringExtra("phoneNumber");
            } else if (intent.hasExtra("name") && intent.hasExtra("number") && intent.hasExtra("id")) {
                String name = intent.getStringExtra("name");
                String number = intent.getStringExtra("number");
                String id = intent.getStringExtra("id");
                Contact newContact = new Contact(name, number, id);
                favorites.add(newContact);
                // Update pebble with the new list of favorites
                sendFavorites();
            }
            Log.d("receiver", "Got phoneNumber: " + phoneNumber);
        }
    };

    private void sendFavorites() {
        PebbleDictionary resultDict = new PebbleDictionary();
        int numContacts = favorites.size();
        resultDict.addInt32(Globals.KEY_NUM_CONTACTS, numContacts);
        for (int i = 0; i < numContacts; i++) {
            int baseKey = i * 2 + Globals.KEY_FIRST_CONTACT;
            resultDict.addString(baseKey, favorites.get(i).getName());
            resultDict.addString(baseKey + 1, favorites.get(i).getNumber());
        }
        PebbleKit.sendDataToPebble(getApplicationContext(), Globals.APP_UUID, resultDict);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

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
                    String number = dict.getString(Globals.KEY_PHONE_NUMBER);
                    Log.i("something", "received message");
                    if (transcription != null) {
                        Log.i("receiveData", "Transcription: " + transcription);
                        sendMessage(number, transcription);
                    }
                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }

        favorites = new ArrayList<>();

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
