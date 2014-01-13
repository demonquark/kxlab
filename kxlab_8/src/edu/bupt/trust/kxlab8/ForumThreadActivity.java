package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab8.BaseListFragment.OnActionSelectedListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

public class ForumThreadActivity extends BaseActivity implements OnActionSelectedListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating forum activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_forum).setEnabled(false);
		
		// Restore the instance
		setupActionBar((savedInstanceState != null) ? savedInstanceState.getInt(Gegevens.EXTRA_SELECTEDTAB, 0) : 0 );
		
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		Loggen.v(this, "Saving instance state of the services activity.");
		// save the selected tab to the instance state
		outState.putInt(Gegevens.EXTRA_SELECTEDTAB, getActionBar().getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}
		
	private void setupActionBar(int selectedTab){
	    // setup action bar for tabs
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayShowTitleEnabled(false);
	    
	    // add the tabs 
	    FragmentManager fragmentMng = getSupportFragmentManager();
	    String [] tabTitles = getResources().getStringArray(R.array.forum_tabs);
	    PostType [] allTypes = PostType.values();
	    for(int i = 0; i < allTypes.length; i++){
	    	// try to find an existing instance of the desired fragment
	    	Fragment existingFragment = fragmentMng.findFragmentByTag(allTypes[i].getFragName());
	    	ActionBar.Tab newTab = null;
	    	if(existingFragment == null){
	    		// Create a new tab using a new fragment
	    		newTab = actionBar.newTab().setText(tabTitles[i]).setTabListener(
						new TabListener<ForumThreadListFragment>(this, allTypes[i].getFragName(), 
								ForumThreadListFragment.class));
	    	} else {
	    		// Create a new tab using the existing fragment as the tab content
				newTab = actionBar.newTab().setText(tabTitles[i]).setTabListener(
						new TabListener<ForumThreadListFragment>(this, allTypes[i].getFragName(), 
								existingFragment));
	    	}

	    	// add the tab to the action bar
			actionBar.addTab(newTab);
	    }
	    
	    // select the correct tab
	    if(0 <= selectedTab && selectedTab < allTypes.length){
	    	actionBar.setSelectedNavigationItem(selectedTab);
	    }
	}
	
	@Override public void onActionSelected(String tag, String goal, Object o) {
		Loggen.v(this, "onActionSelected in ForumThreadActivity.");
	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode,resultCode, data);
	}
}
