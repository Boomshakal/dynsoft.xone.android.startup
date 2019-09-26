package com.motorolasolutions.adc.decoder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteHelper extends SQLiteOpenHelper{
	private static final String DATABASENAME = "Code.db";
	private static final int DATABASEVERSION = 3;
	
	public SqliteHelper(Context context) {
		super(context, DATABASENAME, null, DATABASEVERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE  if not exists CodeType(id integer primary key autoincrement," +
				"type_name text,type_id integer)" );
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS CodeType");	
		onCreate(db);
	}

}
