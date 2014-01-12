package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonreReply extends JsonItem {
	public List <List<JsonReply>> reReplyDetail;
	
	public JsonreReply(){
		reReplyDetail = new ArrayList<List<JsonReply>> ();
	}
	
	public JsonreReply(JsonreReply reReply) {
		this();
		setFromJsonItem(reReply, false);
	}

	@Override public JsonItem getJsonItem() {
		JsonreReply newInstance = new JsonreReply();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isReReply = (aThat instanceof JsonreReply);
		
		if(isReReply){
			JsonreReply that = (JsonreReply) aThat;
			reReplyDetail 				= allowNull ? that.reReplyDetail : copyReReply(that.reReplyDetail);
		}
		return isReReply;
	}

	@Override public int getId() {
		return -1;
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
					Loggen.i(this, "handling: " + reply.rootReplyId + "|" + reply.replyId);
					boolean added = false;
					
					// go through the existing old replies
					for(List<JsonReply> oldReplies : reReplyDetail){
						// If there already is a list with the same root, add it to that list
						if(oldReplies.size() > 0 && reply.rootReplyId == oldReplies.get(0).rootReplyId ){
							Loggen.i(this, "found a match: " + reply.rootReplyId + "|" + reply.replyId);
							
							// try updating an existing reply
							for(JsonReply oldReply : oldReplies){
								if(oldReply.replyId == reply.replyId){
									oldReply.setFromJsonItem(reply, false);
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
	
	private List<List<JsonReply>> copyReReply(List<List<JsonReply>> toCopy){
		List<List<JsonReply>> newCopy = new ArrayList<List<JsonReply>> ();
		if(toCopy != null){
			for(List<JsonReply> item : toCopy){
				newCopy.add(copyReplies(item));
			}
		}
		return newCopy;
	}
	
	private List <JsonReply> copyReplies(List <JsonReply> toCopy){
		List<JsonReply> newCopy = new ArrayList<JsonReply> ();
		if(toCopy != null){
			for(JsonReply item : toCopy){
				newCopy.add(new JsonReply(item)); 
			}
		}
		return newCopy;
	}
	
}
