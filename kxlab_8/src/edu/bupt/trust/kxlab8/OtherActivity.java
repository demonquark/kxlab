package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;

public class OtherActivity extends BaseDetailActivity {

	OtherSettingsFragment viewFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		
		// enable back stack navigation
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_other).setEnabled(false);
		
		// Create the settings fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {
			// launch the view fragment
			viewFragment = new OtherSettingsFragment();
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.details, viewFragment, Gegevens.FRAG_SETTINGS);
			ft.commit();
		}
		
		onBackStackChanged();
	}
	
	@Override protected void onSaveInstanceState(Bundle outState) {
		Loggen.v(this, "Saving instance state of the services activity.");
		// save the selected tab to the instance state
		super.onSaveInstanceState(outState);
	}


	/** Callback from the fragment
	 * This implementation processes settings that require an additional fragment: <br />
	 * From: irrelevant
	 * - Goal = FRAG_INFOLIST: The user selected an item and needs a new .
	 * @param from - The tag of the sending fragment. Note: is NOT the same as the name in the back stack
	 * @param to - The tag of the target fragment. Note: is NOT the same as the name in the back stack
	 * @param o - Should be a string indicating the name of the setting. (Not sure about this... Might change...)
	 */
	@Override public void onActionSelected(String from, String to, Object o) {
		if(Gegevens.FRAG_INFOLIST.equals(to) && o instanceof String){
			// create a bundle and add the provided user to it
			Bundle arguments = new Bundle();
			arguments.putString(Gegevens.EXTRA_MSG, (String) o ); 
			
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			// launch the edit fragment
			OtherSettingsFragment listFragment = new OtherSettingsFragment();
			listFragment.setArguments(arguments);
			ft.replace(R.id.details, listFragment, Gegevens.FRAG_INFOLIST);
			ft.addToBackStack(Gegevens.FRAG_INFOLIST);
			ft.commit();
		}
	}
	
}
