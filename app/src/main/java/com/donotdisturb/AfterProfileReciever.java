package com.donotdisturb;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;

import com.constant.Constant;
import com.database.DataBase;

public class AfterProfileReciever extends Activity
{
	private DataBase  mwDB;   
	private Cursor myCursor;
	private String mode=Constant.RING,endTime=null;
	private Boolean profile_started=true;
	private int id=0;
	private String rowId;
	private AudioManager mAudio; 
	//	public static final int VIBRATE_SETTING_ON = 1;



	@SuppressWarnings("static-access")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		mwDB = new DataBase(this);
		mAudio = (AudioManager) this.getSystemService(Activity.AUDIO_SERVICE);  
		Bundle b = getIntent().getExtras(); 
		rowId=b.getString("id");            
		profile_started=b.getBoolean("profile_start");
		Log.e("","profile_stared is now"+profile_started);
		if(profile_started){
			//			Log.e("scheduler","Started................");
			long row=0;
			try {
				row = Long.parseLong(rowId);
			} catch (NumberFormatException nfe) {
				System.out.println("NumberFormatException: " + nfe.getMessage());
			}
			mwDB.open();    
			myCursor = mwDB.fetch_Data2(row);
			startManagingCursor(myCursor);
			mwDB.close();
			if(myCursor.getCount()>0){
				endTime=myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.SET_PROFILE_END_TIME));
				mode = myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.SET_PROFILE_VIBRATE_RING));
			}else{
				profile_started=false;
			}
			
		}else{
			//			Log.e("Scheduler","Ending...............");
			mode=Constant.RING;
		}


		try{

			AudioManager aManager=(AudioManager)getSystemService(AUDIO_SERVICE);
			aManager.setRingerMode(aManager.RINGER_MODE_NORMAL);
			//						setRing();	
			if(mode.equalsIgnoreCase(Constant.VIBRATE)){
				Log.e("","vibration ok");
				setVibrateSetting(Constant.VIBRATE_SETTING_OFF, Constant.RINGER_MODE_VIBRATE_FALSE);
				mAudio.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
				setRingerVolume(0);	
			}else if(mode.equalsIgnoreCase(Constant.RING)){
				Log.e("","ringing ok");
				setVibrateSetting(Constant.VIBRATE_SETTING_OFF, Constant.RINGER_MODE_VIBRATE_FALSE);
				setRingerVolume(15);
			}else if(mode.equalsIgnoreCase(Constant.SILENT)){
				Log.e("","silent ok");
				setVibrateSetting(Constant.VIBRATE_SETTING_OFF, Constant.RINGER_MODE_VIBRATE_FALSE);
				mAudio.setRingerMode( AudioManager.RINGER_MODE_SILENT);
				setRingerVolume(0);
			}else if(mode.equalsIgnoreCase(Constant.VIBRATE_RING)){
				Log.e("","ringer with vibration ok");
				setVibrateSetting(Constant.VIBRATE_SETTING_OFF, Constant.RINGER_MODE_VIBRATE_TRUE);
				setRingerVolume(15);
			}
			setRing();
		}
		catch (Exception e)
		{ e.printStackTrace();
		}
	}

	public void setRingerVolume(int volume) {
		//		device audio setting
		mAudio.setStreamVolume(AudioManager.STREAM_RING, volume, 0);
	}

	public void setVibrateSetting(int vibrateType, int vibrateSetting) {
		//		device vibration setting
		mAudio.setVibrateSetting(vibrateType, vibrateSetting);

	}


	//	for next time set broadcast receiver
	public void setRing()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		AlarmManager alarmManager1 ;//= (AlarmManager)getSystemService(ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance(); 
		calendar.setTimeInMillis(System.currentTimeMillis()); 
		if(profile_started){
			Intent intent2 = new Intent(AfterProfileReciever.this, BackgroundProfileChanger.class);

			intent2.putExtra("id",rowId);
			intent2.putExtra("profile_start",false);
			//			intent2.putExtra("true", "no");
			PendingIntent pI1 = PendingIntent.getBroadcast(AfterProfileReciever.this, 12345678, intent2, Intent.FLAG_ACTIVITY_NEW_TASK);
			try {
				Date convertedDate= dateFormat.parse(endTime);
				calendar.set(Calendar.DATE, convertedDate.getDate());
				calendar.set(Calendar.MONTH, convertedDate.getMonth());
				calendar.set(Calendar.YEAR, convertedDate.getYear()+1900);
				calendar.set(Calendar.HOUR_OF_DAY, convertedDate.getHours());
				calendar.set(Calendar.MINUTE, convertedDate.getMinutes());
				calendar.set(Calendar.SECOND, convertedDate.getSeconds());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
			alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pI1);
			//			Log.e("@@@@@@@","End time setted");
		}
		else{

			mwDB.open();              
			myCursor=mwDB.sortDate2();
			startManagingCursor(myCursor);
			mwDB.close();

			if(myCursor.getCount()>0)
			{
				//				Log.e("","Database fetchinge new time and setting true");
				myCursor.moveToFirst();
				for (int i = 0; i < myCursor.getCount(); i++) 
				{
					//				Log.w("ROWID, FULL",myCursor.getString(myCursor.getColumnIndexOrThrow(MobileWatchDBAdapter.ROWID))+"," +myCursor.getString(myCursor.getColumnIndexOrThrow(MobileWatchDBAdapter.SET_PROFILE_FULL_DATE_TIME)));
					myCursor.moveToNext();
				}  
				myCursor.moveToFirst();
				id = myCursor.getInt(myCursor.getColumnIndexOrThrow(Constant.ROWID));
				//			Log.w("id", id+"");    

				mwDB.open();		
				myCursor=mwDB.fetch_Data2(id);           
				startManagingCursor(myCursor);
				myCursor.moveToFirst();

				mwDB.close();
				Intent intent = new Intent(AfterProfileReciever.this, BackgroundProfileChanger.class);
				intent.putExtra("id",myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.ROWID)));
				intent.putExtra("profile_start",true);
				//				intent.putExtra("true", "yes");
				PendingIntent p1 = PendingIntent.getBroadcast(AfterProfileReciever.this, 12345678, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
				//				intent2.putExtra("profile_start",false);
				Log.e("","Database fetchinge new time and setting true");
				String datefull=myCursor.getString(myCursor.getColumnIndexOrThrow(Constant.SET_PROFILE_FULL_DATE_TIME));

				try {
					Date convertedDate= dateFormat.parse(datefull);
					calendar.set(Calendar.DATE, convertedDate.getDate());
					calendar.set(Calendar.MONTH, convertedDate.getMonth());
					calendar.set(Calendar.YEAR, convertedDate.getYear()+1900);
					calendar.set(Calendar.HOUR_OF_DAY, convertedDate.getHours());
					calendar.set(Calendar.MINUTE, convertedDate.getMinutes());
					calendar.set(Calendar.SECOND, convertedDate.getSeconds());
					//				Log.e("for profile changer  date  to cheack: month : year  ::  hour : minutes", convertedDate.getDate()+" : "+ convertedDate.getMonth()+" : "+ convertedDate.getYear()+"  :  "+ convertedDate.getHours()+" : "+convertedDate.getMinutes()+"");	
				} catch (ParseException e) {
				
					e.printStackTrace();
				}

				alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);
				alarmManager1.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), p1);
			}
		}
		finish();

	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AfterProfileReciever.this.finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		AfterProfileReciever.this.finish();

	}
	@Override
	protected void onStop() {
		super.onStop();
		AfterProfileReciever.this.finish();
	}
}
