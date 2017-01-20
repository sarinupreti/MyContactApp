package com.provii.contactdisplay;


import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class  ContactDisplay extends AppCompatActivity {
    String server_url;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_display);
        BackgroundTask obj = new BackgroundTask();
        obj.execute();
    }
    class BackgroundTask extends AsyncTask<Void,Void,String>{
        String json_data, res;
        JSONObject jsonObject;
        JSONArray jsonArray;
        DatabaseInserter inserter;
        SQLiteDatabase database;
        @Override
        protected String doInBackground(Void...voids){


            try {
                URL url=new URL(server_url);
                HttpURLConnection httpURLConnection=(HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(7000);
                httpURLConnection.setReadTimeout(7000);
                httpURLConnection.connect();
                if(httpURLConnection.getResponseCode()==200){
                    StringBuilder stringBuilder=new StringBuilder();
                    InputStream inputStream=httpURLConnection.getInputStream();
                    BufferedReader reader=new BufferedReader((new InputStreamReader(inputStream)));
                    while((json_data=reader.readLine()) !=null){
                        stringBuilder.append(json_data+"\n");
                    }
                    res=stringBuilder.toString().trim();
                    //Log.i("Server Data",res);
                    reader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                    return res;
                }
                else{
                    Log.i("Access Problems","Data cannot be fetched");
                }

            }

            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPreExecute(){
            server_url = "http://sarin.000webhostapp.com/dataservice.php";
        }
        @Override
        protected void onPostExecute(String result){
           json_data = result;
           Thread th = new Thread(){
               @Override
               public void run() {
                   if (json_data != null){
                       try {
                           int count = 0;
                           String empID,empName, mobileNo, homeNo, officeNo,email;
                           jsonObject = new JSONObject(json_data);
                           jsonArray = jsonObject.getJSONArray("server_response");
                           inserter = new DatabaseInserter(ContactDisplay.this);
                           database = inserter.getWritableDatabase();
                           database.beginTransaction();
                           database.execSQL("Delete  from emp_contacts");
                           while(count<jsonArray.length()){
                               JSONObject row = jsonArray.getJSONObject(count);
                               empID = row.getString("empID");
                               empName = row.getString("empName");
                               mobileNo = row.getString("mobileNo");
                               homeNo = row.getString("homeNo");
                               officeNo = row.getString("officeNo");
                               email = row.getString("email");
                               inserter.insertData(database,empID,empName,mobileNo,homeNo,officeNo,email);
                               count++;
                           }
                           database.setTransactionSuccessful();

                       }
                       catch (Exception e){
                           e.printStackTrace();
                       }
                        finally{
                           database.endTransaction();
                           database.close();
                       }
                   }
               }
           };
           th.start();
            Intent intent = new Intent(getApplicationContext(),ContactDisplayView.class);
            startActivity(intent);

        }
    }
}
