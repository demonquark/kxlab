package edu.bupt.trust.kxlab8;

import android.os.Bundle;
import android.view.View;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab8.ServiceDetailViewFragment.OnActionSelectedListener;

public class ServiceDetailActivity extends BaseActivity implements OnActionSelectedListener{
	
	public enum Type { VIEW, EDIT, NEW }; // not so sure about new...  

	private ServiceDetailViewFragment mFragment;
	private Type mType; 
	private int mResult;
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating services detail activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(Gegevens.EXTRA_MSG);
		b.setClassLoader(getClass().getClassLoader());

		// disable the footer button of the calling activity
		int footerid = b.getInt(Gegevens.EXTRA_FOOTERID, -1);
		if(footerid > 0) { findViewById(footerid).setEnabled(false); }
		
		// get the service and the tag (identifying which tab this is from)
		TrustService service = b.getParcelable(Gegevens.EXTRA_SERVICE);
		// String tag = b.getString(Gegevens.EXTRA_TAG);
		mType = (Type) b.getSerializable(Gegevens.EXTRA_TYPE);
		if(mType == null) { mType = Type.VIEW; }
		
		// get the result state
		 mResult = b.getInt(Gegevens.EXTRA_RESULT, BaseActivity.RESULT_CANCELED);
		
		// Create the detail fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {

			if(service == null) { service = new TrustService(); }
			
			if(mType == Type.VIEW){
				Bundle arguments = new Bundle();
				arguments.putParcelable(Gegevens.EXTRA_SERVICE, service);
				mFragment = new ServiceDetailViewFragment();
				mFragment.setArguments(arguments);
				getSupportFragmentManager().beginTransaction().add(R.id.details, mFragment).commit();
			} else {
				showConfirmationDialog(Gegevens.FRAG_BACKPRESSED, String.valueOf(-1));
			}
			
		}
	}
	
	@Override protected void onSaveInstanceState(Bundle outState) {
		Loggen.v(this, "Saving instance state of the services activity.");
		// save the selected tab to the instance state
		outState.putInt(Gegevens.EXTRA_RESULT, mResult);
		outState.putSerializable(Gegevens.EXTRA_TYPE, mType);
		
		super.onSaveInstanceState(outState);
	}
	
	public void showConfirmationDialog(String dialogtag, String footerid){
    	DialogFragmentBasic.newInstance(true)
			.setTitle(getString(R.string.details_confirm_close_title))
			.setMessage(getString(R.string.details_confirm_close_text))
			.setObject(footerid)
			.setPositiveButtonText(getString(R.string.yes))
			.setNegativeButtonText(getString(R.string.no))
			.show(getSupportFragmentManager(), dialogtag);
	}
	
    /** Processes the default button pressed method. By default it assumes it is an unknown buttons. */
	public void onBtnClick(View view) {
		int id = view.getId();
		// Do a check if this is a footer button
		if(mType != Type.VIEW && (id == R.id.footer_services || id == R.id.footer_myservice 
				|| id == R.id.footer_forum || id == R.id.footer_myinformation || id == R.id.footer_other)){
			
			showConfirmationDialog(Gegevens.FRAG_FOOTERLINK, String.valueOf(id));
		} else {
			// all other buttons can be handled by the super
			super.onBtnClick(view);
		}
	}

	@Override public void onBackPressed() {
		if(mType != Type.VIEW){
			showConfirmationDialog(Gegevens.FRAG_BACKPRESSED, String.valueOf(-1));
		} else {
			super.onBackPressed();
		}
	}
	
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) { 
		// set the result
		setResult(mResult);

		// exit either the back pressed option or the footer menu
		if(Gegevens.FRAG_FOOTERLINK.equals(tag)){ 
			setResult(BaseActivity.RESULT_FINISH);
			int footerid = Integer.parseInt(String.valueOf(o));
			super.onBtnClick(findViewById(footerid));
			finish();
		} else if (Gegevens.FRAG_BACKPRESSED.equals(tag)){
			finish();
		}
	}
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { }

	@Override
	public void onActionSelected(String tag, TrustService service) {
		Loggen.v(this, "User clicked edit - TODO: switch to edit fragment.");
		
	}

	
}
