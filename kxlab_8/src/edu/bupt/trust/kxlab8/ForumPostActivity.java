package edu.bupt.trust.kxlab8;

import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.Reply;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

/**
 *  The activity expects the following items in its arguments: <br />
 *  - EXTRA_MSG: A bundle added to the intent. The bundle contains all the actual data. Acts as a savedInstanceState. <br />
 *  The bundle should contain one of the following:
 *  - EXTRA_POST: A post object. Used to show post details.<br />
 *  - EXTRA_POSTTYPE: The type identifies the post type. Used to create a new post thread <br />
 */
public class ForumPostActivity extends BaseDetailActivity {
	
	@Override protected void onCreate(Bundle savedInstanceState) {
		Loggen.v(this, "Creating forum post activity.");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_generic);

		Bundle b = (savedInstanceState != null) ? savedInstanceState : getIntent().getBundleExtra(Gegevens.EXTRA_MSG);
		b.setClassLoader(getClass().getClassLoader());

		// get the post. (note: can be null)
		Post post = (Post) b.getParcelable(Gegevens.EXTRA_POST);

		// get the post type. (note: cannot be null)
		PostType type = (post != null) ? post.getPostType() : (PostType) b.getSerializable(Gegevens.EXTRA_POSTTYPE);
		if(type == null){ type = PostType.FORUM; }

		// Create the detail fragment and add it to the activity using a fragment transaction.
		if (savedInstanceState == null) {

			if(post == null) { 
				// If the post is unknown, assume that this is a request to create new post
				onActionSelected(null, Gegevens.FRAG_POSTEDIT, type); 
			} else {
				switch(post.getPostType()){
					case FORUM:
					case SUGGESTION:
						// If this a forum or suggestion post 
						onActionSelected(null, Gegevens.FRAG_POSTLIST, post);
						break;
					case ANNOUNCE:
					case FAQ:
						onActionSelected(null, Gegevens.FRAG_ANNOUNCEVIEW, post);
						break;
				}
			}
		} else {
			// this instance state was restored. Call the onBackStackChanged method to 
			displayHomeAsUpEnabled();
		}
	}
	
	/** Checks if we should display a "back" arrow in the action bar. */
	@Override public void onBackStackChanged() {
		super.onBackStackChanged();
		if(getSupportFragmentManager().getBackStackEntryCount() == 0){ finish(); }
	}
	
	/** Callback from the fragment
	 * This implementation processes three use cases: <br />
	 * From: null <br />
	 * - Goal = FRAG_POSTEDIT: The user clicked create in the action bar and wants to create a new post <br />
	 * - Goal = FRAG_POSTLIST: The clicked a post and wants to see the replies <br />
	 * - Goal = FRAG_ANNOUNCEVIEW: The user clicked an announcement or FAQ and wants to see the details <br />
	 * From: FRAG_POSTLIST <br />
	 * - Goal = FRAG_POSTEDIT: The user was view a post wants to reply to it<br />
	 * @param from - The tag of the sending fragment. Note: should also be the name in the back stack
	 * @param to - The tag of the target fragment. Note: should also be the name in the back stack
	 * @param o - The method expects this to be either a post, a reply or a post type
	 */
	@Override public void onActionSelected(String from, String to, Object o) {
		if(Gegevens.FRAG_POSTEDIT.equals(to)){
			
			// create a bundle and add the provided post or post type to it 
			Bundle arguments = new Bundle();
			if(o instanceof Post) { arguments.putParcelable(Gegevens.EXTRA_POST, (Post) o); } 
			if(o instanceof Reply) { arguments.putParcelable(Gegevens.EXTRA_REPLY, (Reply) o); } 
			if(o instanceof PostType) { arguments.putSerializable(Gegevens.EXTRA_POSTTYPE, (PostType) o); } 
			
			if((Gegevens.FRAG_POSTLIST.equals(from) && (o instanceof Post || o instanceof Reply))
					|| o instanceof PostType) { 

				// launch the edit fragment
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ForumPostCreateFragment editFragment = new ForumPostCreateFragment();
				editFragment.setArguments(arguments);
				ft.replace(R.id.details, editFragment, Gegevens.FRAG_POSTEDIT);
				ft.addToBackStack(Gegevens.FRAG_POSTEDIT);
				ft.commit();
			} else {
				this.postToast(getString(R.string.details_eror_service_edit));
			} 
		} else if(o instanceof Post) {
			
			// create a bundle and add the provided post to it 
			Bundle arguments = new Bundle();
			arguments.putParcelable(Gegevens.EXTRA_POST, (Post) o); 
			
			if(Gegevens.FRAG_POSTLIST.equals(to)) { 
				// launch the post list fragment
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ForumPostListFragment editFragment = new ForumPostListFragment();
				editFragment.setArguments(arguments);
				ft.replace(R.id.details, editFragment, Gegevens.FRAG_POSTLIST);
				ft.addToBackStack(Gegevens.FRAG_POSTLIST);
				ft.commit();
			} else if (Gegevens.FRAG_ANNOUNCEVIEW.equals(to)){
				// launch the post list fragment
				FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				ForumPostListFragment editFragment = new ForumPostListFragment();
				editFragment.setArguments(arguments);
				ft.replace(R.id.details, editFragment, Gegevens.FRAG_ANNOUNCEVIEW);
				ft.addToBackStack(Gegevens.FRAG_ANNOUNCEVIEW);
				ft.commit();
			}
		}
	}
}
