package edu.bupt.trust.kxlab.adapters;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.BitmapTools;
import edu.bupt.trust.kxlab.utils.Loggen;

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
 * UserArrayAdapter(Context context, int resource, List<User> objects) </p>
 * 
 * @author Krishna
 *
 */
public class UserArrayAdapter extends ArrayAdapter <User>{

	private List <User> items;
	
	public UserArrayAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
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
				User user = items.get(position);
				
				// Set the text
				((TextView) v.findViewById(android.R.id.text1)).setText(user.getTimeEnterString());
				((TextView) v.findViewById(android.R.id.text2)).setText(user.getActivityScore());
				((TextView) v.findViewById(android.R.id.title)).setText(user.getUserName());
				((TextView) v.findViewById(android.R.id.content)).setText(user.getEmail());

				//Set the image
				File imgFile = new File(user.getPhotoLocation() != null ? user.getPhotoLocation() : "");
				ImageView thumb = (ImageView) v.findViewById(android.R.id.icon1);
				if(imgFile.exists()){
				    thumb.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
				    		imgFile.getAbsolutePath(),
				    		thumb.getLayoutParams().width, thumb.getLayoutParams().height));
				}

//				Loggen.d(this, "text: " + text1 + " | " + text2 + " | file: " + imgFile.getAbsolutePath());
				
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". "); 
	        }
		}
		return v;
	}
	

}
