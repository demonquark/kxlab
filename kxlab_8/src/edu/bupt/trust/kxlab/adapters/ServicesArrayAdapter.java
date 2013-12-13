package edu.bupt.trust.kxlab.adapters;

import java.io.File;
import java.util.List;

import edu.bupt.trust.kxlab.model.TrustService;
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
 * ServicesArrayAdapter(Context context, int resource, List<TrustService> objects) </p>
 * 
 * @author Krishna
 *
 */
public class ServicesArrayAdapter extends ArrayAdapter <TrustService>{

	private List <TrustService> items;
	
	public ServicesArrayAdapter(Context context, int resource, int textViewResourceId, List<TrustService> objects) {
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
		View firstText = v.findViewById(android.R.id.text1);
		View secondText = v.findViewById(android.R.id.text2);
		if(secondText != null && items != null){
			try {
				// Get the TrustService
				TrustService service = items.get(position);
				String text1 = service.getServicetitle();
				String text2 = service.getServicedetail();
				File imgFile = new File(service.getServicephoto() != null ? service.getServicephoto() : "");
				Loggen.d(this, "text: " + text1 + " | " + text2 + " | file: " + imgFile.getAbsolutePath());
				
				// Set the text
				((TextView) firstText).setText(text1);
				((TextView) secondText).setText(text2);

				//Set the image
				ImageView thumb = (ImageView) v.findViewById(android.R.id.icon1);
				if(imgFile.exists()){
				    thumb.setImageBitmap(BitmapTools.decodeSampledBitmapFromResource(
				    		imgFile.getAbsolutePath(),
				    		thumb.getLayoutParams().width, thumb.getLayoutParams().height));
				}
				
	        } catch (Exception e) {
	        	Loggen.e(this, "Failed to add the text for "+position+". "); 
	        }
		}
		return v;
	}
	

}
