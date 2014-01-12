package edu.bupt.trust.kxlab.adapters;

import java.util.List;

import edu.bupt.trust.kxlab.model.JsonReply;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * <p>This class is just to help me out.
 * The class has one major drawback: 
 * It only works if you use 
 * ReplyArrayAdapter(Context context, int resource, List<Reply> objects) </p>
 * 
 * @author Krishna
 *
 */
public class ReplyArrayAdapter extends ArrayAdapter <JsonReply>{

	private List <JsonReply> items;
	private int replyPadding;
	private OnClickListener mListener;
	
	public ReplyArrayAdapter(Context context, int resource, int textViewResourceId, List<JsonReply> objects) {
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
				// Get the User
				JsonReply reply = items.get(position);
				
				// Set the text
				((TextView) v.findViewById(android.R.id.text1)).setText(String.valueOf(reply.rAuthorEmail));
				((TextView) v.findViewById(android.R.id.text2)).setText(reply.getrTimeString());
				((TextView) v.findViewById(android.R.id.content)).setText(String.valueOf(reply.rContent));

				// hide the reply button and add some padding to re-replies
				View button = v.findViewById(android.R.id.button1); 
				if(reply.rootReplyId != 0 && button != null){
					((View) button.getParent()).setPadding(replyPadding,0,0,0);
					button.setVisibility(View.GONE);
				} else if(button != null) {
					((View) button.getParent()).setPadding(0,0,0,0);
					button.setVisibility(View.VISIBLE);
					button.setTag(reply.replyId);
					button.setOnClickListener(mListener);
				}
				
//				Loggen.d(this, "text: " + text1 + " | " + text2 );
				
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". "); 
	        }
		}
		return v;
	}
	
	public void setOnBtnClickListener(OnClickListener listener){
		mListener = listener;
	}
	

}
