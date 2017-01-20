package com.provii.contactdisplay;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ExpandableListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ContactDisplayView extends AppCompatActivity implements SearchView.OnCloseListener, SearchView.OnQueryTextListener {
    ExpandableListView expandableListView;
    DatabaseInserter inserter;
    ContactDisplayAdapter contactDisplayAdapter;

    private SearchView searchView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_display_view);
        expandableListView = (ExpandableListView)findViewById(R.id.expandableListView);

        searchView = (SearchView) findViewById(R.id.search);
        searchView.setIconifiedByDefault(false);
        searchView.setOnCloseListener(this);
        searchView.setOnQueryTextListener(this);


        try {
            inserter = new DatabaseInserter(ContactDisplayView.this);
            SQLiteDatabase db = inserter.getWritableDatabase();
            Cursor res = inserter.getAllData(db);

            final List<String> heading_items = new ArrayList<String>();
            HashMap<String, List<String>> child_items = new HashMap<String, List<String>>();
            String empName, mobileNo, homeNo, officeNo, email;
            List<String> L3;

            while (res.moveToNext()) {

                empName = res.getString(1);
                mobileNo = res.getString(2);
                homeNo = res.getString(3);
                officeNo = res.getString(4);
                email = res.getString(5);

                L3 = new ArrayList<String>();
                L3.add("Mobile:" + mobileNo);
                L3.add("Office No:" + officeNo);
                L3.add("Home No:" + homeNo);
                L3.add("Email:" + email);
                heading_items.add(empName);
                child_items.put(empName, L3);

            }

            contactDisplayAdapter = new ContactDisplayAdapter(this, heading_items, child_items);
            expandableListView.setAdapter(contactDisplayAdapter);




        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void expandAllGroups(){
        int count = contactDisplayAdapter.getGroupCount();
        for (int i = 0; i<count; i++){
            expandableListView.expandGroup(i);
        }
    }

    @Override
    public boolean onClose() {
       contactDisplayAdapter.filter("");
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        contactDisplayAdapter.filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contactDisplayAdapter.filter(newText);
        expandAllGroups();
        return false;
    }
}
