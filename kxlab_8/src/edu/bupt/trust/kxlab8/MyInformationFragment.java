package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.model.UserInformation;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyInformationFragment extends Fragment implements ProfileListener {
	
	private UserInformation mUser;
	OnActionSelectedListener mListener;
	private View mRootView;
	
	public MyInformationFragment(){
		// Empty constructor required for MyInformationFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the services menu
		inflater.inflate(R.menu.myinformation, menu);
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
        switch (itemId) {
        	case R.id.action_edit:
        		mListener.onActionSelected(getTag(), mUser);
            break;
            default:
            	return super.onOptionsItemSelected(item);
        }
    	
    	return true;
    }
    
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ServiceDetailView instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the user (Note: user remains null if it is neither in the saved state nor the arguments)
		mUser = savedstate.getParcelable(Gegevens.EXTRA_USER); 							
		if(mUser == null){ mUser = arguments.getParcelable(Gegevens.EXTRA_USER); } 
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the MyInformation view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_myinformation, container, false);

		return mRootView;
	}
	
	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring MyInformation instance state.");

		// load the user if requested (generally only if we just created the fragment)
		if(mUser == null){
			// Load the user from the DAO
//			ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
//			profileDAO.readUserInformation(email);
			Loggen.v(this, "Restoring saved Instancestate: Getting user from site");
		}else{
			// If we already have a user, just show the user information
			showUserInformation();
		}
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving MyInformation instance state.");
		// save the list of comments to the instance state
		outState.putParcelable(Gegevens.EXTRA_USER, mUser);
	}
	
	@Override public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof OnActionSelectedListener)) {
			throw new IllegalStateException( "Activity must implement fragment's callbacks.");
		}
		mListener = (OnActionSelectedListener) activity;
	}
	
	@Override public void onDetach() {
		super.onDetach();
		mListener = null;
	}

	private void showUserInformation() {
		
		if(mRootView != null && mUser != null){
			// Set the image
			File imgFile = new File(mUser.getPhotoImage());
			ImageView avatar = (ImageView) mRootView.findViewById(R.id.myinfo_img);
			if(imgFile.exists()){
				avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
			    		imgFile.getAbsolutePath(),avatar.getWidth(), avatar.getHeight()));
			}
			
			// Set the user information
			((TextView) mRootView.findViewById(R.id.myinfo_name)).setText(mUser.getUserName());
			((TextView) mRootView.findViewById(R.id.myinfo_email)).setText(mUser.getUserEmail());
			((TextView) mRootView.findViewById(R.id.myinfo_joindate)).setText(mUser.getTimeEnter());
			((TextView) mRootView.findViewById(R.id.myinfo_activitylevel)).setText(mUser.getActivityScore());
			((TextView) mRootView.findViewById(R.id.myinfo_phone)).setText(mUser.getPhoneNumber());
			((TextView) mRootView.findViewById(R.id.myinfo_source)).setText(mUser.getSource());
		}
	}

	public interface OnActionSelectedListener{
		public void onActionSelected(String tag, UserInformation user);
	}

	@Override public void onReadUserInformation(User user) { 
		
	}

	// Not used
	@Override public void onReadUserList(List<User> users) { }
	@Override public void onReadActivityHistory(ActivityHistory history) {	}
	@Override public void onChangePhoto(boolean success, String errorMessage) {	}
	@Override public void onChangePassword(boolean success, String errorMessage) {	}
	@Override public void onChangePhonenumber(boolean success, String errorMessage) {	}
	@Override public void onChangeSource(boolean success, String errorMessage) {	}
	@Override public void onLocalFallback() { }
	
}