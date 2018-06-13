package com.numberonecall.ui.dialpad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent.CanceledException;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentTransaction;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DialerKeyListener;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.TextView.OnEditorActionListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.numberonecall.R;
import com.numberonecall.api.ISipService;
import com.numberonecall.api.SipCallSession;
import com.numberonecall.api.SipConfigManager;
import com.numberonecall.api.SipManager;
import com.numberonecall.api.SipProfile;
import com.numberonecall.db.DBProvider;
import com.numberonecall.demf.ListenToPhoneState;
import com.numberonecall.ui.SipHome.ViewPagerVisibilityListener;
import com.numberonecall.ui.account.AdoreSharedPreferences;
import com.numberonecall.ui.contacts.AddContactDialog;
import com.numberonecall.ui.more.DidFront;
import com.numberonecall.utils.CallHandlerPlugin;
import com.numberonecall.utils.CallHandlerPlugin.OnLoadListener;
import com.numberonecall.utils.DialingFeedback;
import com.numberonecall.utils.Log;
import com.numberonecall.utils.PreferencesProviderWrapper;
import com.numberonecall.utils.PreferencesWrapper;
import com.numberonecall.utils.Theme;
import com.numberonecall.utils.animation.ActivitySwitcher;
import com.numberonecall.utils.contacts.ContactsSearchAdapter;
import com.numberonecall.widgets.AccountChooserButton;
import com.numberonecall.widgets.AccountChooserButton.OnAccountChangeListener;
import com.numberonecall.widgets.DialerCallBar;
import com.numberonecall.widgets.DialerCallBar.OnDialActionListener;
import com.numberonecall.widgets.Dialpad;
import com.numberonecall.widgets.Dialpad.OnDialKeyListener;


