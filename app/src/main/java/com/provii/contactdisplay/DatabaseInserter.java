package com.provii.contactdisplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DatabaseInserter extends SQLiteOpenHelper{
    public static final String DATABASE_NAME="employee.db";
    public static final String TABLE_NAME="emp_contacts";
    public static final String CREATE_TABLE_QUERY="create table emp_contacts (empID TEXT, empName TEXT,mobileNo TEXT,homeNo TEXT,officeNo TEXT,email TEXT)";

    public DatabaseInserter(Context context){
        super(context,DATABASE_NAME,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXIST "+TABLE_NAME);
    }
    public boolean insertData(SQLiteDatabase db,String empID,String empName,String mobileNo, String homeNo, String officeNo,String email){
        ContentValues contentValues = new ContentValues();
        contentValues.put("empID",empID);
        contentValues.put("empName",empName);
        contentValues.put("mobileNo",mobileNo);
        contentValues.put("homeNo",homeNo);
        contentValues.put("officeNo",officeNo);
        contentValues.put("email",email);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if (result == -1){
            return false;
        }else{
            return true;
        }

    }
    public Cursor getAllData(SQLiteDatabase db){
        Cursor res = db.rawQuery("select * from emp_contacts",null);
                return res;
    }
}
