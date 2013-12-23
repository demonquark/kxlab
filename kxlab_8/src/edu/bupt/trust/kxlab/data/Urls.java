package edu.bupt.trust.kxlab.data;

final class Urls {

	protected static final String urlBASE = "http://10.108.21.97:8080/trustworthy-community/t"; 
	protected static final String pathProfileLogin = "userManagementByPhone/login";				
	protected static final String pathProfileUserInfo = "userManagementByPhone/userInformation";
	protected static final String pathProfileHistory = "userManagementByPhone/activityHistory";	
	protected static final String pathMyServiceCreate = "servicemanagementbyphone/createService";
	protected static final String pathMyServiceDelete = "servicemanagementbyphone/deleteMyService";
	protected static final String pathMyServiceEdit = "servicemanagementbyphone/editMyService";
	protected static final String pathMyServiceList = "servicemanagementbyphone/myServiceList";
	protected static final String pathMyServiceSearch = "servicemanagementbyphone/searchMyServiceList";
	protected static final String pathMyServiceDetail = "servicemanagementbyphone/serviceDetail";
	protected static final String pathServiceScore = "servicemanagementbyphone/importServiceScore";
	protected static final String pathServiceComment = "servicemanagementbyphone/importServiceComment";
	protected static final String pathServiceList = "servicemanagementbyphone/myServiceList";
	protected static final String pathServiceDetail = "servicemanagementbyphone/serviceDetail";
	protected static final String paramServiceType = "servicetype";				
	protected static final String paramServiceListSize = "serviceListSize"; 	
	protected static final String paramServiceListPage = "serviceListPage"; 	
	protected static final String paramServiceSearchKey = "servicesearchkey";	
	protected static final String paramServiceTitle = "servicetitle";
	protected static final String paramServiceDetial = "servicedetail";
	protected static final String paramServicePhoto = "servicephto";
	protected static final String paramUserEmail = "useremail";
	protected static final String paramEmail = "email";
	protected static final String paramServiceId = "serviceid";
	protected static final String paramCommentListPage = "servicecommentlistpage";
	protected static final String paramCommentListSize = "servicecommentlistsize";
	protected static final String paramCommentScore = "commentscore";
	protected static final String paramServiceComment = "servicecomment";
	protected static final String paramProfilePassword = "password";			
	protected static final String jsonLoginOrNot = "loginOrNot";				
	protected static final String jsonLoginErrorMessage = "loginerrormessage";	
	protected static final String jsonServiceDetail = "ServiceDetail";			
	protected static final String jsonCommentDetail = "CommentDetail";			
	protected static final String jsonServiceUserNumber = "ServiceUserNumber";	
	protected static final String jsonReplyCommentDetail = "ReplyCommentDetail";
	protected static final String jsonPostList = "PostList";
	
	
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
