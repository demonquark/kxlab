package edu.bupt.trust.kxlab.data;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.data.ServicesDAOabstract.OnServicesRawDataReceivedListener;
import edu.bupt.trust.kxlab.model.JsonComment;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Loggen;

public class MyServicesDAO implements OnServicesRawDataReceivedListener {
	private static final String LIST_SIZE = "6";
	private static int communityServicePageNo = 0;
	private static int recommendedServiceListPageNo = 0;
	private static int applyServiceListPageNo = 0;
	private static int commentPageNo = 0;
	private static int searchListPageNo = 0;

	public enum Type {
		COMMUNITY, RECOMMENDED, APPLY
	};

	private MyServicesDAOlocal local;
	private MyServicesDAOweb web;
	private MyServicesDAOdummy dummy;
	private WeakReference<MyServicesListListener> listlistener;
	private WeakReference<MyServicesDetailListener> detaillistener;

	// Outward facing methods (used by the class requesting the data)
	public void setServicesListListener(MyServicesListListener listener) {
		this.listlistener = new WeakReference<MyServicesListListener>(listener);
	}

	public void setServicesDetailListener(MyServicesDetailListener listener) {
		this.detaillistener = new WeakReference<MyServicesDetailListener>(
				listener);
	}

	public void setCacheDir(Context c) {
		local.setCacheDir(c);
	}

	// Inward facing methods (used to communicate with the class providing the
	// data)
	protected MyServicesDAO(Context c, MyServicesListListener listener) {
		local = new MyServicesDAOlocal(this, c);
		web = new MyServicesDAOweb(this);
		dummy = new MyServicesDAOdummy(this);
		this.listlistener = new WeakReference<MyServicesListListener>( listener);
	}

	protected MyServicesDAO(Context c, MyServicesDetailListener listener) {
		local = new MyServicesDAOlocal(this, c);
		web = new MyServicesDAOweb(this);
		dummy = new MyServicesDAOdummy(this);
		this.detaillistener = new WeakReference<MyServicesDetailListener>( listener);
	}

	public void readServices(Type type) {
		readServices(type, new String[] {});
	}

	public void readServices(Type type, String... parameters) {
		readServices(type, Page.LATEST, Source.DEFAULT, parameters);
	}

	/**
	 * 
	 * @param type
	 * @param parameters
	 *            parameter[0] represents user mail,parameter[1] represents
	 *            service title,parameter[2] represents service detail
	 */
	public void createService(Type type, String[] parameters) {
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

			path = MyServicesDAO.getPath(true, path, params);
		}
		Loggen.i(this, "Got path: " + path);

//		web.createService(path);

	}

	public void deleteService(int serviceId) {
		String path = Urls.pathMyServiceDelete;
		RequestParams params = new RequestParams();
		params.put(Urls.paramServiceId, serviceId + "");
		path = MyServicesDAO.getPath(true, path, params);

		web.deleteService(0);
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
			path = MyServicesDAO.getPath(true, path, params);
		}
		Loggen.i(this, "Got path: " + path);

//		web.editService(path);
	}

	/**
	 * Reads the list of services. Source options are: DEFAULT (try local then
	 * web), LOCAL (local), WEB (web), DUMMY (dummy data)
	 * 
	 * @param source
	 * @param parameters
	 *            parameters[0] matches user email
	 */
	public void readServices(Type type, Page p, Source source,
			String[] parameters) {

//		int typeparam = 0;
//		int pageNo = 0;
//		switch (type) {
//		case COMMUNITY:
//			typeparam = 1;
//			switch (p) {
//			case PREVIOUS:
//				pageNo = ++communityServicePageNo;
//				break;
//			case LATEST:
//				if (communityServicePageNo > 0)
//					pageNo = --communityServicePageNo;
//				break;
//			}
//			break;
//		case RECOMMENDED:
//			typeparam = 2;
//			switch (p) {
//			case PREVIOUS:
//				pageNo = ++recommendedServiceListPageNo;
//				break;
//			case LATEST:
//				if (recommendedServiceListPageNo > 0)
//					pageNo = --recommendedServiceListPageNo;
//				break;
//			}
//			break;
//		case APPLY:
//			typeparam = 3;
//			switch (p) {
//			case PREVIOUS:
//				pageNo = ++applyServiceListPageNo;
//				break;
//			case LATEST:
//				if (applyServiceListPageNo > 0)
//					pageNo = --applyServiceListPageNo;
//				break;
//			}
//			break;
//		}
//
//		// Get the path of the read services page
//		String path = Urls.pathMyServiceList;
//		if (parameters != null && parameters.length > 0) {
//
//			RequestParams params = new RequestParams();
//			params.put(Urls.paramUserEmail, parameters[0]); // user email
//			params.put(Urls.paramServiceType, typeparam + ""); // service type
//			params.put(Urls.paramServiceListPage, pageNo + ""); // list page
//			params.put(Urls.paramServiceListSize, LIST_SIZE); // list size
//			path = ServicesDAOweb.getPath(true, path, params);
//		}
//		Loggen.i(this, "Got path: " + path);
//		// Send the path to the correct DAO (Note: for DAOlocal, we send the
//		// file name instead of the path)
//		switch (source) {
//		case DEFAULT:
//			if (local.fileExists(ServicesDAOlocal.pathToFileName(path))) {
//				local.readServices(ServicesDAOlocal.pathToFileName(path));
//			} else {
//				web.readServices(path);
//			}
//			break;
//		case WEB:
//			web.readServices(path);
//			break;
//		case LOCAL:
//			local.readServices(ServicesDAOlocal.pathToFileName(path));
//			break;
//		case DUMMY:
//			switch (type) {
//			case COMMUNITY:
//				dummy.readServices("community");
//				break;
//			case RECOMMENDED:
//				dummy.readServices("recommended");
//				break;
//			case APPLY:
//				dummy.readServices("apply");
//				break;
//			}
//
//			break;
//		}

	}

	/**
	 * 
	 * @param type
	 * @param source
	 * @param parameters
	 *            parameters[0] matches search key word, [1] matches user email
	 */
	public void searchService(Type type, Page p, String[] parameters) {
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
			path = MyServicesDAO.getPath(true, path, params);
		}
