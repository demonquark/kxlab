package edu.bupt.trust.kxlab.data;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;

/**
 * <p> The ServicesDAOAbstract class shows all the public methods that we expect a Services data access object to provide.
 * All Services data access objects must extend this class, 
 * to ensure that we get the same functionality regardless of the source. </p>
 * <p> The list of methods is loosely based on the web service interfaces provided by the trust community server. </p>
 * 
 * @author Krishna
 *
 */
abstract class ServicesDAOabstract extends DAOabstract  {

	// Class variables
	protected OnServicesRawDataReceivedListener listener; 
	
	// Methods to be implemented by the children
	
	
	protected abstract void createService(String email, int id, String title, String detail);
	protected abstract void deleteService(int serviceId);
	protected abstract void editService(int id, String title, String detail, String photo);			// method for  "/service/editservice"
	protected abstract void readServices(String email, String searchterm, ServiceFlavor flavor, ServiceType type, int size, Page page);			// method for "/service/serviceList"
	protected abstract void readService(int id, int size, Page page); 			// method for  "/service/serviceDetail"
	protected abstract void updateServiceScore(int serviceId, String userMail, int score); 	// method for "/service/importServiceScore"
	protected abstract void createServiceComment(int serviceId, String userMail, int rootcommentid, String comment);	// method for "/service/importServiceCommend"
	
	
	interface OnServicesRawDataReceivedListener {
		void onCreateService(RawResponse response);
		void onDeleteService(RawResponse response);
		void onEditService(RawResponse response);
		void onReadServices(RawResponse response);
		void onReadService(RawResponse response);		
		void onUpdateServiceScore(RawResponse response);
		void onCreateComment(RawResponse response);
	}

}
