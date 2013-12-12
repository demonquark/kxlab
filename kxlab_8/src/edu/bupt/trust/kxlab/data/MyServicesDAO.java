package edu.bupt.trust.kxlab.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ServicesDAOabstract.OnServicesRawDataReceivedListener;
import edu.bupt.trust.kxlab.model.Comment;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.model.User;
import edu.bupt.trust.kxlab.utils.Loggen;

public class MyServicesDAO implements OnServicesRawDataReceivedListener {
	private static final String LIST_SIZE = "4";
	public enum Type {
		COMMUNITY, RECOMMENDED, APPLY
	};

	private MyServicesDAOlocal local;
	private MyServicesDAOweb web;
	private MyServicesDAOdummy dummy;
	private MyServicesListListener listlistener;
	private MyServicesDetailListener detaillistener;

	// Outward facing methods (used by the class requesting the data)
	public void setServicesListListener(MyServicesListListener listener) {
		this.listlistener = listener;
	}

	public void setServicesDetailListener(MyServicesDetailListener listener) {
		this.detaillistener = listener;
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
		this.listlistener = listener;
	}

	protected MyServicesDAO(Context c, MyServicesDetailListener listener) {
		local = new MyServicesDAOlocal(this, c);
		web = new MyServicesDAOweb(this);
		dummy = new MyServicesDAOdummy(this);
		this.detaillistener = listener;
	}

	public void readServices(Type type) {
		readServices(type, new String[] {});
	}

	public void readServices(Type type, String... parameters) {
		readServices(type, Source.DEFAULT, parameters);
	}

	/**
	 * Reads the list of services. Source options are: DEFAULT (try local then
	 * web), LOCAL (local), WEB (web), DUMMY (dummy data)
	 * 
	 * @param source
	 * @param parameters parameters[0] matches user email, [2] matches list page
	 */
	public void readServices(Type type, Source source, String[] parameters) {
	
		int typeparam = 0;
		switch (type) {
			case COMMUNITY: typeparam = 1; break;
			case RECOMMENDED: typeparam = 2; break;
			case APPLY: typeparam = 3; break;
		}

		
		// Get the path of the read services page
		String path = Urls.pathMyServiceList;
		if (parameters != null && parameters.length > 0) {
			RequestParams params = new RequestParams();
			params.put(Urls.paramUserEmail, parameters[0]);			//user email
			params.put(Urls.paramServiceType, typeparam + "");				//service type
			params.put(Urls.paramServiceListPage, parameters[1]); 	//list page
			params.put(Urls.paramServiceListSize, LIST_SIZE);				//list size
			path = ServicesDAOweb.getPath(true, path, params);
		}
		Loggen.i(this, "Got path: " + path);
		// Send the path to the correct DAO (Note: for DAOlocal, we send the
		// file name instead of the path)
		switch (source) {
		case DEFAULT:
			if (local.fileExists(ServicesDAOlocal.pathToFileName(path))) {
				local.readServices(ServicesDAOlocal.pathToFileName(path));
			} else {
				web.readServices(path);
			}
			break;
		case WEB:
			web.readServices(path);
			break;
		case LOCAL:
			local.readServices(ServicesDAOlocal.pathToFileName(path));
			break;
		case DUMMY:
			switch (type) {
			case COMMUNITY:
				dummy.readServices("community");
				break;
			case RECOMMENDED:
				dummy.readServices("recommended");
				break;
			case APPLY:
				dummy.readServices("apply");
				break;
			}

			break;
		}

	}

	protected void readService(int serviceId) {
		// TODO Auto-generated method stub

	}

	protected void updateServiceScore() {
		// TODO Auto-generated method stub

	}

