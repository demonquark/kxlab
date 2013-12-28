package edu.bupt.trust.kxlab8;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.adapters.PostArrayAdapter;
import edu.bupt.trust.kxlab.data.DaoFactory;
import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ForumDAO;
import edu.bupt.trust.kxlab.data.ForumDAO.ForumListener;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.Reply;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab.widgets.XListView;
import edu.bupt.trust.kxlab.widgets.XListView.IXListViewListener;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.HeaderViewListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *  This fragment shows a list of posts.
 *  The fragment expects the following items in its arguments: <br />
 *  - EXTRA_POSTTYPE: The type identifies the post type (FORUM, SUGGESTION, VOTE, ANNOUNCE, FAQ). Defaults to FORUM.
 */
public class ForumThreadListFragment extends BaseListFragment 
					implements IXListViewListener, OnItemClickListener, ForumListener {
	
	private PostType mPostType;
	private ArrayList <Post> mPosts;
	private View mRootView;
	private XListView mListView;
	
	public ForumThreadListFragment() {
        // Empty constructor required for ServicesListFragment
	}
	
    @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setHasOptionsMenu(true);
    }
    
	@Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		// add the forum menu
		Loggen.d(this, "post type is " + mPostType);
		if(mPostType == PostType.FORUM || mPostType == PostType.SUGGESTION){
			inflater.inflate(R.menu.forum, menu);
		}
	}

    @Override public boolean onOptionsItemSelected(MenuItem item) {

    	// Only process requests if we we've enabled interaction 
    	int itemId = item.getItemId();
        switch (itemId) {
        	case R.id.action_create:
        		startPostActivity(null);
            break;
            default:
            	return super.onOptionsItemSelected(item);
        }
    	return true;
    }
	
	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ThreadList instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the posts (Note: posts remains null if it is neither in the saved state nor the arguments)
		mPosts = savedstate.getParcelableArrayList(Gegevens.EXTRA_POSTS); 							
		Loggen.v(this, "Posts retrieved and is " + (mPosts == null));
		if(mPosts == null){ mPosts = arguments.getParcelableArrayList(Gegevens.EXTRA_POSTS); } 	
		Loggen.v(this, "Posts retrieved and is " + (mPosts == null));
		
		// load the post type
		mPostType = (PostType) savedstate.getSerializable(Gegevens.EXTRA_POSTTYPE);
		Loggen.v(this, "Post type retrieved and is " + mPostType);
		if(mPostType == null){ mPostType = (PostType) arguments.getSerializable(Gegevens.EXTRA_POSTTYPE); } 	
		Loggen.v(this, "Post type retrieved and is " + mPostType);
		if(mPostType == null){
		    PostType [] allTypes = PostType.values();
		    for(int i = 0; i < allTypes.length; i++){
		    	if(allTypes[i].getFragName().equals(this.getTag())){
		    		mPostType = allTypes[i]; break;
		    	}
		    }
		}
		
		Loggen.v(this, "Post type retrieved and is " + mPostType);
		
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the ThreadList view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_generic_xlist, container, false);
		
		// add some padding for the footer 
		int bottomPadding = (int) getResources().getDimension(R.dimen.footer_height);
		mRootView.findViewById(R.id.list_top_container).setPadding(0,0,0,bottomPadding);
		
		mListView = (XListView) mRootView.findViewById(android.R.id.list);
		mListView.setPullLoadEnable(true);
		mListView.setXListViewListener(this);

		return mRootView;
	}

	@Override
	public void onViewStateRestored(Bundle savedInstanceState) {
		super.onViewStateRestored(savedInstanceState);
		Loggen.v(this, getTag() + " - Restoring ThreadList instance state.");

		// load the posts list if requested (generally only if we just created the fragment)
		if(mPosts == null){
			// Load the services from the DAO
			Loggen.v(this, "Restoring saved Instancestate: Hide the list");
			getData(Source.DUMMY, Page.CURRENT);
		}

		// show or hide the list
		showList(mPosts != null);
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving ServiceList instance state.");
		// save the list of services to the instance state
		if(mPosts != null) { outState.putParcelableArrayList(Gegevens.EXTRA_POSTS, mPosts); }
		outState.putSerializable(Gegevens.EXTRA_POSTTYPE, mPostType);
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
		
		// Show or hide the progress bar
		mListView.setVisibility((showlist) ? View.VISIBLE : View.GONE);
		((ProgressBar) mRootView.findViewById(R.id.progress_bar))
							.setVisibility((showlist) ? View.GONE : View.VISIBLE);
		((TextView) mRootView.findViewById(android.R.id.empty)).setVisibility(View.GONE);
		
		// load the posts list
		if(mPosts != null && mListView != null && showlist){
			Loggen.v(this, getTag() + " - Loading the adapter with " + mPosts.size() + " items.");
			
			if(mListView.getAdapter() == null ){ 
				// The comments have not been loaded to the list view. Do that now
				if(getActivity() != null){
					PostArrayAdapter listAdapter = new PostArrayAdapter(getActivity(), 
							R.layout.list_item_post, android.R.id.text1, mPosts);
					mListView.setAdapter(listAdapter);
					mListView.setOnItemClickListener(this);
				}
			} else {
				// The comments are already loaded to the list view.
				((BaseAdapter)((HeaderViewListAdapter)mListView.getAdapter())
						.getWrappedAdapter()).notifyDataSetChanged();
			}
			
			if(mPosts.size() == 0){
				((TextView) mRootView.findViewById(android.R.id.empty)).setVisibility(View.VISIBLE);	
			}
		}
	}

	private void getData(Source source, Page page) {
		if(getActivity() != null){
			ForumDAO forumDAO = DaoFactory.getInstance().setForumDAO(getActivity(), this, mPostType);
			forumDAO.readPostList(source, mPostType, page);
		}
	}
	
	private void startPostActivity(Post post){
		if(getActivity() != null){
			// Bundle the post and send it off to the detail activity
			Bundle b = new Bundle();
			if(post != null) { b.putParcelable(Gegevens.EXTRA_POST, post);
			} else { b.putSerializable(Gegevens.EXTRA_POSTTYPE, mPostType); }
			Intent intent = new Intent(getActivity(), ForumPostActivity.class);
			intent.putExtra(Gegevens.EXTRA_MSG, b);
			startActivity(intent);
		}
	}

	@Override public void onRefresh() {
		Loggen.v(this, " called onRefresh.");
		getData(Source.DUMMY, Page.LATEST);
	}
	
	@Override
	public void onLoadMore() {
		Loggen.v(this, " called onLoadMore.");
		getData(Source.DUMMY, Page.PREVIOUS);
	}

	@Override public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
		if(position > 0 && position <= mPosts.size()){
			position--; 
			// TODO: Figure out why the method is returning the wrong position
			Loggen.v(this, getTag() + " - onItemClick for: " + mPosts.get(position).getPostTitle());
			startPostActivity(mPosts.get(position));
		}
	}

	@Override public void onReadPostList(List<Post> posts) {
		Loggen.v(this, "Got a response onReadPostList. posts exist? " + (posts != null));
		mPosts = (ArrayList<Post>) posts;
		showList(true);
		
	}

	@Override public void onCreatePost(boolean success) {	}
	@Override public void onCreateReply(boolean success) { }
	@Override public void onCreateVote(boolean success) { }
	@Override public void onReadPost(Post post, List <Reply> replies) { }
	@Override public void onReadAnnounceFAQ(Post post) { }
	@Override public void onSearchPostList(List<Post> posts) { }

}