package edu.bupt.trust.kxlab.model;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonAnnounceList extends JsonItem{

	public JsonAnnounceOrgGuideList announceorguideList;
	public List<JsonVote> vote;

	public JsonAnnounceList(){
		announceorguideList = new JsonAnnounceOrgGuideList();
		vote = new ArrayList<JsonVote> ();
	}

	public JsonAnnounceList(JsonAnnounceList list){
		this();
		this.setFromJsonItem(list, false);
	}

	public JsonAnnounceList(JsonAnnounceOrgGuideList list, List<JsonVote> polls){
		this();
		if(list != null) { announceorguideList = list; }
		if(polls != null) { vote = polls; }
	}

	@Override public JsonItem getJsonItem() {
		JsonAnnounceList newInstance = new JsonAnnounceList();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isAnnounce = (aThat instanceof JsonAnnounceList);
		
		if(isAnnounce){
			JsonAnnounceList that = (JsonAnnounceList) aThat;
			announceorguideList = allowNull ? that.announceorguideList : new JsonAnnounceOrgGuideList(that.announceorguideList);
			vote 				= allowNull ? that.vote : copyVotes(that.vote);
		}
		return isAnnounce;
	}

	@Override public int getId() {
		return -1;
	}

	public void updateWithNew(JsonAnnounceList b, boolean pushToEnd){
		if(b != null){
			// Step 0 - make sure we have records
			if(vote == null) { vote = new ArrayList<JsonVote> (); }
			
			// Step 1 - copy the new records
			List<JsonVote> newVotes = copyVotes(b.vote);
					
			// Step 2 - update the old records with the new records
			JsonTools.replaceListOverlap(vote, newVotes);
			
			// Step 3 - add the new records to the old records
			if(pushToEnd) { 
				vote.addAll(newVotes);
			} else {
				vote.addAll(0, newVotes);
			}
			
			// Step 4 - update the guide list
			announceorguideList.updateWithNew(b.announceorguideList, pushToEnd);
		} else {
			Loggen.e(this, "Cannot update with null");
		}
	}
	
	private List<JsonVote> copyVotes(List<JsonVote> votesToCopy){
		List<JsonVote> newVotes = new ArrayList<JsonVote> ();
		if(votesToCopy != null){
			for(JsonVote newvote : votesToCopy){
				newVotes.add(new JsonVote(newvote)); 
			}
		}
		return newVotes;
	}
}
