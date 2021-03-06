package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ActivityRecordsArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.JsonActivityRecord;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.XListView;
import edu.bupt.trust.kxlab.widgets.XListView.IXListViewListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MyInformationRecordsFragment extends BaseDetailFragment implements ProfileListener, IXListViewListener {
	
	private User mUser;
	private ArrayList<JsonActivityRecord> mRecords;
	private View mRootView;
	private XListView mListView;
	
	public MyInformationRecordsFragment(){
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
		mRecords = savedstate.getParcelableArrayList(Gegevens.EXTRA_RECORDS); 							
		if(mRecords == null){ mRecords = arguments.getParcelableArrayList(Gegevens.EXTRA_RECORDS); } 	
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the MyInformationRecordsFragment view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_xlist, container, false);
		inflater.inflate(R.layout.include_user, 
				((ViewGroup) mRootView.findViewById(R.id.fixed_header_holder)), true);

		mListView = (XListView) mRootView.findViewById(android.R.id.list);
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);

		return mRootView;
	}
	
	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring MyInformationRecordsFragment instance state.");

		// load the records if requested (generally only if we just created the fragment)
		if(mRecords == null){
			// Load the services from the DAO
			getData(Source.WEB, Page.CURRENT);
		}
		
		// If we already have a set of records, just show the records
		showList(mRecords != null);

	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving MyInformationRecordsFragment instance state.");
		// save the list of comments to the instance state
		if(mUser != null) { outState.putParcelable(Gegevens.EXTRA_USER, mUser); }
		if(mRecords != null) { outState.putParcelableArrayList(Gegevens.EXTRA_RECORDS, mRecords); }
	}
	
	private void showList(boolean showinfo) {
		
		if(mListView != null){ 
			if(mListView.isPullLoading()){
				mListView.stopLoadMore();
			}
			if(mListView.isPullRefreshing()){
				mListView.stopRefresh(); 
				mListView.updateHeaderTime();
			}
		}

		if(mRootView != null){
			// show or hide the information
			((ProgressBar) mRootView.findViewById(R.id.progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((LinearLayout) mRootView.findViewById(R.id.content_holder))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((TextView) mRootView.findViewById(android.R.id.empty))
				.setVisibility( (mRecords != null && mRecords.size() == 0 ) ? View.VISIBLE : View.GONE);
			
			
			// load the user information
			if(mUser != null && showinfo){
				// Set the image
				File imgFile = new File(mUser.getLocalPhoto());
				ImageView avatar = (ImageView) mRootView.findViewById(R.id.user_img);
				if(imgFile.exists()){
					avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
				    		imgFile.getAbsolutePath(),
				    		avatar.getLayoutParams().width, 
				    		avatar.getLayoutParams().height));
				}
				
				// Set the user information
				((TextView) mRootView.findViewById(R.id.user_name)).setText(mUser.getName());
				((TextView) mRootView.findViewById(R.id.user_email)).setText(mUser.getEmail());
				((TextView) mRootView.findViewById(R.id.user_date)).setText(mUser.getTimeEnterString());
				((TextView) mRootView.findViewById(R.id.user_activitylevel_text)).setText(String.valueOf(mUser.getActivityScore()));
			}
			
			// load the activity history
			if(mRecords != null && mListView != null && showinfo){
				Loggen.v(this, getTag() + " - Loading the adapter with " + mRecords.size() + " items.");

				if(mListView.getAdapter() == null ){ 
					// The records have not been loaded to the list view. Do that now
					if(getActivity() != null){
						mListView.setAdapter(new ActivityRecordsArrayAdapter(getActivity(), 
								R.layout.list_item_activityrecord, android.R.id.text1, mRecords));
					}
				} else {
					// The comments are already loaded to the list view.
					((BaseAdapter)((HeaderViewListAdapter)mListView.getAdapter())
							.getWrappedAdapter()).notifyDataSetChanged();
				}
			}
		}
	}
	
	private void getData(Source source, Page page) {
		if(getActivity() != null){
			ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
			profileDAO.readActivityHistory(source, mUser, mRecords, page);
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

	@Override public void onReadActivityHistory(List <JsonActivityRecord> records) {
		Loggen.v(this, "Got a response onReadActivityHistory. records exist? " + (records != null));
		if(records != null && mRecords != null){
			// We got a response and are updating an existing list
			mRecords.clear();
			mRecords.addAll(records);
		} else if(records == null && mRecords == null){
			// We got no response and have no existing list
			userMustClickOkay(getString(R.string.myinfo_no_records_title), getString(R.string.myinfo_no_records_text)); 
			mRecords = new ArrayList<JsonActivityRecord> ();
			getData(Source.LOCAL, Page.CURRENT);
		} else if (mRecords == null){
			// We got a response and have no existing list
			mRecords = (ArrayList<JsonActivityRecord>) records;			
		}
		
		showList(true);		
	}

	// Not used
	@Override public void onReadUserInformation(User user) { }
	@Override public void onChangeUser(User newUser, String errorMessage) { }
	@Override public void onReadUserList(List<User> users) { }
	@Override public void onLogin(boolean success, String errorMessage) {	}
}