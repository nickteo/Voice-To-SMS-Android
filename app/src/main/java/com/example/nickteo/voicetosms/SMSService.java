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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * Created by Nick Teo on 12/23/2015.
 */
public class SMSService extends Service {

    private String phoneNumber;

    private PebbleKit.PebbleDataReceiver mDataReceiver;

    public static boolean isRunning;

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
                // Update pebble with the new list of favorites
                sendFavorites();
            }
            Log.d("receiver", "Got phoneNumber: " + phoneNumber);
        }
    };

    /**
     * Send the favorites to the pebble
     */
    private void sendFavorites() {
        PebbleDictionary resultDict = new PebbleDictionary();
        int numContacts = Globals.favorites.size();
        resultDict.addInt32(Globals.KEY_NUM_CONTACTS, numContacts);
        for (int i = 0; i < numContacts; i++) {
            int baseKey = i * 2 + Globals.KEY_FIRST_CONTACT;
            resultDict.addString(baseKey, Globals.favorites.get(i).getName());
            resultDict.addString(baseKey + 1, Globals.favorites.get(i).getNumber());
        }
        PebbleKit.sendDataToPebble(getApplicationContext(), Globals.APP_UUID, resultDict);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Helper method to send message status to pebble
     * @param status
     */
    private void sendMessageStatus(int status) {
        PebbleDictionary resultDict = new PebbleDictionary();
        resultDict.addInt32(Globals.KEY_MESSAGE_STATUS, status);
        PebbleKit.sendDataToPebble(getApplicationContext(), Globals.APP_UUID, resultDict);
    }

    /**
     * Send message to phoneNumber
     * @param phoneNumber
     * @param message
     */
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
                sendMessageStatus(Globals.MESSAGE_FAILED);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Missing either message or phone number",
                    Toast.LENGTH_LONG).show();
            sendMessageStatus(Globals.MESSAGE_FAILED);
        }
    }

    /**
     * Helper method to load favorites to Globals.favorites from persistent
     * memory
     */
    private void loadFavoritesPersistent() {
        Contact tempContact;
        try {
            FileInputStream fis = openFileInput(Globals.FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            tempContact = (Contact) ois.readObject();
            while (tempContact != null){
                Globals.favorites.add(tempContact);
                tempContact = (Contact) ois.readObject();
            }
        } catch (IOException ex) {

        } catch (ClassNotFoundException ex) {

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
                    /* Debug code
                    Toast.makeText(getApplicationContext(),
                            "Received message from Pebble!",
                            Toast.LENGTH_LONG).show();*/
                    // Grab the transcription
                    String transcription = dict.getString(Globals.KEY_MESSAGE);
                    String number = dict.getString(Globals.KEY_PHONE_NUMBER);
                    Log.i("something", "received message");

                    if (transcription != null && number != null) {
                        Log.i("receiveData", "Transcription: " + transcription);
                        sendMessage(number, transcription);
                    } else if (dict.getString(Globals.KEY_NEED_CONTACTS) != null) {
                        sendFavorites();
                    }
                }

            };
            PebbleKit.registerReceivedDataHandler(getApplicationContext(), mDataReceiver);
        }

        if (Globals.favorites == null) {
            Globals.favorites = new ArrayList<>();
        }

        loadFavoritesPersistent();
        isRunning = true;
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
