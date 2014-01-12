package edu.bupt.trust.kxlab.model;

public class JsonPostAnnounceDetail extends JsonItem {
	
	public JsonAnnounceFAQ PostAnnounceDetail;
	public String PostType;
	
	public JsonPostAnnounceDetail(){
		PostAnnounceDetail = new JsonAnnounceFAQ();
		PostType = "";
	}
	
	public JsonPostAnnounceDetail(JsonAnnounceFAQ announcement){
		PostAnnounceDetail = announcement != null ? announcement : new JsonAnnounceFAQ();
		PostType = PostAnnounceDetail.agType;
	}
	
	
	@Override public JsonItem getJsonItem() {
		JsonPostAnnounceDetail newInstance = new JsonPostAnnounceDetail();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isAnnounce = (aThat instanceof JsonPostAnnounceDetail);
		
		if(isAnnounce){
			JsonPostAnnounceDetail that = (JsonPostAnnounceDetail) aThat;
			PostAnnounceDetail 	= (allowNull || PostAnnounceDetail != null) ? that.PostAnnounceDetail : PostAnnounceDetail;
			PostType = allowNull ? that.PostType : replace(PostType, that.PostType);
		}
		
		return isAnnounce;
	}

	@Override public int getId() {
		return (PostAnnounceDetail != null) ? PostAnnounceDetail.getId() : -1;
	}

}
