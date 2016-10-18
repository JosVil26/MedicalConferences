package com.example.DataBase;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Select_User extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.select_user);
		

		Button admin_button = (Button) findViewById(R.id.admin_button);
		admin_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

		    	Intent intent = new Intent();
		    	intent.putExtra("type_user", 1);
		    	intent.putExtra("type_screen", 1);
				setResult(RESULT_OK,intent);

				finish();
			}
		});

		Button doctor_Button = (Button) findViewById(R.id.doctor_button);
		doctor_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

		    	Intent intent = new Intent();
		    	intent.putExtra("type_user", 2);
		    	intent.putExtra("type_screen", 1);
				setResult(RESULT_OK,intent);

				finish();
			}
		});
		
	}

}
