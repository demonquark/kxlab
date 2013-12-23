package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class ForumPostActivity extends BaseDetailActivity {
	
	private ServiceDetailViewFragment listFragment;
	private ServiceFlavor mFlavor;
	private ServiceType mType;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating services detail activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(Gegevens.EXTRA_MSG);
		b.setClassLoader(getClass().getClassLoader());

		// get the service type 
		mFlavor = (ServiceFlavor) b.getSerializable(Gegevens.EXTRA_FLAVOR);
		if(mFlavor == null){ mFlavor = ServiceFlavor.MYSERVICE; }

		// disable the footer button based on the service type
		int footerid = 0;
		switch(mFlavor){
			case MYSERVICE: footerid = R.id.footer_myservice; break;
			case SERVICE: footerid = R.id.footer_services; break;
		}
		if( footerid > 0) { findViewById(footerid).setEnabled(false); }
		
		// get the service and the type (identifying which tab this is from)
		TrustService service = b.getParcelable(Gegevens.EXTRA_SERVICE);
		mType = (ServiceType) b.getSerializable(Gegevens.EXTRA_SERVICETYPE);
		
		// Create the detail fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {

			if(service == null) { 
				// If no service was provided, assume that this is a request to create new service
				onActionSelected(null, Gegevens.FRAG_INFOEDIT, null); 
			} else {
				// Launch the view fragment for the existing service 
				Bundle arguments = new Bundle();
				arguments.putParcelable(Gegevens.EXTRA_SERVICE, service);
				arguments.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
				listFragment = new ServiceDetailViewFragment();
				listFragment.setArguments(arguments);
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ft.add(R.id.details, listFragment, Gegevens.FRAG_INFOVIEW);
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
			arguments.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
			if(service instanceof TrustService) { 
				arguments.putParcelable(Gegevens.EXTRA_SERVICE, (TrustService) service); 
			} else if(service != null) {
				this.postToast(getString(R.string.details_eror_service_edit));
			} else if (mType != null){
				arguments.putSerializable(Gegevens.EXTRA_SERVICETYPE, mType);
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
		outState.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
		if(mType != null) {outState.putSerializable(Gegevens.EXTRA_SERVICETYPE, mType); }
		
		super.onSaveInstanceState(outState);
	}
}
