package edu.bupt.trust.kxlab.jsonmodel;

import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class WebPostForumDetail {

	private JsonPost PostDetail;
	private JsonUser PostSponsor;
	private List<JsonReply> PostReply;
	private JsonreReply reReply;
	
	public WebPostForumDetail(JsonPost post, JsonUser user, List<JsonReply> replies){
		this.PostDetail = post;
		this.PostSponsor = user;
		this.PostReply = replies;
	}
	
	public JsonPost getPostDetail() {
		return PostDetail;
	}
	public JsonUser getPostSponsor() {
		return PostSponsor;
	}
	public List<JsonReply> getPostReply() {
		return PostReply;
	}
	public JsonreReply getReReply() {
		return reReply;
	}
	public void setPostDetail(JsonPost postDetail) {
		PostDetail = postDetail;
	}
	public void setPostSponsor(JsonUser postSponsor) {
		PostSponsor = postSponsor;
	}
	public void setPostReply(List<JsonReply> postReply) {
		PostReply = postReply;
	}
	public void setReReply(JsonreReply reReply) {
		this.reReply = reReply;
	}
	
	public boolean updateWithNew(WebPostForumDetail b, boolean pushToEnd){
		
		PostDetail.updateFromJsonPost(b.PostDetail);
		PostSponsor.updateFromJsonUser(b.PostSponsor);

		boolean overlap = false;
		Loggen.i(this, "start update with new: replies");
		
		if(pushToEnd){
			overlap = JsonTools.updateRepliesEndOfList(PostReply, b.PostReply);
		} else {
			overlap =  JsonTools.updateRepliesFrontOfList(PostReply, b.PostReply);
		}
		
		Loggen.i(this, "start update with new: re replies");
		overlap = overlap && reReply.updateWithNew(b.reReply, pushToEnd);
		
		return overlap;
	}
}
