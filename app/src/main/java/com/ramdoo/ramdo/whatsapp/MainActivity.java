package com.ramdoo.ramdo.whatsapp;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    private ArrayList<Map<String, String>> contacts;
    private ListView contactsListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp);

        contactsListView = (ListView) findViewById(R.id.listWhatsAppContacts);

        String[] from = { "ContactName" , "ContactNumber" };
        int[] to = { R.id.txtName, R.id.txtNumber };

        contacts = fetchWhatsAppContacts();

        SimpleAdapter adapter = new SimpleAdapter(this, contacts, R.layout.list_whatsapp, from, to);
        contactsListView.setAdapter(adapter);

    }

    private HashMap<String, String> pushData(String ContactName, String ContactNumber) {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("ContactName", ContactName);
        item.put("ContactNumber", ContactNumber);
        return item;
    }

    private ArrayList<Map<String, String>> fetchWhatsAppContacts(){

        ArrayList<Map<String, String>> list = new ArrayList<Map<String,String>>();

        final String[] projection={
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Data.MIMETYPE,
                "account_type",
                ContactsContract.Data.DATA3,
               // ContactsContract.Data.PHOTO_URI,
        };
        final String selection= ContactsContract.Data.MIMETYPE+" =? and account_type=?";
        final String[] selectionArgs = {
                "vnd.android.cursor.item/vnd.com.whatsapp.profile",
                "com.whatsapp"
        };
        ContentResolver cr = getContentResolver();
        Cursor c = cr.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null);
        while(c.moveToNext()){
            String id=c.getString(c.getColumnIndex(ContactsContract.Data.CONTACT_ID));
            String number=c.getString(c.getColumnIndex(ContactsContract.Data.DATA3));
            String name="";
            Cursor mCursor=getContentResolver().query(
                    ContactsContract.Contacts.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME},
                    ContactsContract.Contacts._ID+" =?",
                    new String[]{id},
                    null);
            while(mCursor.moveToNext()){
                name=mCursor.getString(mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            }
            mCursor.close();
            list.add(pushData(name, number));
        }

        return list;
    }

}