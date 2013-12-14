package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab8.MyServicesListFragment.OnServiceSelectedListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.view.View;

public class OtherActivity extends BaseActivity implements OnServiceSelectedListener{

	final String [] mFragmentTags = new String [] {Gegevens.FRAG_COMMUNITY, Gegevens.FRAG_RECOMMEND, Gegevens.FRAG_APPLY};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_temp);
		
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_other).setEnabled(false);

		// Restore the instance
		setupActionBar((savedInstanceState != null) ? savedInstanceState.getInt(Gegevens.EXTRA_SELECTEDTAB, 0) : 0 );
	}

	public void onBtnClick(View view) {
		int id = view.getId();
		switch(id){
			
			default:
				super.onBtnClick(view);
		}
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

	@Override public void onItemSelected(String tag, int position, TrustService service) {
		Loggen.v(this, "User has selected "+service.getServicetitle()+" from " + tag + ". id=" + service.getServiceid() );
		//startDetailsActivity(tag, ServiceDetailActivity.Type.VIEW, service);
	}

	@Override
	public void onCreateService(String tag) {
		//startDetailsActivity(tag, ServiceDetailActivity.Type.NEW, new TrustService());
		
	}

	
}
