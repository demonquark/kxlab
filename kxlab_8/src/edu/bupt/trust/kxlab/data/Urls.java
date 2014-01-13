package edu.bupt.trust.kxlab.data;

final class Urls {

	protected static final String urlBASE = "http://10.108.24.166:8080/trustworthy-community/t"; 
	protected static final String pathProfileLogin = "userManagementByPhone/login";				
	protected static final String pathProfileUserInfo = "userManagementByPhone/userInformation";
	protected static final String pathProfileHistory = "userManagementByPhone/activityHistory";	
	protected static final String pathProfileuserList = "userManagementByPhone/userlist";	
	protected static final String pathProfileChangePassword = "userManagementByPhone/changePassword";	
	protected static final String pathProfileChangePhoto = "userManagementByPhone/changePhotoImage";	
	protected static final String pathProfileChangePhone = "userManagementByPhone/changePhoneNumber";	
	protected static final String pathProfileChangeSource= "userManagementByPhone/changeSource";	
	protected static final String pathMyServiceCreate = "servicemanagementbyphone/createService";
	protected static final String pathMyServiceDelete = "servicemanagementbyphone/deleteMyService";
	protected static final String pathMyServiceEdit = "servicemanagementbyphone/editMyService";
	protected static final String pathMyServiceList = "servicemanagementbyphone/myServiceList";
	protected static final String pathMyServiceSearch = "servicemanagementbyphone/searchMyServiceList";
	protected static final String pathMyServiceDetail = "servicemanagementbyphone/serviceDetail";
	protected static final String pathServiceScore = "servicemanagementbyphone/importServiceScore";
	protected static final String pathServiceComment = "servicemanagementbyphone/importServiceComment";
	protected static final String pathServiceReplyComment = "servicemanagementbyphone/replyComment";
	protected static final String pathServiceList = "servicemanagementbyphone/serviceList";
	protected static final String pathServiceDetail = "servicemanagementbyphone/serviceDetail";
	protected static final String pathSearchServiceList = "servicemanagementbyphone/searchServiceList";
	protected static final String pathForumPostSearchList = "forum/postSearchList";
	protected static final String pathForumPostList = "forum/postList";
	protected static final String pathForumPostDetail = "forum/postForumDetail";
	protected static final String pathForumCreatePost = "forum/createPost";
	protected static final String pathForumReplyPost = "forum/replyPost";
	protected static final String pathForumReplyReply = "forum/replyReply";
	protected static final String pathForumPostAnnounceDetail = "forum/postAnnounceDetail";
	protected static final String pathForumAnnounceorguideList = "forum/announceorguideList";
	protected static final String pathForumVoteUser = "forum/voteUser";
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
	protected static final String paramCommentListPage = "serviceCommentListPage";
	protected static final String paramCommentListSize = "serviceCommentListSize";
	protected static final String paramCommentScore = "commentscore";
	protected static final String paramServiceComment = "servicecomment";
	protected static final String paramRecommentid = "recommentid";
	protected static final String paramCommentdetail = "commentdetail";
	protected static final String paramProfilePassword = "password";	
	protected static final String paramForumPostListSize = "postListSize";	
	protected static final String paramForumPostListPage = "postListPage";	
	protected static final String paramForumPostType = "postType";	
	protected static final String paramForumPostId = "postId";	
	protected static final String paramForumPostReplyListPage = "postReplyListPage";	
	protected static final String paramForumPostReplyListSize = "postReplyListSize";	
	protected static final String paramForumEmail = "email";	
	protected static final String paramForumPostDetail = "postDetail";	
	protected static final String paramForumPostTitle = "postTitle";	
	protected static final String paramForumReReplyId = "ReReplyId";	
	protected static final String paramForumUserEmail = "useremail";	
	protected static final String paramForumReplyDetail = "ReplyDetail";
	protected static final String paramForumAgType = "agType";
	protected static final String paramForumVoteId = "voteId";
	protected static final String paramForumVoteScore = "voteScore";
	protected static final String paramForumPostSearchKey = "postSearchKey";
	protected static final String paramProfileListPage = "listPage";
	protected static final String paramProfileListSize = "listSize";
	protected static final String paramProfilePhotoImage = "photoImage";
	protected static final String paramProfileSource = "source";
	protected static final String paramProfilePhoneNumber = "phoneNumber";
	protected static final String paramProfileNewPassword = "newPassword";
	protected static final String paramProfileUserListSortKey = "userListSortKey";
	protected static final String paramProfileUserListSize = "userListSize";
	protected static final String paramProfileUserListPage = "userListPage";
	
	protected static final String jsonLoginOrNot = "loginOrNot";				
	protected static final String jsonLoginErrorMessage = "loginerrormessage";	
	protected static final String jsonServiceDetail = "ServiceDetail";			
	protected static final String jsonCommentDetail = "CommentDetail";			
	protected static final String jsonServiceUserNumber = "ServiceUserNumber";	
	protected static final String jsonReplyCommentDetail = "ReplyCommentDetail";
	protected static final String jsonPostList = "PostList";
	protected static final String jsonCreatePostOrNot = "createPostOrNot";
	protected static final String jsonPostAnnounceDetail = "PostAnnounceDetail";
	protected static final String jsonUserInformation = "UserInformation";
	protected static final String jsonSourceChangeOrNot = "sourceChangeOrNot";
	protected static final String jsonPhoneChangeOrNot = "phoneChangeOrNot";
	protected static final String jsonPhotoChangeOrNot = "photoChangeOrNot";
	protected static final String jsonPasswordChangeOrNot = "passwordChangeOrNot";
	protected static final String jsonEditServiceOrNot = "editServiceOrNot";
	protected static final String jsonServiceScoreOrNot = "serviceScoreOrNot";
	protected static final String jsonForumCreateVoteOrNot = "createVoteOrNot";
	protected static final String jsonServiceCommentOrNot = "serviceCommentOrNot";
	protected static final String jsonServiceReplyCommentOrNot = "ReplyCommentOrNot";
	protected static final String jsonDeleteServiceOrNot = "deleteServiceOrNot";
	protected static final String jsonCreateServiceOrNot = "createServiceOrNot";
	
	protected static final String filePost = "post";
	protected static final String fileAnnounce = "post";
	protected static final String fileWebpostforumdetail = "webpostforumdetail";
	protected static final String filePostList = "forum_list";
	protected static final String filePostDetail = "forum_detail";
	protected static final String fileUserInformation = "profile_info_";
	protected static final String fileOldUser = "profile_old_user_";
	protected static final String fileNewUser = "profile_new_user_";
	protected static final String fileActivityHistory = "profile_history_";
	protected static final String fileUserList = "profile_userlist_";
	protected static final String fileServiceList = "services_list_";
	protected static final String fileService = "service_";
	protected static final String fileServiceDetail = "service_detail_";
	
	
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
