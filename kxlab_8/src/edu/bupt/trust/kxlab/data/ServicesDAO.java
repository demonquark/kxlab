package edu.bupt.trust.kxlab.data;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.RequestParams;

import edu.bupt.trust.kxlab.data.DaoFactory.Source;
import edu.bupt.trust.kxlab.data.ServicesDAOabstract.OnServicesRawDataReceivedListener;
import edu.bupt.trust.kxlab.model.TrustService;
import edu.bupt.trust.kxlab.utils.Loggen;

public class ServicesDAO implements OnServicesRawDataReceivedListener {
	
	public enum Type {COMMUNITY, RECOMMENDED, APPLY};
	
	private ServicesDAOlocal local;
	private ServicesDAOweb web;
	private ServicesDAOdummy dummy;
	private ServicesListListener 	listlistener;
	private ServicesDetailListener 	detaillistener;
	
	// Outward facing methods (used by the class requesting the data)
	public void setServicesListListener(ServicesListListener listener) { this.listlistener = listener; }
	public void setServicesDetailListener(ServicesDetailListener listener) { this.detaillistener = listener; }
	public void setCacheDir(Context c) { local.setCacheDir(c); }	
	
	
	// Inward facing methods (used to communicate with the class providing the data)
	protected ServicesDAO(Context c, ServicesListListener listener){
		local = new ServicesDAOlocal(this, c);
		web = new ServicesDAOweb(this);
		dummy = new ServicesDAOdummy(this);
		this.listlistener = listener;
	}
	
	protected ServicesDAO(Context c, ServicesDetailListener listener){
		local = new ServicesDAOlocal(this, c);
		web = new ServicesDAOweb(this);
		dummy = new ServicesDAOdummy(this);
		this.detaillistener = listener;
	}

	public void readServices(Type type) { readServices(type, new String [] {}); }
	public void readServices(Type type, String... parameters) { readServices(type, Source.DEFAULT, parameters); }

	/**
	 * Reads the list of services.
	 * Source options are: DEFAULT (try local then web), LOCAL (local), WEB (web), DUMMY (dummy data)  
	 * @param source
	 */
	public void readServices(Type type, Source source, String [] parameters) {
		
		// Get the path of the read services page
		String path = Urls.pathServiceList;
		if(parameters != null && parameters.length > 0){
			RequestParams params = new RequestParams();
			params.put(Urls.paramServiceSearchKey, parameters[0]);
			path = ServicesDAOweb.getPath(true, path, params);
		}
		
		// Send the path to the correct DAO (Note: for DAOlocal, we send the file name instead of the path)
		switch(source){
			case DEFAULT:
				if(local.fileExists(ServicesDAOlocal.pathToFileName(path))){ 
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
				switch(type){
					case COMMUNITY:  dummy.readServices("community"); break;
					case RECOMMENDED:  dummy.readServices("recommended"); break;
					case APPLY:  dummy.readServices("apply"); break;
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

	
	// TODO: Consider making the implemented OnServicesRawDataReceivedListener methods private (????) 
	// For now we can keep them public. Just in case the data recipient wants the raw data.
	
	@Override public void onReadServices(RawResponse response) {
		Loggen.i("Kris", "Got a response: " + response.message);
		ArrayList <TrustService> services = null;
		if(response.errorStatus == RawResponse.Error.NONE){
			
			// first save the data to the cache
			if(response.path != null && response.message != null){
				local.writeToFile(response.path, response.message);
			}
			
			// Next create a list of Services using the JSON message
			// TODO: implement 
			services = new ArrayList <TrustService> ();
			String lines[] = response.message.split("\\r?\\n");
			for(int i = 0; i < lines.length; i++){ 
				TrustService service = new TrustService();
				service.setServiceid(i);
				service.setServicedetail(i + ") " + lines[i]);
				service.setServicetitle((lines[i].length() > 5) ? lines[i].substring(0,5) : lines[i]);
				service.setServicephoto(dummy.randomPic());
				services.add(service);	 
			}
		} else {
			Log.e("Kris", "We encountered an error: " + response.message);
		}
		
		if(listlistener != null){ listlistener.onReadServices(services); }
	}
	@Override public void onReadService(RawResponse response) { }
	@Override public void writeServiceScore(RawResponse response) { }
	@Override public void writeServiceComment(RawResponse response) { }
	
	public interface ServicesListListener {
		public void onReadServices(List<TrustService> services);
		public void onReadService(String service);
	}

	public interface ServicesDetailListener {
		public void onReadService(String service);
		public void writeServiceScore(boolean success);
		public void writeServiceComment(boolean success);
	}

	@Override
	public void onSearchServices(RawResponse response) {
		// TODO Auto-generated method stub
		
	}
}