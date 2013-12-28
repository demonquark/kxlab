package edu.bupt.trust.kxlab.utils;

import java.util.List;

import edu.bupt.trust.kxlab.jsonmodel.JsonReply;

public class JsonTools {
	public static boolean updateRepliesFrontOfList(List <JsonReply> olditems, List <JsonReply> newitems){

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
	
	public static boolean updateRepliesEndOfList(List <JsonReply> olditems, List <JsonReply> newitems){

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
}
