package edu.bupt.trust.kxlab.model;

public class JsonSortFeatures extends JsonItem {
	
	String direction;
	String property;
	boolean ascending;

	public JsonSortFeatures() {
		direction	= "DESC";
		property	= "";
		ascending	= false;
	}

	public JsonSortFeatures(JsonSortFeatures item) {
		this();
		setFromJsonItem(item, false);
	}
	
	@Override public JsonItem getJsonItem() {
		JsonSortFeatures newInstance = new JsonSortFeatures();
		newInstance.setFromJsonItem(this, false);
		return newInstance;
	}
	
	@Override public boolean setFromJsonItem(JsonItem aThat, boolean allowNull) {
		boolean isSort = (aThat instanceof JsonSortFeatures);
		
		if(isSort){
			JsonSortFeatures that 	= (JsonSortFeatures) aThat;
			direction 	= allowNull ? that.direction : replace(direction, that.direction);
			property 	= allowNull ? that.property : replace(property, that.property);
			ascending 	= that.ascending;
		}
		
		return isSort;
	}
	
	@Override public int getId() {
		return -1;
	}
}
