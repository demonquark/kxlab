package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TrustService implements Parcelable  {

	private int serviceid;
	private int servicetype;
	private String servicetitle;
	private String servicedetail;
	private long servicecreatetime;
	private long servicelastedittime;
	private String credibilityScore;
	private String servicephoto;
	private String useremail;
	private int servicestatus;
	
    private TrustService(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	serviceid = in.readInt();
    	servicetype = in.readInt();
		servicetitle = in.readString();
		servicedetail = in.readString();
		servicecreatetime = in.readLong();
		servicelastedittime = in.readLong();
		credibilityScore = in.readString();
		servicephoto = in.readString();
		useremail = in.readString();
		servicestatus = in.readInt();
    }

    public TrustService() {
		serviceid = -1;
		servicetype = -1;
		servicetitle = "";
		servicedetail = "";
		servicecreatetime = -1;
		servicelastedittime = -1;
		credibilityScore = "";
		servicephoto = "";
		useremail = "";
		servicestatus = -1;
    }


	
    // this is used to regenerate your object.
    public static final Parcelable.Creator<TrustService> CREATOR = new Parcelable.Creator<TrustService>() {
        public TrustService createFromParcel(Parcel in) { return new TrustService(in); }
        public TrustService[] newArray(int size) { return new TrustService[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(serviceid);
    	dest.writeInt(servicetype);
    	dest.writeString(servicetitle);
    	dest.writeString(servicedetail);
    	dest.writeLong(servicecreatetime);
    	dest.writeLong(servicelastedittime);
    	dest.writeString(credibilityScore);
    	dest.writeString(servicephoto);
    	dest.writeString(useremail);
    	dest.writeInt(servicestatus);
    	
    }

	public int getServiceid() {
		return serviceid;
	}

	public void setServiceid(int serviceid) {
		this.serviceid = serviceid;
	}

	public int getServicetype() {
		return servicetype;
	}

	public void setServicetype(int servicetype) {
		this.servicetype = servicetype;
	}

	public String getServicetitle() {
		return servicetitle;
	}

	public void setServicetitle(String servicetitle) {
		this.servicetitle = servicetitle;
	}

	public String getServicedetail() {
		return servicedetail;
	}

	public void setServicedetail(String servicedetail) {
		this.servicedetail = servicedetail;
	}

	public long getServicecreatetime() {
		return servicecreatetime;
	}

	public void setServicecreatetime(long servicecreatetime) {
		this.servicecreatetime = servicecreatetime;
	}

	public long getServicelastedittime() {
		return servicelastedittime;
	}

	public void setServicelastedittime(long servicelastedittime) {
		this.servicelastedittime = servicelastedittime;
	}

	public String getCredibilityScore() {
		return credibilityScore;
	}

	public void setCredibilityScore(String credibilityScore) {
		this.credibilityScore = credibilityScore;
	}

	public String getServicephoto() {
		return servicephoto;
	}

	public void setServicephoto(String servicephoto) {
		this.servicephoto = servicephoto;
		
	}

	public String getUseremail() {
		return useremail;
	}

	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}

	public int getServicestatus() {
		return servicestatus;
	}

	public void setServicestatus(int servicestatus) {
		this.servicestatus = servicestatus;
	}

	public static Parcelable.Creator<TrustService> getCreator() {
		return CREATOR;
	}

}
