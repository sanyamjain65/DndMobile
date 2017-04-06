package com.donotdisturb;




import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import model.PhoneContactsBackupBean;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.constant.Constant;

public class DND_Main  extends Activity{
	private Thread contactsThread;
	private	String hasPhone="";
	private	boolean mShowInvisible;
	private String time;
	private static final String DATE_FORMAT_NOW = "dd-MM-yyyy HH.mm";
	private	ProgressDialog dialog;
	private	List<PhoneContactsBackupBean> contactList = new LinkedList<PhoneContactsBackupBean>();
	private Boolean import_contact=false,export_contact=false;
	private String vfile;
	private File mFileObject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main2);
		vfile = "Contacts.vcf";
	}
	//=======================================CLICK EVENT ON BUTTONS========
	public void click(View view){
		Intent intent;
		switch (view.getId()) {
		case R.id.add_to_block:
			intent =new Intent(DND_Main.this,AddBlockList.class);
			startActivity(intent);
			break;
		case R.id.send_message:
			intent =new Intent(DND_Main.this,Sendmessage.class);
			startActivity(intent);
			break;
		case R.id.block_list:
			intent =new Intent(DND_Main.this,ViewBlockListActivity.class);
			startActivity(intent);
			break;
		case R.id.backup:
			final Dialog dialog= new Dialog(DND_Main.this);
			dialog.setContentView(R.layout.backup);
			dialog.setTitle("BACKUP");
			dialog.show();
			final Button import_Button= (Button) dialog.findViewById(R.id.button1);
			final Button export_Button= (Button) dialog.findViewById(R.id.button2);
			import_Button.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					String path = Environment.getExternalStorageDirectory()
                            .toString() + File.separator + vfile;
					 mFileObject=new File(path);
					try {
						if(mFileObject.createNewFile())
						{
							getVCF();
						}
						else
						{
							mFileObject.delete();
							mFileObject.createNewFile();
							getVCF();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Toast.makeText(DND_Main.this, "Backup is stored", Toast.LENGTH_SHORT).show();
//					finish();
					dialog.dismiss();
					
					
					
				}
			});
			export_Button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String path = Environment.getExternalStorageDirectory()
                            .toString() + File.separator + vfile;
					mFileObject=new File(path);
					try {
						if(mFileObject.createNewFile())
						{
							getVCF();
						}
						else
						{
							mFileObject.delete();
							mFileObject.createNewFile();
							getVCF();
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Intent intent=new Intent(Intent.ACTION_SEND);
					intent.setType("message/rfc822");
					intent.putExtra(Intent.EXTRA_SUBJECT, "Device Contact Backup");
					intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mFileObject));
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
//					finish();
					dialog.dismiss();
				}

				
			});
			break;
		case R.id.profile:
			intent =new Intent(DND_Main.this,Profile.class);
			startActivity(intent);	
			break;

		default:
			break;
		}
	}
	//===========================GATHERING CONTACT DETAILS TO SAVE==========================
