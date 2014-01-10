package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.data.ServicesDAOabstract.OnServicesRawDataReceivedListener;
import edu.bupt.trust.kxlab.jsonmodel.JsonUser;
import edu.bupt.trust.kxlab.jsonmodel.JsonUserInformation;
import edu.bupt.trust.kxlab.model.Comment;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ServicesDAO implements OnServicesRawDataReceivedListener {
	private static final String LIST_SIZE = "6";
	private static int communityServicePageNo = 0;
	private static int recommendedServiceListPageNo = 0;
	private static int applyServiceListPageNo = 0;
	private static int commentPageNo = 0;
	private static int searchListPageNo = 0;
	

	private ServicesDAOlocal local;
	private ServicesDAOweb web;
	private ServicesDAOdummy dummy;
	private WeakReference <ServicesListListener> listlistener;
	private WeakReference <ServicesDetailListener> detaillistener;

	// Outward facing methods (used by the class requesting the data)
	public void setServicesListListener(ServicesListListener listener) {
		this.listlistener = new WeakReference<ServicesListListener> (listener);
	}

	public void setServicesDetailListener(ServicesDetailListener listener) {
		this.detaillistener = new WeakReference<ServicesDetailListener> (listener);
	}

	public void setCacheDir(Context c) {
		local.setCacheDir(c);
	}

	// Inward facing methods (used to communicate with the class providing the
	// data)
	protected ServicesDAO(Context c, ServicesListListener listener) {
		local = new ServicesDAOlocal(this, c);
		web = new ServicesDAOweb(this);
		dummy = new ServicesDAOdummy(this);
		this.listlistener = new WeakReference<ServicesListListener>(listener);
	}

	protected ServicesDAO(Context c, ServicesDetailListener listener) {
		local = new ServicesDAOlocal(this, c);
		web = new ServicesDAOweb(this);
		dummy = new ServicesDAOdummy(this);
		this.detaillistener = new WeakReference<ServicesDetailListener> (listener);
	}

	/**
	 * 
	 * @param type
	 * @param parameters
	 *            parameter[0] represents user mail,parameter[1] represents
	 *            service title,parameter[2] represents service detail
	 */
	public void createService(ServiceType type, String[] parameters) {
		int typeParam = 0;
		switch (type) {
		case COMMUNITY:
			typeParam = 1;
			break;
		case RECOMMENDED:
			typeParam = 2;
			break;
		case APPLY:
			typeParam = 3;
			break;
		}

		String path = Urls.pathMyServiceCreate;
		if (parameters != null && parameters.length > 2) {

			RequestParams params = new RequestParams();
			params.put(Urls.paramUserEmail, parameters[0]);
			params.put(Urls.paramServiceType, typeParam + ""); // list size
			params.put(Urls.paramServiceTitle, parameters[1]); // service type
			params.put(Urls.paramServiceDetial, parameters[2]); // list page

			path = ServicesDAOweb.getPath(true, path, params);
		}
		Loggen.i(this, "Got path: " + path);

		web.createService(path);

	}

	/**
	 * 
	 * @param type
	 * @param parameters
	 *            parameter[0] represents user mail,parameter[1] represents
	 *            service title,parameter[2] represents service detail
	 */
	public void createService(Source source, ServiceType type, String[] parameters) {
		dummy.createService(null);

	}
	public void deleteService(int serviceId) {
		String path = Urls.pathMyServiceDelete;
		RequestParams params = new RequestParams();
		params.put(Urls.paramServiceId, serviceId + "");
		path = ServicesDAOweb.getPath(true, path, params);

		web.deleteService(path);
	}

	/**
	 * 
	 * @param serviceId
	 * @param parameters
	 *            parameter[0] represents service title, parameter[1] represents
	 *            service detail, parameter[2] represents service photo,
	 */
	public void editService(int serviceId, String[] parameters) {
		// Get the path of the read services page
		String path = Urls.pathMyServiceEdit;
		if (parameters != null && parameters.length > 2) {

			RequestParams params = new RequestParams();
			params.put(Urls.paramServiceId, serviceId + "");
			params.put(Urls.paramServiceTitle, parameters[0]); // service type
			params.put(Urls.paramServiceDetial, parameters[1]); // list page
			params.put(Urls.paramServicePhoto, parameters[2]); // list size
			path = ServicesDAOweb.getPath(true, path, params);
		}
		Loggen.i(this, "Got path: " + path);

		web.editService(path);
	}

	/**
	 * 
	 * @param serviceId
	 * @param parameters
	 *            parameter[0] represents service title, parameter[1] represents
	 *            service detail, parameter[2] represents service photo,
	 */
	public void editService(Source source, int serviceId, String[] parameters) {
		dummy.editService(null);
	}

	/**
	 * Reads the list of services. Source options are: DEFAULT (try local then
	 * web), LOCAL (local), WEB (web), DUMMY (dummy data)
	 * 
	 * @param source
	 * @param parameters
	 *            parameters[0] matches user email
	 */
	public void readServices(Source source, ServiceType type, ServiceFlavor flavor, String email, 
								List <TrustService> services, Page page) {

		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && services != null ) ){ 
			Loggen.v(this, "saved content to file.");
			overwriteServicesList(type, flavor, services); 
		}
		
		// determine the records size 
		int size = (services != null) ? services.size() : 0;

		// let the correct source handle the request
		switch (source) {
		case DEFAULT:
		case WEB:
			web.readServices(email, flavor, type, size, page);
			break;
		case LOCAL:
			local.readServices(email, flavor, type, size, page);
			break;
		case DUMMY:
			dummy.readServices(email, flavor, type, size, page);
			break;
		}
	}
	
	
	

	private void overwriteServicesList(ServiceType type, ServiceFlavor flavor, List<TrustService> services) {
		if(services != null){
			// write to file
			String cachefilename = ServicesDAOlocal.getServicesListFilename(type.getFragName(), flavor.toString());
			local.writeToFile(cachefilename, new Gson().toJson(services));
			Loggen.v(this, "saved content to file.");

		}
	}

	/**
	 * 
	 * @param type
	 * @param source
	 * @param parameters
	 *            parameters[0] matches search key word, [1] matches user email,
	 */
	public void searchService(ServiceType type, Page p, String[] parameters) {
		switch (p) {
		case PREVIOUS:
			searchListPageNo++;
			break;
		case LATEST:
			searchListPageNo = (searchListPageNo > 0) ? searchListPageNo-- : 0;
			break;
		}

		int typeParam = 0;
		switch (type) {
		case COMMUNITY:
			typeParam = 1;
			break;
		case RECOMMENDED:
			typeParam = 2;
			break;
		case APPLY:
			typeParam = 3;
			break;
		}

		// Get the path of the read services page
		String path = Urls.pathMyServiceSearch;
		if (parameters != null && parameters.length > 0) {
			RequestParams params = new RequestParams();
			params.put(Urls.paramServiceSearchKey, parameters[0]);
			params.put(Urls.paramUserEmail, parameters[1]); // user email
			params.put(Urls.paramServiceType, typeParam + "");
			params.put(Urls.paramServiceListPage, searchListPageNo + ""); // list
																			// page
			params.put(Urls.paramServiceListSize, LIST_SIZE); // list size
			path = ServicesDAOweb.getPath(true, path, params);
		}
		web.searchService(path);
	}

	/**
	 * 
	 * @param serviceId
	 * @param p
	 *            tell to show latest comments or show previous comments.
	 */
	public void readService(Source source, int serviceId, Page p) {
		switch (p) {
		case PREVIOUS:
			commentPageNo++;
			break;
		case LATEST:
			if (commentPageNo > 0)
				commentPageNo--;
			break;
		}

		// Get the path of the read services page
		String path = Urls.pathMyServiceDetail;

		RequestParams params = new RequestParams();
		params.put(Urls.paramServiceId, serviceId + ""); // service id
		params.put(Urls.paramCommentListPage, commentPageNo + ""); // comment
																	// page no
		params.put(Urls.paramCommentListSize, LIST_SIZE); // list size
		path = ServicesDAOweb.getPath(true, path, params);

		Loggen.i(this, "Got path: " + path);
		// Send the path to the correct DAO (Note: for DAOlocal, we send the
		// file name instead of the path)
		switch (source) {
		case DEFAULT:
			if (local.fileExists(ServicesDAOlocal.pathToFileName(path))) {
//				local.readServices(ServicesDAOlocal.pathToFileName(path));
			} else {
				web.readService(path);
			}
			break;
		case WEB:
			web.readService(path);
			break;
		case LOCAL:
			local.readService(ServicesDAOlocal.pathToFileName(path));
			break;
		case DUMMY:
			dummy.readService(ServicesDAOlocal.pathToFileName(path));
			break;

		}
	}

	public void updateServiceScore(int serviceId, String userMail, int score) {
		String path = Urls.pathServiceScore;

		RequestParams params = new RequestParams();
		params.put(Urls.paramUserEmail, userMail);
		params.put(Urls.paramServiceId, serviceId + "");
		params.put(Urls.paramCommentListPage, commentPageNo + "");
		path = ServicesDAOweb.getPath(true, path, params);

		Loggen.i(this, "Got path: " + path);

		web.updateServiceScore(path);
	}

	public void createServiceComment(int serviceId, String userMail,
			String comment) {
		String path = Urls.pathServiceComment;

		RequestParams params = new RequestParams();
		params.put(Urls.paramUserEmail, userMail);
		params.put(Urls.paramServiceId, serviceId + "");
		params.put(Urls.paramServiceComment, comment);
		path = ServicesDAOweb.getPath(true, path, params);

		Loggen.i(this, "Got path: " + path);

		web.createServiceComment(path);

	}

	public void createServiceComment(Source source, int serviceId, String userMail, String comment) {
		dummy.createServiceComment("");
	}

	public void updateServiceScore(Source source, int serviceId, String userMail, int score) {
		dummy.updateServiceScore("");
	}


	@Override public void onCreateService(RawResponse response) {
		Loggen.i(this, "Got a response: " + response.message);
		boolean success = false;
		if (response.errorStatus == RawResponse.Error.NONE
				&& isJson(response.message)) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// success or not
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("createServiceOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (detaillistener.get() != null) {
			detaillistener.get().onCreateService(success);
		}

	}

	@Override
	public void onDeleteService(RawResponse response) {
		Loggen.i(this, "Got a response: " + response.message);
		boolean success = (Boolean) null;
		if (response.errorStatus == RawResponse.Error.NONE
				&& isJson(response.message)) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// success or not
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("deleteServiceOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener.get() != null) {
			listlistener.get().onDeleteService(success);
		}

	}

	@Override
	public void onEditService(RawResponse response) {
		Loggen.i(this, "Got a response: " + response.message);
		boolean success = true;
		if (response.errorStatus == RawResponse.Error.NONE
				&& isJson(response.message)) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// success or not
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("editServiceOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (detaillistener.get() != null) {
			detaillistener.get().onEditService(success);
		}
	}

	@Override
	public void onReadServices(RawResponse response) {
		
		List <TrustService> services = null;
		Gson gson = new Gson();
		
		if (response.errorStatus == RawResponse.Error.NONE
				&& JsonTools.isValidJSON(response.message)) {

			// Step 1 - convert the message into a JSON object
			java.lang.reflect.Type listType = new TypeToken<ArrayList<TrustService>>() { }.getType();
			services = gson.fromJson(response.message, listType);
			if(services == null) { services = new ArrayList<TrustService> (); } 
			
			// Step 2 - update the message with the cache content
			if(response.page != Page.CURRENT){
			
				// Step 2a - read the existing data from cache. 
				Loggen.v(this, "Start getting old services.");
				List <TrustService> oldRecords = gson.fromJson(local.readFromFile(response.path), listType);

				// Step 2b - read the existing data from cache. 
				if(oldRecords != null){
					// loop through all the old users
					for(int i = oldRecords.size() - 1; i >= 0; i--){

						// get the id and set overlap to false
						int oldId = oldRecords.get(i).getServiceid();
						boolean overlap = false;

						// compare each old record to all the new records
						for(TrustService service : services){
							// if the old record is also in the new records, the records overlap
							if( oldId == service.getServiceid()){ overlap = true; }
						}
						
						// if the records did not overlap, add this record to the list of records
						if(!overlap)
							services.add(0,oldRecords.get(i));
					}
				}
			}
			
			// TODO: FIGURE OUT WHAT TO DO WITH IMAGES 
			for(TrustService service : services){

				String serviceFileName = ServicesDAOlocal.getServiceFilename(service.getServiceid());

				// Step 3 - get the saved user information
				TrustService oldService = gson.fromJson(local.readFromFile(serviceFileName), TrustService.class);
				
				// Step 4 - Copy the image to the file
				String image;
				if(oldService != null && (image = oldService.getServicephoto()) != null 
						&& (image.endsWith(Gegevens.FILE_EXT_JPG) 
						|| image.endsWith(Gegevens.FILE_EXT_PNG) 
						|| image.endsWith(Gegevens.FILE_EXT_GIF))){
					service.setServicephoto(image);
				} else {
					service.setServicephoto(dummy.randomPic().getAbsolutePath());
				}
				
				// Step 6 - Write the service to file
				local.writeToFile(serviceFileName, gson.toJson(service));
			}
			
			// Step 7 - save the date to the cache
			if (response.path != null) {
				local.writeToFile(response.path, gson.toJson(services));
			}
			
		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}
		
		if (listlistener.get() != null) {
			listlistener.get().onReadServices(services);
		}
	}

	@Override
	public void onSearchServices(RawResponse response) {
		// TODO Auto-generated method stub
		Loggen.i(this, "Got a response: " + response.message);
		ArrayList<TrustService> services = null;
		if (response.errorStatus == RawResponse.Error.NONE
				&& isJson(response.message)) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// Next create a list of Services using the JSON message
			// TODO: implement
			services = new ArrayList<TrustService>();
			Gson gson = new Gson();
			java.lang.reflect.Type listType = new TypeToken<ArrayList<TrustService>>() {
			}.getType();
			services = gson.fromJson(response.message, listType);

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener.get() != null) {
			listlistener.get().onSearchService(services);
		}
	}

	@Override public void onReadService(RawResponse response) {
		
		// default return values
		TrustService service = null;
		ArrayList<Comment> comments = null;
		int numberOfUsers = -1;
		
		if (response.errorStatus == RawResponse.Error.NONE) {
			try{
				// first save the data to the cache
				if (response.path != null && response.message != null) {
					local.writeToFile(response.path, response.message);
				}
				
				// Generic Gson instance used for conversion
				Gson gson = new Gson();

				// Step 1 - convert the message into a JSON object
				JsonElement je = new JsonParser().parse(response.message);
				JsonObject jobj = je.getAsJsonObject();
				
				// Step 2 - convert the service details to a TrustService instance
				JsonElement s = jobj.get(Urls.jsonServiceDetail); // get service detail
				if(s != null ) { service = gson.fromJson(s, TrustService.class); }
				
				// Step 3 - get the number of service users 
				JsonElement sn = jobj.get(Urls.jsonServiceUserNumber); // ?
				if(sn != null) { numberOfUsers = sn.getAsInt(); }
				
				// Step 4 - convert the comment details to a list of Comment instances
				JsonElement c = jobj.get(Urls.jsonCommentDetail); // get comment list
				java.lang.reflect.Type listType = new TypeToken<ArrayList<Comment>>() {}.getType();
				if(c != null) { comments = gson.fromJson(c, listType); } else { comments = new ArrayList<Comment> ();}
				
				// Step 5 - get the replies to each comment
				JsonElement rd = jobj.get(Urls.jsonReplyCommentDetail);
				if(rd != null){
					// Step 5a - read the replies into an array
					JsonArray ja = rd.getAsJsonArray();
					for(JsonElement ele : ja) {
						// Step 5b - read each element in the array as an instance of Comment
						Comment rc = gson.fromJson(ele, Comment.class);
						for(Comment co : comments) {
							// Step 5c - Add each comment to its corresponding comment
							if(co.getCommentid() == rc.getRootcommentid()) {
								co.getDetailComments().add(rc);
							}
						}
					}
				}
			}catch(Exception e){
				// If an error occurs while parsing the message, just stop and reply with what we've got.
				Loggen.e(this, "Error (" + e.toString() + ") while parsing " + response.message);
			}
		} else {
			Loggen.e(this, "Error (" + response.errorStatus + ") while parsing " + response.message); 
		}

		if (detaillistener.get() != null) {
			detaillistener.get().onReadService(service, numberOfUsers, comments);
		}
	}

	@Override
	public void writeServiceScore(RawResponse response) {
		Loggen.i(this, "Got a response: " + response.message);
		// TODO change back to false
		boolean success = true;
		if (response.errorStatus == RawResponse.Error.NONE
				&& isJson(response.message)) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// success or not
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("editServiceOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (detaillistener.get() != null) {
			detaillistener.get().writeServiceScore(success);
		}
	}

	@Override
	public void writeServiceComment(RawResponse response) {
		Loggen.v(this, "Got a response: " + response.message);
		// TODO change back to false
		boolean success = true;
		if (response.errorStatus == RawResponse.Error.NONE
				&& isJson(response.message)) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// success or not
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("serviceCommentOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (detaillistener.get() != null) {
			detaillistener.get().writeServiceComment(success);
		}
	}

	public interface ServicesListListener {
		public void onDeleteService(boolean success);

		public void onReadServices(List<TrustService> services);

		public void onSearchService(List<TrustService> services);

	}

	public interface ServicesDetailListener {
		/** Callback from the readService() method. 
		 * @param service The service read from the data source. Null if no service was found or data request failed.
		 * @param numberOfUsers The number of users that have used this service. -1 if data request failed.
		 * @param comments A list of comments. Null if data request failed. 
		 */
		public void onReadService(TrustService service, int numberOfUsers, List<Comment> comments);

		public void writeServiceScore(boolean success);

		public void writeServiceComment(boolean success);

		public void onEditService(boolean success);

		public void onCreateService(boolean success);
	}
	
	/**
	 * Check server response whether it's JSON format
	 */
	private boolean isJson(String message) {
		if (message == "" || message == null) {
			return false;
		} else {
			try {
				new JsonParser().parse(message);
				return true;
			} catch (JsonParseException e) {
				return false;
			}
		}
	}
}
