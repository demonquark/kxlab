package edu.bupt.trust.kxlab8;

import java.util.List;

import edu.bupt.trust.kxlab.data.ForumDAO.ForumListener;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.Reply;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 *  This fragment lets you write a post or a reply
 *  The fragment expects one of the following arguments: <br />
 *  - EXTRA_POST: A post object. This means the user is writing a reply to a post <br />
 *  - EXTRA_REPLY: A reply object. This means the user is writing a reply to a reply <br />
 *  - EXTRA_POSTTYPE: A post type object. This means the user is writing a new post thread <br />
 */
public class ForumPostCreateFragment extends BaseDetailFragment implements ForumListener {
	
	private Post mPost;
	private PostType mPostType;
	private Reply mReply;
	private User mUser;
	private View mRootView;
	private EditText mEditTitle;
	private EditText mEditContent;
	
	public ForumPostCreateFragment(){
		// Empty constructor required for MyInformationFragment
	}
	
	/** Set the options menu and load defaults for the class variables that require a context */
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // create a copy of the user
		if(mUser == null){ mUser = ((BaseActivity) getActivity()).mSettings.getUser(); }

    }

	@Override public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Loggen.v(this, getTag() + " - Creating a new ForumPostCreateFragment instance.");
		
		// Assign values to the class variables (This avoids facing null exceptions when creating / saving a Parcelable)
		// convert the saved state to an empty bundle to avoid errors later on
		Bundle savedstate = (savedInstanceState != null) ? savedInstanceState : new Bundle();
		Bundle arguments = (getArguments() != null) ? getArguments() : new Bundle();
		
		// load the post being replied to. 
		// (Note: mPost remains null if it is neither in the saved state nor the arguments)
		mPost = savedstate.getParcelable(Gegevens.EXTRA_POST); 							
		if(mPost == null){ mPost = arguments.getParcelable(Gegevens.EXTRA_POST); }
		
		// load the reply being replied to. 
		// (Note: mReply remains null if it is neither in the saved state nor the arguments)
		mReply = savedstate.getParcelable(Gegevens.EXTRA_REPLY); 							
		if(mReply == null){ mReply = arguments.getParcelable(Gegevens.EXTRA_REPLY); }

		// load the type of post being posted 
		// (Note: mPostType remains null if it is neither in the saved state nor the arguments)
		mPostType = (PostType) savedstate.getSerializable(Gegevens.EXTRA_POSTTYPE); 							
		if(mPostType == null){ mPostType = (PostType) arguments.getSerializable(Gegevens.EXTRA_POSTTYPE); }
		
	}

	@Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Loggen.v(this, getTag() + " - Creating the PostCreate view. ");

		// Inflate the root view and save references to useful views as class variables
		mRootView = inflater.inflate(R.layout.frag_forum_post_create, container, false);
		mEditContent = (EditText) mRootView.findViewById(R.id.forum_post_text);

		// Inflate a view that shows either the post type or post / reply content.
		View header = null;
		String titleTxt = "";
		String btnTxt = "";
		if(mPost != null || mReply != null){
			header = inflater.inflate(R.layout.list_item_reply, container, false);
			titleTxt = getString(R.string.forum_create_reply_title);
			btnTxt = getString(R.string.forum_reply_btn_save);
		} else if(mPostType != null) {
			header = inflater.inflate(R.layout.include_forum_post_create, container, false);
			titleTxt = getString(R.string.forum_create_post_title);
			btnTxt = getString(R.string.forum_post_btn_save);

			// set the post specific texts 
			String [] tabTitles = getResources().getStringArray(R.array.forum_tabs);
			mEditTitle = (EditText) header.findViewById(R.id.forum_post_title);
			((TextView) header.findViewById(R.id.forum_post_type)).setText(tabTitles[mPostType.getIndex()]);
		}
		
		// add the header view and texts
		if(header != null){ ((LinearLayout) mRootView.findViewById(R.id.post_create_holder)).addView(header, 1); }
		((TextView) mRootView.findViewById(R.id.forum_create_post_title)).setText(titleTxt);
		((Button) mRootView.findViewById(R.id.forum_btn_save)).setText(btnTxt);
		
		// set the on click listener for the log out button
		((Button) mRootView.findViewById(R.id.forum_btn_save)).setOnClickListener(this);

		// Just show the service information
		showInformation(true);

		return mRootView;
	}

	@Override public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Loggen.v(this, getTag() + " - Saving PostCreate instance state.");
		// save the service to the instance state
		if(mPost != null) { outState.putParcelable(Gegevens.EXTRA_SERVICE, mPost); }
		if(mReply != null) { outState.putParcelable(Gegevens.EXTRA_SERVICE, mReply); }
		if(mPostType != null) { outState.putSerializable(Gegevens.EXTRA_SERVICE, mPostType); }
	}

	/** Loads the newUser object's variables to the screen. */
	private void showInformation(boolean showinfo) {
		if(mRootView != null){
			// show or hide the information
			((ScrollView) mRootView.findViewById(R.id.post_container))
				.setVisibility((showinfo) ? View.VISIBLE : View.GONE);
			((ProgressBar) mRootView.findViewById(R.id.forum_progress_bar))
				.setVisibility((showinfo) ? View.GONE : View.VISIBLE);
		}
	}
	
	/** Shows a dialog that asks the user if he wants to save the text he has entered up to now
	 *  If the user added no content, the method does nothing and returns true.
	 *  @return true if the user entered any text.
	 */
	private boolean verifySavePost() {
		// Check if anything has changed
		boolean allow = ((mEditTitle == null || mEditTitle.getText().toString().equals(""))
				&& (mEditContent == null || mEditContent.getText().toString().equals("")));

		// If the user has changed anything, ask for confirmation
		if(!allow){ 
			userMustConfirm(getString(R.string.forum_create_confirm_title),
							getString(R.string.forum_create_confirm_text));
		}
		
		return allow;
	}
	
	/** Saves the post to the web server. The request is only sent if: <br />
	 *  - The user has changed anything in the service <br />
	 *  The reply to this request is handled in the various callback methods of the forum DAO
	 */
	private void savePost(){
		Loggen.v(this, "User wants to save a post");
	}
	
	@Override public void onClick(View v) {
		int id = v.getId();
		switch(id){
			case R.id.forum_btn_save:
				savePost();
			break;
			default:
				super.onClick(v);
		}
	}
	
	@Override public boolean onNavigateUp(){ return verifySavePost(); }
	
	@Override public boolean allowBackPressed(){ return verifySavePost(); }

	/**	Default callback from all dialog fragments.  
	 * There is only one possible dialog worth considering: <br />
	 *  - Fragment Confirm: Only one possibility, namely the user wants to save his changes
	 */
	@Override public void onBasicPositiveButtonClicked(String tag, Object o) {
		if (Gegevens.FRAG_CONFIRM.equals(tag)){
			savePost();
		}
	}
	
	/**	Default callback from all dialog fragments. 
	 *  There is only one possible dialog worth considering: <br />
	 *  - Fragment Confirm: Only one possibility, namely the user wants to go back
	 */
	@Override public void onBasicNegativeButtonClicked(String tag, Object o) { 
		if(Gegevens.FRAG_CONFIRM.equals(tag) && mListener != null){
			mListener.performBackPress();
		}
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
	public void onReadPost(Post post) {
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