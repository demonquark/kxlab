package edu.bupt.trust.kxlab.jsonmodel;

import java.util.ArrayList;
import java.util.List;

import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class JsonreReply {
	List <List<JsonReply>> reReplyDetail;
	
	public boolean updateWithNew(JsonreReply b, boolean pushToEnd){
		
		Loggen.i(this, "re replies 0");
		boolean overlap = false;
		
		if(b != null && b.reReplyDetail != null && b.reReplyDetail.size() > 0){
			Loggen.i(this, "re replies 1 ");
			
			if(reReplyDetail != null && reReplyDetail.size() > 0){
				Loggen.i(this, "re replies 2 ");
				if(pushToEnd){
					overlap = JsonTools.updateRepliesEndOfList(reReplyDetail.get(0), b.reReplyDetail.get(0));
				} else {
					overlap =  JsonTools.updateRepliesFrontOfList(reReplyDetail.get(0), b.reReplyDetail.get(0));
				}
			} else {
				Loggen.i(this, "re replies 3 ");
				if(reReplyDetail == null){ reReplyDetail = new ArrayList<List<JsonReply>> ();}
				reReplyDetail.add(b.reReplyDetail.get(0));
			}
			Loggen.i(this, "re replies 4 ");
		}
		
		Loggen.i(this, "re replies 5");
		return overlap;
	}
}
