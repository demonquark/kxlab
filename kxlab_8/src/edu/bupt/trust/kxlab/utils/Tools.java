package edu.bupt.trust.kxlab.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Log;

public class Tools {
	
	/** Encrypts a string using base64 AES encoding. */ 
	public static String encrypt (String string, String key){
		// Instantiate the cipher
		try {
			// Get the KeyGenerator			
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; 
		    IvParameterSpec ivspec = new IvParameterSpec(iv);
		    SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);
	        
	        String secured = new String(Base64Coder.encode(cipher.doFinal(string.getBytes("UTF-8"))));
	        return secured;
		} catch (Exception e) {
			// Cipher failure
			Log.e("Tools encrypt", "<< ERROR: Cipher failure >>");
		}
		
		return "";
	}
	
	/** Decrypts a string using base64 AES encoding. */ 
	public static String decrypt (String string, String key){
		// Instantiate the cipher
		try {
			// Get the KeyGenerator			
			byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }; 
		    IvParameterSpec ivspec = new IvParameterSpec(iv);
		    SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(), "AES");
		    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
	        
	        byte[] encryptedBytes = Base64Coder.decode(string);
			return (new String(cipher.doFinal(encryptedBytes)));
			
		} catch (Exception e) {
			// Cipher failure
			Log.e("Tools encrypt", "<< ERROR: Cipher failure >>");
		}
		
		return "";
	}

	/** Returns a SHA-256 hashed value of the strings. */
	public static String hashString(String toHash) {
		
		try { // encrypt the password
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] digest = md.digest(toHash.getBytes("UTF-8"));
			StringBuffer sb = new StringBuffer();
			for (byte b : digest)
				sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (NoSuchAlgorithmException e) { //Loggen.e(Gegevens.LOG_Tools,"No SHA-256 algorithm: "+e); 
		return "";
		} catch (UnsupportedEncodingException e) {// Loggen.e(GlobalConst.LOG_Tools,"Could not hash: "+e); 
		return ""; 
		}
	}
	
	public static String getToday(){
		// set the default date value as fall back.
		return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Calendar.getInstance().getTime());
	}

	public static double [] toPrimitiveDoubles (ArrayList <Double> list ){
		double [] doubles = new double [list.size()];
		for(int i = 0; i < doubles.length; i++) {
		    if (list.get(i) != null) { doubles[i] = list.get(i); }
		}
		return doubles;
	}
	
	/**
	 * 验证是否是邮箱格式
	 * @param strEmail
	 * @return
	 */
	public static boolean isEmail(String strEmail) {		
		String strPattern = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(strPattern);		
		Matcher m = p.matcher(strEmail);		
		return m.matches();	
	}
	
}
