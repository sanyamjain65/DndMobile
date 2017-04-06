package com.donotdisturb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.database.DataBase;
//		broadcast receiver class
public class TosendMessage extends BroadcastReceiver 
{
	Context myContext;
	DataBase mwDb;
	String id;
	@Override                        
	public void onReceive(Context context, Intent intent)
	{				
		myContext = context;
		mwDb = new DataBase(myContext);

		Bundle b = intent.getExtras();
	                               
		id=b.getString("id");	

//		call the activity or method to run	
		callIntent();	
	}

	
	public void callIntent()
	{
		Intent newIntent = new Intent();		
		newIntent.setClass(myContext, AfterMessageReciever.class);
		newIntent.setAction(AfterMessageReciever.class.getName());
		newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
		newIntent.putExtra("id", id);	
		myContext.startActivity(newIntent);
		
	}

}
