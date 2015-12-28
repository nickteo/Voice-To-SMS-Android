package com.example.nickteo.voicetosms;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    ListView lv;

    // Defines the text expression
    private static final String SELECTION = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " LIKE ?";
    private static final String _ID = ContactsContract.CommonDataKinds.Phone._ID;
    private static final String LOOKUP_KEY = ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY;
    private static final String DISPLAY_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
    private static final String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    @SuppressLint("InlinedApi")
    private static final String[] PROJECTION =
            {
                    _ID,
                    LOOKUP_KEY,
                    DISPLAY_NAME,
                    NUMBER,

            };

    /*
     * Defines an array that contains column names to move from
     * the Cursor to the ListView.
     */
    @SuppressLint("InlinedApi")
    private final static String[] FROM_COLUMNS = {
            DISPLAY_NAME,
            NUMBER,

    };

    /*
     * Defines an array that contains resource ids for the layout views
     * that get the Cursor column contents. The id is pre-defined in
     * the Android framework, so it is prefaced with "android.R.id"
     */
    private final static int[] TO_IDS = {
            android.R.id.text1,
            android.R.id.text2,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService(new Intent(getBaseContext(), SMSService.class));

        lv = (ListView) findViewById(R.id.list);
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

    /**
     * Hooked up in view on the lookup button. Retrieves contacts from store and creates list
     * @param view
     */
    public void getContacts(View view) {
        EditText contactNameET = (EditText) findViewById(R.id.contact_name);
        String contactName = contactNameET.getText().toString();
        getContacts(getContentResolver(), contactName);
        // Set the item click listener to add contacts to favorites
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long elmId) {
                Cursor cursor = (Cursor) lv.getItemAtPosition(position);
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(NUMBER));
                String id = cursor.getString(cursor.getColumnIndex(_ID));
                sendContactToService(name, number, id);
            }
        });
    }

    /**
     * Called by getContacts to retrieve the contacts from the phone's store
     * @param cr
     * @param contactName
     */
    public void getContacts(ContentResolver cr, String contactName) {

        String[] mSelectionArgs = { '%' + contactName + '%' };

        Uri CONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        Cursor cursor = cr.query(CONTENT_URI, PROJECTION, SELECTION , mSelectionArgs, DISPLAY_NAME + " ASC");

        if (cursor.getCount() > 0) {
            // Gets a CursorAdapter
            SimpleCursorAdapter mCursorAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                FROM_COLUMNS, TO_IDS,
                0);
            lv.setAdapter(mCursorAdapter);
        }
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
     * Use LocalBroadcastManager to send contact to service
     */
    private void sendContactToService(String name, String number, String id) {
        Log.i("sending", "Sending contact to service");
        Intent intent = new Intent("LocalBroadcasting");
        intent.putExtra("name", name);
        intent.putExtra("number", number);
        intent.putExtra("id", id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
