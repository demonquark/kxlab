package edu.bupt.trust.kxlab.utils;

import java.util.List;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import edu.bupt.trust.kxlab.jsonmodel.JsonAnnounceFAQ;
import edu.bupt.trust.kxlab.jsonmodel.JsonPost;
import edu.bupt.trust.kxlab.jsonmodel.JsonReply;
import edu.bupt.trust.kxlab.jsonmodel.JsonVote;

public class JsonTools {

	public static boolean updatePosts(List <JsonPost> olditems, List <JsonPost> newitems, boolean pushToEnd){

		int position = 0;
		boolean updated = false;
		
		// loop through all the new items
		for(int i = 0; i < newitems.size(); i++ ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new item loop through the old items
			for(int j = 0; j < olditems.size(); j++){
				if(newitems.get(i).getPdId() == olditems.get(j).getPdId()){
					// if the new item equals update the old item
					olditems.get(j).updateFromJsonPost(newitems.get(i));
					
					// we've started an overlap, so ignore all items before this one
					updated = true;
					
					// go to the next item in the new items
					break;
				}
			}
			
			if(!updated){
				if(pushToEnd){
					olditems.add(newitems.get(i));
				} else {
					olditems.add(position, newitems.get(i));
					position++;
				}
			}
		}
		
		return position != 0;
	}
	
	public static boolean updateVotes(List <JsonVote> olditems, List <JsonVote> newitems, boolean pushToEnd){

		int position = 0;
		boolean updated = false;
		
		// loop through all the new replies
		for(int i = 0; i < newitems.size(); i++ ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new reply loop through the old replies
			for(int j = 0; j < olditems.size(); j++){
				if(newitems.get(i).getVoteId() != null &&  newitems.get(i).getVoteId().equals(olditems.get(j).getVoteId())){
					// if the new reply equals update the old reply
					olditems.get(j).updateFromJsonVote(newitems.get(i));
					
					// we've started an overlap, so ignore all replies before this one
					updated = true;

					// go to the next item in the new replies
					break;
				}
			}
			
			if(!updated){
				if(pushToEnd){
					olditems.add(newitems.get(i));
				} else {
					olditems.add(position, newitems.get(i));
					position++;
				}
			}
		}
		
		return true;
	}
	
	public static boolean updateReplies(List <JsonReply> olditems, List <JsonReply> newitems, boolean pushToEnd){

		int position = 0;
		boolean updated = false;
		
		// loop through all the new replies
		for(int i = 0; i < newitems.size(); i++ ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new reply loop through the old replies
			for(int j = 0; j < olditems.size(); j++){
				if(newitems.get(i).getReplyId() == olditems.get(j).getReplyId()){
					// if the new reply equals update the old reply
					olditems.get(j).updateFromJsonReply(newitems.get(i));
					
					// we've started an overlap, so ignore all replies before this one
					updated = true;

					// go to the next item in the new replies
					break;
				}
			}
			
			if(!updated){
				if(pushToEnd){
					olditems.add(newitems.get(i));
				} else {
					olditems.add(position, newitems.get(i));
					position++;
				}
			}
		}
		
		return true;
	}


	public static boolean updateAnnounce(List<JsonAnnounceFAQ> olditems, List<JsonAnnounceFAQ> newitems, boolean pushToEnd) {
		int position = 0;
		boolean updated = false;
		
		// loop through all the new replies
		for(int i = 0; i < newitems.size(); i++ ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new reply loop through the old replies
			for(int j = 0; j < olditems.size(); j++){
				if(newitems.get(i).getAgId() != null && newitems.get(i).getAgId().equals(olditems.get(j).getAgId())){
					// if the new reply equals update the old reply
					olditems.get(j).updateFromJsonAnnounceFAQ(newitems.get(i));
					
					// we've started an overlap, so ignore all replies before this one
					updated = true;

					// go to the next item in the new replies
					break;
				}
			}
			
			if(!updated){
				if(pushToEnd){
					olditems.add(newitems.get(i));
				} else {
					olditems.add(position, newitems.get(i));
					position++;
				}
			}
		}
		
		return true;
	}
	
	public static boolean updateRepliesFrontOfListOLD(List <JsonReply> olditems, List <JsonReply> newitems){

		int position = 0;
		boolean updated = false;
		
		// loop through all the new replies
		for(int i = 0; i < newitems.size(); i++ ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new reply loop through the old replies
			for(int j = position; j < olditems.size(); j++){
				if(newitems.get(i).getReplyId() == olditems.get(j).getReplyId()){
					// if the new reply equals update the old reply
					olditems.get(j).updateFromJsonReply(newitems.get(i));
					
					// we've started an overlap, so ignore all replies before this one
					updated = true;
					position = j + 1;
					
					// go to the next item in the new replies
					break;
				}
			}
			
			if(!updated){
				if(position < olditems.size()){ 
					olditems.add(position, newitems.get(i)); 
				} else {
					olditems.add(newitems.get(i));
				}
			}
		}
		
		return position != 0;
	}
	
	public static boolean updateRepliesEndOfListOLD(List <JsonReply> olditems, List <JsonReply> newitems){

		int position = olditems.size() - 1;
		boolean updated = false;
		boolean overlap = false;
		
		// loop through all the new replies
		for(int i = newitems.size() - 1; i >= 0; i-- ){
			
			// we have not updated any old list items with this value
			updated = false;
			
			// for each new reply loop through the old replies
			for(int j = position; j >= 0; j--){
				if(newitems.get(i).getReplyId() == olditems.get(j).getReplyId()){
					// if the new reply equals update the old reply
					olditems.get(j).updateFromJsonReply(newitems.get(i));
					
					// we've started an overlap, so ignore all replies before this one
					overlap = true;
					updated = true;
					position = j - 1;
					
					// go to the next item in the new replies
					break;
				}
			}
			
			if(!updated){
				if(position < 0){ 
					olditems.add(0, newitems.get(i)); 
				} else if (position >= olditems.size() - 1) {
					olditems.add(newitems.get(i));
				} else {
					olditems.add(position, newitems.get(i));
				}
			}
		}
		
		return overlap;
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
