package edu.bupt.trust.kxlab.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Random;

import edu.bupt.trust.kxlab.data.RawResponse.Page;
import edu.bupt.trust.kxlab.model.PostType;
import edu.bupt.trust.kxlab.utils.Gegevens;
import edu.bupt.trust.kxlab.utils.Loggen;

import android.os.AsyncTask;
import android.os.Environment;

public class ForumDAOlocal extends ForumDAOabstract {

	private File cacheDir;
	private Random rand;

	public ForumDAOlocal(OnForumRawDataReceivedListener listener){
		cacheDir = new File(Environment.getExternalStorageDirectory(), 
				Gegevens.FILE_USERDIRSD + Gegevens.FILE_SEPARATOR + Gegevens.FILE_CACHE);
        Loggen.v(this, "Cache directory is: " + cacheDir.getAbsolutePath());
		
		this.listener = listener;
		this.rand = new Random();
	}
	
	protected RawResponse readlocalPost(int postId, PostType type) {
		
		// try to read the file
		String filename = ((type == PostType.FORUM || type == PostType.SUGGESTION) ?
				Urls.fileAnnounce : Urls.filePost )  + postId + Gegevens.FILE_EXT_DAT;
		String filecontent = readFromFile(new File(cacheDir, filename));
		
		// create a raw response
		RawResponse response = new RawResponse();
		if(filecontent == null){ response.errorStatus = RawResponse.Error.FILE_NOT_FOUND;
		} else { response.message = filecontent; }
		
		return response;
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
		filename += i + ".dat";
		
		return new File(cacheDir, filename);
	}
	
	private int counter(int modulus){
		return (rand.nextInt(100) % modulus);
	}

	
	public String readFromFile(File file) {
        Loggen.v(this, "Reading file: " + file.getName()+ ((file.exists() ? " (" : "(does not ") + " exist)"));
		
		// make sure we have a file
		if(!file.exists()){ return null; }
		
        byte[] b  = new byte[(int)file.length()];
		int len = b.length;

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

	public String readFromFile(String filename) {
		return readFromFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT));
	}
	
	public boolean writeToFile(String filename, String string) {
		return writeToFile(new File(cacheDir, filename + Gegevens.FILE_EXT_DAT), string);
	}
	
	public boolean writeToFile(File file, String string) {
        Loggen.v(this, "Writing to file: " + file.getName() );
		
        boolean succesful = false;
        
		// make sure we have a file
        
		if(file.getParentFile() != null){ file.getParentFile().mkdirs(); }
		Writer out = null;
		try { 
			out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file.getAbsolutePath()), "UTF8"));
			out.write(string);
			out.flush();
			out.close();
			succesful = true;
			
		} catch (UnsupportedEncodingException e) { e.printStackTrace();
		} catch (IOException e)  { e.printStackTrace();
		} catch (Exception e) { e.printStackTrace(); 
		} finally {
			if (out != null) { 
				try { out.close(); } catch (IOException e) { e.printStackTrace(); } 
			} 
		}
		
		return succesful;
	}
	
	@Override protected void createPost(String path) {
	}

	@Override protected void createReply(String path) {
	}

	@Override protected void createVote(String path) {
	}

	@Override protected void readPost(String path, Page page) { 
	}

	@Override protected void readAnnounceFAQ(String path) {
	}

	@Override protected void searchPostList(String path) {
	}
}
