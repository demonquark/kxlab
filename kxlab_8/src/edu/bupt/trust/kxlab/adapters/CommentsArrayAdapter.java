package edu.bupt.trust.kxlab.adapters;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.model.Comment;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Loggen;
import edu.bupt.trust.kxlab8.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
public class CommentsArrayAdapter extends ArrayAdapter <Comment>{

	private List <Comment> items;
	
	public CommentsArrayAdapter(Context context, int resource, int textViewResourceId, List<Comment> objects) {
		super(context, resource, textViewResourceId, objects);
		this.items = objects;
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
				Comment comment = items.get(position);

				// Set the text TODO: Figure out where to get the owner score from
				((TextView) v.findViewById(android.R.id.text1)).setText(comment.getCommentdetail());
				((TextView) v.findViewById(R.id.comment_owner_name)).setText(comment.getUseremail());
				((TextView) v.findViewById(R.id.comment_owner_score_text)).setText("TODO");
				((TextView) v.findViewById(R.id.comment_owner_time)).setText(comment.getCommenttimeString());
				((TextView) v.findViewById(R.id.comment_score_text)).setText(comment.getCommentscore());

				// Set the image TODO: Figure out where to get the user image from
				File imgFile = new File(comment.getUseremail() != null ? comment.getUseremail() : "");
				ImageView thumb = (ImageView) v.findViewById(R.id.comment_owner_img);
				if(imgFile.exists()){
				    thumb.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
				    		imgFile.getAbsolutePath(),
				    		thumb.getLayoutParams().width, thumb.getLayoutParams().height));
				}
				
//				Loggen.d(this, "text: " + comment.getUseremail() + " | " + comment.getCommentdetail() 
//						+ " | file: " + imgFile.getAbsolutePath());
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". " + e.getMessage()); 
	        }
		}
		return v;
	}
	
	public void setList(List <Comment> items){
		this.items = items;
	}
}
