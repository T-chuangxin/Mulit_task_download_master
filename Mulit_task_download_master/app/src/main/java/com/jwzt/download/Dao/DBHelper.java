package com.jwzt.download.Dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper
{
	
	private static String DATABASE_NAME="jxjy_down.db";
	private static int version=2;
	
	public DBHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{	
//		_id integer PRIMARY KEY AUTOINCREMENT,
		db.execSQL("create table threadtab(id integer auto_increment," +
				 "news_id varchar(100)," +
				 "name varchar(100), "+
	             "newsAbstract varchar(100)," +
	             "newsPic varchar(100)," +
	             "subTitle varchar(100)," +
	             "playpath varchar(100)," +
	             "savepath varchar(100)," +
	             "currentposition integer," +
	             "vediolength integer," +
	             "downState integer," +
	             "PRIMARY KEY (id))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		onCreate(db);
	}

}
