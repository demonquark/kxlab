package edu.bupt.trust.kxlab.jsonmodel;

import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonPostForumDetail {

	private JsonPost PostDetail;
	private JsonUser PostSponsor;
	private List<JsonReply> PostReply;
	private JsonreReply reReply;
	
	public JsonPostForumDetail(JsonPost post, JsonUser user, List<JsonReply> replies,JsonreReply reReply){
		this.PostDetail = post;
		this.PostSponsor = user;
		this.PostReply = replies;
		this.reReply = reReply;
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
	
	public boolean updateWithNew(JsonPostForumDetail b, boolean pushToEnd){
		
		PostDetail.updateFromJsonPost(b.PostDetail);
		PostSponsor.updateFromJsonUser(b.PostSponsor);

		boolean overlap = false;
		Loggen.i(this, "start update with new: replies");
		overlap =  JsonTools.updateReplies(PostReply, b.PostReply, pushToEnd);
		
		Loggen.i(this, "start update with new: re replies");
		overlap = overlap && reReply.updateWithNew(b.reReply, pushToEnd);
		
		return overlap;
	}
}
