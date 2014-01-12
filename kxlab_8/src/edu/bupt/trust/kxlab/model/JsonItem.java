package edu.bupt.trust.kxlab.model;

public abstract class JsonItem {

	public abstract JsonItem getJsonItem();
	public abstract boolean setFromJsonItem(JsonItem aThat, boolean allowNull);
	public abstract int getId();
	public String replace(String old, String replacement){
		if(replacement != null && !replacement.equals("") && !replacement.equals("null")) { old = replacement; }
		return old;
	}
}
