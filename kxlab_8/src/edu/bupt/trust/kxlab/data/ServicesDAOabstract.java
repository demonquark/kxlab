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
	
	
	protected abstract void createService(String path);
	protected abstract void deleteService(String path);
	protected abstract void editService(String path);			// method for  "/service/editservice"
	protected abstract void readServices(String email, ServiceFlavor flavor, ServiceType type, int size, Page page);			// method for "/service/serviceList"
	protected abstract void searchService(String path);			// method for "/service/searchMyServiceList"
	protected abstract void readService(String path); 			// method for  "/service/serviceDetail"
	protected abstract void updateServiceScore(String path); 	// method for "/service/importServiceScore"
	protected abstract void createServiceComment(String path);	// method for "/service/importServiceCommend"
	
	
	interface OnServicesRawDataReceivedListener {
		void onCreateService(RawResponse response);
		void onDeleteService(RawResponse response);
		void onEditService(RawResponse response);
		void onReadServices(RawResponse response);
		void onSearchServices(RawResponse response);
		void onReadService(RawResponse response);		
		void writeServiceScore(RawResponse response);
		void writeServiceComment(RawResponse response);
	}

}
