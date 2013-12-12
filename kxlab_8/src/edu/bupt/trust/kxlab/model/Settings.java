package edu.bupt.trust.kxlab.model;

import java.util.Locale;

import edu.bupt.trust.kxlab.utils.Gegevens;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {

	private static volatile Settings instance = null;
	
	// The various items saved in shared preferences
	public static enum Language { ENGLISH, SIMPLIFIED_CHINESE, DUTCH; };
	
	// The current status of the application
	public Language language;
	public String email;
	
	/** Get the instance of the singleton */
    public static Settings getInstance(Context c) {
    	if(instance == null) { synchronized (Settings.class){
    			if(instance == null) { instance = new Settings (c); }
    	} }
    	// return the instance
    	return instance;
    }

	/** Creates a settings. This stops us from loading the preferences separately. */
    private Settings(Context c){

		// Get the all the shared preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
    	setLanguage(prefs.getInt(Gegevens.PREF_LANGUAGE, -1));
    	email = prefs.getString(Gegevens.PREF_EMAIL, "");
	}
    
    public String setEmail(String userEmail){
    	if(Settings.isValidEmail(userEmail)){
    		this.email = userEmail;
    	}else{
    		throw new IllegalArgumentException();
    	}
    	
    	return email;
    }
    
	public int setLanguage(int preference) {
		if(preference < 0 || preference >= Language.values().length) { language = null;
		}else{ language = Language.values()[preference]; }
		return preference;
	}

	public void setLocale(String languageCode){
		if(languageCode != null) {
			if("zh".equalsIgnoreCase(languageCode)){ 
				language = Language.SIMPLIFIED_CHINESE;
			}else if("nl".equalsIgnoreCase(languageCode)){ 	
				language = Language.DUTCH;
			}else{
				language = Language.ENGLISH;
			}
		}
	}

	public int getLanguageSelectionId(){
		for(int i = 0; i < Language.values().length;i++){ if(language == Language.values()[i]){ return i; }}
		return 0;
	}

	public Locale getLocale(){
		if(language == null) {return null;}
		switch(language){
			case ENGLISH: return Locale.US;
			case SIMPLIFIED_CHINESE: return Locale.SIMPLIFIED_CHINESE;
			case DUTCH: return new Locale("nl", "NL");
		}
		return null;
	}
	
    public String getEmail(){
    	return email;
    }

	public static boolean isValidEmail(String email){
		java.util.regex.Pattern p = java.util.regex.Pattern.compile(".+@.+\\.[a-z]+");
		java.util.regex.Matcher m = p.matcher(email);
		return m.matches();
	}
}
