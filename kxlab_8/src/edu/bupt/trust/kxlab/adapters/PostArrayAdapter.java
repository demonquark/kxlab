package edu.bupt.trust.kxlab.adapters;

import java.util.List;

import edu.bupt.trust.kxlab.model.Post;
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
 * PostArrayAdapter(Context context, int resource, List<Post> objects) </p>
 * 
 * @author Krishna
 *
 */
public class PostArrayAdapter extends ArrayAdapter <Post>{

	private List <Post> items;
	
	public PostArrayAdapter(Context context, int resource, int textViewResourceId, List<Post> objects) {
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
				Post post = items.get(position);
				
				// Set the text
				((TextView) v.findViewById(android.R.id.text1)).setText(String.valueOf(post.getPostTitle()));
				((TextView) v.findViewById(android.R.id.text2)).setText(String.valueOf(post.getPostDetail()));

//				Loggen.d(this, "text: " + text1 + " | " + text2 );
				
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". "); 
	        }
		}
		return v;
	}
	

}
