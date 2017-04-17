package com.numberone.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.numberone.R;
import com.numberone.api.SipProfile;
import com.numberone.widgets.EditSipUri;
import com.numberone.widgets.EditSipUri.ToCall;

public class PickupSipUri extends Activity implements OnClickListener {

	private EditSipUri sipUri;
	private Button okBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pickup_uri);
		
		
		//Set window size
//		LayoutParams params = getWindow().getAttributes();
//		params.width = LayoutParams.FILL_PARENT;
//		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
		
		//Set title
		// TODO -- use dialog instead
//		((TextView) findViewById(R.id.my_title)).setText(R.string.pickup_sip_uri);
//		((ImageView) findViewById(R.id.my_icon)).setImageResource(android.R.drawable.ic_menu_call);
		
		
		okBtn = (Button) findViewById(R.id.ok);
		okBtn.setOnClickListener(this);
		Button btn = (Button) findViewById(R.id.cancel);
		btn.setOnClickListener(this);

		
		sipUri = (EditSipUri) findViewById(R.id.sip_uri);
		sipUri.getTextField().setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView tv, int action, KeyEvent arg2) {
				if(action == EditorInfo.IME_ACTION_GO) {
					sendPositiveResult();
					return true;
				}
				return false;
			}
		});
		sipUri.setShowExternals(false);
		
		
	}
	
	
	@Override
	public void onClick(View v) {
		int vId = v.getId();
		if (vId == R.id.ok) {
			sendPositiveResult();
		} else if (vId == R.id.cancel) {
			setResult(RESULT_CANCELED);
			finish();
		}
	}

	private void sendPositiveResult() {
		Intent resultValue = new Intent();
		 ToCall result = sipUri.getValue();
		 if(result != null) {
		     // Restore existing extras.
		     Intent it = getIntent();
		     if(it != null) {
		         Bundle b = it.getExtras();
		         if(b != null) {
		             resultValue.putExtras(b);
		         }
		     }
			 resultValue.putExtra(Intent.EXTRA_PHONE_NUMBER,
						result.getCallee());
			 resultValue.putExtra(SipProfile.FIELD_ID,
						result.getAccountId());
			 setResult(RESULT_OK, resultValue);
		 }else {
			setResult(RESULT_CANCELED);
		 }
		finish();
	}
	
}
