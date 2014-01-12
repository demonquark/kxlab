package edu.bupt.trust.kxlab.model;

public class JsonUserInformation extends JsonItem {

	public JsonUser UserInformation;
	
	public JsonUserInformation(){
		UserInformation = new JsonUser();
	}
	
	public JsonUserInformation(JsonUser userinfo){
		this();
		this.setFromJsonItem(userinfo, false);
	}

	@Override public JsonItem getJsonItem() {
		JsonUserInformation newInstance = new JsonUserInformation();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}

	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isUser = true;
		
		if(aThat instanceof JsonUserInformation){
			JsonUserInformation that 	= (JsonUserInformation) aThat;
			UserInformation		= allowNull ? that.UserInformation : new JsonUser(that.UserInformation);
		} else if (aThat instanceof JsonUser){
			JsonUser user = (JsonUser) aThat;
			UserInformation		= allowNull ? user : new JsonUser(user);
		} else {
			isUser = false;
		}

		return isUser;
	}

	@Override public int getId() {
		return UserInformation.getId();
	}
	
}
