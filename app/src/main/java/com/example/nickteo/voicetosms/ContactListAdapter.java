package com.example.nickteo.voicetosms;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Nick Teo on 12/28/2015.
 */
public class ContactListAdapter extends BaseAdapter {
    private ArrayList<Contact> contacts;
    private Activity mContext;

    ContactListAdapter() {
        contacts = null;
        mContext = null;
    }

    public ContactListAdapter(Activity context, ArrayList<Contact> contacts) {
        this.contacts = contacts;
        this.mContext = context;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return contacts.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return contacts.get(arg0);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView; // re-use an existing view, if one is available
        if (view == null) // otherwise create a new one
            view = mContext.getLayoutInflater().inflate(android.R.layout.simple_list_item_2, null);
        TextView textView1 = (TextView) view.findViewById(android.R.id.text1);
        textView1.setText(contacts.get(position).getName());
        TextView textView2 = (TextView) view.findViewById(android.R.id.text2);
        textView2.setText(contacts.get(position).getNumber());
        return view;
    }
}
