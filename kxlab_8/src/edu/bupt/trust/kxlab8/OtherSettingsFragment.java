package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.UserArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.FileManager;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OtherSettingsFragment extends BaseDetailFragment implements ProfileListener, OnItemClickListener {
	
	private String settingName;
	private ArrayList <User> mUsers;
	private View mRootView;
	private ListView mListView;
	
	public OtherSettingsFragment(){
		// Empty constructor required for MyInformationFragment
	}

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the sort menu
		if(Gegevens.PREF_USERLIST.equals(settingName))
			inflater.inflate(R.menu.otherusers, menu);
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	if(mUsers != null){
            switch (itemId) {
        	case R.id.action_sort_name_asc:
        		Collections.sort(mUsers, new Comparator<User>() {
        		    public int compare(User user1, User user2) { 
        		    	return user1.getUserName().compareTo(user2.getUserName()); }
        		});
        		break;
        	case R.id.action_sort_name_desc:
        		Collections.sort(mUsers, new Comparator<User>() {
        		    public int compare(User user1, User user2) { 
        		    	return user2.getUserName().compareTo(user1.getUserName()); }
        		});
        		break;
        	case R.id.action_sort_activitylevel_asc:
        		Collections.sort(mUsers, new Comparator<User>() {
        		    public int compare(User user1, User user2) { 
        		    	return user1.getActivityScore().compareTo(user2.getActivityScore()); }
        		});
        		break;
        	case R.id.action_sort_activitylevel_desc:
        		Collections.sort(mUsers, new Comparator<User>() {
        		    public int compare(User user1, User user2) { 
        		    	return user2.getActivityScore().compareTo(user1.getActivityScore()); }
        		});
        		break;
            default:
            	return super.onOptionsItemSelected(item);
            }
            showList(true);
    	} else {
    		return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
    
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new OtherSettingsFragment instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the setting name (Note: remains null if it is neither in the saved state nor the arguments)
		settingName = savedstate.getString(Gegevens.EXTRA_MSG); 							
		if(settingName == null){ settingName = arguments.getString(Gegevens.EXTRA_MSG); } 							
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the MyInformationRecordsFragment view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_list, container, false);
		mListView = ((ListView) mRootView.findViewById(android.R.id.list));
		
		return mRootView;
	}
	
	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring MyInformationRecordsFragment instance state.");

		// load the any necessary data based on the setting name
		boolean loadingdata = false;
		if(Gegevens.PREF_USERLIST.equals(settingName)){
			if(mUsers == null){
				loadingdata = true;
				readUsers();
			}
		}
		
		showList(!loadingdata);

	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving MyInformationRecordsFragment instance state.");
		// save the list of comments to the instance state
		if(settingName != null) { outState.putString(Gegevens.EXTRA_MSG, settingName); }
	}
	
	private void readUsers() {
		if(getActivity() != null){
			// Load the user from the DAO
			ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
			profileDAO.readUserList(Source.DUMMY);
		} 
	}
	
	private void resetCache(){
		showList(false);
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				Loggen.d(this, "delete and reload the cache");
				FileManager.deleteAssetsFromSDCard();
				FileManager.copyAssetsToSDCard(getActivity());
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				showList(true);
			}
	
		}.execute();
	}

	private void showList(boolean showinfo) {
		if(mRootView != null && mListView != null){
			// show or hide the information
			((ProgressBar) mRootView.findViewById(R.id.progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((LinearLayout) mRootView.findViewById(R.id.content_holder))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((TextView) mRootView.findViewById(android.R.id.empty)).setVisibility(View.GONE);
			
			
			// Load the list adapter based on the function of the fragment
			if(settingName == null){
				// load the main settings menu
				if(mListView.getAdapter() == null && getActivity() != null ){
					String [] settingTitles = getResources().getStringArray(R.array.settings_titles);
					mListView.setAdapter(new ArrayAdapter <String>(getActivity(), 
							android.R.layout.simple_list_item_1, android.R.id.text1, settingTitles));
					mListView.setOnItemClickListener(this);
				}
			} else if (Gegevens.PREF_USERLIST.equals(settingName)) {
				// load the user list
				if(mListView.getAdapter() == null && getActivity() != null && mUsers != null){
					
					mListView.setAdapter(new UserArrayAdapter(getActivity(), 
							R.layout.list_item_user, android.R.id.text1, mUsers));
					mListView.setOnItemClickListener(this);
				} else if (mListView.getAdapter() != null) {
					((BaseAdapter)mListView.getAdapter()).notifyDataSetChanged();
				} else {
					((TextView) mRootView.findViewById(android.R.id.empty)).setVisibility(View.VISIBLE);
				}
			} else if (Gegevens.PREF_LANGUAGE.equals(settingName)) {
				// load the language options. Bad implementation, I'm being lazy. TODO: fix later.
				if(mListView.getAdapter() == null && getActivity() != null ){
					String [] languages = getResources().getStringArray(R.array.settings_languages);
					mListView.setAdapter(new ArrayAdapter <String>(getActivity(), 
							android.R.layout.simple_list_item_1, android.R.id.text1, languages));
					mListView.setOnItemClickListener(this);
				}
				
			}
		}
	}
	
	@Override public void onReadUserList(List<User> users) { 
		if(users != null){
			mUsers = (ArrayList<User>) users;
			showList(true);
		}
	}

	// Not used
	@Override public void onReadUserInformation(User user) { }
	@Override public void onReadActivityHistory(ActivityHistory history) { }
	@Override public void onChangeUser(User newUser, String errorMessage) { }
	@Override public void onChangePhoto(boolean success, String errorMessage) {	}
	@Override public void onChangePassword(boolean success, String errorMessage) {	}
	@Override public void onChangePhonenumber(boolean success, String errorMessage) {	}
	@Override public void onChangeSource(boolean success, String errorMessage) {	}
	@Override public void onLocalFallback() { }

	@Override public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// Really bad implementation, cause I'm lazy

		if(settingName == null){
			// Go the selected preference
			if(position == 0){
				// change the language
				mListener.onActionSelected(getTag(), Gegevens.FRAG_INFOLIST, Gegevens.PREF_LANGUAGE);
			} else if(position == 1){
				// show the user list
				mListener.onActionSelected(getTag(), Gegevens.FRAG_INFOLIST, Gegevens.PREF_USERLIST);
			} else if (position == 2) {
				// delete the cache
				resetCache();
			}
		} else if(Gegevens.PREF_LANGUAGE.equals(settingName)){
			if(getActivity() != null){
				// change the language and go back
				BaseActivity act = ((BaseActivity)getActivity());
				if(position == 1){
					act.mSettings.setLocale("zh");
				} else {
					act.mSettings.setLocale("en");
				}
				
				// restart the activity to update the language
		        Intent intent = new Intent(act.getApplicationContext(), OtherActivity.class);
		        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        startActivity(intent);
		        
			} else {
				userMustClickOkay(getString(R.string.other_language_error_title), 
						getString(R.string.other_language_error_text));
				mListener.performBackPress();	
			}
		}		
	}
}