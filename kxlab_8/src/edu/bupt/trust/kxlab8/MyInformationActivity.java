package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;

public class MyInformationActivity extends BaseDetailActivity {

	MyInformationViewFragment viewFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		
		// enable back stack navigation
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_myinformation).setEnabled(false);
		
		// Create the my information fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {
			// create a bundle and add the default user to it
			Bundle arguments = new Bundle();
			if(mSettings.getUser() != null){ arguments.putParcelable(Gegevens.EXTRA_USER, mSettings.getUser()); }
			
			// launch the view fragment
			viewFragment = new MyInformationViewFragment();
			viewFragment.setArguments(arguments);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.add(R.id.details, viewFragment, Gegevens.FRAG_INFOVIEW);
			ft.commit();
		}
		
		onBackStackChanged();
	}

	/** Callback from the fragment
	 * This implementation processes two use cases: <br />
	 * From: FRAG_INFOVIEW
	 * - Goal = FRAG_INFOEDIT: The user selected the edit button in the action bar.
	 * - Goal = FRAG_INFOLIST: The user selected the activity records button
	 * @param from - The tag of the sending fragment. Note: should also be the name in the back stack
	 * @param to - The tag of the target fragment. Note: should also be the name in the back stack
	 * @param o - The method expects this to be an instance of User. 
	 */
	@Override public void onActionSelected(String from, String to, Object user) {
		if((Gegevens.FRAG_INFOEDIT.equals(to) || Gegevens.FRAG_INFOLIST.equals(to))
				&& Gegevens.FRAG_INFOVIEW.equals(from)){
			// create a bundle and add the provided user to it
			Bundle arguments = new Bundle();
			if(user instanceof User) { 
				arguments.putParcelable(Gegevens.EXTRA_USER, (User) user); 
			} else {
				this.postToast(getString(R.string.myinfo_eror_user_edit));
			}
			
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			if(Gegevens.FRAG_INFOEDIT.equals(to)){
				// launch the edit fragment
				MyInformationEditFragment editFragment = new MyInformationEditFragment();
				editFragment.setArguments(arguments);
				ft.replace(R.id.details, editFragment, Gegevens.FRAG_INFOEDIT);
				ft.addToBackStack(Gegevens.FRAG_INFOEDIT);
			} else if (Gegevens.FRAG_INFOLIST.equals(to)) {
				// launch the edit fragment
				MyInformationRecordsFragment editFragment = new MyInformationRecordsFragment();
				editFragment.setArguments(arguments);
				ft.replace(R.id.details, editFragment, Gegevens.FRAG_INFOLIST);
				ft.addToBackStack(Gegevens.FRAG_INFOLIST);
			}
			
			ft.commit();
		}
	}
}
