package edu.bupt.trust.kxlab.model;

public enum ServiceFlavor {
	MYSERVICE, SERVICE;
	
	/** This returns the index of this item in the enum array.<br />
	 *  IMPORTANT: This method has a MAJOR flaw. It just returns the index of this item in the enum array. 
	 *  The value it returns depends on the order of the enum items in this class.
	 *  
	 * @return index of this in the enum array.
	 */
	public int getIndex(){
		ServiceFlavor [] allTypes = ServiceFlavor.values();
		int index = -1;
		for(int i = 0; i < allTypes.length; i++){
			if(allTypes[i] == this){ index = i; }
		}
		
		return index;
	}
	
	/** This returns the ServiceFlavor that corresponds to this index. <br />
	 *  IMPORTANT: The value it returns depends on the order of the enum items in this class.
	 *  
	 * @return ServiceType that corresponds to the index. SERVICE if no corresponding enum
	 */
	public static ServiceFlavor fromIndex(int index){
		ServiceFlavor [] allTypes = ServiceFlavor.values();
		if(index < 0 || index >= allTypes.length){
			return SERVICE;
		}
		
		return allTypes[index];
	}

}