@SuppressLint("NewApi")
public class DialerFragment extends SherlockFragment implements
		OnClickListener, OnLongClickListener, OnDialKeyListener, TextWatcher,
		OnDialActionListener, ViewPagerVisibilityListener, OnKeyListener {
	
	
    Context contx;
	private static  final String THIS_FILE = "DialerFragment";
	public static boolean wasAccount = false;
	private PreferencesProviderWrapper prefProviderWrapper;
	protected static final int PICKUP_PHONE = 0;
	private static final String myprefs3 = null;
	// private Drawable digitsBackground, digitsEmptyBackground;
	 public static String crdname;
	 public static String RateNumber2,RATE,jsonvalue,ab;
	private DigitsEditText digits;
	public static ImageView addcontact1;
	public static String pinnumber="";
	public static String number;
	public static String initText;
	public static TextView access1,balance1 ;
	public static TextView foot,tv1;
	public static EditText did;
	static SipProfile account;
	private static final int PICK_CONTACT = 0;
	// private ImageButton switchTextView;
	// param;
	// private View digitDialer;

	private AccountChooserButton accountChooserButton;
	private Boolean isDigit = null;
	/* , isTablet */

	private DialingFeedback dialFeedback;

	/*
	 * private final int[] buttonsToAttach = new int[] { R.id.switchTextView };
	 */
	private final int[] buttonsToLongAttach = new int[] { R.id.button0,
			R.id.button1 };

	// TimingLogger timings = new TimingLogger("SIP_HOME", "test");

	public static ISipService service;
	private ServiceConnection connection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			service = ISipService.Stub.asInterface(arg1);
			
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			service = null;
		}
	};

	// private GestureDetector gestureDetector;
	private Dialpad dialPad;
	View v;
	private PreferencesWrapper prefsWrapper;
	private AlertDialog missingVoicemailDialog,alertDialog;
	public static TextView balance;
	public String rr="0";
	// Auto completion for text mode
	// private ListView autoCompleteList;
	private ContactsSearchAdapter autoCompleteAdapter;

	private DialerCallBar callBar;
	private boolean mDualPane;

	private DialerAutocompleteDetailsFragment autoCompleteFragment;
	private PhoneNumberFormattingTextWatcher digitFormater;
	private OnAutoCompleteListItemClicked autoCompleteListItemListener;
	private ImageView logo,del;
	private DialerLayout dialerLayout;
	private AdoreSharedPreferences adorePreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDualPane = getResources().getBoolean(R.bool.use_dual_panes);
		//sharad
		adorePreferences=new AdoreSharedPreferences(getActivity().getApplicationContext());
		digitFormater = new PhoneNumberFormattingTextWatcher();
		// Auto complete list in case of text
		autoCompleteAdapter = new ContactsSearchAdapter(getActivity());
		autoCompleteListItemListener = new OnAutoCompleteListItemClicked(
				autoCompleteAdapter);

		// This implies
		if (isDigit == null) {
			isDigit = prefsWrapper.startIsDigit();
		}
		

		setHasOptionsMenu(false);
	}

	private Intent getIntent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		 prefProviderWrapper = new PreferencesProviderWrapper(getActivity());
		v = inflater.inflate(R.layout.dialer_digit, container, false);
	
		digits = (DigitsEditText) v.findViewById(R.id.digitsText);
		//access1 = (TextView)v.findViewById(R.id.t_access);
		//did = (EditText)v.findViewById(R.id.passText1);
		tv1 = (TextView) v.findViewById(R.id.rate);
		logo = (ImageView) v.findViewById(R.id.adoreLogo);
		addcontact1 = (ImageView) v.findViewById(R.id.addcontact);
		balance = (TextView) v.findViewById(R.id.balance);
		dialPad = (Dialpad) v.findViewById(R.id.dialPad);
		
		handleSSLHandshake();
		callBar = (DialerCallBar) v.findViewById(R.id.dialerCallBar);
		
		// autoCompleteList = (ListView) v.findViewById(R.id.autoCompleteList);
		accountChooserButton = (AccountChooserButton) v
				.findViewById(R.id.accountChooserButton);
		dialerLayout = (DialerLayout) v.findViewById(R.id.top_digit_dialer);
		// switchTextView = (ImageButton) v.findViewById(R.id.switchTextView);

		// isTablet = Compatibility.isTabletScreen(getActivity());
		balance1 = (TextView) v.findViewById(R.id.accountButton); 
		
		
		
		del = (ImageView)v.findViewById(R.id.deleteButton);
		
		
		  del.setOnLongClickListener(new View.OnLongClickListener() {
				    public boolean onLongClick(View v) {
				    	
				    	// DialerFragment.tv1.setText("");
				    	deleteAll();
				    	
				    	System.out.println("vvvvvvvvvvvvvvvvvv");
				        return true;
				    }
				});
		  del.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 //DialerFragment.tv1.setText("");
					deleteChar();
					
				}
			});
		
		  
		  addcontact1.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					 //DialerFragment.tv1.setText("");
					addContact();
					
				}

				private void addContact() {
					// TODO Auto-generated method stub
					Intent i = new Intent(getActivity(),AddContactDialog.class);
					startActivity(i);
				}
			});
		
		
		
		// Digits field setup
		if (savedInstanceState != null) {
			isDigit = savedInstanceState.getBoolean(TEXT_MODE_KEY, isDigit);
		}

		digits.setOnEditorActionListener(keyboardActionListener);

		// Layout
		dialerLayout.setForceNoList(mDualPane);

		// Account chooser button setup
		accountChooserButton.setShowExternals(true);
		accountChooserButton
				.setOnAccountChangeListener(accountButtonChangeListener);

		// Layout params for logo adjust
		// param=new DialerLayout.LayoutParams();
		// Dialpad
		dialPad.setOnDialKeyListener(this);

		/*
		 * // We only need to add the autocomplete list if we
		 * autoCompleteList.setAdapter(autoCompleteAdapter);
		 * autoCompleteList.setOnItemClickListener
		 * (autoCompleteListItemListener);
		 * autoCompleteList.setFastScrollEnabled(true);
		 */

		// Bottom bar setup
		callBar.setOnDialActionListener(this);
		callBar.setVideoEnabled(prefsWrapper
				.getPreferenceBooleanValue(SipConfigManager.USE_VIDEO));
	
		// switchTextView.setVisibility(Compatibility.isCompatible(11) ?
		// View.GONE : View.VISIBLE);

		// Init other buttons
		initButtons(v);
		// Ensure that current mode (text/digit) is applied
	//	setTextDialing(!isDigit, true);
		if (initText != null) {
			digits.setText(initText);
			initText = null;
		}

		// Apply third party theme if any
		applyTheme(v);
		v.setOnKeyListener(this);
		applyTextToAutoComplete();
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		
		
		
	//	balance.setText("");
		   Log.e(THIS_FILE, "On Resume Dialpad");
		if (wasAccount){
			ActivitySwitcher.animationIn(v.findViewById(R.id.top_digit_dialer),
					getActivity().getWindowManager());
			wasAccount=false;
		}
		if (initText != null) {
			digits.setText(initText);
			initText = null;
		}
		String user=null;
		new LongOperation().execute(user); 
		new LongOperation1().execute(user); 
	
	}
	public static void handleSSLHandshake() {
	    
		 try {
		        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
		         public X509Certificate[] getAcceptedIssuers() {
		                return new X509Certificate[0];
		                }

		@Override
		      public void checkClientTrusted(X509Certificate[] certs, String authType) {
		            }

		@Override
		      public void checkServerTrusted(X509Certificate[] certs, String authType) {
		            }
		        }};

		        SSLContext sc = SSLContext.getInstance("SSL");
		        sc.init(null, trustAllCerts, new SecureRandom());
		        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
		            
		@Override
		    public boolean verify(String arg0, SSLSession arg1) {
		          return true;
		          }});
		          }
		 
		 catch (Exception ignored) {
		        }}
	 @Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    // TODO Auto-generated method stub
		    super.onActivityResult(requestCode, resultCode, data);
		    
		    try{

		   if(requestCode == PICK_CONTACT){
		   if(resultCode == resultCode){
		    Uri contactData = data.getData();
		    Cursor cursor =  getActivity().managedQuery(contactData, null, null, null, null);
		    cursor.moveToFirst();

		      String number =       cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
		      digits.setText(number);
		      digits.requestFocus();
		      
		      System.out.println("number="+number);

		     
		     }
		     }
		   
		    }catch(NullPointerException e){
		    	System.out.println(e);
		    }
		     }
	
	

	private void applyTheme(View v) {
		Theme t = Theme.getCurrentTheme(getActivity());
		if (t != null) {
			dialPad.applyTheme(t);

			View subV;
			// Delete button
			subV = v.findViewById(R.id.deleteButton);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_dial_delete");
				t.applyLayoutMargin(subV, "btn_dial_delete_margin");
				t.applyImageDrawable((ImageView) subV, "ic_dial_action_delete");
			}

			// Dial button
			subV = v.findViewById(R.id.dialButton);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_dial_action");
				t.applyLayoutMargin(subV, "btn_dial_action_margin");
				t.applyImageDrawable((ImageView) subV, "ic_dial_action_call");
			}

			
			
			// Additional button
			subV = v.findViewById(R.id.accountButton);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_add_action");
				t.applyLayoutMargin(subV, "btn_dial_add_margin");
			}

			// Action dividers
			subV = v.findViewById(R.id.divider1);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_bar_divider");
				t.applyLayoutSize(subV, "btn_dial_divider");
			}
			subV = v.findViewById(R.id.divider2);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "btn_bar_divider");
				t.applyLayoutSize(subV, "btn_dial_divider");
			}

			// Dialpad background
			subV = v.findViewById(R.id.dialPad);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "dialpad_background");
			}

			// Callbar background
			subV = v.findViewById(R.id.dialerCallBar);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "dialer_callbar_background");
			}
			
			

			// Top field background
			subV = v.findViewById(R.id.topField);
			if (subV != null) {
				t.applyBackgroundDrawable(subV, "dialer_textfield_background");
			}

			subV = v.findViewById(R.id.digitsText);
			if (subV != null) {
				t.applyTextColor((TextView) subV, "textColorPrimary");
			}

		}

		// Fix dialer background
		if (callBar != null) {
			Theme.fixRepeatableBackground(callBar);
		}
		
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		getActivity().bindService(new Intent(SipManager.INTENT_SIP_SERVICE),
				connection, Context.BIND_AUTO_CREATE);
		// timings.addSplit("Bind asked for two");
		if (prefsWrapper == null) {
			prefsWrapper = new PreferencesWrapper(getActivity());
		}
		if (dialFeedback == null) {
			dialFeedback = new DialingFeedback(getActivity(), false);
		}

		dialFeedback.resume();

	}

	@Override
	public void onDetach() {
		try {
			getActivity().unbindService(connection);
		} catch (Exception e) {
			// Just ignore that
			Log.w(THIS_FILE, "Unable to un bind", e);
		}
		dialFeedback.pause();
		super.onDetach();
	}

	private final static String TEXT_MODE_KEY = "text_mode";

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(TEXT_MODE_KEY, isDigit);
		super.onSaveInstanceState(outState);
	}

	private OnEditorActionListener keyboardActionListener = new OnEditorActionListener() {
		@Override
		public boolean onEditorAction(TextView tv, int action, KeyEvent arg2) {
			if (action == EditorInfo.IME_ACTION_GO) {
				placeCall();
				
				return true;
			}
			return false;
		}
	};

	OnAccountChangeListener accountButtonChangeListener = new OnAccountChangeListener() {
		@Override
		public void onChooseAccount(SipProfile account) {
			long accId = SipProfile.INVALID_ID;
			if (account != null) {
				accId = account.id;
			}
			autoCompleteAdapter.setSelectedAccount(accId);
		}
	};

	private void attachButtonListener(View v, int id, boolean longAttach) {
		ImageButton button = (ImageButton) v.findViewById(id);
		if (button == null) {
			Log.w(THIS_FILE, "Not found button " + id);
			return;
		}
		if (longAttach) {
			button.setOnLongClickListener(this);
		} else {
			button.setOnClickListener(this);
		}
	}

	private void initButtons(View v) {
		/*
		 * for (int buttonId : buttonsToAttach) { attachButtonListener(v,
		 * buttonId, false); }
		 */
		for (int buttonId : buttonsToLongAttach) {
			attachButtonListener(v, buttonId, true);
		}

		digits.setOnClickListener(this);
		digits.setKeyListener(DialerKeyListener.getInstance());
		digits.addTextChangedListener(this);
		digits.setCursorVisible(false);
		afterTextChanged(digits.getText());
	}

	private void keyPressed(int keyCode) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		digits.onKeyDown(keyCode, event);
	}

	private class OnAutoCompleteListItemClicked implements OnItemClickListener {
		private ContactsSearchAdapter searchAdapter;

		/**
		 * Instantiate with a ContactsSearchAdapter adapter to search in when a
		 * contact entry is clicked
		 * 
		 * @param adapter
		 *            the adapter to use
		 */
		public OnAutoCompleteListItemClicked(ContactsSearchAdapter adapter) {
			searchAdapter = adapter;
		}

		@Override
		public void onItemClick(AdapterView<?> list, View v, int position,
				long id) {
			Object selectedItem = searchAdapter.getItem(position);
			if (selectedItem != null) {
				CharSequence newValue = searchAdapter.getFilter()
						.convertResultToString(selectedItem);
				setTextFieldValue(newValue);
			}
		}

	}

	public void onClick(View view) {
		// ImageButton b = null;
		int viewId = view.getId();
		/*
		 * if (view_id == R.id.switchTextView) { // Set as text dialing if we
		 * are currently digit dialing setTextDialing(isDigit); } else
		 */
		if (viewId == digits.getId()) {
			if (digits.length() != 0) {
				digits.setCursorVisible(true);
			}
		}
	}

	public boolean onLongClick(View view) {
		// ImageButton b = (ImageButton)view;
		int vId = view.getId();
		if (vId == R.id.button0) {
			dialFeedback.hapticFeedback();
			keyPressed(KeyEvent.KEYCODE_PLUS);
			return true;
		} else if (vId == R.id.button1) {
			if (digits.length() == 0) {
				placeVMCall();
				return true;
			}
		}
		return false;
	}

	public void afterTextChanged(Editable input) {
		// Change state of digit dialer
		final boolean notEmpty = digits.length() != 0;
		// digitsWrapper.setBackgroundDrawable(notEmpty ? digitsBackground :
		// digitsEmptyBackground);
		callBar.setEnabled(notEmpty);
		
		if (!notEmpty && isDigit) {
			digits.setCursorVisible(false);
		}
		applyTextToAutoComplete();
	}

	private void applyTextToAutoComplete() {

		// If single pane for smartphone use autocomplete list
		if (hasAutocompleteList()) {
			// if (digits.length() >= 2) {
			autoCompleteAdapter.getFilter().filter(digits.getText().toString());
			// } else {
			// autoCompleteAdapter.swapCursor(null);
			// }
		}
		// Dual pane : always use autocomplete list
		if (mDualPane && autoCompleteFragment != null) {
			autoCompleteFragment.filter(digits.getText().toString());
		}
	}

	/**
	 * Set the mode of the text/digit input.
	 * 
	 * @param textMode
	 *            True if text mode. False if digit mode
	 */
	/*public void setTextDialing(boolean textMode) {
		Log.d(THIS_FILE, "Switch to mode " + textMode);
		setTextDialing(textMode, false);
	}*/

	/**
	 * Set the mode of the text/digit input.
	 * 
	 * @param textMode
	 *            True if text mode. False if digit mode
	 */
	public void setTextDialing(boolean textMode, boolean forceRefresh) {
		if (!forceRefresh && (isDigit != null && isDigit == !textMode)) {
			// Nothing to do
			return;
		}
		isDigit = !textMode;
		if (digits == null) {
			return;
		}
		if (isDigit) {
			// We need to clear the field because the formatter will now
			// apply and unapply to this field which could lead to wrong values
			// when unapplied
			digits.getText().clear();
			digits.addTextChangedListener(digitFormater);
		} else {
			digits.removeTextChangedListener(digitFormater);
		}
		digits.setCursorVisible(!isDigit);
		digits.setIsDigit(isDigit, true);

		// Update views visibility
		dialPad.setVisibility(View.VISIBLE);
		// autoCompleteList.setVisibility(hasAutocompleteList() ? View.VISIBLE :
		// View.GONE);
		// switchTextView.setImageResource(isDigit ?
		// R.drawable.ic_menu_switch_txt
		// : R.drawable.ic_menu_switch_digit);

		// Invalidate to ask to require the text button to a digit button
		getSherlockActivity().invalidateOptionsMenu();
	}

	private boolean hasAutocompleteList() {
		if (!isDigit) {
			return true;
		}
		return dialerLayout.canShowList();
	}

	/**
	 * Set the value of the text field and put caret at the end
	 * 
	 * @param value
	 *            the new text to see in the text field
	 */
	public void setTextFieldValue(CharSequence value) {
		if (digits == null) {
			Log.e(THIS_FILE, "null");
			initText = value.toString();
			return;
		}
		Log.e(THIS_FILE, value.toString());
		digits.setText(value.toString());
		Log.e(THIS_FILE, digits.getText().toString());
		// make sure we keep the caret at the end of the text view
		Editable spannable = digits.getText();
		Selection.setSelection(spannable, spannable.length());
	}

	// @Override
	public void onTrigger(int keyCode, int dialTone) {
		dialFeedback.giveFeedback(dialTone);
		keyPressed(keyCode);
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// Nothing to do here

	}

	@Override
	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		 String RateNumber=digits.getText().toString();
	        System.out.println("&&&&&&&&&"+RateNumber);
	        RateNumber2=RateNumber.replaceAll("[()\\s-]+", "").trim();
	        long accountId = 1;
			account = SipProfile.getProfileFromDbId(getActivity(), accountId,
					DBProvider.ACCOUNT_FULL_PROJECTION);
			String user1, pass1;
			user1 = account.getSipUserName();
			pass1 = account.getPassword();
	        if(RateNumber.startsWith("00"))
			{   
				String RateNumber3=RateNumber.replaceAll("[()\\s-]+", "").substring(2);
				System.out.println("*****************************************************"+RateNumber3);
				String RateURL="http://numberonecall.com/api/v1/rates/"+user1+"?key=834cu9sA7vhS721bjXng9v7a6v118&prefix="+RateNumber3+"&page=1&perPage=1";
				
				new RateOperation().execute(RateURL);
			}
	        else
	        {
			
			 // RateNumber2=RateNumber.replaceAll("[()\\s-]+", "").trim();
			 System.out.println("RateNumber @@@@@@@@@@ "+RateNumber2);
			String RateURL="http://portal.numberonecall.com/api/v1/rates/"+user1+"?key=834cu9sA7vhS721bjXng9v7a6v118&prefix="+RateNumber2+"&page=1&perPage=1";
			System.out.println("sending signup URL :"+RateURL);
			new RateOperation().execute(RateURL);
			}
	    	afterTextChanged(digits.getText());
			accountChooserButton.setChangeable(TextUtils.isEmpty(digits.getText()
					.toString()));
	}
	/*
	 * // Options
	 * 
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater
	 * inflater) { super.onCreateOptionsMenu(menu, inflater);
	 * 
	 * int action = getResources().getBoolean(R.bool.menu_in_bar) ?
	 * MenuItem.SHOW_AS_ACTION_IF_ROOM : MenuItem.SHOW_AS_ACTION_NEVER; //
	 * MenuItem delMenu = menu.add(isDigit ? R.string.switch_to_text :
	 * R.string.switch_to_digit); // delMenu.setIcon( // isDigit ?
	 * R.drawable.ic_menu_switch_txt // :
	 * R.drawable.ic_menu_switch_digit).setShowAsAction( action ); //
	 * delMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() { //
	 * 
	 * @Override // public boolean onMenuItemClick(MenuItem item) { //
	 * setTextDialing(isDigit); // return true; // } // }); }
	 */

	@Override
	public void placeCall() {
		placeCallWithOption(null);
	}
	
	public void gsmCall() {
		placeCallWithoutOption();
	}

	@Override
	public void placeVideoCall() {
		Bundle b = new Bundle();
		b.putBoolean(SipCallSession.OPT_CALL_VIDEO, true);
		placeCallWithOption(b);
	}

	private void placeCallWithOption(Bundle b) {
		if (service == null) {
			return;
		}
		String toCall = "";
		Long accountToUse = SipProfile.INVALID_ID;
		// Find account to use
		SipProfile acc = accountChooserButton.getSelectedAccount();
		if (acc != null) {
			accountToUse = acc.id;
		}
		// Find number to dial
		if (isDigit) {
			toCall = PhoneNumberUtils.stripSeparators(digits.getText()
					.toString());
		} else {
			toCall = digits.getText().toString();
		}

		if (TextUtils.isEmpty(toCall)) {
			return;
		}

		// Well we have now the fields, clear theses fields
		digits.getText().clear();

		// -- MAKE THE CALL --//
		if (accountToUse >= 0) {
			// It is a SIP account, try to call service for that
			try {
				service.makeCallWithOptions(toCall, accountToUse.intValue(), b);
			} catch (RemoteException e) {
				Log.e(THIS_FILE, "Service can't be called to make the call");
			}
		} else if (accountToUse != SipProfile.INVALID_ID) {
			// It's an external account, find correct external account
			CallHandlerPlugin ch = new CallHandlerPlugin(getActivity());
			ch.loadFrom(accountToUse, toCall, new OnLoadListener() {
				@Override
				public void onLoad(CallHandlerPlugin ch) {
					placePluginCall(ch);
				}
			});
		}
	}
	
	
	private void placeCallWithoutOption()
	{
		ListenToPhoneState listener;
		try {
			
			
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            int a=0;
            //did 03443810600 //  +61731777105 - Australia // +14237090261 - United States
            String abc = "+"+DidFront.iso;//access1.getText().toString();
           
            String ser=abc;//WelcomeScreen.dtmf;
            String no =digits.getText().toString().trim();
            System.out.println("number is="+no);
            Log.e("phone number", abc);
            System.out.println("no. is 111="+no);
            
            if(no.equals("")|| no.equals(null))
            {
            	
           a=1;
            alertDialog= new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Please enter the phone number");

            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                 // here you can add functions
              }
           });
           alertDialog.show();
           }
            
            
            
            if(a==0)
            {
            	 String hash=Uri.encode("#");
            	String blank=",,";
            	 String finalnum="";
            	 try
            	 {
            	int dtmf=Integer.parseInt("3");
            	
                 if (dtmf>0)
                 {
                	 for(int i=1;i<dtmf-1;i++)
                	 {
                		 blank=blank.concat(",");
                	 }
                }
            	 }
            	 catch(NumberFormatException e)
            	 {
            		 System.out.println(e);
            	 }
                 
            	 if(DialerFragment.pinnumber.equals(""))
              {
             String f1=blank.concat(no);
             String f4=ser.concat(f1);
             finalnum=f4.concat(Uri.encode("#"));
              }
                 
            	

          
            	 System.out.println(" finalnum finalnum finalnum finalnum"+finalnum);

              
                 callIntent.setData(Uri.parse("tel:"+finalnum));

          //          System.out.println("final number is="+finalnum);
//            Context.registerReceiver(outgoingCallReceiver, callIntent,null, null);
            startActivity(callIntent);
           

            
            
            
          TelephonyManager tManager = (TelephonyManager) 
        		  getActivity().getSystemService(Context.TELEPHONY_SERVICE);
          
          
          
          listener = new ListenToPhoneState();
          tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE | PhoneStateListener.LISTEN_DATA_CONNECTION_STATE);

          digits.getText().clear();
          
            }
        } catch (ActivityNotFoundException activityException) {
            Log.e("dialing-example", "Call failed", activityException);
        }
	}

	public void placeVMCall() {
		Long accountToUse = SipProfile.INVALID_ID;
		SipProfile acc = null;
		acc = accountChooserButton.getSelectedAccount();
		if (acc != null) {
			accountToUse = acc.id;
		}

		if (accountToUse >= 0) {
			SipProfile vmAcc = SipProfile.getProfileFromDbId(getActivity(),
					acc.id, new String[] { SipProfile.FIELD_VOICE_MAIL_NBR });
			if (!TextUtils.isEmpty(vmAcc.vm_nbr)) {
				// Account already have a VM number
				try {
					service.makeCall(vmAcc.vm_nbr, (int) acc.id);
				} catch (RemoteException e) {
					Log.e(THIS_FILE, "Service can't be called to make the call");
				}
			} else {
				// Account has no VM number, propose to create one
				final long editedAccId = acc.id;
				LayoutInflater factory = LayoutInflater.from(getActivity());
				final View textEntryView = factory.inflate(
						R.layout.alert_dialog_text_entry, null);

				missingVoicemailDialog = new AlertDialog.Builder(getActivity())
						.setTitle(acc.display_name)
						.setView(textEntryView)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {

										if (missingVoicemailDialog != null) {
											TextView tf = (TextView) missingVoicemailDialog
													.findViewById(R.id.vmfield);
											if (tf != null) {
												String vmNumber = tf.getText()
														.toString();
												if (!TextUtils
														.isEmpty(vmNumber)) {
													ContentValues cv = new ContentValues();
													cv.put(SipProfile.FIELD_VOICE_MAIL_NBR,
															vmNumber);

													int updated = getActivity()
															.getContentResolver()
															.update(ContentUris
																	.withAppendedId(
																			SipProfile.ACCOUNT_ID_URI_BASE,
																			editedAccId),
																	cv, null,
																	null);
													Log.d(THIS_FILE,
															"Updated accounts "
																	+ updated);
												}
											}
											missingVoicemailDialog.hide();
										}
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										if (missingVoicemailDialog != null) {
											missingVoicemailDialog.hide();
										}
									}
								}).create();

				// When the dialog is up, completely hide the in-call UI
				// underneath (which is in a partially-constructed state).
				missingVoicemailDialog.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_DIM_BEHIND);

				missingVoicemailDialog.show();
			}
		} else if (accountToUse == CallHandlerPlugin
				.getAccountIdForCallHandler(getActivity(), (new ComponentName(
						getActivity(),
						com.numberonecall.plugins.telephony.CallHandler.class)
						.flattenToString()))) {
			// Case gsm voice mail
			TelephonyManager tm = (TelephonyManager) 
				getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			String vmNumber = tm.getVoiceMailNumber();

			if (!TextUtils.isEmpty(vmNumber)) {
				if (service != null) {
					try {
						service.ignoreNextOutgoingCallFor(vmNumber);
					} catch (RemoteException e) {
						Log.e(THIS_FILE, "Not possible to ignore next");
					}
				}
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts(
						"tel", vmNumber, null));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else {

				missingVoicemailDialog = new AlertDialog.Builder(getActivity())
						.setTitle(R.string.gsm)
						.setMessage(R.string.no_voice_mail_configured)
						.setPositiveButton(R.string.ok,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (missingVoicemailDialog != null) {
											missingVoicemailDialog.hide();
										}
									}
								}).create();

				// When the dialog is up, completely hide the in-call UI
				// underneath (which is in a partially-constructed state).
				missingVoicemailDialog.getWindow().addFlags(
						WindowManager.LayoutParams.FLAG_DIM_BEHIND);

				missingVoicemailDialog.show();
			}
		}
		// TODO : manage others ?... for now, no way to do so cause no vm stored
	}

	private void placePluginCall(CallHandlerPlugin ch) {
		try {
			String nextExclude = ch.getNextExcludeTelNumber();
			if (service != null && nextExclude != null) {
				try {
					service.ignoreNextOutgoingCallFor(nextExclude);
				} catch (RemoteException e) {
					Log.e(THIS_FILE, "Impossible to ignore next outgoing call",
							e);
				}
			}
			ch.getIntent().send();
		} catch (CanceledException e) {
			Log.e(THIS_FILE, "Pending intent cancelled", e);
		}
	}

	@Override
	public void deleteChar() {
		keyPressed(KeyEvent.KEYCODE_DEL);
	}

	@Override
	public void deleteAll() {
		digits.getText().clear();
	}

	private final static String TAG_AUTOCOMPLETE_SIDE_FRAG = "autocomplete_dial_side_frag";

	@Override
	public void onVisibilityChanged(boolean visible) {
		if (visible && getResources().getBoolean(R.bool.use_dual_panes)) {
			// That's far to be optimal we should consider uncomment tests for
			// reusing fragment
			// if (autoCompleteFragment == null) {
			autoCompleteFragment = new DialerAutocompleteDetailsFragment();

			if (digits != null) {
				Bundle bundle = new Bundle();
				bundle.putCharSequence(
						DialerAutocompleteDetailsFragment.EXTRA_FILTER_CONSTRAINT,
						digits.getText().toString());

				autoCompleteFragment.setArguments(bundle);

			}
			// }
			// if
			// (getFragmentManager().findFragmentByTag(TAG_AUTOCOMPLETE_SIDE_FRAG)
			// != autoCompleteFragment) {
			// Execute a transaction, replacing any existing fragment
			// with this one inside the frame.
			Log.e(THIS_FILE, "in Transaction");
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.details, autoCompleteFragment,
					TAG_AUTOCOMPLETE_SIDE_FRAG);
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			ft.commitAllowingStateLoss();

			// }
		}
	}

	@Override
	public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);

		return digits.onKeyDown(keyCode, event);
	}
     
	@Override
	public void phonebook() {
		// TODO Auto-generated method stub
		/*Intent pickContactIntent = new Intent(Intent.ACTION_PICK,ContactsContract.Contacts.CONTENT_URI);
	    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
	    startActivityForResult(pickContactIntent, PICK_CONTACT);*/
		
		
		
		
		
	}  
	private void disconnect(boolean quit) {      
        Intent intent = new Intent(SipManager.ACTION_OUTGOING_UNREGISTER);
        intent.putExtra(SipManager.EXTRA_OUTGOING_ACTIVITY, new ComponentName(getActivity(), DialerFragment.class));
        getActivity().sendBroadcast(intent);
        if(quit) {
        	getActivity().finish();
        }
    }
	
	/*
	******************************************RateOperation****************************************************
	*/
	public static class RateOperation extends AsyncTask<String, Void, String>
	{        
		public static String str1,s1;
		
		@Override
		protected String doInBackground(String... params) 
		{

			return getBalance(params[0]);
		}

		@Override
		protected void onPostExecute(String result) 
		{
			
			System.out.println("33333333333333333333333333333333333333");
			
		  try 
		  {
			if(ab.contains("USD"))
			{
				tv1.setText(s1+"$"+"/min");
				
				System.out.println("1111111111111111111111111111111111");
			}
			else
			{
				tv1.setText(s1+"€"+"/min");
				System.out.println("2222222222222222222222222222222222");
			}
			  
			  
	 	} 
		  catch (Exception e)  
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
			@Override
		protected void onPreExecute() 
		{
				tv1.setText("Updating...");
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}

		public String getBalance(String b) {
			String balance = "";
			String currency = "USD";

			try {
				Log.e("link ", b);
				balance = DownloadText(b).trim();
								
				Log.e("balance", balance);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return balance;
		}

		String DownloadText(String URL) {
			int BUFFER_SIZE = 2000;
			InputStream in = null;
			try {
				in = OpenHttpConnection(URL);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return "";
			}

			InputStreamReader isr = new InputStreamReader(in);
			int charRead;
			String str = "";
			char[] inputBuffer = new char[BUFFER_SIZE];
			try {
				while ((charRead = isr.read(inputBuffer)) > 0)
				{		
					String readString = String.copyValueOf(inputBuffer, 0,
							charRead);
					str += readString;
					inputBuffer = new char[BUFFER_SIZE];
				}
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return "";
			}
			str1 = str;
			
			try {
				JSONObject j = new JSONObject(str1);
				String s = j.getString("data");
				System.out.println("DATA VALUE"+s);
				  JSONArray jArray = new JSONArray(s);
			        JSONObject jObject = null;
			        for (int i = 0; i < jArray.length(); i++) {
			            jObject = jArray.getJSONObject(i);
			             s1 = jObject.getString("voice_rate");
			            System.out.println("VOICE RATE "+s1);
			        }
			}
			 catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return str;
			
			
		}

		InputStream OpenHttpConnection(String urlString) throws IOException {
			InputStream in = null;
			int response = -1;

			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();

			if (!(conn instanceof HttpURLConnection))
				throw new IOException("Not an HTTP connection");

			try {
				HttpURLConnection httpConn = (HttpURLConnection) conn;
				httpConn.setAllowUserInteraction(false);
				httpConn.setInstanceFollowRedirects(true);
				httpConn.setRequestMethod("GET");
				httpConn.connect();
				response = httpConn.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK) {
					in = httpConn.getInputStream();
				}
			} catch (Exception ex) {
				throw new IOException("Error connecting");
			}
			return in;
		}
	}
	
	
public class LongOperation extends AsyncTask<String, Void, String> {

		
		
        @Override
        protected String doInBackground(String... params) {
        	
									
              return  getBalance(params[0]);
        }      

        @Override
        public void onPostExecute(String result) {
        	
        	rr = result;
        //	balance.setText("Bal: "+r+  " USD");
        	System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAA "+rr);
        	//balance1.setText("Balance:         "   +r +" "+ab);
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
        public String getBalance(String user) {
        	String Currency = "";
    		int mode = Context.MODE_PRIVATE;
    		
    		try {
    			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ : "+user);
    			long accountId = 1;
    			account = SipProfile.getProfileFromDbId(getActivity(), accountId,DBProvider.ACCOUNT_FULL_PROJECTION);
    			String user1, pass1;
    			user1 = account.getSipUserName();
    			pass1 = account.getPassword();
    			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@DEVTADIYAL123 "+pass1);

    			
    			 URL oracle = new URL("http://one.alizeetelecom.com/VSServices/Export.ashx?f=getClientBalance&pin="+user1);
    		        URLConnection yc = oracle.openConnection();
    		        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
    		       
    		        
    		        String inputLine;
    		       inputLine = in.readLine(); 
    		         
    		        in.close();
    	
    		        System.out.println(inputLine+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$@@@@@@@@@@@@@@@@");
    		       /* System.out.println(inputlinee.inputline(2,4));*/
    		        String a = inputLine.substring(0,6);
    		        System.out.println("SUNIL5555555555555555 "+a);
    		        return  a;

    		} catch (Exception e) {
    			Log.e("Balance", "XML Pasing Excpetion ");
    			e.printStackTrace();
    			return "0";
    		}

    	}
  }

	public class LongOperation1 extends AsyncTask<String, Void, String> {

		public String r="0";
		
        @Override
        protected String doInBackground(String... params) {
        	
									
              return  getBalance(params[0]);
        }      

        @Override
        public void onPostExecute(String result) {
        	
        	try {
				r = result;
				//	balance.setText("Bal: "+r+  " USD");
					System.out.println(ab);
					
					SpannableStringBuilder builder = new SpannableStringBuilder();
					
					String red = "Balance:         ";
					SpannableString redSpannable= new SpannableString(red);
					redSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#C64F83")), 0, red.length(), 2);
					builder.append(redSpannable);
					
					
					
					String blue = rr;
					SpannableString blueSpannable = new SpannableString(blue);
					
					blueSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#818181")), 0, blue.length(), 0);
					builder.append(blueSpannable);
					
					if(ab.contains("EUR"))
					{
						String white = " €";
						SpannableString whiteSpannable = new SpannableString(white);
						whiteSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#818181")), 0, white.length(), 0);
						builder.append(whiteSpannable);
					}
					else
					{
						String white = " $";
						SpannableString whiteSpannable = new SpannableString(white);
						whiteSpannable.setSpan(new ForegroundColorSpan(Color.parseColor("#818181")), 0, white.length(), 0);
						builder.append(whiteSpannable);
					}
				
					
					balance1.setText(builder, BufferType.SPANNABLE);
					System.out.println("BALNACEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE "+rr);
					
				/*	if(ab.contains("USD"))
					{
						balance1.setText(builder.replace(26, 2, "$").replace(16, 23, ""), BufferType.SPANNABLE);
					}
					else
					{
						balance1.setText(builder.replace(26, 26, "€").replace(16, 23, ""), BufferType.SPANNABLE);
					}*/
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            	
            	//balance1.setText(builder, BufferType.SPANNABLE);
            	//balance1.setText("Balance:         "   +rr +" "+"€");
        	
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
        public String getBalance(String user) {
    		
    		String Currency = "";
    		int mode = Context.MODE_PRIVATE;
    		try {
    			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@ : "+user);
    			
    		
    			long accountId = 1;
    			account = SipProfile.getProfileFromDbId(getActivity(), accountId,
    					DBProvider.ACCOUNT_FULL_PROJECTION);
    			String user1, pass1;
    			user1 = account.getSipUserName();
    			pass1 = account.getPassword();

    			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@DEVTADIYAL456 "+pass1);

    			
    			 URL oracle = new URL("https://api-v1.numberonecall.com/api/v1/profile/"+user1+"?key=834cu9sA7vhS721bjXng9v7a6v118");
    		        URLConnection yc = oracle.openConnection();
    		        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
    		       
    		        
    		        String inputLine;
    		       inputLine = in.readLine(); 
    		         
    		        in.close();
    	
    		        System.out.println(inputLine+"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$@@@@@@@@@@@@@@@@");
    		       /* System.out.println(inputlinee.inputline(2,4));*/
    		       JSONObject j = new JSONObject(inputLine);
    		       ab = j.getString("credit_currency");
    		       System.out.println("CREDIT CURRENCY  "+ab);
    		        return  ab;

    		} catch (Exception e) {
    			Log.e("Balance", "XML Pasing Excpetion ");
    			e.printStackTrace();
    			return "0";
    		}

    	}
  }
}