package edu.bupt.trust.kxlab.model;

public enum SortKey {

	NAME, JOINTIME, ACTIVITYSCORE;
	
	/** This returns the corresponding server type for this enum.<br />
	 * @return corresponding server type for the SortKey
	 */
	public String getServerType(){
		String serverType = "";
		switch(this) {
			case JOINTIME:
				serverType = "jointime";
				break;
			case ACTIVITYSCORE:
				serverType = "activityScore";
				break;
			case NAME:
				serverType = "name";
		}
		return serverType;
	}
	
	/** This returns the index of this item in the enum array.<br />
	 *  IMPORTANT: This method has a MAJOR flaw. It just returns the index of this item in the enum array. 
	 *  The value it returns depends on the order of the enum items in this class.
	 *  
	 * @return index of this in the enum array.
	 */
	public int getIndex(){
		SortKey [] allTypes = SortKey.values();
		int index = -1;
		for(int i = 0; i < allTypes.length; i++){
			if(allTypes[i] == this){ index = i; }
		}
		
		return index;
	}
	
	/** This returns the SortKey that corresponds to this index. <br />
	 *  IMPORTANT: The value it returns depends on the order of the enum items in this class.
	 *  
	 * @return SortKey that corresponds to the index. NAME if no corresponding enum
	 */
	public static SortKey fromIndex(int index){
		SortKey [] allTypes = SortKey.values();
		if(index < 0 || index >= allTypes.length){
			return NAME;
		}
		
		return allTypes[index];
	}

	/** This returns the corresponding enum object for this server type.<br />
	 * @return SortKey that corresponds to the server type. NAME if no corresponding enum
	 */
	public static SortKey fromServerType(String serverType){
		SortKey [] allTypes = SortKey.values();
		SortKey type = NAME;
		for(int i = 0; i < allTypes.length; i++){
			if(allTypes[i].getServerType().equals(serverType)){
				type = allTypes[i];
				break; 
			}
		}
		
		return type;
	}
}

