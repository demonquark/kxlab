package edu.bupt.trust.kxlab.data;

import android.content.Context;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesDetailListener;
import edu.bupt.trust.kxlab.data.MyServicesDAO.MyServicesListListener;
import edu.bupt.trust.kxlab.data.ProfileDAO.ProfileListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesDetailListener;
import edu.bupt.trust.kxlab.data.ServicesDAO.ServicesListListener;

public class DaoFactory {
	
	public enum DAO { SERVICES, PEOPLE, COMMENT};
	public enum Source { DEFAULT, WEB, LOCAL, DUMMY };
	
	private static DaoFactory mInstance = null;
 
	private ServicesDAO servicesDAO;
	private MyServicesDAO myServicesDAO;
	private ProfileDAO	profileDAO;
 
	private DaoFactory(){ }
 
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

	public MyServicesDAO setMyServicesDAO(Context c, MyServicesListListener listener){
		if(myServicesDAO == null) { 
			myServicesDAO = new MyServicesDAO(null, listener); 
		} else {
			myServicesDAO.setServicesListListener(listener);
			myServicesDAO.setServicesDetailListener(null);
		}
		return myServicesDAO;
	}

	public MyServicesDAO setMyServicesDAO(Context c, MyServicesDetailListener listener){
		if(myServicesDAO == null) { 
			myServicesDAO = new MyServicesDAO(null, listener); 
		} else {
			myServicesDAO.setServicesListListener(null);
			myServicesDAO.setServicesDetailListener(listener);
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

}