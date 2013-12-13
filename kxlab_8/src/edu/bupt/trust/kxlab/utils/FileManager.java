package edu.bupt.trust.kxlab.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class FileManager {
	
	public static boolean copyAssetsToSDCard(AssetManager am, boolean removeJet, String path, String output){
	    boolean copied = false;
		try {
		    String [] assets = am.list(path);
	        if (assets.length == 0) { 
	        	// if there are no children, this is either a file or an empty folder
	        	if(!(new File(output)).exists()){ copyAssetFileToCard(am, removeJet, path, output);  }
	        } else {
	        	// if there are children, this is clearly a folder
	            File outputFile = new File(output);
	            if (!outputFile.exists()) { outputFile.mkdir(); }
	            // copy the folder content
	            for (int i = 0; i < assets.length; ++i) { 
	            	copyAssetsToSDCard(am, removeJet,
	            			path + Gegevens.FILE_SEPARATOR + assets[i], 
	            			output + Gegevens.FILE_SEPARATOR + assets[i]); }
	        }
	        copied = true;
	    } catch (IOException ex) { Loggen.e(FileManager.class, "I/O Exception " + ex.getMessage()); }
		
		return copied;
	}
	
	public static boolean copyAssetFileToCard(AssetManager am, boolean removeJet, String src, String dst) {
		boolean copied = false;
		
	    if(!removeJet || !( src.endsWith(Gegevens.FILE_EXT_JET) && new File(dst.replace(Gegevens.FILE_EXT_JET, "")).exists() )){ 
		    int read;
	        byte[] buffer = new byte[1024];
		    InputStream in = null;
		    OutputStream out = null;
		    try {
		    	in = am.open(src);
		    	out = new FileOutputStream(dst);
		        while ((read = in.read(buffer)) != -1) { out.write(buffer, 0, read); }
		    	copied = true;
		    } catch (Exception e) { 
		    	Loggen.e(FileManager.class, "Could not copy "+ src + " to " + dst + ". "+ e.getMessage()); 
		    } finally { 
		    	if(in != null){ try { in.close(); 
		    	} catch (IOException e) { e.printStackTrace(); } in = null; } 
		    	if(out != null){ try { out.flush(); out.close(); 
		    	} catch (IOException e) { e.printStackTrace(); } out = null; } 
		    }
		    
		    if(removeJet && dst.endsWith(Gegevens.FILE_EXT_JET)){ 
		    	(new File (dst)).renameTo(new File(dst.replace(Gegevens.FILE_EXT_JET, ""))); 
		    }
	    }
	    
	    return copied;
	}
	
	public static void writeStringToFile(File folder, String filename, String message) throws IOException {
        
		// create the file
		File file = new File(folder, filename);
        Loggen.v(FileManager.class, "Writing to file: " + file.getName());

        // overwrite the file with the new message string
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
		writer.write(message);
		writer.close(); 
	}

	public String readStringFromFile(File file) throws IOException {
        Loggen.v(this, "Reading from file: " + file.getName());

        // get the file length
		byte[] b  = new byte[(int)file.length()];
		int len = b.length;

		// read the file input
		InputStream in = new FileInputStream(file);
		int total = 0;
		while (total < len) {
			int result = in.read(b, total, len - total);
			if (result == -1) { break; }
			total += result;
		}
		in.close();
			
		// convert to UTF-8 text
		return Charset.forName("UTF-8").decode(ByteBuffer.wrap(b)).toString();
	}
	
	public void writeBitmapToFile(File folder, String filename, Bitmap bmp) throws IOException{
		FileOutputStream out = new FileOutputStream(filename);
		bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
		out.close();
	}
	
	public Bitmap readBitmapFromFile(File file, int reqWidth, int reqHeight) {
		return BitmapTools.decodeSampledBitmapFromResource(file.getAbsolutePath(), reqWidth, reqHeight);
	}

	public Bitmap readBitmapFromFile(File file) {
	    // use default bitmap options
		final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    
	    // return the read bitmap
	    return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
	}
}
