package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ActivityRecordsArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ForumPostListFragment extends BaseDetailFragment implements ProfileListener {
	
	private User mUser;
	private ActivityHistory mHistory;
	private View mRootView;
	
	public ForumPostListFragment(){
		// Empty constructor required for MyInformationFragment
	}
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new MyInformationRecordsFragment instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the user (Note: user remains null if it is neither in the saved state nor the arguments)
		mUser = savedstate.getParcelable(Gegevens.EXTRA_USER); 							
		if(mUser == null){ mUser = arguments.getParcelable(Gegevens.EXTRA_USER); } 

		// load the history (Note: history remains null if it is neither in the saved state nor the arguments)
		mHistory = savedstate.getParcelable(Gegevens.EXTRA_RECORDS); 							
		if(mHistory == null){ mHistory = arguments.getParcelable(Gegevens.EXTRA_RECORDS); } 
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the MyInformationRecordsFragment view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_list, container, false);
		inflater.inflate(R.layout.list_header_myinformation, 
				((ViewGroup) mRootView.findViewById(R.id.fixed_header_holder)), true);
		
		return mRootView;
	}
	
	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring MyInformationRecordsFragment instance state.");

		// load the user if requested (generally only if we just created the fragment)
		if(mHistory == null){
			
			if(getActivity() != null && mUser != null){
				// Load the user from the DAO
				ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
				profileDAO.readActivityHistory(mUser);
				Loggen.v(this, "Restoring saved Instancestate: Getting reading activity history from server");
			} else {
				// inform the user that we cannot load the activity history
				userMustClickOkay("Error", "Can't load activity history");
			}
		}
		
		// If we already have a user, just show the user information
		showList(mHistory != null);

	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving MyInformationRecordsFragment instance state.");
		// save the list of comments to the instance state
		outState.putParcelable(Gegevens.EXTRA_USER, mUser);
		outState.putParcelable(Gegevens.EXTRA_RECORDS, mHistory);
	}
	
	private void showList(boolean showinfo) {
		if(mRootView != null){
			// show or hide the information
			((ProgressBar) mRootView.findViewById(R.id.progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((LinearLayout) mRootView.findViewById(R.id.content_holder))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((TextView) mRootView.findViewById(android.R.id.empty))
				.setVisibility( (mHistory != null && mHistory.getRecords().size() > 0 ) ? View.GONE : View.VISIBLE);
			
			// load the user information
			if(mUser != null && showinfo){
				// Set the image
				File imgFile = new File(mUser.getPhotoLocation());
				ImageView avatar = (ImageView) mRootView.findViewById(R.id.myinfo_owner_img);
				if(imgFile.exists()){
					avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
				    		imgFile.getAbsolutePath(),
				    		avatar.getLayoutParams().width, 
				    		avatar.getLayoutParams().height));
				}
				
				// Set the user information
				((TextView) mRootView.findViewById(R.id.myinfo_owner_name)).setText(mUser.getUserName());
				((TextView) mRootView.findViewById(R.id.myinfo_owner_time))
					.setText(getString(R.string.myinfo_joindate_title) + "\n" + mUser.getTimeEnter());
				String grade = (mHistory != null) ? String.valueOf(mHistory.getFinalGrade()) : mUser.getActivityScore();
				((TextView) mRootView.findViewById(R.id.myinfo_owner_score))
					.setText(getString(R.string.myinfo_activityrecord_title) + ": " + grade );
			}
			
			
			// load the activity history
			if(mHistory != null && getActivity() != null && showinfo){
				
				// get the list
				ListView activityList = ((ListView) mRootView.findViewById(android.R.id.list));
				
				// load and set the adapter
				Loggen.v(this, getTag() + " - Loading the adapter with " + mHistory.getRecords().size() + " items.");
				activityList.setAdapter(new ActivityRecordsArrayAdapter(getActivity(), 
						R.layout.list_item_activityrecord, android.R.id.text1, mHistory.getRecords()));
				
				// set the choice mode and reaction to the choices 
				activityList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
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
        		if(mListener != null) { mListener.onActionSelected(getTag(), Gegevens.FRAG_INFOLIST, mUser); }
				break;
			default:
				super.onClick(v);
			break;
		}
	}

	@Override public void onReadActivityHistory(ActivityHistory history) {
		// Inform the user of any failures
		if(history == null){
			userMustClickOkay(getString(R.string.myinfo_no_records_title), getString(R.string.myinfo_no_records_text));
		}
		
		// update the list
		mHistory = history;
		showList(true);
	}

	// Not used
	@Override public void onReadUserInformation(User user) { }
	@Override public void onChangeUser(User newUser, String errorMessage) { }
	@Override public void onReadUserList(List<User> users) { }
	@Override public void onChangePhoto(boolean success, String errorMessage) {	}
	@Override public void onChangePassword(boolean success, String errorMessage) {	}
	@Override public void onChangePhonenumber(boolean success, String errorMessage) {	}
	@Override public void onChangeSource(boolean success, String errorMessage) {	}
	@Override public void onLocalFallback() { }

}