	protected void createServiceComment() {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param type
	 * @param source
	 * @param parameters parameters[0] matches search key word, [1] matches user email, [2] matches list page
	 */
	public void searchService(Type type, Source source, String[] parameters) {
		// Get the path of the read services page
		String path = Urls.pathMyServiceSearch;
		if (parameters != null && parameters.length > 0) {
			RequestParams params = new RequestParams();
			params.put(Urls.paramServiceSearchKey, parameters[0]);
			params.put(Urls.paramUserEmail, parameters[1]);			//user email
			params.put(Urls.paramServiceType, type.ordinal() + "");	//service type
			params.put(Urls.paramServiceListPage, parameters[2]); 	//list page
			params.put(Urls.paramServiceListSize, LIST_SIZE);		//list size
			path = ServicesDAOweb.getPath(true, path, params);
		}

		// Send the path to the correct DAO (Note: for DAOlocal, we send the
		// file name instead of the path)
		switch (source) {
		case DEFAULT:
			if (local.fileExists(ServicesDAOlocal.pathToFileName(path))) {
				local.readServices(ServicesDAOlocal.pathToFileName(path));
			} else {
				web.searchService(path);
			}
			break;
		case WEB:
			web.searchService(path);
			break;
		case LOCAL:
			local.searchService(ServicesDAOlocal.pathToFileName(path));
			break;
		case DUMMY:
			switch (type) {
			case COMMUNITY:
				dummy.searchService("community");
				break;
			case RECOMMENDED:
				dummy.searchService("recommended");
				break;
			case APPLY:
				dummy.searchService("apply");
				break;
			}

			break;
		}
	}

	// TODO: Consider making the implemented OnServicesRawDataReceivedListener
	// methods private (????)
	// For now we can keep them public. Just in case the data recipient wants
	// the raw data.

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

			// Print out a debug string
			/*String debugInstances = "";
			for (TrustService t : services) {
				debugInstances += t.getUseremail() + " | " + t.getServiceid()
						+ " | " + t.getServicetitle() + " | "
						+ t.getServicephoto();
				Loggen.d(this, debugInstances);
				debugInstances = "";
			}*/

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener != null) {
			listlistener.onReadServices(services);
		}
	}

	@Override
	public void onReadService(RawResponse response) {
		TrustService service = null;
		ArrayList<Comment> comment = null;
		if (response.errorStatus == RawResponse.Error.NONE) {

			// first save the data to the cache
			if (response.path != null && response.message != null) {
				local.writeToFile(response.path, response.message);
			}

			// Next create detail of Services using the JSON message
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(response.message);
			JsonObject jobj = je.getAsJsonObject();

			JsonElement s = jobj.get("ServiceDetail");// get service detail
			Gson gson = new Gson();
			service = gson.fromJson(s, TrustService.class);

			JsonElement c = jobj.get("CommentDetail");// get comment list
			java.lang.reflect.Type listType = new TypeToken<ArrayList<User>>() {
			}.getType();
			comment = gson.fromJson(c, listType);

			JsonElement sn = jobj.get("ServiceUserNumber");// ?

			JsonElement rd = jobj.get("ReplyCommentDetail");// ?
		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener != null) {
			listlistener.onReadService(service);
		}
	}

	@Override
	public void writeServiceScore(RawResponse response) {
	}

	@Override
	public void writeServiceComment(RawResponse response) {
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

			// Print out a debug string
			String debugInstances = "";
			for (TrustService t : services) {
				debugInstances += t.getUseremail() + " | " + t.getServiceid()
						+ " | " + t.getServicetitle() + " | "
						+ t.getServicephoto();
				Loggen.d(this, debugInstances);
				debugInstances = "";
			}

		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}

		if (listlistener != null) {
			listlistener.onSearchService(services);
		}
	}

	public interface MyServicesListListener {
		public void onReadServices(List<TrustService> services);

		public void onReadService(TrustService service);

		public void onCreateService(boolean success);

		public void onDeleteService(boolean success);

		public void onEditService(boolean success);

		public void onSearchService(List<TrustService> services);

	}

	public interface MyServicesDetailListener {
		public void onReadService(TrustService service);

		public void writeServiceScore(boolean success);

		public void writeServiceComment(boolean success);
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
