package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonServiceDetail extends JsonItem {

	public List<JsonComment> CommentDetail;
	public int ServiceUserNumber;
	public JsonTrustService ServiceDetail;
	public JsonReplyComment ReplyComment;
	
	public JsonServiceDetail(){
		CommentDetail = new ArrayList<JsonComment> ();
		ServiceUserNumber = 0;
		ServiceDetail = new JsonTrustService();
		ReplyComment = new JsonReplyComment();
		
	}
	public JsonServiceDetail(JsonTrustService service, int numberOfUsers,
							List<JsonComment> comments, JsonReplyComment reComments){
		this.CommentDetail = copyComments(comments);
		this.ServiceUserNumber = numberOfUsers;
		this.ServiceDetail = new JsonTrustService(service);
		this.ReplyComment = new JsonReplyComment(reComments);
	}
	
	
	@Override public JsonItem getJsonItem() {
		JsonServiceDetail newInstance = new JsonServiceDetail();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isJsonServiceDetail = (aThat instanceof JsonServiceDetail);
		
		if(isJsonServiceDetail){
			JsonServiceDetail that = (JsonServiceDetail) aThat;
			ServiceUserNumber = that.ServiceUserNumber;
			ServiceDetail = allowNull ? that.ServiceDetail : new JsonTrustService(that.ServiceDetail);
			ReplyComment = allowNull ? that.ReplyComment : new JsonReplyComment(that.ReplyComment);
			if(allowNull){
				CommentDetail = that.CommentDetail;	
			} else {
				CommentDetail = this.copyComments(that.CommentDetail);
			}
		}
		return isJsonServiceDetail;
	}

	@Override public int getId() {
		return ServiceDetail.getId();
	}
	
	public void updateWithNew(JsonServiceDetail b, boolean pushToEnd){
		if(b != null){
			// Step 0 - make sure we have records
			if(CommentDetail == null) { CommentDetail = new ArrayList<JsonComment> (); }
			
			// Step 1 - copy the new records
			List<JsonComment> newRecords = copyComments(b.CommentDetail);
					
			// Step 2 - update the old records with the new records
			JsonTools.replaceListOverlap(CommentDetail, newRecords);
			
			// Step 3 - add the new records to the old records
			if(pushToEnd) { 
				CommentDetail.addAll(newRecords);
			} else {
				CommentDetail.addAll(0, newRecords);
			}
			
			// Step 4 - update the re replies
			ReplyComment.updateWithNew(b.ReplyComment, pushToEnd);
			
			// Step 5 - update the rest 
			ServiceDetail = new JsonTrustService(b.ServiceDetail);
			ServiceUserNumber = b.ServiceUserNumber;

		} else {
			Loggen.e(this, "Cannot update with null");
		}
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
