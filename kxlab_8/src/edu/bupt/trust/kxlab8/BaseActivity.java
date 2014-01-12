package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.Settings;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.widgets.BottomBar;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic.BasicDialogListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class BaseActivity extends ActionBarActivity  implements BasicDialogListener {
	
	public static final int RESULT_FINISH = 11; 
	
	protected Toast toastMsg;
	protected Settings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = Settings.getInstance(getApplicationContext());
        setLanguage();
        loadViews();
	}
	
	protected void loadViews(){ }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return true;
	}

    /** Processes the action bar buttons. Returns false if the menu item is not found. */
    @Override public boolean onOptionsItemSelected(MenuItem item) {
//        int itemId = item.getItemId();
//		if (itemId == android.R.id.home) {
//			// The super should know who the home is, so have the super process it.
//			return super.onOptionsItemSelected(item);
//		}else{
//			// else, the option item is unknown
//			postToast(getString(R.string.unknownBtn));
//		}
		
		return super.onOptionsItemSelected(item);
	}
	
    /** Processes the default button pressed method. By default it assumes it is an unknown buttons. */
	public void onBtnClick(View view) {
		int id = view.getId();
		switch(id){
		case R.id.footer_services:
		case R.id.footer_myservice:
			// create an bundle with the specific flavor
			Bundle b = new Bundle();
			if(id == R.id.footer_myservice)
				b.putSerializable(Gegevens.EXTRA_FLAVOR, ServiceFlavor.MYSERVICE);
			else
				b.putSerializable(Gegevens.EXTRA_FLAVOR, ServiceFlavor.SERVICE);
				
			// add the bundle to the intent and open the activity
			Intent intent = new Intent(this, ServicesListActivity.class);
			intent.putExtra(Gegevens.EXTRA_MSG, b);
			
			if(this instanceof ServicesListActivity){
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
			} else{
					openActivity(intent);
			}
		break;
		case R.id.footer_forum:
			openActivity(new Intent(this, ForumThreadActivity.class));
		break;
		case R.id.footer_myinformation:
			if(mSettings.getUser().isLogin()){
				openActivity(new Intent(this, MyInformationActivity.class));
			} else{
				userMustClickOkay(getString(R.string.myinfo_guest_title), getString(R.string.myinfo_guest_text));
			}
		break;
		case R.id.footer_other:
			openActivity(new Intent(this, OtherActivity.class));
		break;
		default:
			postToast(getString(R.string.unknownBtn));
		}
	}
    
	/** Check if we have network connectivity. No point in trying anything online if we have no connection. */
	protected static boolean isNetworkAvailable(Context c) {
	    ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
	}
	
	/** Tests to see if the uri is available. Use this method to check if an another app link works. */
	public boolean isUriAvailable(String uri) {
	    Intent test = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
	    return getPackageManager().resolveActivity(test, 0) != null;
	}
	
	/** Check to see whether or not we should save this as the last activity. 
	 *  Default response is true. Overwrite it for activities that you don't want to save. */
	protected boolean saveAsLastActivity(){ return true; }
	
	/** Launch a confirmation dialog from the activity context */
	protected void userMustClickOkay(String title, String message){
		DialogFragmentBasic.newInstance(false).setTitle(title).setMessage(message)
			.setPositiveButtonText(getString(R.string.ok))
			.show(getSupportFragmentManager(), Gegevens.FRAG_DIALOG);
	}
	
	/** Launch a confirmation dialog from the activity context that will finish the activity after the user clicks okay. */
	protected void userMustClickExit(String title, String message){
		DialogFragmentBasic d = DialogFragmentBasic.newInstance(false).setTitle(title).setMessage(message);
		d.setCancelable(false);
		d.show(getSupportFragmentManager(), Gegevens.FRAG_FINISH);
	}
	
	/** Launch a toast message. */
	protected void postToast(String text){

		// change or create the toast text
		if(toastMsg == null){
			toastMsg = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			toastMsg.setGravity(Gravity.CENTER, 0, 0);
		}else{
			toastMsg.setText(text);
		}
		
		// show the toast message
		toastMsg.show();
	}
	
	/** Starts a new activity with FLAG_ACTIVITY_REORDER_TO_FRONT using the given intent and close this one */
	protected void openActivity(Intent i) {

		// add the clear top 
		if(i != null ) { i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT); }

		// start the activity
		startActivity(i);
		finish();
	}

	/** <p>This methods sets the language of the application.</p> */
	protected void setLanguage(){
		
		Resources res = getResources();
		Configuration newConfig = new Configuration(res.getConfiguration());

		if(mSettings.getLocale() != null){
			newConfig.locale = mSettings.getLocale();
			res.updateConfiguration(newConfig, null);
		}else{
			// save the default language to the preferences
			mSettings.setLocale(newConfig.locale.getLanguage());
			PreferenceManager.getDefaultSharedPreferences(this)
				.edit().putInt(Gegevens.PREF_LANGUAGE, mSettings.getLanguageSelectionId()).commit();
		}
	}
	
	protected void setupBottomBar(int botttomBarResourceId, int selectedViewResourceId){
	    // assign the activated bottombar
		((BottomBar) findViewById(botttomBarResourceId)).setActivatedView(selectedViewResourceId);
	}
	

    // TODO: Implement life cycle methods
	protected void onPause() { super.onPause(); }
    protected void onResume() {  super.onResume(); }
    protected void onStart() { super.onStart(); }
    protected void onRestart() { super.onRestart(); }
    protected void onStop() { 	super.onStop(); }
    protected void onDestroy() { super.onDestroy(); }

	@Override public void onBasicPositiveButtonClicked(String tag, Object o) { if(Gegevens.FRAG_FINISH.equals(tag)){ finish(); } }
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { }

}
