package edu.bupt.trust.kxlab8;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

/**
 *  The activity expects the following items in its arguments: <br />
 *  - EXTRA_MSG: A bundle added to the intent. The bundle contains all the actual data. Acts as a savedInstanceState. <br />
 *  The bundle should contain the following:
 *  - EXTRA_FOOTERID: The id of our place in the footer. Should be one of the items in the widget_footer layout. <br />
 *  - EXTRA_SERVICE: A service object <br />
 *  - EXTRA_TYPE: The type of request. There are three possible requests VIEW, EDIT or NEW. <br />
 *  - EXTRA_TAG: The tag identifies which tab it is from (community, recommended, apply)<br />
 * @author Krishna
 *
 */
public class ServiceDetailActivity extends BaseDetailActivity {
	
	public enum ServiceType { MYSERVICE, SERVICE};

	private ServiceDetailViewFragment viewFragment;
	private ServiceType mServiceType;
	private String mCategoryTag;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating services detail activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(Gegevens.EXTRA_MSG);
		b.setClassLoader(getClass().getClassLoader());

		// get the service type 
		mServiceType = (ServiceType) b.getSerializable(Gegevens.EXTRA_SERVICETYPE);
		if(mServiceType == null){ mServiceType = ServiceType.MYSERVICE; }

		// disable the footer button based on the service type
		int footerid = 0;
		switch(mServiceType){
			case MYSERVICE: footerid = R.id.footer_myservice; break;
			case SERVICE: footerid = R.id.footer_services; break;
		}
		if( footerid > 0) { findViewById(footerid).setEnabled(false); }
		
		// get the service and the tag (identifying which tab this is from)
		TrustService service = b.getParcelable(Gegevens.EXTRA_SERVICE);
		mCategoryTag = b.getString(Gegevens.EXTRA_TAG);
		
		// Create the detail fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {

			if(service == null) { 
				// If no service was provided, assume that this is a request to create new service
				onActionSelected(null, Gegevens.FRAG_INFOEDIT, null); 
			} else {
				// Launch the view fragment for the existing service 
				Bundle arguments = new Bundle();
				arguments.putParcelable(Gegevens.EXTRA_SERVICE, service);
				arguments.putSerializable(Gegevens.EXTRA_SERVICETYPE, mServiceType);
				arguments.putString(Gegevens.EXTRA_TAG, mCategoryTag);
				viewFragment = new ServiceDetailViewFragment();
				viewFragment.setArguments(arguments);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.add(R.id.details, viewFragment, Gegevens.FRAG_INFOVIEW);
				ft.addToBackStack(Gegevens.FRAG_INFOVIEW);
				ft.commit();
			}
		} else {
			// this instance state was restored. Call the onBackStackChanged method to 
			displayHomeAsUpEnabled();
		}
	}
	
	/** Checks if we should display a "back" arrow in the action bar. */
	@Override public void onBackStackChanged() {
		super.onBackStackChanged();
		if(getSupportFragmentManager().getBackStackEntryCount() == 0){ finish(); }
	}
	
	/** Callback from the fragment
	 * This implementation processes one use case: <br />
	 * From: FRAG_INFOVIEW or null
	 * - Goal = FRAG_INFOEDIT: The user selected the edit button in the action bar.
	 * @param from - The tag of the sending fragment. Note: should also be the name in the back stack
	 * @param to - The tag of the target fragment. Note: should also be the name in the back stack
	 * @param o - The method expects this to be an instance of Service. 
	 */
	@Override public void onActionSelected(String from, String to, Object service) {
		if(Gegevens.FRAG_INFOEDIT.equals(to)){
			// create a bundle and add the provided Service to it
			Bundle arguments = new Bundle();
			arguments.putSerializable(Gegevens.EXTRA_SERVICETYPE, mServiceType);
			arguments.putString(Gegevens.EXTRA_TAG, mCategoryTag);
			if(service instanceof TrustService) { 
				arguments.putParcelable(Gegevens.EXTRA_SERVICE, (TrustService) service); 
			} else if(service != null) {
				this.postToast(getString(R.string.details_eror_service_edit));
			}
			
			// launch the edit fragment
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			ServiceDetailEditFragment editFragment = new ServiceDetailEditFragment();
			editFragment.setArguments(arguments);
			ft.replace(R.id.details, editFragment, Gegevens.FRAG_INFOEDIT);
			ft.addToBackStack(Gegevens.FRAG_INFOEDIT);
			ft.commit();
		}

		Loggen.v(this, "User clicked edit - switch to edit fragment.");
	}

	@Override protected void onSaveInstanceState(Bundle outState) {
		Loggen.v(this, "Saving instance state of the service details activity.");
		// save the service type of the service
		outState.putSerializable(Gegevens.EXTRA_SERVICETYPE, mServiceType);
		outState.putString(Gegevens.EXTRA_TAG, mCategoryTag);
		
		super.onSaveInstanceState(outState);
	}
}