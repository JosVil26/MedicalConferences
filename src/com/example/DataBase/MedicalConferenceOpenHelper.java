package com.example.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MedicalConferenceOpenHelper extends SQLiteOpenHelper {

	// Conference table
	final static String CONFERENCE_TABLE_NAME = "Conference";
	// Conference columns
	final static String CONFERENCE_ID = "_id";
	final static String CONFERENCE_NAME = "name";
	final static String CONFERENCE_DATETIME = "datetime";
	final static String CONFERENCE_STATUS = "status";
	final static String[] conference_columns = { CONFERENCE_ID, CONFERENCE_NAME, 
		CONFERENCE_DATETIME, CONFERENCE_STATUS };
	
	// Topics table
	final static String TOPIC_TABLE_NAME = "Topic";
	// Topic columns
	final static String TOPIC_ID = "_id";
	final static String TOPIC_NAME = "name";
	final static String[] topic_columns = { TOPIC_ID, TOPIC_NAME };

	final private static String CREATE_CONFERENCE_CMD =

	"CREATE TABLE " + CONFERENCE_TABLE_NAME + " (" + CONFERENCE_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ CONFERENCE_NAME + " TEXT NOT NULL, " 
			+ CONFERENCE_DATETIME + " TEXT NOT NULL, "
			+ CONFERENCE_STATUS + " INTEGER NOT NULL)";

	final private static String CREATE_TOPIC_CMD =

	"CREATE TABLE " + TOPIC_TABLE_NAME + " (" + TOPIC_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ TOPIC_NAME + " TEXT NOT NULL)";

	final private static String NAME = "conferences_db";
	final private static Integer VERSION = 1;
	final private Context mContext;

	public MedicalConferenceOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
		this.mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_CONFERENCE_CMD);
		db.execSQL(CREATE_TOPIC_CMD);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// N/A
	}

	void deleteDatabase() {
		mContext.deleteDatabase(NAME);
	}

}