private void getVCF() {

    Cursor phones = getContentResolver().query(
    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                          null, null, null);
    phones.moveToFirst();
    for (int i = 0; i < phones.getCount(); i++) {
           String lookupKey = phones.getString(phones
                   .getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
           Uri uri = Uri.withAppendedPath(
           ContactsContract.Contacts.CONTENT_VCARD_URI,
                                         lookupKey);
           AssetFileDescriptor fd;
           try {
                   fd = getContentResolver().openAssetFileDescriptor(uri, "r");
                   FileInputStream fis = fd.createInputStream();
                   byte[] buf = new byte[(int) fd.getDeclaredLength()];
                   if(0<fis.read(buf))
                   {
                	   String VCard = new String(buf);
                   FileOutputStream mFileOutputStream = new FileOutputStream(mFileObject,
                                true);
                      
                      mFileOutputStream.write(VCard.toString().getBytes());
                      mFileOutputStream.close();
                  }
                   
                   phones.moveToNext();
                   
           } catch (Exception e1) {
                   // TODO Auto-generated catch block
                   e1.printStackTrace();
           }
    }
}
//	public void contactList(){
//		contactList.clear();
//		contactsThread = new Thread(){
//			public void run(){
//				Message message=new Message();
//				Cursor cursor = getContacts();
//				startManagingCursor(cursor);
//				cursor.moveToFirst();
//				for (int i = 0; i < cursor.getCount(); i++) 
//				{
//					PhoneContactsBackupBean modclass=new PhoneContactsBackupBean();
//					Long contactId=(long) 0.00;
//					if ( cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).equalsIgnoreCase("1"))
//						hasPhone = "true";
//					else
//						hasPhone = "false" ;
//					if (Boolean.parseBoolean(hasPhone)) 
//					{
//						Cursor phones = getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ cursor.getString(cursor.getColumnIndex(  ContactsContract.Contacts._ID)) , null, null);
//						startManagingCursor(phones);
//						phones.moveToFirst();
//						if(!(phones==null)&&phones.getCount()>=0){
//							for (int j = 0; j < phones.getCount(); j++) {
//								modclass.setName(cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME)));
//								modclass.setPhoneNo(phones.getString(phones.getColumnIndexOrThrow( ContactsContract.CommonDataKinds.Phone.NUMBER)));
//								int type=phones.getInt(phones.getColumnIndex(Phone.TYPE));
//								Log.e("contact id", cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID)));
//								modclass.setType(getContactType(type));
//								contactId=cursor.getLong(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
//								phones.moveToNext();
//							}
//						}
//					}
//
//					Cursor emails = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
//					Log.e("Email count= ",""+emails.getCount());
//					while (emails.moveToNext()) { 
//						String emailAddress = emails.getString( 
//								emails.getColumnIndex(ContactsContract.Data.DATA1));
//						if(emailAddress.equalsIgnoreCase("null")){
//							emailAddress="";
//							Log.e("","got null");
//						}
//						modclass.setEmail(emailAddress); 
//						//						Log.e("",""+emailAddress);//CommonDataKinds.CommonDataColumns.DATA)); 
//					}
////					emails.close(); 
//					Cursor Address = getContentResolver().query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactId, null, null);
//					String PostalAddress ="";
//					while (Address.moveToNext()) { 
//						PostalAddress=PostalAddress+ Address.getString( 
//								emails.getColumnIndex(ContactsContract.Data.DATA1));
//						PostalAddress=PostalAddress.replace("\n"," ");
//						PostalAddress=PostalAddress.replace(",","~");
//					}
//
//					modclass.setAddress(PostalAddress);
//					Address.close();
//					contactList.add(modclass);
//					cursor.moveToNext();
//				}
//				message.obj="success";
//				contactHandler.sendMessage(message);
//
//			}
//		};
//		contactsThread.start();
//	}
	private Handler contactHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialog.dismiss();
			contactsThread.interrupt();
			if(msg.obj.toString().equalsIgnoreCase("success")){
				//				dialog.dismiss();
				if(import_contact){
					try{
						InputStream input = new FileInputStream("/sdcard/contact/newcontact.csv");
						Log.e("","Aveleble bites"+input.available());
						BufferedReader reader = new BufferedReader(new InputStreamReader(input));
						String line;
						int count=0;
						while ((line = reader.readLine()) != null) {
							if(count==1)continue;
//							Log.e(""+count++,""+line);
							String[] RowData = line.split(",");
//							System.out.println(RowData[0]+"=========="+RowData[1]+"=========="+RowData[2]);
							if(RowData.length==5){
							addcontact(RowData[0],RowData[1],TypeId(RowData[2]),RowData[3],RowData[4]);
							}else if(RowData.length==4)
							{
								addcontact(RowData[0], RowData[1], TypeId(RowData[2]), RowData[3], "");
							}else if(RowData.length==3){
								addcontact(RowData[0], RowData[1], TypeId(RowData[2]), "", "");
							}else if(RowData.length==2){
								addcontact(RowData[0], RowData[1],1, "", "");//(1=for "TYPE_HOME")DEFAULT
							}
//							dialog.dismiss();
						}
					}catch (IOException ex) {
						Toast.makeText(getApplicationContext(), "File Not Found To update contact list", Toast.LENGTH_SHORT).show();
						ex.printStackTrace();
					}
					catch (ArrayIndexOutOfBoundsException e) {
						e.printStackTrace();
					}
				}
				else if(export_contact){
					WriteTOSDcard();
				}
			}else if(msg.obj.toString().equalsIgnoreCase("error exported")) {
			}


		}
	};
	//====================================WRITING TO THE SDCARD AT ROOT DIRECTORY======================
	private void WriteTOSDcard(){
		try{
			// Create file 
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			File root = Environment.getExternalStorageDirectory();
			if (root.canWrite()){
				//				Log.e("***********************","@@@@@@@@@@@@@@@@@@@@@@@@2");
				time=sdf.format(cal.getTime());
				File pathOfile = new File(root, "MyContactNumbers("+time+").csv");
				ProgressBar pgb;
				TextView current;
				TextView total;
				FileWriter fstream = new FileWriter(pathOfile);
				BufferedWriter out = new BufferedWriter(fstream);
				out.write(Constant.NAME+","+Constant.NUMBER+","+Constant.TYPE+","+Constant.E_MAIL+","+Constant.POSTAL_ADDRESS);
				final Dialog mydialog= new Dialog(DND_Main.this);
				mydialog.setContentView(R.layout.exportcontacts);
				mydialog.setTitle("Progress");
				pgb=(ProgressBar)mydialog.findViewById(R.id.progressBar1);
				current=(TextView)mydialog.findViewById(R.id.txv_current);
				total=(TextView)mydialog.findViewById(R.id.txv_Total);
				mydialog.show();
				pgb.setMax(contactList.size()); 
				total.setText("/"+contactList.size());
				Log.e("",""+contactList.size());
				for (int i = 0; i < contactList.size(); i++) {
					String email="";
					//					Log.e("Name :"+contactList.get(i).getName().replace(",", "~"),"Number :"+contactList.get(i).getPhoneNo()+"And Type :"+contactList.get(i).getType()+","+contactList.get(i).getEmail()+","+contactList.get(i).getAddress());		
					//							out.write("\n"+contactList.get(i).getName().replace(",", "~")+","+Integer.parseInt(contactList.get(i).getPhoneNo().replace("-", ""))+","+contactList.get(i).getType());
					if(contactList.get(i).getEmail()!=null)
						email=contactList.get(i).getEmail();
					out.write("\n"+contactList.get(i).getName().replace(",", "~")+","+contactList.get(i).getPhoneNo().replace("-", "")+","+contactList.get(i).getType()+","+email+","+contactList.get(i).getAddress());
					Log.e("Now contact is ",""+i);
					current.setText(String.valueOf(i));
					pgb.setProgress(i);
				}
				out.close();
				fstream.close();
			mydialog.cancel();
			showDialog(Constant.DIALOG_EMAIL_ID); 
				
			}
			

		} catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
	}
	private Cursor getContacts()
	{
		Uri uri = ContactsContract.Contacts.CONTENT_URI;
		String[] projection = new String[] {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.Contacts.HAS_PHONE_NUMBER,
		};
		String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + (mShowInvisible ? "0" : "1") + "'";
		String[] selectionArgs = null;
		String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
		return managedQuery(uri, projection, selection, selectionArgs, sortOrder);
	}


	private void Disable_user_Intration(){
		dialog = new ProgressDialog(DND_Main.this);
		dialog.setCancelable(false);
		dialog.setMessage("Loading Contacts  to save ...");
		dialog.show();
	}

	@Override
	protected Dialog onCreateDialog(int id){
		if(id == Constant.DIALOG_EMAIL_ID){
			return new AlertDialog.Builder(this)
			.setIcon(android.R.drawable.ic_dialog_alert)
			.setMessage(android.text.Html.fromHtml("Backup of your Contacts is saved on the SD card in Excel format.<br><br><b>Do you want to email the backup(recommended)?</b>"))
			.setCancelable(false)
			.setPositiveButton ("Email", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					sendEmail();
					dialog.dismiss();
					removeDialog(Constant.DIALOG_EMAIL_ID);
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.dismiss();
					removeDialog(Constant.DIALOG_EMAIL_ID);
				}

			}).create();
		}
		return null;
	}
	//==================================SENDING THE BACKUP TO EMAIL ID=======================
	public void sendEmail(){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("text/html");
		i.putExtra(android.content.Intent.EXTRA_SUBJECT, "Phone contacts  Backup");
		i.putExtra(android.content.Intent.EXTRA_STREAM, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),"/MyContactNumbers("+time+").csv")));
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(DND_Main.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	private String getContactType(int number){
		String type = null;
		switch (number) {
		case 1:
			type="TYPE_HOME";
			break;
		case 2:
			type="TYPE_MOBILE";
			break;
		case 3:
			type="TYPE_WORK";
			break;

		case 4:
			type="TYPE_FAX_WORK";
			break;

		case 5:
			type="TYPE_FAX_HOME";
			break;

		case 6:
			type="TYPE_PAGER";
			break;

		case 7:
			type="TYPE_OTHER";
			break;

		case 8:
			type="TYPE_CALLBACK";
			break;

		case 9:
			type="TYPE_CAR";
			break;

		case 10:
			type="TYPE_COMPANY_MAIN";
			break;

		case 11:
			type="TYPE_ISDN";
			break;

		case 12:
			type="TYPE_MAIN";
			break;

		case 17:
			type="TYPE_WORK_MOBILE";
			break;

		default:
			break;
		}
		return type;

	}
	
	//================================ADDING CONTACT FROM FILE===========================================
	public void addcontact(String name,String number,int Type,String email,String address){
		Boolean math=false;

		for (int i = 0; i < contactList.size(); i++) {
			if(name.equalsIgnoreCase(contactList.get(i).getName())){
				if(number.equalsIgnoreCase(contactList.get(i).getPhoneNo())){
					Log.e("","Name"+name+" and Number"+number+" Matched");
					math=true;
					break;
				}
			}
		}
		if(!math) {
			ContentValues personValues = new ContentValues();
			personValues.put(Contacts.People.NAME, name);
			personValues.put(Contacts.People.STARRED, 1);
			Uri newPersonUri = getContentResolver()
			.insert(Contacts.People.CONTENT_URI, personValues);


			//=================insert Mobile Number=============================
			ContentValues mobileValues = new ContentValues();
			Uri mobileUri = Uri.withAppendedPath(newPersonUri,
					Contacts.People.Phones.CONTENT_DIRECTORY);
			mobileValues.put(Contacts.Phones.NUMBER,
					number);
			mobileValues.put(Contacts.Phones.TYPE,
					Type);
			Uri phoneUpdate = getContentResolver()
			.insert(mobileUri, mobileValues);

			// ===============add email======================
			if(!email.equalsIgnoreCase("")){
			ContentValues emailValues = new ContentValues();
			Uri emailUri = Uri
			.withAppendedPath(
					newPersonUri,
					Contacts.People.ContactMethods
					.CONTENT_DIRECTORY);
			emailValues.put(Contacts.ContactMethods.KIND,
					Contacts.KIND_EMAIL);
			emailValues.put(Contacts.ContactMethods.TYPE,
					Contacts.ContactMethods.TYPE_HOME);
			emailValues.put(Contacts.ContactMethods.DATA,
					email);
			Uri emailUpdate = getContentResolver()
			.insert(emailUri, emailValues);
			}

			//==================== add address============================
			if(!address.equalsIgnoreCase("")){
			ContentValues addressValues = new ContentValues();
			Uri addressUri = Uri
			.withAppendedPath(
					newPersonUri,
					Contacts.People.ContactMethods
					.CONTENT_DIRECTORY);
			addressValues.put(Contacts.ContactMethods.KIND,
					Contacts.KIND_POSTAL);
			addressValues.put(Contacts.ContactMethods.TYPE,
					Contacts.ContactMethods.TYPE_HOME);
			addressValues.put(Contacts.ContactMethods.DATA,
					address);
			Uri addressUpdate = getContentResolver().insert(addressUri,
					addressValues);
			}
		}
	}
	public int TypeId(String type){
		if(type.equalsIgnoreCase("TYPE_CUSTOM")){
			return 0;
		}
		else if(type.equalsIgnoreCase("TYPE_FAX_HOME")){
			return 5;
		}
		else if(type.equalsIgnoreCase("TYPE_FAX_WORK")){
			return 4;
		}
		else if(type.equalsIgnoreCase("TYPE_HOME")){
			return 1;
		}
		else if(type.equalsIgnoreCase("TYPE_MOBILE")){
			return 2;
		}
		else if(type.equalsIgnoreCase("TYPE_OTHER")){
			return 7;
		}
		else if(type.equalsIgnoreCase("TYPE_PAGER")){
			return 6;
		}
		else if(type.equalsIgnoreCase("TYPE_WORK")){
			return 3;
		}
		else {
			return 7;
		}
	}
}

