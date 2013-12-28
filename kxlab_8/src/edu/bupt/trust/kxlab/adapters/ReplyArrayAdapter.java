package edu.bupt.trust.kxlab.adapters;

import java.util.List;

import edu.bupt.trust.kxlab.model.Reply;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.view.View;
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
public class ReplyArrayAdapter extends ArrayAdapter <Reply>{

	private List <Reply> items;
	
	public ReplyArrayAdapter(Context context, int resource, int textViewResourceId, List<Reply> objects) {
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
				// Get the User
				Reply reply = items.get(position);
				
				// Set the text
				((TextView) v.findViewById(android.R.id.text1)).setText(reply.getrAuthorEmail());
				((TextView) v.findViewById(android.R.id.text2)).setText(reply.getrTimeString());
				((TextView) v.findViewById(android.R.id.content)).setText(reply.getrContent());

//				Loggen.d(this, "text: " + text1 + " | " + text2 );
				
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". "); 
	        }
		}
		return v;
	}
	

}
