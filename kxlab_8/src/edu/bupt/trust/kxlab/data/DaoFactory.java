package edu.bupt.trust.kxlab.data;

import java.util.HashMap;

import android.content.Context;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesDetailListener;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesDetailListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesListListener;
import edu.bupt.trust.kxlab.model.PostType;

public class DaoFactory {
	
	public enum Source { DEFAULT, WEB, LOCAL, DUMMY };
	
	private static DaoFactory mInstance = null;
 
	private ServicesDAO servicesDAO;
	private HashMap <MyServicesDAO.Type, MyServicesDAO> myServicesDAOMap;
	private HashMap <PostType, ForumDAO> forumDAOMap;
	private ProfileDAO	profileDAO;
 
	private DaoFactory(){ 
		myServicesDAOMap = new HashMap <MyServicesDAO.Type, MyServicesDAO>();
		forumDAOMap = new HashMap <PostType, ForumDAO>();		
	}
 
	public static DaoFactory getInstance(){
		if(mInstance == null) { mInstance = new DaoFactory(); }
		return mInstance;
	}
 
	public ServicesDAO setServicesDAO(Context c, ServicesListListener listener){
		if(servicesDAO == null) { 
			servicesDAO = new ServicesDAO(null, listener); 
		} else {
			servicesDAO.setServicesListListener(listener);
			servicesDAO.setServicesDetailListener(null);
		}
		return servicesDAO;
	}

	public ServicesDAO setServicesDAO(Context c, ServicesDetailListener listener){
		if(servicesDAO == null) { 
			servicesDAO = new ServicesDAO(null, listener); 
		} else {
			servicesDAO.setServicesListListener(null);
			servicesDAO.setServicesDetailListener(listener);
		}
		return servicesDAO;
	}

	public MyServicesDAO setMyServicesDAO(Context c, MyServicesListListener listener, MyServicesDAO.Type type){
		MyServicesDAO myServicesDAO;
		if(myServicesDAOMap.get(type) == null) {
			myServicesDAO = new MyServicesDAO(null, listener);
			myServicesDAOMap.put(type,myServicesDAO);
		} else {
			myServicesDAO = myServicesDAOMap.get(type);
			myServicesDAO.setServicesListListener(listener);
			myServicesDAO.setServicesDetailListener(null);
		}
		return myServicesDAO;
	}
	
	public MyServicesDAO setMyServicesDAO(Context c, MyServicesDetailListener listener, MyServicesDAO.Type type){
		MyServicesDAO myServicesDAO;
		if(myServicesDAOMap.get(type) == null) {
			myServicesDAO = new MyServicesDAO(null, listener);
			myServicesDAOMap.put(type,myServicesDAO);
		} else {
			myServicesDAO = myServicesDAOMap.get(type);
			myServicesDAO.setServicesDetailListener(listener);
			myServicesDAO.setServicesListListener(null);
		}
		return myServicesDAO;
	}
	
	public ProfileDAO setProfileDAO(Context c, ProfileListener listener){
		if(profileDAO == null) { 
			profileDAO = new ProfileDAO(null, listener); 
		} else {
			profileDAO.setProfileListener(listener);
		}
		return profileDAO;
	}

	public ForumDAO setForumDAO(Context c, ForumDAO.ForumListener listener, PostType type){
		ForumDAO forumDAO;
		if(forumDAOMap.get(type) == null) {
			forumDAO = new ForumDAO(c, listener);
			forumDAOMap.put(type,forumDAO);
		} else {
			forumDAO = forumDAOMap.get(type);
			forumDAO.setForumListener(listener);
		}
		return forumDAO;
	}	
}