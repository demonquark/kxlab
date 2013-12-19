package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab8.BaseDetailFragment.OnActionSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

public class MyInformationActivity extends BaseActivity implements OnActionSelectedListener, OnBackStackChangedListener{

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
			ft.add(R.id.details, viewFragment,Gegevens.FRAG_INFOVIEW);
			ft.commit();
		}
	}

	/**
	 * This implementation only processes on use case of the action selected, namely <br />
	 * - The user selected the edit button in the action bar.
	 * @param tag - The tag of the fragment. Note: should also be the name in the back stack
	 * @param o - The method expects this to be an instance of User. 
	 */
	@Override public void onActionSelected(String tag, Object user) {
		if(Gegevens.FRAG_INFOVIEW.equals(tag)){
			// create a bundle and add the provided user to it
			Bundle arguments = new Bundle();
			if(user instanceof User) { 
				arguments.putParcelable(Gegevens.EXTRA_USER, (User) user); 
			} else {
				this.postToast(getString(R.string.myinfo_eror_user_edit));
			}
			
			// launch the edit fragment
			MyInformationEditFragment editFragment = new MyInformationEditFragment();
			editFragment.setArguments(arguments);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ft.replace(R.id.details, editFragment,Gegevens.FRAG_INFOEDIT);
			ft.addToBackStack(Gegevens.FRAG_INFOEDIT);
			ft.commit();
		}
	}
	
	@Override public void performBackPress() { super.onBackPressed(); }

	
	/** Checks if we should display a "back" arrow in the action bar. */
	@Override public void onBackStackChanged() {
		boolean canback = getSupportFragmentManager().getBackStackEntryCount() > 0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
	}

	/**
	 * Calls onNavigateUp() in the top fragment. <br />
	 * If onNavigateUp() returns true: It pops the top fragment from the back stack. <br />
	 * If onNavigateUp() returns false: It does NOT pop the top fragment from the back stack. <br />
	 * @return true if popped, false if not.
	 */
	@Override public boolean onSupportNavigateUp() {
		
		Fragment topfrag = getBackStackTop();
		boolean popped = false;
		
		if(topfrag != null && topfrag instanceof BaseDetailFragment 
				&& (popped = ((BaseDetailFragment)topfrag).onNavigateUp())){
			getSupportFragmentManager().popBackStack();
		}
		
	    return popped;
	}
	
	/** Checks with the top fragment on back stack if we should allow back pressed. */
	@Override public void onBackPressed() {
		Fragment topfrag = getBackStackTop();
		
		if(topfrag == null || !(topfrag instanceof BaseDetailFragment) 
				|| ((BaseDetailFragment)topfrag).allowBackPressed()){
			super.onBackPressed();
		}
	}

	/**
	 * Returns an instance of the fragment at the top of the fragment stack. 
	 * This method makes several assumptions:
	 * 1) The fragment has a tag 
	 * 2) The fragment was added to the back stack using its tag as name
	 * @return the fragment at the top of the stack or null if back stack is empty.
	 */
	private Fragment getBackStackTop(){
		Fragment topfrag = null;
		FragmentManager fragmentManager = getSupportFragmentManager();
		int topindex = fragmentManager.getBackStackEntryCount() - 1;
		if(topindex >= 0){
			String fragmentTag = fragmentManager.getBackStackEntryAt(topindex).getName();
			topfrag = getSupportFragmentManager().findFragmentByTag(fragmentTag);
		}
		return topfrag;
	}
	
	/** Forwards the dialog button click to the fragment */
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		Fragment topfrag = getBackStackTop();
		if(topfrag != null && topfrag instanceof BaseDetailFragment){
			((BaseDetailFragment) topfrag).onBasicPositiveButtonClicked(tag, o);
		} else {
			super.onBasicPositiveButtonClicked(tag, o);
		}
	}
	
	/** Forwards the dialog button click to the fragment */
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { 
		Fragment topfrag = getBackStackTop();
		if(topfrag != null && topfrag instanceof BaseDetailFragment){
			((BaseDetailFragment) topfrag).onBasicNegativeButtonClicked(tag, o);
		} else {
			super.onBasicNegativeButtonClicked(tag, o);
		}
	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode,resultCode, data);
	}
}
