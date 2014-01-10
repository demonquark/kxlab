package edu.bupt.trust.kxlab.data;

import edu.bupt.trust.kxlab.data.RawResponse.Page;

/**
 * <p> The UserDAOabstract class shows all the public methods that we expect a User data access object to provide.
 * All User data access objects must extend this class, 
 * to ensure that we get the same functionality regardless of the source. </p>
 * <p> The list of methods is loosely based on the web service interfaces provided by the trust community server. </p>
 * 
 * @author Krishna
 *
 */
abstract class ProfileDAOabstract extends DAOabstract {

	// Class variables 
	protected OnProfileRawDataReceivedListener listener;
	
	// Methods to be implemented by the children
	protected abstract void login(String email, String password);					// method for "/profile/login"
	protected abstract void readUsers(String path); 			// method for "/service/filterPostList"
	protected abstract void readUserInformation(String email); 	// method for "/profile/userInformation"
	protected abstract void readActivityHistory(String email, int size, Page page);	// method for "/profile/activityHistory"
	protected abstract void changePhoto(String email, String photo);			// method for "/profile/changePhotoImage"
	protected abstract void changePassword(String email, String password, String newPassword);		// method for "/profile/changePassword"
	protected abstract void changePhonenumber(String email, String phone);		// method for "/profile/changePhoneNumber"
	protected abstract void changeSource(String email, int type);			// method for "/profile/changeSource"
	
	interface OnProfileRawDataReceivedListener {
		void onLogin(RawResponse response);
		void onReadUserList(RawResponse response);
		void onReadUserInformation(RawResponse response);
		void onReadActivityHistory(RawResponse response);
		void onChangePhoto(RawResponse response);
		void onChangePassword(RawResponse response);
		void onChangePhonenumber(RawResponse response);
		void onChangeSource(RawResponse response);
	}
}
