package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonPostSearchList extends JsonItem {

	public List<JsonPost> PostSearchList;
	public String postType;
	
	public JsonPostSearchList(){
		PostSearchList = new ArrayList<JsonPost> ();
		postType = "";
	}
	
	public JsonPostSearchList(List<JsonPost> posts){
		PostSearchList = copyPosts(posts);
	}

	@Override public JsonItem getJsonItem() {
		JsonPostSearchList newInstance = new JsonPostSearchList();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isPostList = (aThat instanceof JsonPostSearchList);
		
		if(isPostList){
			JsonPostSearchList that = (JsonPostSearchList) aThat;
			if(allowNull){
				PostSearchList = that.PostSearchList;	
			} else {
				PostSearchList = new ArrayList<JsonPost>();
				if(that.PostSearchList != null){
					for(JsonPost reply : that.PostSearchList){
						PostSearchList.add(new JsonPost(reply));
					}
				}
			}
		}
		return isPostList;
	}

	@Override public int getId() {
		return -1;
	}

	public void updateWithNew(JsonPostSearchList b, boolean pushToEnd){
		if(b != null){
			// Step 0 - make sure we have records
			if(PostSearchList == null) { PostSearchList = new ArrayList<JsonPost> (); }
			
			// Step 1 - copy the new records
			List<JsonPost> newRecords = copyPosts(b.PostSearchList);
					
			// Step 2 - update the old records with the new records
			JsonTools.replaceListOverlap(PostSearchList, newRecords);
			
			// Step 3 - add the new records to the old records
			if(pushToEnd) { 
				PostSearchList.addAll(newRecords);
			} else {
				PostSearchList.addAll(0, newRecords);
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
