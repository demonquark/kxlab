package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.os.AsyncTask;
import android.os.Environment;

public class MyServicesDAOdummy extends ServicesDAOabstract {

	private File cacheDir;
	private Random rand;

	public MyServicesDAOdummy(OnServicesRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), 
				Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);

		this.listener = listener;
		this.rand = new Random();
	}

	@Override protected void readServices(final String path) {
		
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = null;
				if(path != null){
					response = readFromFile(randomServicesList(path));
					
					while(response.contains("\"servicephoto\":null")){
						response = response.replaceFirst("\"servicephoto\":null", "\"servicephoto\":\"" 
								+ randomPic().getAbsolutePath() + "\"");
					}
				}
				listener.onReadServices(new RawResponse(response, "dummyserviceslist"));
			}
		}.execute();
		
	}

	@Override protected void readService(String path) {

		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
		        Loggen.v(this, "Send a request with: " + randomServiceDetails().getAbsolutePath());
				String response = readFromFile(randomServiceDetails());
				while(response.contains("\"servicephoto\":null")){
					response = response.replaceFirst("\"servicephoto\":null", "\"servicephoto\":\"" 
							+ randomPic().getAbsolutePath() + "\"");
				}
				listener.onReadService(new RawResponse(response, "dummservicedetail"));
			}
		}.execute();
	}

	@Override protected void updateServiceScore(String path) {
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.writeServiceScore(new RawResponse(null, "dummservicedetailscore"));
			}
		}.execute();
	}

	@Override protected void createServiceComment(String path) {
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.writeServiceComment(new RawResponse(null, "dummservicedetailcomment"));
			}
		}.execute();
	}
	
	@Override protected void createService(String path) {
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.onCreateService(new RawResponse()); 
			}
		}.execute();
	}
	
	@Override protected void editService(String path) { 
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				listener.onEditService(new RawResponse()); 
			}
		}.execute();
	}

	public File randomPic(){
		String filename = "service";
		
		int i = counter(8);
		filename += (i < 6) ? i + ".jpg" : (i < 7) ?  (i - 6) + ".png" : "0.gif";
		
		return new File(cacheDir, filename);
	}
	
	public File randomServiceDetails(){
		String filename = "servicedetail";
		
		int i = counter(4);
		filename += i + ".dat";
		
		return new File(cacheDir, filename);
	}
	
	public File randomServicesList(String categoryTag){
		
		String [] categories = {"community", "recommend", "apply" };
		if(categoryTag == null){ categoryTag = ""; }
		String filename = categories[counter(3)];
		
		for(int i = 0; i < categories.length; i++){
			if(categoryTag.contains(categories[i])){
				filename = categories[i];
			}
		}
		
		int i = counter(1);
		filename += i + ".dat";
		
		return new File(cacheDir, filename);
	}
	
	private int counter(int modulus){
		return (rand.nextInt(100) % modulus);
	}

	
	public String readFromFile(File file) {
        byte[] b  = new byte[(int)file.length()];
		int len = b.length;
        Loggen.v(this, "Reading from file: " + file.getName());

		InputStream in = null;
		try{
			in = new FileInputStream(file);
			int total = 0;
			while (total < len) {
				int result = in.read(b, total, len - total);
				if (result == -1) { break; }
				total += result;
			}
		} catch (IOException e) { e.printStackTrace(); 
		} finally {
			if (in != null) { 
				try { in.close(); } catch (IOException e) { e.printStackTrace(); } 
			} 
		}
		
		return Charset.forName("UTF-8").decode(ByteBuffer.wrap(b)).toString();
	}

	@Override protected void searchService(String path) { }
	@Override protected void deleteService(String path) { }
}
