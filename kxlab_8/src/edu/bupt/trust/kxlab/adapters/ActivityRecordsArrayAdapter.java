package edu.bupt.trust.kxlab.adapters;

import java.util.List;

import edu.bupt.trust.kxlab.model.ActivityRecord;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * <p>This class is just to help me out.
 * The class has one major drawback: It only works if you use 
 * ActivityRecordsArrayAdapter(Context context, int resource, List<ActivityRecord> objects) </p>
 * 
 * @author Krishna
 *
 */
public class ActivityRecordsArrayAdapter extends ArrayAdapter <ActivityRecord>{

	private List <ActivityRecord> items;
	
	public ActivityRecordsArrayAdapter(Context context, int resource, int textViewResourceId, List<ActivityRecord> objects) {
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
		View titleText = v.findViewById(android.R.id.title);
		View firstText = v.findViewById(android.R.id.text1);
		View secondText = v.findViewById(android.R.id.text2);
		if(secondText != null && items != null){
			try {
				// Get the TrustService
				ActivityRecord record = items.get(position);
				String title = record.getDateString();
				String text1 = record.getWhatDo();
				String text2 = String.valueOf(record.getScore());
//				Loggen.d(this,  "title: " + title + " | " + "text: " + text1 + " | " + text2 );
				
				// Set the text
				((TextView) titleText).setText(title);
				((TextView) firstText).setText(text1);
				((TextView) secondText).setText(text2);
				
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". "); 
	        }
		}
		return v;
	}
	

}
