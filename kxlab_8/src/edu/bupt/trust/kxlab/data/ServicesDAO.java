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
import edu.bupt.trust.kxlab.model.JsonComment;
import edu.bupt.trust.kxlab.model.JsonPost;
import edu.bupt.trust.kxlab.model.JsonPostForumDetail;
import edu.bupt.trust.kxlab.model.JsonReply;
import edu.bupt.trust.kxlab.model.JsonReplyComment;
import edu.bupt.trust.kxlab.model.JsonServiceDetail;
import edu.bupt.trust.kxlab.model.JsonTrustService;
import edu.bupt.trust.kxlab.model.JsonUser;
import edu.bupt.trust.kxlab.model.JsonreReply;
import edu.bupt.trust.kxlab.model.Post;
import edu.bupt.trust.kxlab.model.ServiceFlavor;
import edu.bupt.trust.kxlab.model.ServiceType;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.JsonTools;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ServicesDAO implements OnServicesRawDataReceivedListener {
	
	private static final String LIST_SIZE = "6";
	private static int commentPageNo = 0;
	private static int searchListPageNo = 0;
	

	private ServicesDAOlocal local;
	private ServicesDAOweb web;
	private ServicesDAOdummy dummy;
	private WeakReference <ServicesListener> listener;

	// Outward facing methods (used by the class requesting the data)
	public void setServicesListener(ServicesListener listener) {
		this.listener = new WeakReference<ServicesListener> (listener);
	}

	public void setCacheDir(Context c) {
		local.setCacheDir(c);
	}

	// Inward facing methods (used to communicate with the class providing the
	// data)
	protected ServicesDAO(Context c, ServicesListener listener) {
		local = new ServicesDAOlocal(this, c);
		web = new ServicesDAOweb(this);
		dummy = new ServicesDAOdummy(this);
		this.listener = new WeakReference<ServicesListener>(listener);
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

//			path = ServicesDAOweb.getPath(true, path, params);
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
//		path = ServicesDAOweb.getPath(true, path, params);

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
//			path = ServicesDAOweb.getPath(true, path, params);
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
	
	public void readService(Source source, TrustService service, ArrayList<JsonComment> comments, Page page) {
		
		int size = 0;
		
		// save the current page to the cache 
		if(page != Page.CURRENT || (source == Source.WEB && comments != null ) ){ 
			Loggen.v(this, "saved content to file.");
			size = overwriteService(service, comments); 
		}
		
		// let the correct source handle the request
		switch (source) {
		case DEFAULT:
		case WEB:
			web.readService(service.getId(), size, page);
			break;
		case LOCAL:
			local.readService(service.getId(), size, page);;
			break;
		case DUMMY:
			dummy.readService(service.getId(), size, page);;
			break;
		}
	}	
	
	private int overwriteService(TrustService service, ArrayList<JsonComment> comments) {
		// create a JSON representation of the comments
		ArrayList <JsonComment> jsoncomments = new ArrayList<JsonComment> ();
		JsonReplyComment replyComment = new JsonReplyComment();
		if(comments != null){ 
			for(JsonComment comment : comments){
				if(comment.rootcommentid == 0){
					// this is a top level reply add it there
					jsoncomments.add(comment);
				} else {
					// this is a re reply add it there
					boolean added = false;
					// try adding it to an existing re reply list
					for(List<JsonComment> replylist : replyComment.ReplyCommentDetail){
						if(replylist.size() > 0 && replylist.get(0).rootcommentid == comment.rootcommentid){
							replylist.add(comment);
							added = true;
						}
					}
					
					// if there is no list yet, create a re reply list
					if(!added){
						List<JsonComment> newlist = new ArrayList <JsonComment> ();
						newlist.add(comment);
						replyComment.ReplyCommentDetail.add(newlist);
					}
				}
			} 
		}

		// create a JSON representation of the current forum detail page.
		JsonServiceDetail oldDetails = new JsonServiceDetail(
				(JsonTrustService) service.getJsonService(), 
				service.getServiceUserNumber(),
				jsoncomments,
				replyComment);
		
		// write to file
		local.writeToFile(ServicesDAOlocal.getServiceFilename(service.getId()), 
				new GsonBuilder().serializeNulls().create().toJson(oldDetails));
		
		Loggen.v(this, "Done writing to file.");
		
		return jsoncomments.size();
	}

	private void overwriteServicesList(ServiceType type, ServiceFlavor flavor, List<TrustService> services) {
		if(services != null){
			
			// copy the JSON representation of the services
			List<JsonTrustService> jsonServices = new ArrayList<JsonTrustService>();
			for(TrustService service : services) { jsonServices.add(service.getJsonService()); }
			
			// write to file
			String cachefilename = ServicesDAOlocal.getServicesListFilename(type.getFragName(), flavor.toString());
			local.writeToFile(cachefilename, new Gson().toJson(jsonServices));
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
//			path = ServicesDAOweb.getPath(true, path, params);
		}
		web.searchService(path);
	}
	


	public void updateServiceScore(int serviceId, String userMail, int score) {
		String path = Urls.pathServiceScore;

		RequestParams params = new RequestParams();
		params.put(Urls.paramUserEmail, userMail);
		params.put(Urls.paramServiceId, serviceId + "");
		params.put(Urls.paramCommentListPage, commentPageNo + "");
//		path = ServicesDAOweb.getPath(true, path, params);

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
//		path = ServicesDAOweb.getPath(true, path, params);

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

		if (listener.get() != null) {
			listener.get().onCreateService(success);
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

		if (listener.get() != null) {
			listener.get().onDeleteService(success);
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

		if (listener.get() != null) {
			listener.get().onEditService(success);
		}
	}

	@Override
	public void onReadServices(RawResponse response) {
		
		List <TrustService> services = null;
		Gson gson = new Gson();
		
		if (response.errorStatus == RawResponse.Error.NONE
				&& JsonTools.isValidJSON(response.message)) {

			// Step 1 - convert the message into a JSON object
			java.lang.reflect.Type listType = new TypeToken<ArrayList<JsonTrustService>>() { }.getType();
			List <JsonTrustService> jsonServices = gson.fromJson(response.message, listType);
			if(jsonServices == null) { jsonServices = new ArrayList<JsonTrustService> (); } 
			
			// Step 2 - update the message with the cache content
			if(response.page != Page.CURRENT){
			
				// Step 2a - read the existing data from cache. 
				Loggen.v(this, "Start getting old services.");
				List <JsonTrustService> oldRecords = gson.fromJson(local.readFromFile(response.path), listType);
				
				// Step 3 - update the old records with the new records
				JsonTools.replaceListOverlap(oldRecords, jsonServices);
				
				// Step 4 - add the old records to the new records
				if(response.page == Page.PREVIOUS) { 
					jsonServices.addAll(0, oldRecords); 
				} else {
					jsonServices.addAll(oldRecords);
				} 
			}
			
			// TODO: FIGURE OUT WHAT TO DO WITH IMAGES 
			for(JsonTrustService jsonservice : jsonServices){

				String serviceFileName = ServicesDAOlocal.getServiceFilename(jsonservice.getId());

				// Step 3 - get the saved user information
				JsonTrustService oldService = gson.fromJson(local.readFromFile(serviceFileName), JsonTrustService.class);
				
				// Step 4 - Copy the image to the file
				String image;
				if(oldService != null && (image = oldService.localPhoto) != null 
						&& (image.endsWith(Gegevens.FILE_EXT_JPG) 
						|| image.endsWith(Gegevens.FILE_EXT_PNG) 
						|| image.endsWith(Gegevens.FILE_EXT_GIF))){
					jsonservice.localPhoto = image;
				} else {
					jsonservice.localPhoto = dummy.randomPic().getAbsolutePath();
				}
				
				// Step 6 - Write the service to file
				local.writeToFile(serviceFileName, gson.toJson(jsonservice));
			}
			
			// Step 7 - save the date to the cache
			if (response.path != null) {
				local.writeToFile(response.path, gson.toJson(jsonServices));
			}
			
			// Step 8 - convert it to no normal services
			services = new ArrayList<TrustService> ();
			for(JsonTrustService jsonservice : jsonServices) { 
				services.add(new TrustService(null, jsonservice, new User(jsonservice.useremail))); 
			}
			
		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}
		
		if (listener.get() != null) {
			listener.get().onReadServices(services);
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

		if (listener.get() != null) {
			listener.get().onSearchService(services);
		}
	}

	@Override public void onReadService(RawResponse response) {
		Loggen.v(this, "Got a response onReadService: " + response.message);
		
		// default return values
		TrustService service = null;
		ArrayList<JsonComment> comments = null;
		Gson gson = new GsonBuilder().serializeNulls().create();
		
		// convert the provided JSON string to a JSON object
		if(response.errorStatus == RawResponse.Error.NONE){
			try{
				JsonServiceDetail newService = null;
				if(JsonTools.isValidJSON(response.message)){
					newService = gson.fromJson(response.message,JsonServiceDetail.class);
				} else {
					newService = gson.fromJson(local.readFromFile(response.path), JsonServiceDetail.class);
				}
				
				// update the local file with the content we have now.
				if(response.page != Page.CURRENT){
				
					// read and update the existing post from cache. 
					JsonServiceDetail oldService = 
							gson.fromJson(local.readFromFile(response.path), JsonServiceDetail.class);

					oldService.updateWithNew(newService, response.page == Page.PREVIOUS);
					newService = oldService;
				}

				// save the values to file
				local.writeToFile(response.path, gson.toJson(newService));
				
				// TODO: Figure out when to get overlap?
				
				// get the post from the data
				service = new TrustService(ServiceFlavor.SERVICE, newService.ServiceDetail, 
							new User(newService.ServiceDetail.useremail));
				service.setServiceUserNumber(newService.ServiceUserNumber);

				
				comments = new ArrayList<JsonComment> ();

				
				// get the replies from the data
				List<JsonComment> jsonreplies = newService.CommentDetail;
				for(JsonComment reply : jsonreplies){
					comments.add(new JsonComment(reply));
					Loggen.v(this, "added " + comments.get(comments.size() -1 ).commentid);
				}
				
				// get the replies to the replies
				JsonReplyComment reReply = newService.ReplyComment;
				if(reReply != null){
					Loggen.v(this, "re reply exists");
					List <List<JsonComment>> reReplyDetail = reReply.ReplyCommentDetail;
					if(reReplyDetail != null){
						Loggen.v(this, "re reply details exists");
						// loop through the list of re replies
						for(List<JsonComment> rereplies : reReplyDetail){
							Loggen.v(this, "going through the loop");
							// pick the first re reply
							if(rereplies.size() > 0 ){
								int rootId = rereplies.get(0).rootcommentid;
								Loggen.v(this, "rerereply detail is: " + rootId);

								// loop through the list of replies
								for(int i = 0; i < comments.size(); i++){
									// once we found the reply that this belongs to, add the replies after it.
									if(comments.get(i).rootcommentid == rootId){
										for(int j = rereplies.size() - 1; j >= 0; j--){
											// add the replies to the list
											comments.add(i + 1, new JsonComment(rereplies.get(j)));
											Loggen.v(this, "added " + comments.get(i).commentid);											
										}
										break;
									}
								}
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

		if (listener.get() != null) {
			listener.get().onReadService(service, comments);
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

		if (listener.get() != null) {
			listener.get().writeServiceScore(success);
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

		if (listener.get() != null) {
			listener.get().writeServiceComment(success);
		}
	}

	public interface ServicesListener {
		/** Callback from the readService() method. 
		 * @param service The service read from the data source. Null if no service was found or data request failed.
		 * @param numberOfUsers The number of users that have used this service. -1 if data request failed.
		 * @param comments A list of comments. Null if data request failed. 
		 */
		public void onReadService(TrustService service, List<JsonComment> comments);
		public void writeServiceScore(boolean success);
		public void writeServiceComment(boolean success);
		public void onEditService(boolean success);
		public void onCreateService(boolean success);
		public void onDeleteService(boolean success);
		public void onReadServices(List<TrustService> services);
		public void onSearchService(List<TrustService> services);
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
