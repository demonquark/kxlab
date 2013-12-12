package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.UserInformation;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab8.MyInformationFragment.OnActionSelectedListener;
import android.os.Bundle;

public class MyInformationActivity extends BaseActivity implements OnActionSelectedListener{

	private MyInformationFragment mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);
		
		// disable the button for this footer menu item
		findViewById(R.id.footer_myinformation).setEnabled(false);
		
		// Create the my information fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {
			Bundle arguments = new Bundle();
			mFragment = new MyInformationFragment();
			mFragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.details, mFragment,Gegevens.FRAG_INFOVIEW).commit();
		}
	}

	@Override
	public void onActionSelected(String tag, UserInformation user) {
		// TODO Auto-generated method stub
		
	}
}
