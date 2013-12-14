package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import edu.bupt.trust.kxlab.utils.Loggen;

import android.os.AsyncTask;
import android.os.Environment;

public class ServicesDAOdummy extends ServicesDAOabstract {

	private File cacheDir;
	private int counter;

	public ServicesDAOdummy(OnServicesRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), ServicesDAOlocal.REPLACE_cachefolder);
		this.listener = listener;
		this.counter = 0;
	}

	@Override protected void readServices(final String path) {
		
		new AsyncTask<Void, Integer, Void>  (){
			@Override protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = null;
				if(path != null){
					String filename = (path.contains(".")) ? path : path + ServicesDAOlocal.REPLACE_extension; 
					response = readFromFile(filename);
				}
				
				listener.onReadServices(new RawResponse(response, path));
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

	@Override
	protected void searchService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void editService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createService(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void deleteService(String path) {
		// TODO Auto-generated method stub
		
	}


}
