package com.numberonecall.ui.more;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Element;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.numberonecall.R;
import com.numberonecall.api.SipProfile;
import com.numberonecall.db.DBProvider;
import com.numberonecall.ui.dialpad.DigitsEditText;
import com.numberonecall.utils.Log;


public class MobileMessage extends Activity{ 
	
	private ProgressDialog dialog;
	EditText edittext1,mobile;
	public static EditText job;
	TextView refill,balance,country;
   public static String str,user,pass,msg,ccp,jsonvalue;
	SipProfile account;
   Button send,cancel,contact;
   private static final int PICK_CONTACT = 0;
   private DigitsEditText digits;
   
	public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	
	setContentView(R.layout.mobile_message);
	
	mobile = (EditText) findViewById(R.id.cw_mob);
	
	job = (EditText) findViewById(R.id.cw_job);
	/*if(More.count==1)
	{
	job.setText("Please download the vsvoip app! Calls starting to International destination from 1/2p. Click the link https://www.vsvoip.co.uk/download.php. vsvoip Call More Pay Less");
	More.count=0;
	}else
	{
		job.setText("");
	}*/
	
	
   send=(Button)findViewById(R.id.save_bt);
	cancel=(Button)findViewById(R.id.cancel_bt);
	contact=(Button)findViewById(R.id.contacts);
	
	
	 /*if(More.count==1)
		{
		job.setText("hello");
		}else
		if(DialerCallBar.count==2)
		{
			job.setText("");
			
		}*/
			
	
	/* country.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				//Toast.makeText(getApplication(),"Hello I am your OPCODE", Toast.LENGTH_LONG).show();
				
				Intent moveToCountryDialog=new Intent(InviteFriend.this, CountryDialog.class);
				startActivity(moveToCountryDialog);
				
				
	              System.out.println("saved value of country name in AccountWizard : "+CountryDialog.savedCountryName);
				//  System.out.println("saved value of country flag in AccountWizard : "+CountryDialog.savedCountryFlag);
	              System.out.println("saved value of country code in AccountWizard : "+CountryDialog.savedCountryCode);
				
			}
		});
	*/
	
	}
	
 public void onResume()
 {
	super.onResume();
			
	 send.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				
				ConnectivityManager connMgr = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

			       NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				//Toast.makeText(getApplication(),"Hello I am send button", Toast.LENGTH_LONG).show();
				String MobileNumber = mobile.getText().toString();
				String jobTitle = job.getText().toString();
		        
				System.out.println("loooloooooooooooooo: "+jobTitle+"loooooooooooolaaaaaa: "+MobileNumber);
			

				 if( networkInfo != null && networkInfo.isConnected())
		   	     {
					 if(validate())
					 { 
		   		 str=mobile.getText().toString();
		   		 msg=job.getText().toString();
		   		 
		   		  System.out.println("destination number : "+str);
		   		  System.out.println("destination message : "+msg);
		   		  
		   		 new LongOperation().execute(user);
					 }
		   		  }
				 
	             else
			      {
			    	  showNetworkAlert(); 
			      }
					
				}
		});
	 
	 cancel.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				
				String text = "";
				mobile.setText(text);
			    job.setText(text);
			    finish();
			
		        // fetch data
				
		    	  //showNetworkAlert();  
		    	
		    
				
			}
		});
	 contact.setOnClickListener(new OnClickListener() 
		{
			@Override
			public void onClick(View v)
			{
				//Toast.makeText(getApplication(),"Hello I am your Contact button", Toast.LENGTH_LONG).show();
				contact();
			}
		});
 }
 
 void showNetworkAlert() 
	{
		new AlertDialog.Builder(this)
				.setTitle("Status")
				.setMessage(
						"Ensure Network is enabled")
				.setNeutralButton("OK", new DialogInterface.OnClickListener()
				{

					public void onClick(DialogInterface dialog, int which)
					{
						/*Intent siphome = new Intent(getApplicationContext(),SipHome.class);
						startActivity(siphome);*/
						
						
							}
				}).show();
				
	}

 
 
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // TODO Auto-generated method stub
	    super.onActivityResult(requestCode, resultCode, data);
	    
	    try{

	   if(requestCode == PICK_CONTACT){
	   if(resultCode == resultCode){
	    Uri contactData = data.getData();
	    Cursor cursor =  getContentResolver().query(contactData, null, null, null, null);
	                    
	      cursor.moveToFirst();

	      String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
	      mobile.setText(number);
	      mobile.requestFocus();
	      
	      System.out.println(cursor+"xxxxxxxxxxxxxxxxxxxxxxxnumber="+number);

	      //contactName.setText(name);
	      //contactNumber.setText(number);
	      //contactEmail.setText(email);
	     }
	     }
	   
	    }catch(NullPointerException e){
	    	System.out.println(e);
	    }
	     }
 @SuppressLint("NewApi") public void contact() {
		// TODO Auto-generated method stub

		
	   // Toast.makeText(getApplication(),"Hello ", Toast.LENGTH_LONG).show();
	    
		Intent pickContactIntent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
	    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
	    startActivityForResult(pickContactIntent, PICK_CONTACT);
       
	    System.out.println("hello you had click the contact method <*><*><*><*><*><*>");
	
	}

 
 
 
 
 
