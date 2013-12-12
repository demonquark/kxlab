package edu.bupt.trust.kxlab.utils;

import android.util.Log;

public class Loggen {

	public static boolean debug = Gegevens.debug;
	
	public static void d(Class<?> id, String msg){
		if(filter(id)){ Log.d(getLogTag(id), msg); }
	}

	public static void e(Class<?> id, String msg){
		if(filter(id)){ Log.e(getLogTag(id), msg); }
	}

	public static void i(Class<?> id, String msg){
		if(filter(id)){ Log.i(getLogTag(id), msg); }
	}

	public static void v(Class<?> id, String msg){
		if(filter(id)){ Log.v(getLogTag(id), msg); }
	}

	public static void w(Class<?> id, String msg) {
		if(filter(id)){ Log.w(getLogTag(id), msg); }
	}

	public static void d(Object id, String msg){ d(id.getClass(), msg); }
	public static void e(Object id, String msg){ e(id.getClass(), msg); }
	public static void i(Object id, String msg){ i(id.getClass(), msg); }
	public static void v(Object id, String msg){ v(id.getClass(), msg); }
	public static void w(Object id, String msg){ w(id.getClass(), msg); }

	private static boolean filter(Class<?> id) {
		boolean allow = debug;
		if(allow){ 
			// add Class specific filters here.
		}
		
		return debug;
	}

	private static String getLogTag(Class<?> id) {
		// Edit the log tag before sending it to the debugger.
		return id.getSimpleName();
	}
}
