package com.example.nickteo.voicetosms;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends ActionBarActivity {
    5f8e15dd-acad-4d8b-9f01-1869ef95b57e

    private static final int KEY_BUTTON_UP = 0;
    private static final int KEY_BUTTON_DOWN = 1;
    private static final UUID APP_UUID = UUID.fromString("3717c824-d2db-4dea-b977-bcd263dd505e");

    private PagerAdapter mPagerAdapter;

    private PebbleKit.PebbleDataReceiver mDataReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    /** */
    /** Called when the user clicks the Send button */
    public void sendMessage(View view) {
        EditText phoneNumber = (EditText) findViewById(R.id.phone_number);
        EditText message = (EditText) findViewById(R.id.message);
        String phoneNumberStr = phoneNumber.getText().toString();
        String messageStr = message.getText().toString();
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumberStr, null, messageStr, null, null);
            Toast.makeText(getApplicationContext(),
                    "SMS sent!",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS failed, please try again later!",
                    Toast.LENGTH_LONG).show();
        }
    }
}
