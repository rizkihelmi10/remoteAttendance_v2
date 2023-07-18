package com.smartlab.remoteattendance_v2;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import android.database.sqlite.SQLiteException;

public class MySQLiteHelper extends SQLiteOpenHelper {

	static final String TAG = "DbHelper";
	static final String DB_NAME = "workplace.db"; //
	static final int DB_VERSION = 3; //
	static final String TABLE = "wpnewsmd"; //
	static final String _ID = "_ID";					// index = 0
	static final String NewsID = "NewsID";				// index = 1
//	static final String Author = "Author";
//	static final String AuthorMail = "AuthorMail";
	static final String Title = "Title";				// index = 2
	static final String Info = "Info";					// index = 3
	static final String NType = "NType";				// index = 4
	static final String Pic = "Pic";					// index = 5
//	static final String SynCB = "SynCB";
	static final String SynCA = "SynCA";				// index = 6
	static final String Domain = "domain";				// index = 7
	static final String imgLoc = "imgLoc";				// index = 8
	
	Context context;
	
	 public MySQLiteHelper(Context context) {
    	 
	    	super(context, DB_NAME, null, DB_VERSION);
	    	this.context = context;
	    }	
 

    // Called only once, first time the DB is created
    @Override
    public void onCreate(SQLiteDatabase db) {
    	
    	Log.i("MySQLiteHelper", "onCreate");
    	
    	/*
    	 String sql = "create table " + TABLE + " (" + "_ID" + " int primary key, "+ NewsID + " int, "
		    + Author + " text, " + AuthorMail + " text, "+ Title + " text, "+ Info + " text, "
		    + NType + " text, " + Pic + " blob, "+ SynCB + " text, " + SynCA + " text)"; //		    
		    db.execSQL(sql);
    	 */
    	
		    String sql = "create table " + TABLE + " (" + "_ID" + " int primary key, "+ NewsID + " int, "
		    + Title + " text, "+ Info + " text, "
		    + NType + " text, " + Pic + " blob, " + SynCA + " text, " + Domain + " text, " + imgLoc + " text)"; //		    
		    db.execSQL(sql); //
		    
		    Log.i(TAG, "onCreated '" + TABLE + "' sql: " + sql);
		    
		    String sql2 = "create table wpconfigmd (_ID int primary key, username text, password text, server text, active int, token text, sessionID text, expired text, loca text, lockey text)";
		    db.execSQL(sql2); //
		    
		    Log.i(TAG, "onCreated 'wpconfig' sql: " + sql2);

		String sql3 = "create table tblSetting (_ID int primary key, active int)"; //
		db.execSQL(sql3); //

		Log.i(TAG, "onCreated 'tblSetting' sql: " + sql2);
    }
    
    // Called whenever newVersion != oldVersion
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //
    	
		    // Typically do ALTER TABLE statements, but...we're just in development,
		    // so:
		    db.execSQL("drop table if exists " + TABLE);// drops the old database
		    Log.i(TAG, "onUpdated table '" + TABLE + "'");
		    
		    db.execSQL("drop table if exists wpconfigmd");// drops the old database
		    Log.i(TAG, "onUpdated table 'wpconfig'");


		db.execSQL("drop table if exists tblSetting");// drops the old database
		Log.i(TAG, "onUpdated table 'wpconfig'");


		onCreate(db); // run onCreate to get new database
    }

	public boolean deleteAllRows(){

	 	boolean bflag = false;

		SQLiteDatabase db = this.getWritableDatabase();

		try{

			//db.delete(TABLE, "", null);

			db.execSQL("delete from wpconfigmd"); //delete all rows in a table
			bflag = true;
		}

		catch(SQLException e){

			Log.e("Error delete" + TABLE, e.getMessage());

		}

		db.close();
		return bflag;

	}

	public Boolean isTableExists() {
		SQLiteDatabase checkDB = this.getReadableDatabase();


		if(checkDB != null){
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}
}	 