public boolean validate()
{
	String str=mobile.getText().toString();
	 msg=job.getText().toString();
	int dest_Length=str.length();
	int dest_Length1=msg.length();
	if(dest_Length==0 && dest_Length1==0)
	{
		Toast.makeText(this, "Enter mobile number and message.", Toast.LENGTH_LONG).show();
		
	return false;
	}
	
	if (job.getText().length() == 0) {
		
		Toast.makeText(this, "Enter message.", Toast.LENGTH_LONG).show();
		
		return false;
	}
	if (dest_Length<=6) {
		Toast.makeText(this, "Please enter correct mobile number.", Toast.LENGTH_LONG).show();
		return false;
	}
	
	/*if(dest_Length==0 && dest_Length1==0)
	{
		Toast.makeText(this, "Please first enter Mobile Number and write your message first.", Toast.LENGTH_LONG).show();
		
	return false;}*/
	else
	{
		return true;}
	
}

void showPinAlert() 
{
	new AlertDialog.Builder(this)
			.setTitle("Status")
			.setMessage(
					"Invalid mobile number, check your account")
			.setNeutralButton("OK", new DialogInterface.OnClickListener()
			{

				public void onClick(DialogInterface dialog, int which)
				{
					
					// anurag
					/*Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.setAction(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
					startActivity(intent);*/
				}
			}).show();
}






public class LongOperation extends AsyncTask<String, Void, String> {
    
	
	 JSONObject json;
	public String message,age,c1;
   @Override
   protected String doInBackground(String... params) {
   	
								
         return  getBalance(params[0]);
   }      

   @Override
   public void onPostExecute(String result) {  
       try{
   
    
   		
   		new AlertDialog.Builder(MobileMessage.this)
			.setTitle("Alert")
			.setMessage("SMS message sent.")
			.setNeutralButton("OK", new DialogInterface.OnClickListener()
			{

				public void onClick(DialogInterface dialog, int which)
				{
					
					
				}
			}).show();
   	}
   
       catch(Exception e)
       {
       	
       }
   	
   	
   	}
   	/*toast.setMargin();  */
    }

  public void onPreExecute() {
	   
	  
	   
   }

   public void onProgressUpdate(Void... values) {
   }
   public String getBalance(String user) {
		
		String Currency = "";
		int mode = Context.MODE_PRIVATE;
		try {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ : "+user);
			
			SipProfile account;
			long accountId = 1;
			account = SipProfile.getProfileFromDbId(MobileMessage.this, accountId,
					DBProvider.ACCOUNT_FULL_PROJECTION);
			String user1, pass1;
		
			String Balance,doller;
			user1 = account.getSipUserName();
			pass1 = account.getPassword();
			String str=mobile.getText().toString();
		   String   str1=str.replaceAll("[()\\s-]+", "");
			System.out.println(str+"<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+str1);
			 msg=job.getText().toString();
			 String msg1  = msg.replaceAll(" ", "%20");
		
			 
				URL oracle = new URL("http://one.alizeetelecom.com/VSServices/SendSms.ashx?login="+user1+"&pass="+pass1+"&text="+msg+"&from="+user1+"&to="+str1);
           BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
           String inputLine;
           int i=1;
           String inputLine1="";
           inputLine=in.readLine();
           while((inputLine = in.readLine()) != null)
           {
           inputLine1+=inputLine;
           }
           String xml = inputLine1;
           System.out.println("DEVTADIYAL)$ "+xml);

String[] a = xml.split("<");
System.out.println(a[4]);
String b = a[4];
 String c1 = b.substring(18);
System.out.println(c1);
       /*  DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
           InputSource src = new InputSource();
           src.setCharacterStream(new StringReader(xml));

           Document doc = builder.parse(src);
           NodeList nList = doc.getElementsByTagName("staff");

       	System.out.println("----------------------------");

       	for (int temp = 0; temp < nList.getLength(); temp++) {

       		Node nNode = nList.item(temp);

       		System.out.println("\nCurrent Element :" + nNode.getNodeName());

       		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

       			Element eElement = (Element) nNode;

       			System.out.println("Staff id : " + eElement.getAttribute("id"));*/
       		

       		//}
      // 	}
       		
           in.close();
			
			return inputLine;

		} catch (Exception e) {
			Log.e("Balance", "XML Pasing Excpetion ");
			e.printStackTrace();
			return null;
		}

	}
}  







