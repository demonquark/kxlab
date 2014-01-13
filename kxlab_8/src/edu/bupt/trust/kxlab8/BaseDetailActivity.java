package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab8.BaseDetailFragment.OnActionSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;

public class BaseDetailActivity extends BaseActivity implements OnActionSelectedListener, OnBackStackChangedListener{
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// enable back stack navigation
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		
	}

	/** Callback from the fragment. Default does nothing. */
	@Override public void onActionSelected(String from, String to, Object user) { }
	@Override public void performBackPress() { super.onBackPressed(); }
	
	@Override public void onBackStackChanged() {
		displayHomeAsUpEnabled();
	}
	
	/** Checks if we should display a "back" arrow in the action bar. */
	public void displayHomeAsUpEnabled(){
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
