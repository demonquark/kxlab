package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.CommentsArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ProfileDAO;
import edu.bupt.trust.kxlab.data.ServicesDAO;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesDetailListener;
import edu.bupt.trust.kxlab.model.ActivityHistory;
import edu.bupt.trust.kxlab.model.Comment;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.DialogFragmentBasic;
import edu.bupt.trust.kxlab.widgets.DialogFragmentEditText;
import edu.bupt.trust.kxlab.widgets.DialogFragmentScore;
import edu.bupt.trust.kxlab.data.RawResponse.Page;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *  This fragment shows the details and comments of a service and lets you score and comment on the service.
 *  The fragment expects the following items in its arguments: <br />
 *  - EXTRA_SERVICE: A service object. Loads additional details from ServicesDAO. <br />
 *  - EXTRA_FLAVOR: The service flavor (MYSERVICE, SERVICE). Defaults to SERVICE. <br />
 *  The fragment supplements those arguments with the following items (added to saved state): <br />
 *  - EXTRA_COMMENTS: Comments to the service. Loaded from ServicesDAO. <br />
 *  - EXTRA_USER: The current user. Loaded from the settings. <br />
 *  - EXTRA_USER2: The owner of the service. Loaded from the ProfileDAO. <br />
 *  - EXTRA_USERSNUMBER: The number of users using the service. Loaded from ServicesDAO. <br />
 *  
 */
public class ServiceDetailViewFragment extends BaseDetailFragment implements ServicesDetailListener, ProfileListener{
	
	private ListView mCommentsList;
	private CommentsArrayAdapter mListAdapter;
	private View mRootView;
	private ServiceFlavor mFlavor;
	private TrustService mService;
	private ArrayList<Comment> comments;
	private User mOwner;
	private User mUser;
	
	// temporary variables until I think of something better
	int mNumberOfUsers;
	int mScore;
	String mCommentText;
	
	public ServiceDetailViewFragment (){
		// Empty constructor required for ServiceDetailViewFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
		Loggen.v(this, getTag() + " - onActivityCreated for ServiceDetailView instance.");
        // create objects that require the Activity context
		if(mUser == null){ mUser = ((BaseActivity) getActivity()).mSettings.getUser(); }
        
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the services menu
		if(mFlavor == ServiceFlavor.MYSERVICE)
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
		
		// load the service flavor. This determines what the user can do while viewing the service details.
		Loggen.d(this, getTag() + " - Flavor is " + mFlavor);
		mFlavor = (ServiceFlavor) savedstate.getSerializable(Gegevens.EXTRA_FLAVOR);
		Loggen.d(this, getTag() + " - Flavor is " + mFlavor);
		if(mFlavor == null) { mFlavor = (ServiceFlavor) arguments.getSerializable(Gegevens.EXTRA_FLAVOR); }
		Loggen.d(this, getTag() + " - Flavor is " + mFlavor);
		if(mFlavor == null) { mFlavor = ServiceFlavor.SERVICE; }
		Loggen.d(this, getTag() + " - Flavor is " + mFlavor);

		// load the comments (Note: comments remains null if it is neither in the saved state nor the arguments)
		comments = savedstate.getParcelableArrayList(Gegevens.EXTRA_COMMENTS); 							
		if(comments == null){ comments = arguments.getParcelableArrayList(Gegevens.EXTRA_COMMENTS); } 	

		// load the user. (Note: We use the settings user as fall back )
		if(savedstate.containsKey(Gegevens.EXTRA_USER)) { mUser = savedstate.getParcelable(Gegevens.EXTRA_USER); } 							
		if(mUser == null && arguments.containsKey(Gegevens.EXTRA_USER)) { 
			mUser = arguments.getParcelable(Gegevens.EXTRA_USER); } 							
		if(mUser == null && getActivity() != null){ mUser = ((BaseActivity) getActivity()).mSettings.getUser(); } 
		
		// load the service owner. (Note: owner remains null if it is neither in the saved state nor the arguments)
		mOwner = savedstate.getParcelable(Gegevens.EXTRA_USER2); 							
		if(mOwner == null){ mOwner = arguments.getParcelable(Gegevens.EXTRA_USER2); } 
		
		// load the number of users that the service has. Kind of an ugly way to keep track of this.
		mNumberOfUsers = savedstate.getInt(Gegevens.EXTRA_USERSNUMBER, -1); 							
		if(mNumberOfUsers == -1){ mNumberOfUsers = arguments.getInt(Gegevens.EXTRA_USERSNUMBER, -1); } 
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ServiceDetailView view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_service_detail_view, container, false);
		mCommentsList = (ListView) mRootView.findViewById(android.R.id.list);
		mCommentsList.addHeaderView(
				LayoutInflater.from(getActivity()).inflate(R.layout.list_header_service_details, null));
		((ImageButton) mRootView.findViewById(R.id.details_service_btn_comment)).setOnClickListener(this);
		((Button) mRootView.findViewById(R.id.details_service_btn_score)).setOnClickListener(this);		
		
