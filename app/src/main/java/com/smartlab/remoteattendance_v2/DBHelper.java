package com.smartlab.remoteattendance_v2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.Date;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String TABLE_NAME = "time";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_Name = "name";
    public static final String COLUMN_CheckIn = "CheckIn";
    public static final String COLUMN_CheckOut = "CheckOut";



    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table time " +
                        "(id integer primary key, name text,checkIn text,checkOut text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS time");
        onCreate(db);
    }

//    public boolean insertContact(String name, String checkin, String checkout) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("checkIn", checkin);
//        contentValues.put("CheckOut", checkout);
//
//        db.insert("time", null, contentValues);
//        return true;
//    }
public boolean insertData(String name,String checkin,String checkout) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues = new ContentValues();
    contentValues.put(COLUMN_Name,name);
    contentValues.put(COLUMN_CheckIn,checkin);
    contentValues.put(COLUMN_CheckOut,checkout);
    long result = db.insert(TABLE_NAME,null ,contentValues);
    if(result == -1)
        return false;
    else
        return true;
}


    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }

    public boolean updateContact(String name, String checkin, String checkout) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("checkIn", checkin);
        contentValues.put("CheckOut", checkout);

        db.update("time", contentValues, "name = ? ", new String[]{name});
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("time",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public Cursor getListContents() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
        return data;

    }

    public int getMaxId(){
        int primary = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT max(id) FROM " + TABLE_NAME, null);
        if (data.moveToFirst())
        {
            do
            {
                primary = data.getInt(0);
            } while (data.moveToNext());
        }
        else
        {
            primary = 0;
        }

        db.close();
        return primary;
    }

    public String[] getResultAtt(String att, String selection,String order,String limit)
    {
        Log.i("getResultAtt", "Get: " + att);

        String[] value;
        int i = 0;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cur = null ;
        cur = db.query(TABLE_NAME,
                new String[] {"*"},
                selection,
                null,
                null,
                null,
                null,
                null);
        value = new String[cur.getColumnCount()];

        if (cur.moveToFirst())
        {
            do
            {
                for (i=0;i<4;i++)
                {
                    value[i]= cur.getString(i);
                    Log.i("DB getResultConfig","val = "+cur.getString(i));
                }

            } while (cur.moveToNext());
        }
        else
        {
            Log.d("DB getResultConfig","no val");
        }
        db.close();
        return value;
    }

    public boolean getContact(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME
                + " where name=?", new String[]{name});
        boolean exists = (cursor.getCount() > 0);
    /*cursor.close();
    db.close();*/
        return exists;

    }

    public boolean checkUser(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select name from " + TABLE_NAME
                + " where name=?", new String[]{name});
        boolean exists = (cursor.getCount() > 0);
    /*cursor.close();
    db.close();*/
        return exists;
    }



}