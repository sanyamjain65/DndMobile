package com.donotdisturb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//		broadcast receiver class
public class BackgroundProfileChanger extends BroadcastReceiver 
{
	private Context myContext;
	private String id;
	private Boolean profile_start=null;
	private Bundle b;
	@Override                        
	public void onReceive(Context context, Intent intent)
	{				
		myContext = context;
		b = intent.getExtras();
		id=b.getString("id");	
		profile_start=b.getBoolean("profile_start");
		callIntent();	
	}


	public void callIntent()
	{
		Intent newIntent = new Intent();		
		newIntent.setClass(myContext, AfterProfileReciever.class);
		newIntent.setAction(AfterProfileReciever.class.getName());
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		newIntent.putExtra("id", id);	
		newIntent.putExtra("profile_start", profile_start);
		myContext.startActivity(newIntent);

	}


}