		return mRootView;
	}

	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ServiceDetailView instance state.");

		// Note: Without a service, we can't do anything. (Server requests require a service id) 
		if(mService != null){
			// Load the service author's information
			if(mOwner != null) { showOwner(); 
			} else if(getActivity() != null) {
				ProfileDAO profileDAO = DaoFactory.getInstance().setProfileDAO(getActivity(), this);
				profileDAO.readUserInformation(Source.DUMMY, mService.getUseremail());
			}
			
			// Load the service and the comments 
			if(comments != null) { showService(); 
			} else if(getActivity() != null) {
				ServicesDAO servicesDAO = DaoFactory.getInstance().setServicesDAO(getActivity(), this);
				servicesDAO.readService(DaoFactory.Source.DUMMY, mService.getServiceid(), Page.LATEST);
			}
		} else {
			// give an error message ... 
			 userMustClickOkay(getString(R.string.details_error_title), getString(R.string.details_error_text2));
		}
	}
	
	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving ServiceDetailView instance state.");
		// save the list of comments to the instance state
		outState.putParcelable(Gegevens.EXTRA_SERVICE, mService);
		outState.putSerializable(Gegevens.EXTRA_FLAVOR, mFlavor);
		outState.putParcelableArrayList(Gegevens.EXTRA_COMMENTS, comments);
		if(mUser != null){ outState.putParcelable(Gegevens.EXTRA_USER, mUser); }
		outState.putParcelable(Gegevens.EXTRA_USER2, mOwner);
		outState.putInt(Gegevens.EXTRA_USERSNUMBER, mNumberOfUsers);
	}

	private void showInformation(boolean showinfo) {
		if(mRootView != null){
			// show or hide the progress bar
			((ProgressBar) mRootView.findViewById(R.id.progress_bar)).setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((LinearLayout) mRootView.findViewById(R.id.list_holder)).setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((TextView) mRootView.findViewById(android.R.id.empty))
				.setVisibility( (comments != null && comments.size() > 0) ? View.GONE : View.VISIBLE);
		} else {
			userMustClickOkay(getString(R.string.details_error_title), getString(R.string.details_error_text));
		}
	}
	
	private void showOwner(){
		// load the user information
		if(mOwner != null && mRootView != null){
			// Set the image
			setImageView(mOwner.getPhotoLocation(), (ImageView) mRootView.findViewById(R.id.details_owner_img));
			
			// Set the user information
			((TextView) mRootView.findViewById(R.id.details_owner_name)).setText(mOwner.getUserName());
			((TextView) mRootView.findViewById(R.id.details_owner_time)).setText(mOwner.getTimeEnter());
			((TextView) mRootView.findViewById(R.id.details_owner_score)).setText(mOwner.getActivityScore());
		}
		
		// hide the progress bar
		showInformation(comments != null);
	}
	
	private void showService(){
		if(mService != null && mRootView != null){

			//Set the service image
			Loggen.v(this, getTag() + " - Showing service with image: " + mService.getServicephoto());
			setImageView(mService.getServicephoto(), (ImageView) mRootView.findViewById(R.id.details_service_img));

			// Get the String values for the screen (number of users and credibility score)
			// TODO figure out what credibility and service status are...
			String numberOfUsers = getString(R.string.details_service_number) + ": ";
			numberOfUsers += (mNumberOfUsers >= 0) ? String.valueOf(mNumberOfUsers) : getString(R.string.unknown); 
			String credibility = getString(R.string.details_service_score) + ": " 
								+ String.valueOf(mService.getServicestatus());
			
			// Set the text 
			((TextView) mRootView.findViewById(R.id.details_service_title)).setText(mService.getServicetitle());
			((TextView) mRootView.findViewById(R.id.details_service_description)).setText(mService.getServicedetail());
			((TextView) mRootView.findViewById(R.id.details_service_score)).setText(credibility);
			((TextView) mRootView.findViewById(R.id.details_service_number)).setText(numberOfUsers);
		}

		// show the comments list 
		if(comments != null && mCommentsList != null){
			Loggen.v(this, getTag() + " - Loading the adapter with " + comments.size() + " items.");
			
			if(mCommentsList.getAdapter() == null ){ 
				// The comments have not been loaded to the list view. Do that now
				if(getActivity() != null){
					mListAdapter = new CommentsArrayAdapter(getActivity(), 
							R.layout.list_item_comment, android.R.id.text1, comments);
					mCommentsList.setAdapter(mListAdapter);
				}
			} else {
				// The comments are already loaded to the list view.
				((BaseAdapter)((HeaderViewListAdapter)mCommentsList.getAdapter())
						.getWrappedAdapter()).notifyDataSetChanged();
			}
		}

		// hide the progress bar
		showInformation(comments != null);
	}
	
	private void saveScore(int score){ 
		mScore = score;
		ServicesDAO servicesDAO = DaoFactory.getInstance().setServicesDAO(getActivity(), this);
		servicesDAO.updateServiceScore(DaoFactory.Source.DUMMY, mService.getServiceid(), mUser.getEmail(), score);
		showInformation(false);
	}
	private void saveComment(String commentText) {
		mCommentText = commentText;
		ServicesDAO servicesDAO = DaoFactory.getInstance().setServicesDAO(getActivity(), this);
		servicesDAO.createServiceComment(DaoFactory.Source.DUMMY, mService.getServiceid(), mUser.getEmail(), commentText);
		showInformation(false);
	}

	/**	Callback for all button click events  
	 * There are two buttons worth considering: <br />
	 *  - R.id.details_service_btn_comment: The user wants to comment on the service
	 *  - R.id.details_service_btn_score: The user wants to score the service
	 *  - TODO: Button click even for the comment replies.
	 */
	@Override public void onClick(View view) {
		int id = view.getId();
		
		switch(id){
			case R.id.details_service_btn_comment:
			case R.id.details_service_btn_score:
				// create a are you sure confirm dialog 
				DialogFragmentBasic.newInstance(true)
					.setTitle(getString(R.string.details_dialog_confirm_user_title))
					.setMessage(getString(R.string.details_dialog_confirm_user_text))
					.setPositiveButtonText(getString(R.string.yes))
					.setNegativeButtonText(getString(R.string.no))
					.setObject(Integer.valueOf(id))
					.show(getFragmentManager(), Gegevens.FRAG_CONFIRM);
			break;
		}
		
	}

	/**	Default callback from all dialog fragments.  
	 * There are three possible dialogs worth considering: <br />
	 *  - FRAG_CONFIRM: Expect the return object to be an Integer. Two possibility <br />
	 *  - - mGenericObject == R.id.details_service_btn_comment: The user wants to comment on the service
	 *  - - mGenericObject == R.id.details_service_btn_score: The user wants to score the service
	 *  - FRAG_COMMENT: The user has commented on the service. Expect the return object to be a String
	 *  - FRAG_SCORE: The user has scored the service. Expect the return object to be an Integer
	 */
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if (Gegevens.FRAG_CONFIRM.equals(tag) && o instanceof Integer){ 
			int id = (Integer) o;
			switch(id){
				case R.id.details_service_btn_comment:
					// call the dialog fragment that allows you to leave a comment
					DialogFragmentEditText.newInstance(true, this, R.layout.dialog_comment)
						.setTitle(getString(R.string.details_dialog_comment_title))
						.setMessage(getString(R.string.details_dialog_comment_text))
						.setPositiveButtonText(getString(R.string.submit))
						.setNegativeButtonText(getString(R.string.cancel))
						.setCancelableAndReturnSelf(false)
						.show(getFragmentManager(), Gegevens.FRAG_COMMENT);
				break;
				case R.id.details_service_btn_score:
					// call the dialog fragment that allows you to score
					DialogFragmentScore.newInstance(true)
						.setTitle(getString(R.string.details_dialog_rate_title))
						.setMessage(getString(R.string.details_dialog_rate_text))
						.setPositiveButtonText(getString(R.string.submit))
						.setNegativeButtonText(getString(R.string.cancel))
						.setCancelableAndReturnSelf(false)
						.show(getFragmentManager(), Gegevens.FRAG_SCORE);
				break;
			}
		} else if (Gegevens.FRAG_COMMENT.equals(tag) && o instanceof String) {
			saveComment(String.valueOf(o));
		} else if (Gegevens.FRAG_SCORE.equals(tag) && o instanceof Integer) {
			saveScore((Integer) o);
		}
	}	
	@Override public void onReadService(TrustService service, int numberOfUsers, List<Comment> comments) { 
		Loggen.i(this, getTag() + " - Returned from onReadservice. ");

		// Inform the user of any failures
		if(service == null){
			userMustClickOkay(getString(R.string.details_no_service_title), getString(R.string.details_no_service_text));
		} else if(comments == null){
			userMustClickOkay(getString(R.string.details_no_comments_title), getString(R.string.details_no_comments_text));
		}
		
		// update the service details
		this.mService = (service != null) ? service : mService;
		this.comments = (ArrayList<Comment>) ((comments != null) ? comments : new ArrayList <Comment> ());
		this.mNumberOfUsers = numberOfUsers;
		showService();
	}
	
	@Override public void onReadUserInformation(User user) {
		Loggen.v(this," Received User information from dao.");
		if(user != null){ 
			// show the user information
			mOwner = user;
		}else{
			// show an error message
			userMustClickOkay(getString(R.string.myinfo_no_user_title), getString(R.string.myinfo_no_user_text));
		}
		showOwner();
	}

	@Override public void writeServiceScore(boolean success) {
		Loggen.v(this," Received Score update from DAO");
		showService();
	}
	
	@Override public void writeServiceComment(boolean success) {
		Loggen.v(this," Received Comment update from DAO");
		if(comments != null){
			Comment myComment = new Comment();
			myComment.setCommentdetail(mCommentText);
			myComment.setCommenttime(System.currentTimeMillis());
			myComment.setUseremail(mUser.getEmail());
			myComment.setCommentscore(String.valueOf(mScore));
			comments.add(0, myComment);
		}
		showService();
	}

	@Override public void onReadUserList(List<User> users) {}
	@Override public void onCreateService(boolean success) { }
	@Override public void onReadActivityHistory(ActivityHistory history) {}
	@Override public void onChangeUser(User newUser, String errorMessage) {}
	@Override public void onChangePhoto(boolean success, String errorMessage) {}
	@Override public void onChangePassword(boolean success, String errorMessage) {}
	@Override public void onChangePhonenumber(boolean success, String errorMessage) {}
	@Override public void onChangeSource(boolean success, String errorMessage) {}
	@Override public void onEditService(boolean success) { }
	@Override public void onLocalFallback() {}

}
