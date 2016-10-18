package com.example.DataBase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class Conference_Edit extends Activity implements OnClickListener {

	//UI References
	private EditText conf_name_et;
	private EditText conf_date_et;
	private Button add_button;
	private Button send_button;
	private Button update_button;
	private Button delete_button;
	private int id_Conference;
	
	private DatePickerDialog Conf_DatePickerDialog;
	
	private SimpleDateFormat dateFormatter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.conference_edit);
		
		dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
		
		findViewsById();
		
		setDateTimeField();
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    int value = extras.getInt("type_control", 0);
		    int type_user = extras.getInt("type_user", 0);
		    switch(value) {
		    case 1:
		    	add_button.setVisibility(View.VISIBLE);
		    	send_button.setVisibility(View.INVISIBLE);
		    	update_button.setVisibility(View.INVISIBLE);
		    	delete_button.setVisibility(View.INVISIBLE);
		    	break;
		    case 2:
		    	add_button.setVisibility(View.INVISIBLE);
		    	send_button.setVisibility(View.VISIBLE);
		    	if (type_user == 1)
		    		update_button.setVisibility(View.VISIBLE);
		    	else if (type_user == 2)
		    		update_button.setVisibility(View.INVISIBLE);
		    	delete_button.setVisibility(View.VISIBLE);
		    	conf_name_et.setText(extras.getString("name"));
		    	conf_date_et.setText(extras.getString("date"));
		    	id_Conference = extras.getInt("id");
		    	break;
		    default:
		    	break;
		    }
		    switch(type_user) {
		    case 1:
		    	send_button.setText("Send");
		    	delete_button.setText("Delete");
		    	conf_name_et.setEnabled(true);
		    	conf_date_et.setEnabled(true);
		    	break;
		    case 2:
		    	send_button.setText("Accept");
		    	delete_button.setText("Reject");
		    	conf_name_et.setEnabled(false);
		    	conf_date_et.setEnabled(false);
		    	break;
		    default:
		    	break;
		    }
		}

		add_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				add_update_Clicked(1);
			}
		});
		send_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send_delete_Clicked(2);
			}
		});
		update_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				add_update_Clicked(3);
			}
		});
		delete_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				send_delete_Clicked(4);
			}
		});
	}
	
	private void add_update_Clicked(int type) {
		boolean yn_Data = true;
		if (conf_name_et.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "It is necessary a name for the conference", Toast.LENGTH_SHORT).show();
			yn_Data = false;
		}
		if (conf_date_et.getText().toString().trim().length() == 0) {
			Toast.makeText(this, "It is necessary a date for the conference", Toast.LENGTH_SHORT).show();
			yn_Data = false;
		}

		if (yn_Data) {
			String conf_name = conf_name_et.getText().toString();
			String conf_date = conf_date_et.getText().toString();

	    	Intent intent = new Intent();
	    	intent.putExtra("conf_name", conf_name);
	    	intent.putExtra("conf_date", conf_date);
	    	intent.putExtra("type_button", type);
	    	intent.putExtra("type_screen", 2);
			setResult(RESULT_OK,intent);

			finish();
		}
	}
	
	private void send_delete_Clicked(int type) {
    	Intent intent = new Intent();
    	intent.putExtra("id", id_Conference);
    	intent.putExtra("type_button", type);
    	intent.putExtra("type_screen", 2);
		setResult(RESULT_OK,intent);

		finish();
	}

	private void findViewsById() {
		conf_name_et = (EditText) findViewById(R.id.editText_conference_name);	
		//conf_name_et.setInputType(InputType.TYPE_NULL);
		//conf_name_et.requestFocus();
		
		conf_date_et = (EditText) findViewById(R.id.editText_conference_date);
		conf_date_et.setInputType(InputType.TYPE_NULL);

		add_button = (Button) findViewById(R.id.button_add);
		send_button = (Button) findViewById(R.id.button_send);
		update_button = (Button) findViewById(R.id.button_update);
		delete_button = (Button) findViewById(R.id.button_delete);
	}

	private void setDateTimeField() {
		conf_date_et.setOnClickListener(this);
		
		Calendar newCalendar = Calendar.getInstance();

		Conf_DatePickerDialog = new DatePickerDialog(this, new OnDateSetListener() {

	        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
	            Calendar newDate = Calendar.getInstance();
	            newDate.set(year, monthOfYear, dayOfMonth);
	            conf_date_et.setText(dateFormatter.format(newDate.getTime()));
	        }

	    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View view) {
		if(view == conf_date_et) {
			Conf_DatePickerDialog.show();
		}		
	}
}
