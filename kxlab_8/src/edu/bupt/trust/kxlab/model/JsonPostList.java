package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonPostList extends JsonItem {

	public List<JsonPost> PostList;
	
	public JsonPostList(){
		PostList = new ArrayList<JsonPost> ();
	}
	
	public JsonPostList(List<JsonPost> posts){
		PostList = copyPosts(posts);
	}

	@Override public JsonItem getJsonItem() {
		JsonPostList newInstance = new JsonPostList();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isPostList = (aThat instanceof JsonPostList);
		
		if(isPostList){
			JsonPostList that = (JsonPostList) aThat;
			if(allowNull){
				PostList = that.PostList;	
			} else {
				PostList = new ArrayList<JsonPost>();
				if(that.PostList != null){
					for(JsonPost reply : that.PostList){
						PostList.add(new JsonPost(reply));
					}
				}
			}
		}
		return isPostList;
	}

	@Override public int getId() {
		return -1;
	}

	public void updateWithNew(JsonPostList b, boolean pushToEnd){
		if(b != null){
			// Step 0 - make sure we have records
			if(PostList == null) { PostList = new ArrayList<JsonPost> (); }
			
			// Step 1 - copy the new records
			List<JsonPost> newRecords = copyPosts(b.PostList);
					
			// Step 2 - update the old records with the new records
			JsonTools.replaceListOverlap(PostList, newRecords);
			
			// Step 3 - add the new records to the old records
			if(pushToEnd) { 
				PostList.addAll(newRecords);
			} else {
				PostList.addAll(0, newRecords);
			}
		} else {
			Loggen.e(this, "Cannot update with null");
		}
	}
	
	private List <JsonPost> copyPosts(List <JsonPost> toCopy){
		List<JsonPost> newCopy = new ArrayList<JsonPost> ();
		if(toCopy != null){
			for(JsonPost item : toCopy){
				newCopy.add(new JsonPost(item)); 
			}
		}
		return newCopy;
	}


	
}
