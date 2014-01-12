package edu.bupt.trust.kxlab8;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;

import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab8.MyServicesListFragment.OnServiceSelectedListener;

public class MyServicesListActivity extends BaseActivity implements OnServiceSelectedListener{
	
	final String [] mFragmentTags = new String [] {Gegevens.FRAG_COMMUNITY, Gegevens.FRAG_RECOMMEND, Gegevens.FRAG_APPLY};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating services activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_serviceslist);
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_myservice).setEnabled(false);
		
		// Restore the instance
		setupActionBar((savedInstanceState != null) ? savedInstanceState.getInt(Gegevens.EXTRA_SELECTEDTAB, 0) : 0 );
		
	}
	/**
	 * 将当前选中的Fragment页的索引保存到Bundle中
	 */
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
	    String [] tabTitles = getResources().getStringArray(R.array.services_tab_titles);
	    for(int i = 0; i < mFragmentTags.length; i++){
	    	// try to find an existing instance of the desired fragment
	    	Fragment existingFragment = fragmentMng.findFragmentByTag(mFragmentTags[i]);
	    	if(existingFragment == null){
	    		// if it does not exist, create a new fragment
				actionBar.addTab(actionBar.newTab().setText(tabTitles[i]).setTabListener(
						new TabListener<MyServicesListFragment>(this, mFragmentTags[i], MyServicesListFragment.class)));
	    	} else {
	    		// if it does it exist, use that fragment as the tab content
				actionBar.addTab(actionBar.newTab().setText(tabTitles[i]).setTabListener(
						new TabListener<MyServicesListFragment>(this, mFragmentTags[i], existingFragment)));
	    	}
	    }
	    
	    // select the correct tab
	    if(0 <= selectedTab && selectedTab < mFragmentTags.length){
	    	actionBar.setSelectedNavigationItem(selectedTab);
	    }
	}
	
	private void startDetailsActivity(String tag, TrustService service){
		// Note: The Activity handles delegating the service details to ServicesDetailFragment. 
		// This keeps the MyServicesListFragment completely independent of the ServicesDetailFragment (i.e. low cohesion)
		// In our case we show the service details in a new activity. 
		// This can easily be modified to a local fragment for tablet devices
		System.out.println("Into startDetails");
		// Put the necessary arguments in a bundle
		Bundle b = new Bundle();
		b.putInt(Gegevens.EXTRA_FOOTERID,  R.id.footer_myservice);
		b.putString(Gegevens.EXTRA_TAG, tag);
		if(service != null) { b.putParcelable(Gegevens.EXTRA_SERVICE, service); }
		b.putSerializable(Gegevens.EXTRA_FLAVOR, ServiceFlavor.MYSERVICE);
		
		// Send the bundle off to the detail activity
		Intent detailIntent = new Intent(this, ServiceDetailActivity.class);
		detailIntent.putExtra(Gegevens.EXTRA_MSG, b);
		startActivityForResult(detailIntent, BaseActivity.RESULT_FIRST_USER);
	}

	@Override public void onItemSelected(String tag, int position, TrustService service) {
		Loggen.v(this, "User has selected "+service.getServicetitle()+" from " + tag + ". id=" + service.getId() );
		startDetailsActivity(tag, service);
	}
	
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

	@Override
	public void onCreateService(String tag) {
		startDetailsActivity(tag, null);
	}

}
