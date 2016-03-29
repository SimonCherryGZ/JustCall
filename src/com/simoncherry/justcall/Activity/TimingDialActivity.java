package com.simoncherry.justcall.Activity;

import java.util.Calendar;
import java.util.Random;

import com.simoncherry.justcall.Activity.TimingDialActivity;
import com.simoncherry.justcall.Receiver.AlarmReceiver;
import com.simoncherry.justcall.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TimingDialActivity extends Activity implements OnClickListener{
	private LinearLayout layout_btn;
	private TextView name_text;
	private TextView dialog_text;
	private Button btn1;
	private Button btn2;
	private Button btn3;
	private Animation mlayoutAnim;
	private Animation mbtnAnim;
	private String name_str;
	private String number_str;
	final static int MODE_ATONCE = 1;
	final static int MODE_TIMING = 2;
	final static int MODE_RANDOM = 3;
	private int whichMode = 0;
	final static int MSG_SELECT_MODE = 128;
	
	Calendar calendar;
	
	Handler myHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			
			switch(msg.what)
			{
				case MSG_SELECT_MODE:
					switch(whichMode){
						case MODE_ATONCE :
							SetTimer(0, 1);
							break;
						case MODE_TIMING :
							SetTimer(1, 0);
							break;
						case MODE_RANDOM :	
							Random getRandrom = new Random();
							int setM = getRandrom.nextInt(4);
							int setS = getRandrom.nextInt(58);
							Log.v("setM", String.valueOf(setM));
							Log.v("setS", String.valueOf(setS));
							SetTimer(setM, setS);
							break;
					}
					
					break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timing_dial);
		calendar = Calendar.getInstance();
		setTitle(R.string.timingdial_title);
		
		layout_btn = (LinearLayout)findViewById(R.id.layout_btn);
		name_text = (TextView)findViewById(R.id.name_text);
		dialog_text = (TextView)findViewById(R.id.dialog_text);
		btn1 = (Button)findViewById(R.id.button1);
		btn2 = (Button)findViewById(R.id.button2);
		btn3 = (Button)findViewById(R.id.button3);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		
		Init();
		
		layout_btn.startAnimation(mlayoutAnim);
		layout_btn.setVisibility(View.VISIBLE);

	}
	
	public void Init(){
		Intent myintent = this.getIntent();
		Bundle mybundle = myintent.getExtras();
		name_str = mybundle.getString("name");
		number_str = mybundle.getString("number");
		name_text.setText("Call: " + name_str);
		
		mbtnAnim = AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out_later);
		mbtnAnim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				hideButton();
				switch(whichMode){
					case MODE_ATONCE:
						dialog_text.setText(R.string.dialog_mode1_text);
						setTitle(R.string.timingdial_mode1);
						break;
					case MODE_TIMING:
						dialog_text.setText(R.string.dialog_mode2_text);
						setTitle(R.string.timingdial_mode2);
						break;
					case MODE_RANDOM:
						dialog_text.setText(R.string.dialog_mode3_text);
						setTitle(R.string.timingdial_mode3);
						break;
				}
				
				SharedPreferences preference = getSharedPreferences("justcall",
						Context.MODE_MULTI_PROCESS);
				Editor edit = preference.edit();
				edit.putString("name", name_str);
				edit.putString("number", number_str);
				edit.commit();
				
				myHandler.sendEmptyMessage(MSG_SELECT_MODE);
				//SetTimer();
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}		
		});
		
		mlayoutAnim = AnimationUtils.loadAnimation(this, R.anim.menu_slide_down_in);
		mlayoutAnim.setAnimationListener(new AnimationListener(){
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				dialog_text.setText(R.string.dialog_select_text);
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}		
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			btn1.startAnimation(mbtnAnim);
			btn2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out));
			btn3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out));
			whichMode = MODE_ATONCE;
			break;
		case R.id.button2:
			btn1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out));
			btn2.startAnimation(mbtnAnim);
			btn3.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out));
			whichMode = MODE_TIMING;
			break;
		case R.id.button3:
			btn1.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out));
			btn2.startAnimation(AnimationUtils.loadAnimation(this, R.anim.btn_slide_down_out));
			btn3.startAnimation(mbtnAnim);
			whichMode = MODE_RANDOM;
			break;
		}
	}
	
	public void hideButton() {
		btn1.setVisibility(View.GONE);
		btn2.setVisibility(View.GONE);
		btn3.setVisibility(View.GONE);
	}
	
	public void SetTimer(int setMinute, int setSecond){
		calendar.setTimeInMillis(System.currentTimeMillis());
		int mHour = calendar.get(Calendar.HOUR_OF_DAY);
		int mMinute = calendar.get(Calendar.MINUTE);
		int mSecond = calendar.get(Calendar.SECOND);
		
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.set(Calendar.HOUR_OF_DAY, mHour);
		calendar.set(Calendar.MINUTE, mMinute + setMinute);
		calendar.set(Calendar.SECOND, mSecond + setSecond);
		calendar.set(Calendar.MILLISECOND, 0);
		
		SharedPreferences preference = getSharedPreferences(
				"justcall", Context.MODE_MULTI_PROCESS);
		Editor edit = preference.edit();
		edit.putString("name", name_str);
		edit.putString("number", number_str);
		edit.putLong("time", calendar.getTimeInMillis());
		edit.commit();
		
		Intent intent = new Intent(
				TimingDialActivity.this,
				AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent
				.getBroadcast(TimingDialActivity.this,
						0, intent, 0);
		AlarmManager am;
		am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				pendingIntent);
	}

}
