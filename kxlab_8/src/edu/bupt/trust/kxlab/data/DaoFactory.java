package edu.bupt.trust.kxlab.data;

import java.util.HashMap;

import android.content.Context;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesDetailListener;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.data.ProfileDAO.LoginListener;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesDetailListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesListListener;

public class DaoFactory {
	
	public enum DAO { SERVICES, PEOPLE, COMMENT};
	public enum Source { DEFAULT, WEB, LOCAL, DUMMY };
	public enum Page {
		PREVIOUS, LATEST
	};
	
	private static DaoFactory mInstance = null;
 
	private ServicesDAO servicesDAO;
	private HashMap <MyServicesDAO.Type, MyServicesDAO> myServicesDAOMap;
	private ProfileDAO	profileDAO;
 
	private DaoFactory(){ 
		myServicesDAOMap = new HashMap <MyServicesDAO.Type, MyServicesDAO>();
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

	public ProfileDAO setProfileDAO(Context c, LoginListener listener){
		if(profileDAO == null) { 
			profileDAO = new ProfileDAO(null, listener); 
		} else {
			profileDAO.setLoginListener(listener);
		}
		return profileDAO;
	}
	
}