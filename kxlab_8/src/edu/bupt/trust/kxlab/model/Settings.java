package edu.bupt.trust.kxlab.model;

import java.io.File;
import java.util.Locale;

import com.google.gson.GsonBuilder;

import edu.bupt.trust.kxlab.utils.FileManager;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

public class Settings {

	private static volatile Settings instance = null;
	
	// The various items saved in shared preferences
	public static enum Language { ENGLISH, SIMPLIFIED_CHINESE, DUTCH; };
	
	// The current status of the application
	public Language language;
	public User user;
	
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
    	loadSettingsFromSharedPreferences(c);
		if(Gegevens.debug){
			// Get the application directory
			File appdir = new File(Environment.getExternalStorageDirectory(),Gegevens.FILE_USERDIRSD);
			
			// Check if the dummy data has already been copied (bit of a hack)
			File dummycheck = new File (appdir, 
					Gegevens.FILE_CACHE + Gegevens.FILE_SEPARATOR + Gegevens.FILE_DUMMYCHECK);

			if(!dummycheck.exists()){
				Loggen.v(this, "Copying assets to the SD card.");
				// copy the asset files to the SD card
				if(!appdir.exists()){ appdir.mkdirs(); }
				FileManager.copyAssetsToSDCard(c.getAssets(), false,
						Gegevens.APP_NAME, appdir.getAbsolutePath());
			}
		}
	}
    
    public User setUser(User user){
    	if(user != null){ this.user = user; }    	
    	return this.user;
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
	
    public User getUser(){
    	return user;
    }
    
    public void loadSettingsFromSharedPreferences(Context c){
		// Get the all the shared preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
    	setLanguage(prefs.getInt(Gegevens.PREF_LANGUAGE, -1));
    	
    	String userjson = prefs.getString(Gegevens.PREF_USER, "");
        if (userjson.equals("")) {
        	user = new User();
        } else {
            user = new GsonBuilder().create().fromJson(userjson, User.class);
        }
    }

    public void saveSettingsToSharedPreferences(Context c){
		// Get the all the shared preferences
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
    	prefs.edit().putInt(Gegevens.PREF_LANGUAGE, getLanguageSelectionId()).commit();
    	prefs.edit().putString(Gegevens.PREF_USER, new GsonBuilder().create().toJson(user)).commit();
    }
}
