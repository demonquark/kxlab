package edu.bupt.trust.kxlab.model;

import edu.bupt.trust.kxlab.utils.Gegevens;

public enum ServiceType {
	COMMUNITY, RECOMMENDED, APPLY;
	
	public String getFragName(){
		String fragName = null;
		switch(this) {
			case COMMUNITY:
				fragName = Gegevens.FRAG_COMMUNITY;
				break;
			case RECOMMENDED:
				fragName = Gegevens.FRAG_COMMUNITY;
				break;
			case APPLY:
				fragName = Gegevens.FRAG_COMMUNITY;
				break;
		}
		
		return fragName;
	}

	/** This returns the corresponding server type for this enum.<br />
	 *  TODO: Change to a switch statement. Right now it just returns the index.
	 *  
	 * @return corresponding server type for the TrustService object
	 */
	public int getServerType(){
		return getIndex();
	}
	
	/** This returns the index of this item in the enum array.<br />
	 *  IMPORTANT: This method has a MAJOR flaw. It just returns the index of this item in the enum array. 
	 *  The value it returns depends on the order of the enum items in this class.
	 *  
	 * @return index of this in the enum array.
	 */
	public int getIndex(){
		ServiceType [] allTypes = ServiceType.values();
		int index = -1;
		for(int i = 0; i < allTypes.length; i++){
			if(allTypes[i] == this){ index = i; }
		}
		
		return index;
	}
	
	/** This returns the ServiceType that corresponds to this index. <br />
	 *  IMPORTANT: The value it returns depends on the order of the enum items in this class.
	 *  
	 * @return ServiceType that corresponds to the index. COMMUNITY if no corresponding enum
	 */
	public static ServiceType fromIndex(int index){
		ServiceType [] allTypes = ServiceType.values();
		if(index < 0 || index >= allTypes.length){
			return COMMUNITY;
		}
		
		return allTypes[index];
	}

	/** This returns the corresponding enum object for this server type.<br />
	 *  TODO: Change to a switch statement. Right now it just uses from index.
	 *  
	 * @return ServiceType that corresponds to the server type. COMMUNITY if no corresponding enum
	 */
	public static ServiceType fromServerType(int serverType){
		return fromIndex(serverType);
	}
}
