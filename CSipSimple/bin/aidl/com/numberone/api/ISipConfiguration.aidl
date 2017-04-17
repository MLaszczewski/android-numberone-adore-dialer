
package com.numberone.api;
import com.numberone.api.SipProfile;

interface ISipConfiguration {
	
	//Prefs
	void setPreferenceString(in String key, in String value);
	void setPreferenceBoolean(in String key, boolean value);
	void setPreferenceFloat(in String key, float value);
	
	String getPreferenceString(in String key);
	boolean getPreferenceBoolean(in String key);
	float getPreferenceFloat(in String key);
	
}