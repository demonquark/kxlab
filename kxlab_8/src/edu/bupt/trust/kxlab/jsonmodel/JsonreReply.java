package edu.bupt.trust.kxlab.jsonmodel;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonreReply {
	List <List<JsonReply>> reReplyDetail;
	
	public JsonreReply(){
		reReplyDetail = new ArrayList<List<JsonReply>> ();
	}
	
	public List<List<JsonReply>> getReReplyDetail() {
		return reReplyDetail;
	}

	public void setReReplyDetail(List<List<JsonReply>> reReplyDetail) {
		this.reReplyDetail = reReplyDetail;
	}

	public boolean updateWithNew(JsonreReply b, boolean pushToEnd){
		
		Loggen.i(this, "re replies 0");
		boolean overlap = false;
		
		if(b != null && b.reReplyDetail != null && b.reReplyDetail.size() > 0){
			Loggen.i(this, "re replies 1 ");
			
			// go through the new reply lists
			for(List<JsonReply> newReplies : b.reReplyDetail){
				// handle each new reply
				for(JsonReply reply : newReplies){
					// make sure there is a reReplyDetail
					if(reReplyDetail == null){reReplyDetail = new ArrayList<List<JsonReply>> (); }
					Loggen.i(this, "handling: " + reply.getRootReplyId() + "|" + reply.getReplyId());
					boolean added = false;
					
					// go through the existing old replies
					for(List<JsonReply> oldReplies : reReplyDetail){
						// If there already is a list with the same root, add it to that list
						if(oldReplies.size() > 0 && reply.getRootReplyId() == oldReplies.get(0).getRootReplyId() ){
							Loggen.i(this, "found a match: " + reply.getRootReplyId() + "|" + reply.getReplyId());
							
							// try updating an existing reply
							for(JsonReply oldReply : oldReplies){
								if(oldReply.getReplyId() == reply.getReplyId()){
									oldReply.updateFromJsonReply(reply);
									added = true;
								}
							}
							
							// if not, add it to the end
							if(!added){
								oldReplies.add(reply);
								added = true;
							}
						} 
					}
					
					// if there was no list with the same root, create a new list and add it.
					if(!added) {
						List<JsonReply> newlist = new ArrayList<JsonReply> ();
						newlist.add(reply);
						reReplyDetail.add(newlist);
					}
				}
			}
			Loggen.i(this, "re replies 4 ");
		}
		
		Loggen.i(this, "re replies 5");
		return overlap;
	}
}
