package edu.bupt.trust.kxlab.jsonmodel;

import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonPostList {

	private List<JsonPost> PostList;
	
	public JsonPostList(List<JsonPost> posts){
		this.PostList = posts;
	}
	
	public List<JsonPost> getPostList() {
		return PostList;
	}

	public void setPostList(List<JsonPost> postList) {
		PostList = postList;
	}

	public boolean updateWithNew(JsonPostList b, boolean pushToEnd){
		
		boolean overlap = false;
		Loggen.i(this, "update with new | pushToEnd=" + pushToEnd + " | old: " + PostList.size() + " | new: " +b.PostList.size());
		
		JsonTools.updatePosts(PostList, b.PostList, pushToEnd);
		
		return overlap;
	}
}
