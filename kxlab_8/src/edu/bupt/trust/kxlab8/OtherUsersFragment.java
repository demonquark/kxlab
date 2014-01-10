package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.UserArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.ActivityRecord;
import edu.bupt.trust.kxlab.model.SortKey;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.XListView;
import edu.bupt.trust.kxlab.widgets.XListView.IXListViewListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class OtherUsersFragment extends BaseDetailFragment implements ProfileListener, IXListViewListener {
	
	private String settingName;
	private ArrayList <User> mUsers;
	private View mRootView;
	private XListView mListView;
	private SortKey mSortKey;
	
	public OtherUsersFragment(){
		// Empty constructor required for MyInformationFragment
	}

    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the sort menu
		inflater.inflate(R.menu.otherusers, menu);
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
    	if(mUsers != null){
            switch (itemId) {
        	case R.id.action_sort_name_desc:
        		mSortKey = SortKey.NAME;
        		break;
        	case R.id.action_sort_jointime_desc:
        		mSortKey = SortKey.JOINTIME;
        		break;
        	case R.id.action_sort_activitylevel_desc:
        		mSortKey = SortKey.ACTIVITYSCORE;
        		break;
            default:
            	return super.onOptionsItemSelected(item);
            }

            mUsers.clear();
            getData(Source.WEB, Page.CURRENT);
            showList(false);
    	} else {
    		return super.onOptionsItemSelected(item);
    	}
    	
    	return true;
    }
    
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new OtherUsersFragment instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the setting name (Note: remains null if it is neither in the saved state nor the arguments)
		settingName = savedstate.getString(Gegevens.EXTRA_MSG); 							
		if(settingName == null){ settingName = arguments.getString(Gegevens.EXTRA_MSG); } 							
		
		// sort the sort key
		mSortKey = (SortKey) savedstate.getSerializable(Gegevens.EXTRA_SORTKEY);
		if(mSortKey == null) { mSortKey = (SortKey) arguments.getSerializable(Gegevens.EXTRA_SORTKEY); }
		if(mSortKey == null) { mSortKey = SortKey.NAME; }
		
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the OtherUsersFragment view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_xlist, container, false);
		mListView = (XListView) mRootView.findViewById(android.R.id.list);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		
		return mRootView;
	}
	
	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring OtherUsersFragment instance state.");

		// load the any necessary data based on the setting name
		if(mUsers == null){
			getData(Source.WEB, Page.CURRENT);
		}
		
		showList(mUsers != null);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving OtherUsersFragment instance state.");
		// save the list of comments to the instance state
		if(settingName != null) { outState.putString(Gegevens.EXTRA_MSG, settingName); }
	}
	
	private void getData(Source source, Page page) {
		if(getActivity() != null){
			// Load the user from the DAO
			ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
			profileDAO.readUserList(source, mSortKey, mUsers, page);
		} 
	}

	private void showList(boolean showlist) {
		if(mListView != null){ 
			if(mListView.isPullLoading()){
				mListView.stopLoadMore();
			}
			if(mListView.isPullRefreshing()){
				mListView.stopRefresh(); 
				mListView.updateHeaderTime();
			}
		}


		if(mRootView != null && mListView != null){
			
			// show or hide the progress bar
			mListView.setVisibility((showlist) ? View.VISIBLE : View.GONE);
			((ProgressBar) mRootView.findViewById(R.id.progress_bar))
				.setVisibility((showlist) ? View.GONE : View.VISIBLE);
			
			
			// load the services list
			if(mUsers != null && mListView != null && showlist){
				Loggen.v(this, getTag() + " - Loading the adapter with " + mUsers.size() + " items.");
				
				// The comments have not been loaded to the list view. Do that now
				if(getActivity() != null){
					mListView.setAdapter(new UserArrayAdapter(getActivity(), 
							R.layout.list_item_user, android.R.id.text1, mUsers));
				}
				
				// set the choice mode and reaction to the choices 
				mListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

				
				if(mUsers.size() == 0){
					((TextView) mRootView.findViewById(android.R.id.empty)).setVisibility(View.VISIBLE);	
				}
			}
		}
	}

	@Override public void onRefresh() {
		Loggen.v(this, " called onRefresh.");
		getData(Source.WEB, Page.LATEST);
	}
	
	@Override
	public void onLoadMore() {
		Loggen.v(this, " called onLoadMore.");
		getData(Source.WEB, Page.PREVIOUS);
	}

	@Override public void onReadUserList(List<User> users) { 
		Loggen.v(this, "Got a response onReadUserList. users exist? " + (users != null) + " | " + (mUsers != null) );
		
		if(users != null && mUsers != null){
			// We got a response and are updating an existing list
			mUsers.clear();
			mUsers.addAll(users);
		} else if(users == null && mUsers == null){
			// We got no response and have no existing list
			userMustClickOkay(getString(R.string.forum_error_update_title), getString(R.string.forum_error_update_text)); 
			mUsers = new ArrayList<User> ();
			getData(Source.LOCAL, Page.CURRENT);
		} else if (mUsers == null) {
			// We got a response, but have no existing list 
			mUsers = (ArrayList<User>) users;			
		}
		
		showList(true);	
		
		if(users != null){
			mUsers = (ArrayList<User>) users;
			showList(true);
		}
	}

	// Not used
	@Override public void onReadUserInformation(User user) { }
	@Override public void onReadActivityHistory(List<ActivityRecord> records) { }
	@Override public void onChangeUser(User newUser, String errorMessage) { }
	@Override public void onLogin(boolean success, String errorMessage) { }

}