package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonReplyComment extends JsonItem {
	
	public List <List<JsonComment>> ReplyCommentDetail;

	public JsonReplyComment(){
		ReplyCommentDetail = new ArrayList<List<JsonComment>> ();
	}
	
	public JsonReplyComment(JsonReplyComment reReply) {
		this();
		setFromJsonItem(reReply, false);
	}

	
	@Override public JsonItem getJsonItem() {
		JsonReplyComment newInstance = new JsonReplyComment();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override
	public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isReReply = (aThat instanceof JsonReplyComment);
		
		if(isReReply){
			JsonReplyComment that = (JsonReplyComment) aThat;
			ReplyCommentDetail = allowNull ? that.ReplyCommentDetail : copyCommentDetail(that.ReplyCommentDetail);
		}
		return isReReply;
	}

	@Override public int getId() {
		return -1;
	}
	
	public boolean updateWithNew(JsonReplyComment b, boolean pushToEnd){
		
		Loggen.i(this, "reply comment 0");
		boolean overlap = false;
		
		if(b != null && b.ReplyCommentDetail != null && b.ReplyCommentDetail.size() > 0){
			Loggen.i(this, "reply comment 1");
			
			// go through the new reply lists
			for(List<JsonComment> newReplies : b.ReplyCommentDetail){
				// handle each new reply
				for(JsonComment reply : newReplies){
					// make sure there is a reReplyDetail
					if(ReplyCommentDetail == null){ReplyCommentDetail = new ArrayList<List<JsonComment>> (); }
					Loggen.i(this, "2 handling: " + reply.rootcommentid + "|" + reply.commentid);
					boolean added = false;
					
					// go through the existing old replies
					for(List<JsonComment> oldReplies : ReplyCommentDetail){
						// If there already is a list with the same root, add it to that list
						if(oldReplies.size() > 0 && reply.rootcommentid == oldReplies.get(0).rootcommentid ){
							Loggen.i(this, "3 found a match: " + reply.rootcommentid + "|" + reply.commentid);
							
							// try updating an existing reply
							for(JsonComment oldReply : oldReplies){
								if(oldReply.commentid == reply.commentid){
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
						List<JsonComment> newlist = new ArrayList<JsonComment> ();
						newlist.add(reply);
						ReplyCommentDetail.add(newlist);
					}
				}
			}
			Loggen.i(this, "reply comment 4");
		}
		
		Loggen.i(this, "reply comment 5");
		return overlap;
	}
	
	

	private List<List<JsonComment>> copyCommentDetail(List<List<JsonComment>> toCopy){
		List<List<JsonComment>> newCopy = new ArrayList<List<JsonComment>> ();
		if(toCopy != null){
			for(List<JsonComment> item : toCopy){
				newCopy.add(copyComments(item));
			}
		}
		return newCopy;
	}
	
	private List <JsonComment> copyComments(List <JsonComment> toCopy){
		List<JsonComment> newCopy = new ArrayList<JsonComment> ();
		if(toCopy != null){
			for(JsonComment item : toCopy){
				newCopy.add(new JsonComment(item)); 
			}
		}
		return newCopy;
	}
}
