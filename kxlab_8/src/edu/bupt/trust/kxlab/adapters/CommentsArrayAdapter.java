package edu.bupt.trust.kxlab.adapters;

import java.util.List;

import edu.bupt.trust.kxlab.model.JsonComment;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * <p>This class is just to help me out.
 * The class has one major drawback: 
 * It only works if you use 
 * CommentsArrayAdapter(Context context, int resource, List<Comment> objects) </p>
 * 
 * @author Krishna
 *
 */
public class CommentsArrayAdapter extends ArrayAdapter <JsonComment>{

	private List <JsonComment> items;
	private int replyPadding;
	private OnClickListener mListener;
	
	public CommentsArrayAdapter(Context context, int resource, int textViewResourceId, List<JsonComment> objects) {
		super(context, resource, textViewResourceId, objects);
		this.items = objects;
		replyPadding = (int) (context.getResources().getDimension(android.R.dimen.app_icon_size) * 3 / 4);
	}

	@Override public View getView(int position, View convertView, ViewGroup parent) {
		return addItemContent(position, super.getView(position, convertView, parent));
    }
	
	@Override public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return addItemContent(position, super.getView(position, convertView, parent));
	}
	
	private View addItemContent(int position, View v){
		if(items != null){
			try {
				// Get the comment
				JsonComment comment = items.get(position);

				// Set the text TODO: Figure out where to get the owner score from
				((TextView) v.findViewById(android.R.id.content)).setText(String.valueOf(comment.commentdetail));
				((TextView) v.findViewById(android.R.id.text1)).setText(String.valueOf(comment.reuseremail));
				((TextView) v.findViewById(android.R.id.text2)).setText(comment.getCommenttimeString());


				// hide the reply button and add some padding to re-replies
				View button = v.findViewById(android.R.id.button1); 
				if(comment.rootcommentid != 0 && button != null){
					((View) button.getParent()).setPadding(replyPadding,0,0,0);
					button.setVisibility(View.GONE);
				} else if(button != null) {
					((View) button.getParent()).setPadding(0,0,0,0);
					button.setVisibility(View.VISIBLE);
					button.setTag(comment.commentid);
					button.setOnClickListener(mListener);
				}
				
//				Loggen.d(this, "text: " + comment.getUseremail() + " | " + comment.getCommentdetail() 
//						+ " | file: " + imgFile.getAbsolutePath());
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". " + e.getMessage()); 
	        }
		}
		return v;
	}
	
	public void setList(List <JsonComment> items){
		this.items = items;
	}
}
