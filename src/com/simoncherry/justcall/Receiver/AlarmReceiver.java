package com.simoncherry.justcall.Receiver;

import com.simoncherry.justcall.R;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	private final static String TAG = "AlarmReceiver";
	private Context context;

	private String telNumber = "10086";
	private long time = 0;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		SharedPreferences preference = context.getSharedPreferences("justcall",
				Context.MODE_MULTI_PROCESS);		
		time = preference.getLong("time", 0);
		if (time != 0) {
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
					0, intent, 0);
			AlarmManager am;
			am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			am.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
			Editor edit = context.getSharedPreferences(
					"justcall", Context.MODE_MULTI_PROCESS).edit();
			edit.putLong("time", 0);
			edit.commit();
		}

		PhoneCall(context);
	}
	
	private void PhoneCall(Context context) {
		Log.i(TAG, "PhoneCall");
		SharedPreferences preference = context.getSharedPreferences("justcall",
				Context.MODE_MULTI_PROCESS);
		telNumber = preference.getString("number", "10086");
		Log.i("telNumber", telNumber);
		
		// TODO
		Intent intent = new Intent();
		intent.setAction("com.simoncherry.justcall.dial.receiver");
		intent.putExtra("data", context.getString(R.string.dialog_over_text));
		//context.sendBroadcast(intent);
		
		Uri localUri = Uri.parse("tel:" + telNumber);
		Intent call = new Intent(Intent.ACTION_CALL, localUri);
		call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(call);
		
		context.sendBroadcast(intent);
	}
	
}
