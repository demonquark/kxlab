package edu.bupt.trust.kxlab.utils;

import java.util.List;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import edu.bupt.trust.kxlab.model.JsonAnnounceFAQ;
import edu.bupt.trust.kxlab.model.JsonItem;
import edu.bupt.trust.kxlab.model.JsonPost;
import edu.bupt.trust.kxlab.model.JsonReply;
import edu.bupt.trust.kxlab.model.JsonVote;

public class JsonTools {

	public static boolean replaceListOverlap(List <? extends JsonItem> olditems, List <? extends JsonItem> newitems){
		boolean updated = false;
		
		// loop through all the new items
		for(int i = 0; i < newitems.size(); i++ ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new item loop through the old items
			for(int j = 0; j < olditems.size(); j++){
				if(newitems.get(i).getId() == olditems.get(j).getId()){
					
					// update the old item
					updated = olditems.get(j).setFromJsonItem(newitems.get(i), false) || updated;
					
					// remove the updated item from the list of new items
					newitems.remove(i);
					i--;
					
					break;
				}
			}
		}
		
		return updated;
	}
	
	/**
	 * <p>Checks to see if the supplied string is valid JSON.</p>
	 * 
	 * @param test the (JSON) string that you want to test
	 * @return whether or not this is a valid JSON string 
	 */
	public static boolean isValidJSON(String test){
	    boolean valid = false;
	    try {
	    	new JsonParser().parse(test);
	        valid = true;
	    } catch(JsonSyntaxException ex) { }
	    
	    return valid;
	}
}
