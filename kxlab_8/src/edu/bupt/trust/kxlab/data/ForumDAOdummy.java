package edu.bupt.trust.kxlab.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.os.AsyncTask;
import android.os.Environment;

public class ForumDAOdummy extends ForumDAOabstract {

	private File cacheDir;
	private Random rand;

	public ForumDAOdummy(OnForumRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), 
				Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
        Loggen.v(this, "Cache directory is: " + cacheDir.getAbsolutePath());
		
		this.listener = listener;
		this.rand = new Random();
	}
	
	@Override protected void readPostList(final String path) {
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				String response = readFromFile(randomPostList(path));
				listener.onReadPostList(new RawResponse(response, "dummypostlist"));
			}
		}.execute();
	}

	public File randomPostList(String type){
		
		String [] types = {"forum"};
		if(type == null){ type = ""; }
		String filename = types[0];
		
		for(int i = 0; i < types.length; i++){
			if(type.contains(types[i])){
				filename = types[i];
			}
		}
		
		int i = counter(1);
		filename += i + Gegevens.FILE_EXT_DAT;
		
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

	@Override
	protected void createPost(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createReply(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createVote(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override protected void readPost(final String path, final Page page) {
		new AsyncTask<Void, Integer, Void>  () {
			@Override protected Void doInBackground(Void... params) {
				try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
				return null;
			}

			@Override protected void onPostExecute(Void v) {
				
				RawResponse response = new RawResponse();
				response.page = page;
				response.path = path;
				if(page == Page.CURRENT){
					response.message = readFromFile(new File(cacheDir, "post5.dat"));	
				} else if (page == Page.LATEST){
					response.message = readFromFile(new File(cacheDir, "post6.dat"));
				} else {
					response.message = readFromFile(new File(cacheDir, "post7.dat"));
				}
				
				
				listener.onReadPost(response);
			}
		}.execute();
		
	}

	@Override
	protected void readAnnounceFAQ(String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void searchPostList(String path) {
		// TODO Auto-generated method stub
		
	}
}
