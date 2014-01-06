package edu.bupt.trust.kxlab.jsonmodel;

import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonAnnounceList {

	JsonAnnounceOrgGuideList announceorguideList;
	List<JsonVote> vote;
	
	public JsonAnnounceOrgGuideList getAnnounceorguideList() {
		return announceorguideList;
	}

	public List<JsonVote> getVote() {
		return vote;
	}

	public void setAnnounceorguideList(JsonAnnounceOrgGuideList announceorguideList) {
		this.announceorguideList = announceorguideList;
	}

	public void setVote(List<JsonVote> vote) {
		this.vote = vote;
	}

	public boolean updateWithNew(JsonAnnounceList b, boolean pushToEnd){
		
		boolean overlap = false;
		Loggen.i(this, "start update with new: replies");
		overlap =  JsonTools.updateVotes(vote, b.vote, pushToEnd);
		
		Loggen.i(this, "start update with new: re replies");
		if(announceorguideList != null){
			overlap = overlap && announceorguideList.updateWithNew(b.announceorguideList, pushToEnd);	
		}
		
		return overlap;
	}
}
