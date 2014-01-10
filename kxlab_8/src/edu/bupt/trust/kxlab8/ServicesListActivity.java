package edu.bupt.trust.kxlab8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab8.BaseListFragment.OnActionSelectedListener;

public class ServicesListActivity extends BaseActivity  implements OnActionSelectedListener {
	
	private ServiceFlavor mFlavor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating services activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		
		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(Gegevens.EXTRA_MSG);
		b.setClassLoader(getClass().getClassLoader());

		// get the service flavor 
		mFlavor = (ServiceFlavor) b.getSerializable(Gegevens.EXTRA_FLAVOR);
		if(mFlavor == null){ mFlavor = ServiceFlavor.SERVICE; }
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_services).setEnabled(mFlavor != ServiceFlavor.SERVICE);
		findViewById(R.id.footer_myservice).setEnabled(mFlavor != ServiceFlavor.MYSERVICE);
		
		// Restore the instance
		setupActionBar((savedInstanceState != null) ? savedInstanceState.getInt(Gegevens.EXTRA_SELECTEDTAB, 0) : 0 );
		
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		Loggen.v(this, "Saving instance state of the services activity.");
		// save the selected tab to the instance state
		outState.putInt(Gegevens.EXTRA_SELECTEDTAB, getActionBar().getSelectedNavigationIndex());
		outState.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
		super.onSaveInstanceState(outState);
	}
		
	private void setupActionBar(int selectedTab){
	    // setup action bar for tabs
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    actionBar.setDisplayShowTitleEnabled(false);
	    
	    // add the tabs 
	    FragmentManager fragmentMng = getSupportFragmentManager();
	    String [] tabTitles = getResources().getStringArray(R.array.services_tab_titles);
	    ServiceType [] allTypes = ServiceType.values();
	    for(int i = 0; i <  allTypes.length; i++){
	    	// try to find an existing instance of the desired fragment
	    	Fragment existingFragment = fragmentMng.findFragmentByTag(allTypes[i].getFragName());
	    	ActionBar.Tab newTab = null;
	    	if(existingFragment == null){
	    		// Create a new tab using a new fragment
	    		newTab = actionBar.newTab().setText(tabTitles[i]).setTabListener(
						new TabListener<ServicesListFragment>(this, allTypes[i].getFragName(), 
								ServicesListFragment.class));
	    	} else {
	    		// Create a new tab using the existing fragment as the tab content
				newTab = actionBar.newTab().setText(tabTitles[i]).setTabListener(
						new TabListener<ServicesListFragment>(this, allTypes[i].getFragName(), 
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
	
	public ServiceFlavor getFlavor(){
		return mFlavor;
	}

//	@Override public void onItemSelected(String tag, int position, TrustService service) {
//		Loggen.v(this, "User has selected "+service.getServicetitle()+" from " + tag + ". id=" + service.getServiceid() );
//		// Note: The Activity handles delegating the service details to ServicesDetailFragment. 
//		// This keeps the ServicesListFragment completely independent of the ServicesDetailFragment (i.e. low cohesion)
//		// In our case we show the service details in a new activity. 
//		// This can easily be modified to a local fragment for tablet devices
//		
//		// Put the necessary arguments in a bundle
//		Bundle b = new Bundle();
//		b.putInt(Gegevens.EXTRA_FOOTERID,  R.id.footer_services);
//		b.putString(Gegevens.EXTRA_TAG, tag);
//		b.putParcelable(Gegevens.EXTRA_SERVICE, service);
//		b.putSerializable(Gegevens.EXTRA_FLAVOR, ServiceFlavor.SERVICE);
//		
//		// Send the bundle off to the detail activity
//		Intent detailIntent = new Intent(this, ServiceDetailActivity.class);
//		detailIntent.putExtra(Gegevens.EXTRA_MSG, b);
//		startActivityForResult(detailIntent, BaseActivity.RESULT_FIRST_USER);
//	}
	
	@Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Note: only do something if the result is OKAY
		if(resultCode == BaseActivity.RESULT_OK) { 
			// TODO: update the list 
			Loggen.v(this, "result was okay.");
		} else if (resultCode == BaseActivity.RESULT_FINISH) {
			Loggen.v(this, "result was finish.");
			finish();
		} else {
			Loggen.v(this, "result was not okay.");
			
		}
	}

	@Override public void onActionSelected(String tag, String goal, Object o) { }
}
