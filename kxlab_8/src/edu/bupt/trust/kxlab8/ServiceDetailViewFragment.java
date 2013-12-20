package edu.bupt.trust.kxlab8;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ServicesArrayAdapter;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.DialogFragmentScore;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ServiceDetailViewFragment extends BaseDetailFragment implements MyServicesListListener, ProfileListener{
	
	OnActionSelectedListener mListener;
	ServiceDetailActivity.ServiceType mServiceType;
	private ListView mCommentsList;
	private View mRootView;
	TrustService mService;
	User mOwner;
	User mUser;
	ArrayList<TrustService> comments;
	
	public ServiceDetailViewFragment (){
		// Empty constructor required for ServiceDetailViewFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((BaseActivity) getActivity()).mSettings.getUser();
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the services menu
		if(mServiceType == ServiceDetailActivity.ServiceType.MYSERVICE)
			inflater.inflate(R.menu.service_detail_view, menu);
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
        switch (itemId) {
        	case R.id.action_edit:
        		if(mListener != null) { mListener.onActionSelected(getTag(), Gegevens.FRAG_INFOEDIT, mService); }
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
		
		// load the service (Note: service remains null if it is neither in the saved state nor the arguments)
		mService = savedstate.getParcelable(Gegevens.EXTRA_SERVICE); 							
		if(mService == null){ mService = arguments.getParcelable(Gegevens.EXTRA_SERVICE); } 
		
		// load the comments (Note: comments remains null if it is neither in the saved state nor the arguments)
		comments = savedstate.getParcelableArrayList(Gegevens.EXTRA_COMMENTS); 							
		if(comments == null){ comments = arguments.getParcelableArrayList(Gegevens.EXTRA_COMMENTS); } 	

		// load the service type. This determines what the user can do while viewing the service details.
		if(savedstate.containsKey(Gegevens.EXTRA_SERVICETYPE)){
			mServiceType = (ServiceDetailActivity.ServiceType) savedstate.getSerializable(Gegevens.EXTRA_SERVICETYPE);
		} else if(arguments.containsKey(Gegevens.EXTRA_SERVICETYPE)) {
			mServiceType = (ServiceDetailActivity.ServiceType) arguments.getSerializable(Gegevens.EXTRA_SERVICETYPE);
		} else {
			mServiceType = ServiceDetailActivity.ServiceType.SERVICE;
		}
		
		// load the service author. (Note: author remains null if it is neither in the saved state nor the arguments)
		mOwner = savedstate.getParcelable(Gegevens.EXTRA_USER); 							
		if(mOwner == null){ mOwner = arguments.getParcelable(Gegevens.EXTRA_USER); } 
		
		// TODO: figure out if we need the categoryTag. For now: assume NO.
		
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ServiceDetailView view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_services_details_view, container, false);
		mCommentsList = (ListView) mRootView.findViewById(android.R.id.list);
		mCommentsList.addHeaderView(LayoutInflater.from(getActivity()).inflate(R.layout.list_header_service_details, null));
		((ImageButton) mRootView.findViewById(R.id.details_service_btn_comment)).setOnClickListener(this);
		((Button) mRootView.findViewById(R.id.details_service_btn_score)).setOnClickListener(this);		
		
		return mRootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ServiceDetailView instance state.");

		// TODO: Add an else contact DAO call to each of them.
		if(mOwner != null) { showAuthor(); }
		if(mService != null) { showService(); } 
		if(comments != null) { 
			showComments(); 
		} else {
			onReadServices(null);
		}
		showInformation(comments != null);
		
	}
	
	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving ServiceDetailView instance state.");
		// save the list of comments to the instance state
		outState.putParcelableArrayList(Gegevens.EXTRA_COMMENTS, comments);
		outState.putParcelable(Gegevens.EXTRA_SERVICE, mService);
		outState.putParcelable(Gegevens.EXTRA_USER, mOwner);
		outState.putSerializable(Gegevens.EXTRA_SERVICETYPE, mServiceType);
	}

	private void showInformation(boolean showinfo) {
		if(mRootView != null){
			// show or hide the progress bar
			((ProgressBar) mRootView.findViewById(R.id.progress_bar)).setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((LinearLayout) mRootView.findViewById(R.id.list_holder)).setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((TextView) mRootView.findViewById(android.R.id.empty))
				.setVisibility( (comments == null || comments.size() > 0) ? View.GONE : View.VISIBLE);
		} else {
			userMustClickOkay(getString(R.string.details_error_title), getString(R.string.details_error_text));
		}
	}
	
	private void showAuthor(){
		// load the user information
		if(mOwner != null && mRootView != null){
			// Set the image
			File imgFile = new File(mOwner.getPhotoLocation());
			ImageView avatar = (ImageView) mRootView.findViewById(R.id.details_owner_img);
			if(imgFile.exists()){
				avatar.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
			    		imgFile.getAbsolutePath(),
			    		avatar.getLayoutParams().width, 
			    		avatar.getLayoutParams().height));
			}
			
			// Set the user information
			((TextView) mRootView.findViewById(R.id.details_owner_name)).setText(mOwner.getUserName());
			((TextView) mRootView.findViewById(R.id.details_owner_time)).setText(mOwner.getTimeEnter());
			((TextView) mRootView.findViewById(R.id.details_owner_score)).setText(mOwner.getActivityScore());
		}
		
		// hide the progress bar
		showInformation(comments != null);
	}
	
	private void showComments(){
		// show the comments list 
		if(comments != null && mCommentsList != null && getActivity() != null){
			Loggen.v(this, getTag() + " - Loading the adapter with " + comments.size() + " items.");

			// load and set the adapter
			mCommentsList.setAdapter(new ServicesArrayAdapter(getActivity(), 
					R.layout.list_item_services, android.R.id.text1, comments));
		}

		// hide the progress bar
		showInformation(comments != null);
	}
	
	private void showService(){
		if(mService != null && mRootView != null){
			//Set the image
			File imgFile = new File(mService.getServicephoto());
			ImageView serviceImg = (ImageView) mRootView.findViewById(R.id.details_service_img);
			Loggen.v(this, getTag() + " - Showing service with image: " + imgFile.getAbsolutePath());
			if(imgFile.exists()){
				serviceImg.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
			    		imgFile.getAbsolutePath(),
			    		serviceImg.getLayoutParams().width, 
			    		serviceImg.getLayoutParams().height));
			}
			
			// Set the text TODO: Figure out what service score and number of users are??? 
			((TextView) mRootView.findViewById(R.id.details_service_title)).setText(mService.getServicetitle());
			((TextView) mRootView.findViewById(R.id.details_service_description)).setText(mService.getServicedetail());
			((TextView) mRootView.findViewById(R.id.details_service_score)).setText(String.valueOf(mService.getServicestatus()));
		}

		// hide the progress bar
		showInformation(comments != null);
	}

	@Override public void onReadServices(List<TrustService> services) {
		Loggen.i(this, getTag() + " - Returned from onReadservices. ");

		// Inform the user of any failures
		if(services == null){
			userMustClickOkay(getString(R.string.myinfo_no_comments_title), getString(R.string.myinfo_no_comments_text));
		}
		
		// update the services
		this.comments = (ArrayList<TrustService>) ((services != null) ? services : new ArrayList <TrustService> ());
		showComments();
		
	}

	@Override public void onClick(View view) {
		int id = view.getId();
		
		switch(id){
			case R.id.details_service_btn_comment:
			case R.id.details_service_btn_score:
				// create a are you sure confirm dialog 
				DialogFragmentBasic.newInstance(true)
					.setTitle("Are you sure")
					.setMessage("Only participating users are allowed to do this?")
					.setPositiveButtonText(getString(R.string.ok))
					.setNegativeButtonText(getString(R.string.cancel))
					.setObject(Integer.valueOf(id))
					.show(getFragmentManager(), Gegevens.FRAG_CONFIRM);
			break;
			default:
				((BaseActivity) getActivity()).onBtnClick(view);
		}
		
	}
	
	public void launchDialog(int id){
		switch(id){
			case R.id.details_service_btn_comment:
				// TODO: call the fragment that allows you to leave a comment
			break;
			case R.id.details_service_btn_score:
				// call the fragment that allows you to score
				DialogFragmentScore scorer =  (DialogFragmentScore) DialogFragmentScore.newInstance(true)
					.setTitle("Score the service")
					.setMessage("Please rate the service's credibility you have used")
					.setPositiveButtonText(getString(R.string.submit))
					.setNegativeButtonText(getString(R.string.cancel));
				scorer.setCancelable(false);
				scorer.show(getFragmentManager(), Gegevens.FRAG_SCORE);
			break;
		}
	}
	
	public void saveScore(int score){ }
	public void saveComment(String valueOf) { }

	@Override public void onCreateService(boolean success) { }
	@Override public void onDeleteService(boolean success) { }
	@Override public void onEditService(boolean success) { }
	@Override public void onSearchService(List<TrustService> services) { }
	@Override public void onReadUserList(List<User> users) {}
	@Override public void onReadUserInformation(User user) {}
	@Override public void onReadActivityHistory(ActivityHistory history) {}
	@Override public void onChangeUser(User newUser, String errorMessage) {}
	@Override public void onChangePhoto(boolean success, String errorMessage) {}
	@Override public void onChangePassword(boolean success, String errorMessage) {}
	@Override public void onChangePhonenumber(boolean success, String errorMessage) {}
	@Override public void onChangeSource(boolean success, String errorMessage) {}
	@Override public void onLocalFallback() {}
}
