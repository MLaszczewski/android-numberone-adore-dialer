package com.numberonecall.ui.more;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.numberonecall.R;
import com.numberonecall.api.SipManager;
import com.numberonecall.api.SipProfile;
import com.numberonecall.api.SipUri;
import com.numberonecall.db.DBProvider;
import com.numberonecall.ui.SipHome;
import com.numberonecall.ui.dialpad.CheckVariable;
import com.numberonecall.utils.Log;
public class DidFront extends Activity{ 
	private ProgressDialog dialog;
	
	public static ListView lv,lv1;
	
	EditText edittext1,edittext2;
	TextView SelectCountry;
	Button button10;
	TextView refill,balance;
	public static Boolean bool;
 CheckBox checkBox11;
  public static String str,user,pass,voucher,message,countryname,text,iso;
	SipProfile account;
	ConnectivityManager connMgr;
	NetworkInfo networkInfo;
View viewLayout;
	public void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	setContentView(R.layout.didfront);
	  lv = (ListView) findViewById(R.id.idListView);
	  lv1 = (ListView) findViewById(R.id.idListView1);
	  checkBox11 = (CheckBox) findViewById(R.id.checkBox1);
	  Boolean mycheck = CheckVariable.checkme;
      checkBox11.setChecked(mycheck);
  }
	
	
	
	
	
public void onResume()
{
	super.onResume();
	new LongOperation().execute(user);
	
}


public void onClick(View v) {
  	 
  	 
  	  if(checkBox11.isChecked()){
  		  CheckVariable.checkme=true;
  		 bool = checkBox11.isChecked();
  		  System.out.println(bool);
  		/* Intent q = new Intent(Accessno.this,SipHome.class);
        	//  it.putExtra("did",str);
        	  q.setAction(SipManager.ACTION_SIP_DIALER);
        	  q.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_SIP, ""));
        	  startActivity(q);*/
  		  
  	  }
  	  else
  	  {
  		 CheckVariable.checkme=false;
  	  }
  	 
  	}

public class LongOperation extends AsyncTask<String, Void, String> {
    
	 JSONObject json;
	 String arr[],arr1[],age1;
	 ArrayList<String> al;
	 String message,countrylist;
	
  @Override
  protected String doInBackground(String... params) {
  	
								
        return  getBalance(params[0]);
  }      

  @Override
  public void onPostExecute(String result) {  
  	 try{
  		 String[] toshow = new String[al.size()];
 	    for(int i= 0;i< toshow.length; i++ )
 	    {
 	    	toshow[i] = al.get(i).replaceAll("[0-9 ]", "");
 	    }
  	
  		 ArrayAdapter<String> adapter = new ArrayAdapter<String>(DidFront.this,android.R.layout.simple_list_item_1,toshow);
	      lv.setAdapter(adapter);
	      
	      //Setting onClickListener on ListView
	      lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	          @Override
	          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
	        	  
	        	  TextView c = (TextView) view; //<--this one 
	  	  	        //text = c.getText().toString();
	  	  	    text = al.get(i);
	  	  	        iso =  text.replaceAll("[a-zA-Z ]", "");
	  	  	        System.out.println("IIIIIISSSSSSSSSSSOOOOOOOOOO "+iso);
	  	  	    Intent q = new Intent(DidFront.this,SipHome.class);
	        	//  it.putExtra("did",str);
	        	  q.setAction(SipManager.ACTION_SIP_DIALER);
	        	  q.setData(SipUri.forgeSipUri(SipManager.PROTOCOL_SIP, ""));
	        	  startActivity(q);
	              Toast.makeText(getApplicationContext(),"Selected DID No: "+iso,Toast.LENGTH_SHORT).show();
	             
	          }
	      });		
  		
      }
      catch(Exception e)
      {
      	
      }
  	
  
   }

 protected void onPreExecute() {
	  
	
		
   }
	   
  

  protected void onProgressUpdate(Void... values) {
  }
  public String getBalance(String user) {
		
		try {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ : "+user);
			
			SipProfile account;
			long accountId = 1;
			account = SipProfile.getProfileFromDbId(DidFront.this, accountId,
					DBProvider.ACCOUNT_FULL_PROJECTION);
			String inputLine;
			  String data1 = account.getSipUserName();
			  System.out.println("USERNAME "+data1);
			  String data2=account.getPassword();	  
			  System.out.println("PASSWORD "+data2);
			String url = "http://one.alizeetelecom.com/access_ani.xml";

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");


			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			StringBuffer response = new StringBuffer();

			String inputLine1="";
			while((inputLine = in.readLine()) != null)
			{
				inputLine1+=inputLine;
			}
			String xml = inputLine1;
			
			
			System.out.println(response.append(inputLine));
			
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			InputSource src = new InputSource();
			src.setCharacterStream(new StringReader(xml));

			Document doc = builder.parse(src);
			
			
			al = new ArrayList<String>();
			
			
			for(int i = 0; i<doc.getChildNodes().getLength();i++)
			{
				String age = doc.getElementsByTagName("DialerAccessNumbers").item(i).getTextContent();
				Node node = doc.getElementsByTagName("DialerAccessNumbers").item(i);
				NodeList nl = node.getChildNodes();
				System.out.println("DEVTADIYAL"+nl.getLength());
				for(int j = 0; j< nl.getLength(); j++)
				{
					age1 = doc.getElementsByTagName("Country").item(j).getTextContent();
				//	System.out.println(age1);
					
					
					
					
					al.add(age1);
					
					
				}
				
				 
				 
				 
				/*String countryname[] = age.split("ANI");
				System.out.println(countryname[i]);*/
			}
			System.out.println(al);

			
			
			in.close();

		
		}
	  
	  catch (Exception e) {
			Log.e("Balance", "XML Pasing Excpetion ");
			e.printStackTrace();
			return null;
		}
		return user;

	}

	
	}
