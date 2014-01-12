package edu.bupt.trust.kxlab.data;

import java.util.HashMap;

import android.content.Context;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesDetailListener;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesListener;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;

public class DaoFactory {
	
	public enum Source { DEFAULT, WEB, LOCAL, DUMMY };
	
	private static DaoFactory mInstance = null;
 
	private HashMap <MyServicesDAO.Type, MyServicesDAO> myServicesDAOMap;
	private HashMap <PostType, ForumDAO> forumDAOMap;
	private HashMap <ServiceFlavor, HashMap<ServiceType, ServicesDAO>> servicesDAOmap;
	private ProfileDAO	profileDAO;
 
	private DaoFactory(){ 
		myServicesDAOMap = new HashMap <MyServicesDAO.Type, MyServicesDAO>();
		forumDAOMap = new HashMap <PostType, ForumDAO>();
		servicesDAOmap = new HashMap <ServiceFlavor, HashMap<ServiceType, ServicesDAO>>();
		for(ServiceFlavor flavor : ServiceFlavor.values()){
			servicesDAOmap.put(flavor, new HashMap<ServiceType, ServicesDAO> ());
		}
		
	}
 
	public static DaoFactory getInstance(){
		if(mInstance == null) { mInstance = new DaoFactory(); }
		return mInstance;
	}
 
	public ServicesDAO setServicesDAO(Context c, ServicesListener listener, ServiceType type, ServiceFlavor flavor){
		ServicesDAO servicesDAO;
		if(servicesDAOmap.get(flavor).get(type) == null) {
			servicesDAO = new ServicesDAO(c, listener);
			servicesDAOmap.get(flavor).put(type, servicesDAO);
		} else {
			servicesDAO = servicesDAOmap.get(flavor).get(type);
			servicesDAO.setServicesListener(listener);
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