//		web.searchService(path);
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
		path = MyServicesDAO.getPath(true, path, params);

		Loggen.i(this, "Got path: " + path);
		// Send the path to the correct DAO (Note: for DAOlocal, we send the
		// file name instead of the path)
		switch (source) {
		case DEFAULT:
			if (local.fileExists(ServicesDAOlocal.pathToFileName(path))) {
//				local.readServices(ServicesDAOlocal.pathToFileName(path));
			} else {
//				web.readService(path);
			}
			break;
		case WEB:
//			web.readService(path);
			break;
		case LOCAL:
//			local.readService(ServicesDAOlocal.pathToFileName(path));
			break;
		case DUMMY:
//			local.readService(ServicesDAOlocal.pathToFileName(path));
			break;

		}
	}

	@Override
	public void onCreateService(RawResponse response) {
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

			JsonElement s = jobj.get("createServiceOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener.get() != null) {
			listlistener.get().onCreateService(success);
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

			JsonElement s = jobj.get("editServiceOrNot");
			if (s.getAsInt() == 1) {
				success = true;
			} else {
				success = false;
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener.get() != null) {
			listlistener.get().onEditService(success);
		}
	}

	@Override
	public void onReadServices(RawResponse response) {
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
			listlistener.get().onReadServices(services);
		}
	}

	@Override
	public void onReadService(RawResponse response) {
		TrustService service = null;
		ArrayList<JsonComment> comments = null;
		if (response.errorStatus == RawResponse.Error.NONE) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// Next create detail of Services using the JSON message
			JsonParser jp = new JsonParser();
			Gson gson = new Gson();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("ServiceDetail");// get service detail

			service = gson.fromJson(s, TrustService.class);
			JsonElement c = jobj.get("CommentDetail");// get comment list
			java.lang.reflect.Type listType = new TypeToken<ArrayList<JsonComment>>() {
			}.getType();
			comments = gson.fromJson(c, listType);

			JsonElement sn = jobj.get("ServiceUserNumber");// # of people who
															// use the service

			JsonElement rd = jobj.get("ReplyCommentDetail");// comment on
															// comment
			JsonArray ja = rd.getAsJsonArray();
			for (JsonElement ele : ja) {
				JsonComment rc = gson.fromJson(ele, JsonComment.class);
				for (JsonComment co : comments) {
					if (co.getId() == rc.getId()) {
//						co.getDetailComments().add(rc);
					}
				}
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (detaillistener.get() != null) {
			detaillistener.get().onReadService(service, comments);
		}
	}

	public interface MyServicesListListener {
		public void onCreateService(boolean success);

		public void onDeleteService(boolean success);

		public void onEditService(boolean success);

		public void onReadServices(List<TrustService> services);

		public void onSearchService(List<TrustService> services);

	}

	public interface MyServicesDetailListener {
		public void onReadService(TrustService service, List<JsonComment> comments);
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

	@Override
	public void onUpdateServiceScore(RawResponse response) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCreateComment(RawResponse response) {
		// TODO Auto-generated method stub

	}

    /**
     * Will encode url, if not disabled, and adds params on the end of it
     *
     * @param url             String with URL, should be valid URL without params
     * @param params          RequestParams to be appended on the end of URL
     * @param shouldEncodeUrl whether url should be encoded (replaces spaces with %20)
     * @return encoded url if requested with params appended if any available
     */
    public static String getPath(boolean shouldEncodeUrl, String path, RequestParams params) {
    	return AsyncHttpClient.getUrlWithQueryString(shouldEncodeUrl, path, params);
    }

	
}