public class DidOperation extends AsyncTask<String, Void, String> {
    
	 JSONObject json;
	 String arr[];
	 String message,countrylist;
	
 @Override
 protected String doInBackground(String... params) {
 	
								
       return  getBalance(params[0]);
 }      

@Override
    public void onPostExecute(String result) {  
    try {
	   ArrayAdapter<String> adapter = new ArrayAdapter<String>(DidFront.this,android.R.layout.simple_list_item_1,arr);
 	   lv1.setAdapter(adapter);
 	   lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 	          
@Override
 	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
	Toast.makeText(getApplicationContext(),"Item Clicked: "+i,Toast.LENGTH_SHORT).show();
 	}
 	});
 		
     }
     catch(Exception e)
     {
     	
     }
 	
 
  }

protected void onPreExecute() {
		
  }
	   
 

 protected void onProgressUpdate(Void... values) {
 }
 public String getBalance(String user) {
		
		try {
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ : "+user);
			
			SipProfile account;
			long accountId = 1;
			account = SipProfile.getProfileFromDbId(DidFront.this, accountId,
					DBProvider.ACCOUNT_FULL_PROJECTION);
			String inputLine;
			  String data1 = account.getSipUserName();
			  System.out.println("USERNAME "+data1);
			  String data2=account.getPassword();	  
			  System.out.println("PASSWORD "+data2);
			String url = "http://209.126.66.4/vibgyor_4700/billing_didww/didww_api.php?task=get_cities_by_country_iso&country_iso="+iso;
            System.out.println("ttttttttttttttttttttttttt      "+text); 
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");


			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			
			  ArrayList<String> listdata = new ArrayList<String>();
			  
				JSONArray jArray = new JSONArray(response.toString()); 
				if (jArray != null) { 
				   for (int i=0;i<jArray.length();i++){ 
				    listdata.add(jArray.getString(i));
				   } 
				}
				arr= new String[listdata.size()];
				for(int i=0; i< listdata.size(); i++)
				{
					JSONObject jsonObj  = new JSONObject(listdata.get(i));
			  
					arr[i]=jsonObj.getString("city_name");
								
				}
	
		}
	  
	  catch (Exception e) {
			Log.e("Balance", "XML Pasing Excpetion ");
			e.printStackTrace();
			return null;
		}
		return user;

	}

	
	}
  }
