package edu.bupt.trust.kxlab.data;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

public class RawResponse {

	public static enum Error {
		NONE,
		NO_CONNECTION,
		IOEXCEPTION,
		ILLEGALSTATE,
		ILLEGALARGUMENT,
		INTERRUPTED_IO,
		UNSUPPORTEDENCODING,
		UNKNOWN_HOST,
		SERVER_REPLY,
		FILE_NOT_FOUND;
	};

	// constructors
	public RawResponse(){ this(Error.NONE); }
	public RawResponse(Error e){ this(e, null, null); }
	public RawResponse(String msg){ this(Error.NONE, msg, null); }
	public RawResponse(String msg, String path){ this(Error.NONE, msg, path); }
	public RawResponse(Error e, String msg, String path){ errorStatus = e; message = msg; this.path = path; }
	
	public RawResponse(Throwable t, String msg, String path){
		if(t == null){ 											errorStatus = Error.NONE;
		} else if (t instanceof IOException){					errorStatus = Error.IOEXCEPTION;
		} else if (t instanceof IllegalStateException){			errorStatus = Error.ILLEGALSTATE;
		} else if (t instanceof InterruptedIOException){		errorStatus = Error.INTERRUPTED_IO;
		} else if (t instanceof UnsupportedEncodingException){	errorStatus = Error.UNSUPPORTEDENCODING;
		} else if (t instanceof UnknownHostException){			errorStatus = Error.UNKNOWN_HOST;
		} else {												errorStatus = Error.SERVER_REPLY; }  
		
		message = msg;
		this.path = path;
	}
	
	// use these values if we have an error 
	public Error errorStatus;
	public String message;
	public String path;
	
}
