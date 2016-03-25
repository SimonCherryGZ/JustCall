package com.simoncherry.justcall.Activity;

import com.simoncherry.justcall.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 *
 * 
 * @author simon cherry
 * 
 */
public class MainActivity extends Activity implements OnClickListener {
	private Button btnQuickStart;
	private Button btnLoadContacts;
	//private String getName = "China Mobile";
	//private String getNumber = "10086";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnQuickStart = (Button)findViewById(R.id.btn_quick_start);
		btnLoadContacts = (Button) findViewById(R.id.btn_load_contacts);
		btnQuickStart.setOnClickListener(this);
		btnLoadContacts.setOnClickListener(this);
		
	}
	
	protected void onResume() {
		super.onResume();
//		SharedPreferences preference = getSharedPreferences(
//				"justcall", Context.MODE_PRIVATE);
//		String getName = preference.getString("name", "China Mobile");
//		String getNumber = preference.getString("number", "10086");
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
			case R.id.btn_quick_start:
				SharedPreferences preference = getSharedPreferences(
						"justcall", Context.MODE_PRIVATE);
				
				Bundle mybundle = new Bundle();
				String getName = preference.getString("name", "China Mobile");
				String getNumber = preference.getString("number", "10086");
				
				mybundle.putString("name", getName);
				mybundle.putString("number", getNumber);
				
				intent = new Intent(this, TimingDialActivity.class);
				intent.putExtras(mybundle);
				break;
			case R.id.btn_load_contacts:
				intent = new Intent(this, ContactListActivity.class);
				break;
		}
		startActivity(intent);
		//overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
		//MainActivity.this.finish();
	}

}
