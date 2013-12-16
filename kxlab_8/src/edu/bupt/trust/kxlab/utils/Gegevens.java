package edu.bupt.trust.kxlab.utils;

/** File containing application constants  */
public class Gegevens {
	
	public static final boolean debug	= true;

	/** Application package names */
	public static final String APP_NAME 			= "edu.bupt.trust.kxlab8";
	
	/** Preferences */
	public static final String PREF_LANGUAGE 		= "language";
	public static final String PREF_ISLOGIN			= "islogin";
	public static final String PREF_ISREMEMBER	    ="isremember";
	public static final String PREF_USER	        ="defaultuser";
	public static final String PREF_USERFACE		="userface";
	public static final String PREF_DUMMY 			= "dummy";

	/** Intent and Bundle Extras */
	public static final String EXTRA_MSGID			= "msgId";
	public static final String EXTRA_MSG			= "message";
	public static final String EXTRA_DAO 			= "dao";
	public static final String EXTRA_HASNEGATIVE	= "hasnegative";
	public static final String EXTRA_STRINGS		= "strings";
	public static final String EXTRA_FOOTERID		= "footerid";
	public static final String EXTRA_SERVICES		= "trustservices";
	public static final String EXTRA_SERVICE		= "trustservice";
	public static final String EXTRA_SELECTEDTAB	= "selectedtab";
	public static final String EXTRA_SELECTEDITEM	= "selecteditem";
	public static final String EXTRA_TAG			= "tag";
	public static final String EXTRA_STATE			= "state";
	public static final String EXTRA_TYPE			= "type";
	public static final String EXTRA_RESULT			= "result";
	public static final String EXTRA_COMMENTS		= "comments";
	public static final String EXTRA_SERVICETYPE	= "servicetype";
	public static final String EXTRA_USER			= "user";
	public static final String EXTRA_USER2			= "user2";
	
	/** Folder and file names */
	public static final String FILE_EXT_MP3 		= ".mp3";
	public static final String FILE_EXT_RAW 		= ".raw";
	public static final String FILE_EXT_WAV 		= ".wav";
	public static final String FILE_EXT_TXT 		= ".txt";
	public static final String FILE_EXT_LOG 		= ".log";
	public static final String FILE_EXT_JET 		= ".jet";
	public static final String FILE_EXT_DAT 		= ".dat";
	public static final String FILE_SEPARATOR 		= "/"; 
	public static final String FILE_CACHE			= "cache";
	public static final String FILE_USERDIRPHONE	= "data" + FILE_SEPARATOR + APP_NAME; 
	public static final String FILE_USERDIRSD 		= "Android" + FILE_SEPARATOR + "data" + FILE_SEPARATOR + APP_NAME;
	public static final String FILE_DUMMYCHECK		= "dummycheck.dat";

	/** Fragment tags */
	public static final String FRAG_DIALOG 			= "dialog";
	public static final String FRAG_COMMENT 		= "dialogcomment";
	public static final String FRAG_SCORE 			= "dialogscore";
	public static final String FRAG_DELETE			= "dialogdelete";
	public static final String FRAG_CONFIRM			= "dialogconfirm";
	public static final String FRAG_PASSWORD		= "dialogpassword";
	public static final String FRAG_FOOTERLINK		= "dialogfooterexit";
	public static final String FRAG_BACKPRESSED		= "dialogbackpressed";
	public static final String FRAG_FINISH 			= "finish";
	public static final String FRAG_COMMUNITY		= "community";
	public static final String FRAG_RECOMMEND 		= "recommended";
	public static final String FRAG_APPLY	 		= "apply";
	public static final String FRAG_INFOVIEW 		= "infoview";
	public static final String FRAG_INFOEDIT 		= "infoedit";
	
	/** Frequently used URLs. Note: there is no trailing slash. */
	public static final String URL_ANDROID_MARKET 		= "http://market.android.com";

}