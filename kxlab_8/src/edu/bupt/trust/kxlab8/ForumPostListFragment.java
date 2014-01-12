package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.ReplyArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.ForumDAO;
import edu.bupt.trust.kxlab.data.ForumDAO.ForumListener;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.model.JsonReply;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
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
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class ForumPostListFragment extends BaseDetailFragment implements ForumListener, IXListViewListener {
	
	private XListView mReplyList;
	private ReplyArrayAdapter mListAdapter;
	private View mRootView;
	private Post mPost;
	private ArrayList<JsonReply> replies;
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
			inflater.inflate(R.menu.forum_detail, menu);
		}
	}
	
    @Override public boolean onOptionsItemSelected(MenuItem item) {
    	int itemId = item.getItemId();
        switch (itemId) {
        	case R.id.action_reply:
				if(mListener != null && mUser.isLogin()) { 
					if(BaseActivity.isNetworkAvailable(getActivity()))
						mListener.onActionSelected(getTag(), Gegevens.FRAG_POSTEDIT, mPost);
					else
						userMustClickOkay(getString(R.string.no_network_title), getString(R.string.no_network_text));
				} else {
					userMustClickOkay(getString(R.string.myinfo_guest_title), getString(R.string.myinfo_guest_text));
				}
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
		Loggen.v(this, "start - mPost has type: " + mPost.getPostType());

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
		mRootView = inflater.inflate(R.layout.frag_generic_xlist, container, false);

		// Set the list view
		mReplyList = (XListView) mRootView.findViewById(android.R.id.list);

		// add the header
		if(mPost.getPostType() == PostType.ANNOUNCE || mPost.getPostType() == PostType.FAQ){
			((LinearLayout) mRootView.findViewById(R.id.fixed_header_holder)).addView(
					LayoutInflater.from(getActivity()).inflate(R.layout.list_header_forum_announce, null));
		} else {
			View header = inflater.inflate(R.layout.list_header_forum_post_title, container, false);
			((LinearLayout) mRootView.findViewById(R.id.fixed_header_holder)).addView(header);
			mReplyList.addHeaderView(
					LayoutInflater.from(getActivity()).inflate(R.layout.list_header_forum_post_text, null));
		}
		mReplyList.setPullLoadEnable(true);
		mReplyList.setXListViewListener(this);

		return mRootView;
	}

	@Override public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ForumPostListFragment instance state.");

		// Note: Without a post, we can't do anything. (Server requests require a service id) 
		if(mPost != null){
			
			// Hack to handle newly created replies.
			boolean forceupdate = false;
			if(getActivity() != null && getActivity() instanceof ForumPostActivity){
				ForumPostActivity act =  (ForumPostActivity) getActivity();
				forceupdate = act.getForceUpdate();
				act.forceUpdate(false);
			}
			
			// Load the post and the replies
			if(forceupdate || replies == null){
				getData(Source.WEB, Page.CURRENT);
			} else if(replies != null) { 
				showPost(); 
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
		
		if(mPost.getPostType() == PostType.ANNOUNCE || mPost.getPostType() == PostType.FAQ){
			Loggen.i(this, "-------- hiding view");
			((XListView) mRootView.findViewById(android.R.id.list)).setVisibility(View.GONE);
		}
		
		if(mReplyList != null){ 
			if(mReplyList.isPullLoading()){
				mReplyList.stopLoadMore();
			}
			if(mReplyList.isPullRefreshing()){
				mReplyList.stopRefresh(); 
				mReplyList.updateHeaderTime();
			}
		}

		if(mRootView != null){
			// show or hide the progress bar
			((ProgressBar) mRootView.findViewById(R.id.progress_bar)).setVisibility((showinfo) ? View.GONE : View.VISIBLE);
			((RelativeLayout) mRootView.findViewById(R.id.list_holder)).setVisibility((showinfo) ? View.VISIBLE : View.GONE);
		} else {
			userMustClickOkay(getString(R.string.details_error_title), getString(R.string.details_error_text));
		}
	}
	
	private void showPost(){
		if(mPost != null && mRootView != null){

			// set the basic information
			User owner = mPost.getPostSponsor();
			((TextView) mRootView.findViewById(R.id.user_email)).setText(owner.getEmail());
			((TextView) mRootView.findViewById(R.id.forum_post_title)).setText(mPost.getPostTitle());
			((TextView) mRootView.findViewById(R.id.forum_post_text)).setText(mPost.getPostDetail());
			((TextView) mRootView.findViewById(R.id.user_date)).setText(mPost.getPublishTimeString());

			// set the post specific information
			if(mPost.getPostType() != PostType.ANNOUNCE && mPost.getPostType() != PostType.FAQ){
				
				//Set the service image
				Loggen.v(this, getTag() + " - Showing post sponsor with image: " + owner.getLocalPhoto());
				setImageView(owner.getLocalPhoto(), (ImageView) mRootView.findViewById(R.id.user_img));
				
				// Set the text 
				((TextView) mRootView.findViewById(R.id.user_name)).setText(String.valueOf(owner.getName()));
				((TextView) mRootView.findViewById(R.id.user_date)).setText(owner.getTimeEnterString());
				((TextView) mRootView.findViewById(R.id.user_activitylevel_text)).setText(String.valueOf(owner.getActivityScore()));
			}
		}

		// show the comments list 
		if(replies != null && mReplyList != null){
			Loggen.v(this, getTag() + " - Loading the adapter with " + replies.size() + " items.");
			
			if(mReplyList.getAdapter() == null ){ 

				if(getActivity() != null){
					// The replies have not been loaded to the list view. Do that now
					mListAdapter = new ReplyArrayAdapter(getActivity(), 
							R.layout.list_item_reply, android.R.id.text1, replies);
					mListAdapter.setOnBtnClickListener(this);
					mReplyList.setAdapter(mListAdapter);
				}
			} else {
				// The replies are already loaded to the list view.
				((BaseAdapter)((HeaderViewListAdapter)mReplyList.getAdapter())
						.getWrappedAdapter()).notifyDataSetChanged();
			}
		}

		// hide the progress bar
		showInformation(replies != null);
	}
	
	private void getData(Source source, Page page){
		ForumDAO forumDAO = DaoFactory.getInstance().setForumDAO(getActivity(), this, mPost.getPostType());
		forumDAO.readPost(source, mPost, replies, page);
	}
	
	@Override public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case android.R.id.button1:
				int replyId = Integer.parseInt(String.valueOf(v.getTag()));
				JsonReply reply = null;
				for(JsonReply r : replies){ if(replyId == r.replyId){ reply = r; break; }}
				
				if(mListener != null && reply != null) { 
					if(mUser.isLogin()){
						if(BaseActivity.isNetworkAvailable(getActivity()))
							mListener.onActionSelected(getTag(), Gegevens.FRAG_POSTEDIT, reply);
						else 
							userMustClickOkay(getString(R.string.no_network_title), getString(R.string.no_network_text));
					} else {
						userMustClickOkay(getString(R.string.myinfo_guest_title), getString(R.string.myinfo_guest_text));
					}
				}
			break;
			default:
				super.onClick(v);
			break;
		}
	}

	@Override public void onRefresh() {
		Loggen.v(this, " caled onRefresh.");
		getData(Source.WEB, Page.LATEST);
	}
	
	@Override
	public void onLoadMore() {
		Loggen.v(this, " called onLoadMore.");
		getData(Source.WEB, Page.PREVIOUS);
	}

	@Override public void onReadPost(Post post, List <JsonReply> replies) {
		
		// make sure the replies are not empty
		 if(replies == null && this.replies == null){
			userMustClickOkay(getString(R.string.forum_error_update_title), getString(R.string.forum_error_update_text)); 
			this.replies = new ArrayList <JsonReply> ();
			getData(Source.LOCAL, Page.CURRENT);
		}
		
		// update the post
		if(post != null){
			this.mPost.setFromPost(post, false); 
		}

		// add the replies
		if(replies != null){
			if(this.replies == null){ this.replies = new ArrayList<JsonReply> (); }
			this.replies.clear();
			this.replies.addAll(replies);
		}
		
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


	@Override public void onReadAnnounceFAQ(Post post) {
		// update the post
		if(post != null){
			if(post.getPostType() != mPost.getPostType()){
				Loggen.e(this, "Got a different post type!!!!");
			}
			this.mPost.setFromPost(post, false); 
		}
		showPost();
		showInformation(true);
		
	}

	@Override
	public void onSearchPostList(List<Post> posts) {
		// TODO Auto-generated method stub
		
	}
}
