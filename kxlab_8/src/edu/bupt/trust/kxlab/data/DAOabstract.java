package edu.bupt.trust.kxlab.data;

import org.json.JSONException;
import org.json.JSONObject;

abstract class DAOabstract {

	/**
	 * <p>Converts the path of a URL address to a file name. <br /> 
	 * Note: For our purposes the path is the part of the URL after the base URL. <br /> 
	 * e.g. If URL = "http://www.bupt.edu.cn/specific/folder?id=12" then the path is "specific/folder?id=12" </p> 
	 * 
	 * <p> The method removes any initial and trailing slashes and then replaces all remaining slashes (/),
	 * colons (:) and question marks (?) with their ASCII HTML encoding value &#47; &#58; and &#63; respectively. <br />
	 * e.g. "specific/folder?id=12" becomes "specific&#47;folder&#63;id=12"
	 * </p>
	 * 
	 * @return a filename (without an extension) 
	 */
	protected static String pathToFileName(String path){
		
		// remove slashes at the front
		while(path.length() > 0 && path.charAt(0) == '/'){ path = path.substring(1); }
		
		// remove slashes at the end
		int len = path.length();
		while(len > 0 && path.charAt(len-1) == '/'){ path = path.substring(0,len-1); len = path.length(); }
		
		// replace the special characters
		path = path.replaceAll("/", "&#47;");
		path = path.replaceAll(":", "&#58;");
		path = path.replaceAll("\\?", "&#63;");
		
		return path;
	}
	
	/**
	 * <p>Converts the complete URL address to a file name. <br /> 
	 * Note: For our purposes the path is the part of the URL after the base URL. <br /> 
	 * e.g. If URL = "http://www.bupt.edu.cn/specific/folder?id=12" then the path is "specific/folder?id=12" </p> 
	 * 
	 * <p> The method removes the base URL (see Urls class) and any initial and trailing slashes. 
	 * Then it replaces all remaining slashes (/), colons (:) and question marks (?) 
	 * with their ASCII HTML encoding value &#47; &#58; and &#63; respectively. <br />
	 * e.g. "specific/folder?id=12" becomes "specific&#47;folder&#63;id=12"
	 * </p>
	 * 
	 * @return a filename (without an extension) 
	 */
	protected static String urlToFileName(String url){
		// remove any instances of http:// or https:// (all other protocols are ignored)
		if(url.startsWith("http://")){ url = url.substring(7); }
		if(url.startsWith("https://")){ url = url.substring(8); }
		
		// remove the http:// from the base URL
		String domain = Urls.urlBASE.substring(7);
		
		// now let's get the path
		String path = "";
		if(url.length() >= domain.length() && domain.equalsIgnoreCase(url.substring(0, domain.length()))){
			path = url.substring(domain.length());
		} 
		
		return pathToFileName(path);
	}

	/**
	 * <p>Converts a file name to the path of a URL address. <br /> 
	 * Note 1: For our purposes the path is the part of the URL after the base URL. <br /> 
	 * e.g. If URL = "http://www.bupt.edu.cn/specific/folder" then the path is "specific/folder". <br />
	 * Note 2: The path does NOT have starting slash or and trailing slash.  </p> 
	 * 
	 * <p> The replaces &#47; with slash (/), &#58; with colon (:) and &#63; with question marks (?) <br />
	 * e.g. "specific&#47;folder&#63;id=12" becomes "specific/folder?id=12" </p>
	 * 
	 * @param a filename WITHOUT the extension
	 * @return a usable path (without starting or trailing slash)
	 */
	protected static String fileNameToPath(String fileName){
				
		// replace the special characters
		fileName = fileName.replaceAll("&#47;", "/");
		fileName = fileName.replaceAll("&#58;", ":");
		fileName = fileName.replaceAll("&#63;", "?");
		
		return fileName;
	}
	
	
	/**
	 * <p>Checks to see if the supplied string is valid JSON.</p>
	 * 
	 * @param test the (JSON) string that you want to test
	 * @return whether or not this is a valid JSON string 
	 */
	protected static boolean isValidJSON(String test){
	    boolean valid = false;
	    try {
	    	new JSONObject(test);
	        valid = true;
	    } catch(JSONException ex) { }
	    
	    return valid;
	}

}
