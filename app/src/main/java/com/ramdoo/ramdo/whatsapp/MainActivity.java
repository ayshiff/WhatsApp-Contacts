package com.ramdoo.ramdo.whatsapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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

        getPermissionToReadUserContacts();

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

    // Identifier for the permission request
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;

    // Called when the user is performing an action which requires the app to read the
    // user's contacts
    public void getPermissionToReadUserContacts() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.

            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {

            }
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}