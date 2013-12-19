package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.Settings;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class MyInformationViewFragment extends BaseDetailFragment implements ProfileListener {
	
	private User mUser;
	private View mRootView;
	
	public MyInformationViewFragment(){
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
        		if(mListener != null) { mListener.onActionSelected(getTag(), Gegevens.FRAG_INFOEDIT, mUser); }
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
		mRootView = inflater.inflate(R.layout.frag_myinformation_view, container, false);
		
		// set the on click listener for the log out button
		((Button) mRootView.findViewById(R.id.myinfo_btn_logout)).setOnClickListener(this);
		((ImageButton) mRootView.findViewById(R.id.myinfo_btn_activityrecord)).setOnClickListener(this);

		return mRootView;
	}
	
	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring MyInformation instance state.");

		// load the user if requested (generally only if we just created the fragment)
		if(mUser == null && getActivity() != null){
			// Load the user from the DAO
			ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
			profileDAO.readUserInformation(Source.DUMMY, Settings.getInstance(getActivity()).getUser().getEmail());
			Loggen.v(this, "Restoring saved Instancestate: Getting user from site");
		}
		
		// If we already have a user, just show the user information
		showUserInformation(mUser != null);

	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving MyInformation instance state.");
		// save the list of comments to the instance state
		outState.putParcelable(Gegevens.EXTRA_USER, mUser);
	}
	
	@Override public void onPause() {
		if(getActivity() != null){
			// Save the current user to our shared preferences
			BaseActivity parentActivity = (BaseActivity) getActivity();
			parentActivity.mSettings.setUser(mUser);
			parentActivity.mSettings.saveSettingsToSharedPreferences(parentActivity);
		}
		super.onPause(); 
	}

	
	private void showUserInformation(boolean showinfo) {
		if(mRootView != null){
			// show or hide the information
			((ScrollView) mRootView.findViewById(R.id.myinfo_container))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((ProgressBar) mRootView.findViewById(R.id.myinfo_progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			
			// load the user information
			if(mUser != null && showinfo){
				// Set the image
				File imgFile = new File(mUser.getPhotoLocation());
				ImageView avatar = (ImageView) mRootView.findViewById(R.id.myinfo_img);
				if(imgFile.exists()){
					
					avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
				    		imgFile.getAbsolutePath(),
				    		avatar.getLayoutParams().width, 
				    		avatar.getLayoutParams().height));
				}
				
				// Set the user information
				((TextView) mRootView.findViewById(R.id.myinfo_name)).setText(mUser.getUserName());
				((TextView) mRootView.findViewById(R.id.myinfo_email)).setText(mUser.getEmail());
				((TextView) mRootView.findViewById(R.id.myinfo_joindate)).setText(mUser.getTimeEnter());
				((TextView) mRootView.findViewById(R.id.myinfo_activitylevel)).setText(mUser.getActivityScore());
				((TextView) mRootView.findViewById(R.id.myinfo_phone)).setText(mUser.getPhoneNumber());
				((TextView) mRootView.findViewById(R.id.myinfo_source)).setText(mUser.getSource());
			}
		}
	}
	
	private void logoutUser(){
		mUser.setLogin(false);
		if(getActivity() != null){
			// return to the login activity
			BaseActivity parentActivity = (BaseActivity) getActivity();
			parentActivity.openActivity(new Intent(parentActivity, LoginActivity.class));
		}
	}

	@Override public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.myinfo_btn_logout:
				logoutUser();
				break;
			case R.id.myinfo_btn_activityrecord:
				Loggen.v(this,"Clicked activity record.");
        		if(mListener != null) { mListener.onActionSelected(getTag(), Gegevens.FRAG_INFOLIST, mUser); }
				break;
			default:
				super.onClick(v);
			break;
		}
	}

	@Override public void onReadUserInformation(User user) { 
		Loggen.v(this,"Received User information from dao.");
		if(user != null){ 
			// show the userinformation
			mUser = user;
		}else{
			// show an error message
			if(getActivity() != null){
				mUser = ((BaseActivity) getActivity()).mSettings.getUser();
				((BaseActivity) getActivity()).userMustClickOkay(
						getString(R.string.myinfo_no_user_title), getString(R.string.myinfo_no_user_text));
			}
		}
		showUserInformation(true);
	}

	// Not used
	@Override public void onReadUserList(List<User> users) { }
	@Override public void onReadActivityHistory(ActivityHistory history) {	}
	@Override public void onChangePhoto(boolean success, String errorMessage) {	}
	@Override public void onChangePassword(boolean success, String errorMessage) {	}
	@Override public void onChangePhonenumber(boolean success, String errorMessage) {	}
	@Override public void onChangeSource(boolean success, String errorMessage) {	}
	@Override public void onLocalFallback() { }

	@Override
	public void onChangeUser(User newUser, String errorMessage) {
		// TODO Auto-generated method stub
		
	}
}