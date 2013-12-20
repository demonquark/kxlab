package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.os.AsyncTask;
import android.os.Environment;

public class ServicesDAOdummy extends ServicesDAOabstract {

	private File cacheDir;
	private int counter;

	public ServicesDAOdummy(OnServicesRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), 
				Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
		this.listener = listener;
		this.counter = 0;
	}

	@Override protected void readServices(final String path) {
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = null;
				String filename = "random";
				if(path != null){
					filename = (path.contains(".")) ? path : path + Gegevens.FILE_EXT_DAT;  
			        Loggen.v(this, "Reading from dummy: " + filename);
					response = readFromFile(filename);
					filename = filename.substring(0, path.length() - 4);
					
					while(response.contains("\"servicephoto\":null")){
						response = response.replaceFirst("\"servicephoto\":null", "\"servicephoto\":\"" + randomPic() + "\"");
					}
				}
				
				listener.onReadServices(new RawResponse(response, filename));
			}
	
		}.execute();
		
	}

	@Override protected void readService(String path) {
		
	}

	@Override protected void updateServiceScore(String path) {
		
	}

	@Override protected void createServiceComment(String path) {
		
	}
	
	public String randomPic(){
		String filename = "service";

		counter ++;
		int i = counter % 8;
		filename += (i < 3) ? i + ".jpg" : (i == 4) ? "0.png" : (i == 6) ? "0.gif" : ".dat";
		
		return new File(cacheDir, filename).getAbsolutePath();
	}
	
	public String readFromFile(String filename) {
        File file = new File(cacheDir, filename);
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
	@Override protected void editService(String path) { }
	@Override protected void createService(String path) { }
	@Override protected void deleteService(String path) { }
}
