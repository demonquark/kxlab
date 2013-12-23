package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.CommentsArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ForumDAO;
import edu.bupt.trust.kxlab.data.ForumDAO.ForumListener;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.model.Comment;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.Reply;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 *  This fragment shows the details and comments of a post and lets you reply on the post
 *  The fragment expects the following items in its arguments: <br />
 *  - EXTRA_POST: A post object. Loads additional details from ForumDAO. <br />
 *  The fragment supplements those arguments with the following items (added to saved state): <br />
 *  - EXTRA_REPLIES: Comments to the service. Loaded from ServicesDAO. <br />
 *  - EXTRA_USER: The current user. Loaded from the settings. <br />
 */
public class ForumPostListFragment extends BaseDetailFragment implements ForumListener {
	
	private ListView mCommentsList;
	private CommentsArrayAdapter mListAdapter;
	private View mRootView;
	private Post mPost;
	private ArrayList<Reply> replies;
	private User mUser;
	
	public ForumPostListFragment (){
		// Empty constructor required for ServiceDetailViewFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
		Loggen.v(this, getTag() + " - onActivityCreated for ForumPostListFragment instance.");
        // create objects that require the Activity context
		if(mUser == null){ mUser = ((BaseActivity) getActivity()).mSettings.getUser(); }
        
        setHasOptionsMenu(true);
    }

	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	
		// add the forum menu
		if(mPost != null && 
				(mPost.getPostType()  == PostType.FORUM || mPost.getPostType() == PostType.SUGGESTION)){
			inflater.inflate(R.menu.forum, menu);
		}
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
        switch (itemId) {
        	case R.id.action_create:
        		if(mListener != null) { mListener.onActionSelected(getTag(), Gegevens.FRAG_POSTEDIT, mPost); }
            break;
            default:
            	return super.onOptionsItemSelected(item);
        }
    	
    	return true;
    }
    
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ForumPostListFragment instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the post being replied to. 
		// (Note: mPost remains null if it is neither in the saved state nor the arguments)
		mPost = savedstate.getParcelable(Gegevens.EXTRA_POST); 							
		if(mPost == null){ mPost = arguments.getParcelable(Gegevens.EXTRA_POST); }
		

		// load the replies (Note: replies remains null if it is neither in the saved state nor the arguments)
		replies = savedstate.getParcelableArrayList(Gegevens.EXTRA_REPLIES); 							
		if(replies == null){ replies = arguments.getParcelableArrayList(Gegevens.EXTRA_REPLIES); } 	

		// load the user. (Note: We use the settings user as fall back )
		if(savedstate.containsKey(Gegevens.EXTRA_USER)) { mUser = savedstate.getParcelable(Gegevens.EXTRA_USER); } 							
		if(mUser == null && arguments.containsKey(Gegevens.EXTRA_USER)) { 
			mUser = arguments.getParcelable(Gegevens.EXTRA_USER); } 							
		if(mUser == null && getActivity() != null){ mUser = ((BaseActivity) getActivity()).mSettings.getUser(); } 
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ForumPostListFragment view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_list, container, false);
		View header = inflater.inflate(R.layout.list_header_forum_post, container, false);

		mCommentsList = (ListView) mRootView.findViewById(android.R.id.list);
		((LinearLayout) mRootView.findViewById(R.id.fixed_header_holder)).addView(header);
		
		return mRootView;
	}

	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ForumPostListFragment instance state.");

		// Note: Without a service, we can't do anything. (Server requests require a service id) 
		if(mPost != null){
			
			// Load the service and the comments 
			if(replies != null) { showPost(); 
			} else if(getActivity() != null) {
				ForumDAO forumDAO = DaoFactory.getInstance().setForumDAO(getActivity(), this, mPost.getPostType());
				forumDAO.readPost(Source.DUMMY,mPost);
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
		outState.putParcelable(Gegevens.EXTRA_POST, mPost);
		outState.putParcelableArrayList(Gegevens.EXTRA_REPLIES, replies);
		if(mUser != null){ outState.putParcelable(Gegevens.EXTRA_USER, mUser); }
	}

	private void showInformation(boolean showinfo) {
		if(mRootView != null){
			// show or hide the progress bar
			((ProgressBar) mRootView.findViewById(R.id.progress_bar)).setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((RelativeLayout) mRootView.findViewById(R.id.list_holder)).setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((TextView) mRootView.findViewById(android.R.id.empty))
				.setVisibility( (replies != null && replies.size() == 0) ? View.VISIBLE : View.GONE);
		} else {
			userMustClickOkay(getString(R.string.details_error_title), getString(R.string.details_error_text));
		}
	}
	
	private void showPost(){
		if(mPost != null && mRootView != null){

			User owner = mPost.getPostSponsor();
			
			//Set the service image
			Loggen.v(this, getTag() + " - Showing service with image: " + owner.getPhotoLocation());
			setImageView(owner.getPhotoLocation(), (ImageView) mRootView.findViewById(R.id.user_img));
			
			// Set the text 
			((TextView) mRootView.findViewById(R.id.user_name)).setText(owner.getUserName());
			((TextView) mRootView.findViewById(R.id.user_activitylevel_text)).setText(owner.getActivityScore());
			((TextView) mRootView.findViewById(R.id.user_date)).setText(owner.getTimeEnter());
			((TextView) mRootView.findViewById(R.id.forum_post_title)).setText(mPost.getPostTitle());
			
		}

		// show the comments list 
		if(replies != null && mCommentsList != null){
			Loggen.v(this, getTag() + " - Loading the adapter with " + replies.size() + " items.");
			
			if(mCommentsList.getAdapter() == null ){ 
				// The comments have not been loaded to the list view. Do that now
				
				ArrayList <Comment> comments = new ArrayList <Comment> ();
				Comment x = new Comment();
				x.setCommentdetail(mPost.getPostDetail());
				comments.add(x);
				
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
		showInformation(replies != null);
	}

	@Override
	public void onReadPost(Post post) {
		// TODO Auto-generated method stub
		replies = new ArrayList<Reply> ();
		replies.add(new Reply());
		showPost();
		
	}

	
	@Override
	public void onCreatePost(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateReply(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCreateVote(boolean success) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadPostList(List<Post> posts) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onReadAnnounceFAQ(Post post) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSearchPostList(List<Post> posts) {
		// TODO Auto-generated method stub
		
	}
	
}