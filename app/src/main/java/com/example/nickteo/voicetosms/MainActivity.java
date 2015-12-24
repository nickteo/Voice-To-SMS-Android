package com.example.nickteo.voicetosms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

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
        String phoneNumber = phoneNumberET.getText().toString();
        Toast.makeText(getApplicationContext(),
                String.format("Saved phone number: %s!", phoneNumber),
                Toast.LENGTH_LONG).show();
        sendMessageToService("phoneNumber", phoneNumber);
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
    }

}
