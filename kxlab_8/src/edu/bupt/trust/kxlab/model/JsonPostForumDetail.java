package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonPostForumDetail extends JsonItem {

	public JsonPost PostDetail;
	public JsonUser PostSponsor;
	public List<JsonReply> PostReply;
	public JsonreReply reReply;
	
	public JsonPostForumDetail(){
		PostDetail = new JsonPost();
		PostSponsor = new JsonUser();
		PostReply = new ArrayList<JsonReply> ();
		reReply = new JsonreReply();
		
	}
	public JsonPostForumDetail(JsonPost post, JsonUser user, List<JsonReply> replies,JsonreReply reReply){
		this.PostDetail = new JsonPost(post);
		this.PostSponsor = new JsonUser(user);
		this.PostReply = copyReplies(replies);
		this.reReply = new JsonreReply(reReply);
	}
	
	@Override public JsonItem getJsonItem() {
		JsonPostForumDetail newInstance = new JsonPostForumDetail();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isForumDetail = (aThat instanceof JsonPostForumDetail);
		
		if(isForumDetail){
			JsonPostForumDetail that = (JsonPostForumDetail) aThat;
			PostDetail = allowNull ? that.PostDetail : new JsonPost(that.PostDetail);
			PostSponsor = allowNull ? that.PostSponsor : new JsonUser(that.PostSponsor);
			reReply = allowNull ? that.reReply : new JsonreReply(that.reReply);
			if(allowNull){
				PostReply = that.PostReply;	
			} else {
				PostReply = new ArrayList<JsonReply>();
				if(that.PostReply != null){
					for(JsonReply reply : that.PostReply){
						PostReply.add(new JsonReply(reply));
					}
				}
			}
		}
		return isForumDetail;
	}

	@Override public int getId() {
		return PostDetail.getId();
	}

	public void updateWithNew(JsonPostForumDetail b, boolean pushToEnd){
		if(b != null){
			// Step 0 - make sure we have records
			if(PostReply == null) { PostReply = new ArrayList<JsonReply> (); }
			
			// Step 1 - copy the new records
			List<JsonReply> newRecords = copyReplies(b.PostReply);
					
			// Step 2 - update the old records with the new records
			JsonTools.replaceListOverlap(PostReply, newRecords);
			
			// Step 3 - add the new records to the old records
			if(pushToEnd) { 
				PostReply.addAll(newRecords);
			} else {
				PostReply.addAll(0, newRecords);
			}
			
			// Step 4 - update the re replies
			reReply.updateWithNew(b.reReply, pushToEnd);
			
			// Step 5 - update the rest 
			PostDetail = new JsonPost(b.PostDetail);
			PostSponsor = new JsonUser(b.PostSponsor);

		} else {
			Loggen.e(this, "Cannot update with null");
		}
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
