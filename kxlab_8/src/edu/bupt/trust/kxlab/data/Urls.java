package edu.bupt.trust.kxlab.data;

final class Urls {

	protected static final String urlBASE = "http://10.108.21.97:8080/trustworthy-community/t"; 
	protected static final String pathMyServiceList = "servicemanagementbyphone/myServiceList";
	protected static final String pathMyServiceSearch = "servicemanagementbyphone/searchMyServiceList?servicesearchkey=1&useremail=3&servicetype=1&serviceListPage=0&serviceListSize=2";
	protected static final String pathServiceList = "?id=readall";				// path for "/service/serviceList"
	protected static final String pathServiceDetail = "?id=readdetail";			// path for "/service/serviceDetail"
	protected static final String pathServiceImportScore = "?id=score";			// path for "/service/importServiceScore"
	protected static final String pathServiceImportComment = "?id=comment";		// path for "/service/importServiceCommend"
	protected static final String paramServiceType = "servicetype";				// param for "/service/serviceList"
	protected static final String paramServiceListSize = "serviceListSize"; 	// param for "/service/serviceList"
	protected static final String paramServiceListPage = "serviceListPage"; 	// param for "/service/serviceList"
	protected static final String paramServiceSearchKey = "serviceSearchKey";	// param for "/service/serviceList"
	protected static final String paramUserEmail = "useremail";
	
	protected static final String build(String... parts){
		String finalURL = ( parts.length > 0 ) ? parts[0] : "";
		for(int i = 1; i < parts.length; i++){
			// check if either the part or 
			if(parts[i].length() != 0 && parts[i].charAt(0) != '/'  
					&& finalURL.charAt(finalURL.length() - 1 ) !=  '/' ){ finalURL += "/"; }
			finalURL += parts[i];  
		}
		
		return finalURL;
	}
}
