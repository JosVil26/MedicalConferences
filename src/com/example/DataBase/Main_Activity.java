package com.example.DataBase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main_Activity extends ListActivity {

	private SQLiteDatabase mDB = null;
	private MedicalConferenceOpenHelper mDbHelper;
	private SimpleCursorAdapter mAdapter;
	static private final int GET_TEXT_REQUEST_CODE = 1;
	private int id_Conference;
	private static final String TYPE_USER_KEY = "type_user";
	private int type_user = 0;
	private Button add_conf_Button;
	private Button send_all_Button;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		
		Intent Select_User_Intent = new Intent(Main_Activity.this, Select_User.class);
		startActivityForResult(Select_User_Intent, GET_TEXT_REQUEST_CODE);

		// Check for previously saved state
		if (savedInstanceState != null) {
			type_user = savedInstanceState.getInt(TYPE_USER_KEY);
		}

		// Create a new DatabaseHelper
		mDbHelper = new MedicalConferenceOpenHelper(this);

		// Get the underlying database for writing
		mDB = mDbHelper.getWritableDatabase();

		send_all_Button = (Button) findViewById(R.id.send_all_button);
		send_all_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// execute database operations
				send_all();

				// Redisplay data
				mAdapter.getCursor().requery();
				mAdapter.notifyDataSetChanged();
			}
		});

		add_conf_Button = (Button) findViewById(R.id.Add_Conference_button);
		add_conf_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (type_user == 1) {
					Intent Conference_Edit_Intent = new Intent(Main_Activity.this, Conference_Edit.class);
					Conference_Edit_Intent.putExtra("type_control", 1);
					startActivityForResult(Conference_Edit_Intent, GET_TEXT_REQUEST_CODE);
				} else if (type_user == 2) {
					ContentValues values = new ContentValues();
					values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, "Accepted");

					mDB.update(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME, values,
							MedicalConferenceOpenHelper.CONFERENCE_STATUS + "=?",
							new String[] { "Sended" });

					Toast.makeText(Main_Activity.this, "Conference(s) updated successfully.", Toast.LENGTH_SHORT).show();
					// Redisplay data
					mAdapter.getCursor().requery();
					mAdapter.notifyDataSetChanged();
				}

			}
		});
		
	}

	// Returns all conferences records in the database
	private Cursor readConferences() {
		if (type_user == 1) {
			return mDB.query(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME,
					MedicalConferenceOpenHelper.conference_columns, null, new String[] {}, null, null,
					null);			
		} else if (type_user == 2) {
			return mDB.query(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME,
					MedicalConferenceOpenHelper.conference_columns, 
					MedicalConferenceOpenHelper.CONFERENCE_STATUS + "= 'Sended' OR " 
					+ MedicalConferenceOpenHelper.CONFERENCE_STATUS + "= 'Accepted'", new String[] {}, null, null,
					null);
		}
		return null;
	}

	// Modify the contents of the database
	private void send_all() {
		String status_name = "";
		String status_filter = "";
		if (type_user == 1) {
			status_name = "Sended";
			status_filter = "To Send";
		} else if (type_user == 2) {
			status_name = "Rejected";
			status_filter = "Sended";
		}
		
		ContentValues values = new ContentValues();
		values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, status_name);

		mDB.update(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME, values,
				MedicalConferenceOpenHelper.CONFERENCE_STATUS + "=?",
				new String[] { status_filter });

		Toast.makeText(this, "Conference(s) updated successfully.", Toast.LENGTH_SHORT).show();
	}

	// Close database
	@Override
	protected void onDestroy() {

		mDB.close();
		//mDbHelper.deleteDatabase();

		super.onDestroy();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == GET_TEXT_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Update DB
				if (data != null) {
					int type_screen = data.getIntExtra("type_screen", 0);
					if (type_screen == 1) {
						type_user = data.getIntExtra("type_user", 1);
						// Create a cursor
						Cursor c = readConferences();
						mAdapter = new SimpleCursorAdapter(this, R.layout.list_layout, c,
								MedicalConferenceOpenHelper.conference_columns, new int[] { R.id._id, R.id.name, R.id.date, R.id.status },
								0);

						setListAdapter(mAdapter);
						if (type_user == 1) {
							add_conf_Button.setText("Add");
							send_all_Button.setText("Send All");
						} else if (type_user == 2) {
							add_conf_Button.setText("Accept All");
							send_all_Button.setText("Reject All");
						}
					} else if (type_screen == 2) {
						int type_button = data.getIntExtra("type_button", 0);
						ContentValues values = new ContentValues();

						switch (type_button) {
						case 1: // Add
							values.put(MedicalConferenceOpenHelper.CONFERENCE_NAME, 
									data.getStringExtra("conf_name"));
							values.put(MedicalConferenceOpenHelper.CONFERENCE_DATETIME, 
									data.getStringExtra("conf_date"));
							values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, "To Send");
							mDB.insert(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME, null, values);
							Toast.makeText(this, "Conference added successfully.", Toast.LENGTH_SHORT).show();
							break;
						case 2: // Send/Accept
							if (type_user == 1) {
								values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, 
										"Sended");
							} if (type_user == 2) {
								values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, 
										"Accepted");
							}

							mDB.update(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME, values,
									MedicalConferenceOpenHelper.CONFERENCE_ID + "=?",
									new String[] { "" + id_Conference });
							Toast.makeText(this, "Conference updated successfully.", Toast.LENGTH_SHORT).show();
							
							if (type_user == 2) {
								Cursor c_cal = mDB.query(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME,
										MedicalConferenceOpenHelper.conference_columns, 
										MedicalConferenceOpenHelper.CONFERENCE_ID + "=?", new String[] {"" + id_Conference}, null, null,
										null);
								if (c_cal.moveToFirst()) {
						            String conference_date = c_cal.getString(c_cal.getColumnIndex("datetime"));
						            String conference_name = c_cal.getString(c_cal.getColumnIndex("name"));
						            Add_Calendar(conference_name, conference_date);
								}
							}
							break;
						case 3: // Update
							values.put(MedicalConferenceOpenHelper.CONFERENCE_NAME, 
									data.getStringExtra("conf_name"));
							values.put(MedicalConferenceOpenHelper.CONFERENCE_DATETIME, 
									data.getStringExtra("conf_date"));

							mDB.update(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME, values,
									MedicalConferenceOpenHelper.CONFERENCE_ID + "=?",
									new String[] { "" + id_Conference });
							Toast.makeText(this, "Conference updated successfully.", Toast.LENGTH_SHORT).show();
							break;
						case 4: // Cancel/Reject
							if (type_user == 1) {
								values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, 
										"Canceled");
							} else if (type_user == 2) {
								values.put(MedicalConferenceOpenHelper.CONFERENCE_STATUS, 
										"Rejected");
							}

							mDB.update(MedicalConferenceOpenHelper.CONFERENCE_TABLE_NAME, values,
									MedicalConferenceOpenHelper.CONFERENCE_ID + "=?",
									new String[] { "" + id_Conference });
							Toast.makeText(this, "Conference updated successfully.", Toast.LENGTH_SHORT).show();
							break;
						}
						// Redisplay data
						Cursor c = readConferences();
						mAdapter.changeCursor(c);
						mAdapter.notifyDataSetChanged();
					}
					
				}
			}
			
		}

	}
	
	private void Add_Calendar(String s_name, String s_date) {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		Date date = null;
		try {
			date = dateFormatter.parse(s_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		 Intent intent = new Intent(Intent.ACTION_INSERT);
		 intent.setType("vnd.android.cursor.item/event");

		 Calendar cal = Calendar.getInstance();
		 if (date != null)
			 cal.setTime(date);
		 long startTime = cal.getTimeInMillis();
		 long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;

		 intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
		 intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
		 intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

		 intent.putExtra(Events.TITLE, s_name);
		 intent.putExtra(Events.DESCRIPTION, "A medical conference");

		 startActivity(intent);
	}
	
	@Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView c_status = (TextView) v.findViewById(R.id.status);
        String conference_status = c_status.getText().toString();

        if (conference_status.equals("To Send") || conference_status.equals("Sended")) {
            TextView c_name = (TextView) v.findViewById(R.id.name);
            String conference_name = c_name.getText().toString();
            TextView c_date = (TextView) v.findViewById(R.id.date);
            String conference_date = c_date.getText().toString();

            Intent Conference_Edit_Intent = new Intent(Main_Activity.this, Conference_Edit.class);
    		Conference_Edit_Intent.putExtra("type_control", 2);
    		Conference_Edit_Intent.putExtra("type_user", type_user);
    		Conference_Edit_Intent.putExtra("name", conference_name);
    		Conference_Edit_Intent.putExtra("date", conference_date);
    		Conference_Edit_Intent.putExtra("id", id);
    		id_Conference = (int) id;
    		startActivityForResult(Conference_Edit_Intent, GET_TEXT_REQUEST_CODE);
        } else if (conference_status.equals("Canceled")) {
			Toast.makeText(this, "Conference canceled, it is not possible to edit.", Toast.LENGTH_SHORT).show();
        } else if (conference_status.equals("Accepted")) {
			Toast.makeText(this, "Conference accepted, it is not possible to edit.", Toast.LENGTH_SHORT).show();
        } else if (conference_status.equals("Rejected")) {
			Toast.makeText(this, "Conference rejected, it is not possible to edit.", Toast.LENGTH_SHORT).show();
        }

	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putInt(TYPE_USER_KEY, type_user);
		super.onSaveInstanceState(savedInstanceState);

	}

}
