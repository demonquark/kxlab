package edu.bupt.trust.kxlab.data;

/**
 * <p> The ServicesDAOAbstract class shows all the public methods that we expect a Services data access object to provide.
 * All Services data access objects must extend this class, 
 * to ensure that we get the same functionality regardless of the source. </p>
 * <p> The list of methods is loosely based on the web service interfaces provided by the trust community server. </p>
 * 
 * @author Krishna
 *
 */
abstract class ServicesDAOabstract extends DAOabstract {

	// Class variables
	protected OnServicesRawDataReceivedListener listener; 
	
	// Methods to be implemented by the children
	protected abstract void readServices(String path);			// method for "/service/serviceList"
	protected abstract void readService(String path); 			// method for  "/service/serviceDetail"
	protected abstract void updateServiceScore(String path); 	// method for "/service/importServiceScore"
	protected abstract void createServiceComment(String path);	// method for "/service/importServiceCommend"
	
	interface OnServicesRawDataReceivedListener {
		void onReadServices(RawResponse response);
		void onReadService(RawResponse response);
		void writeServiceScore(RawResponse response);
		void writeServiceComment(RawResponse response);
	}

}
