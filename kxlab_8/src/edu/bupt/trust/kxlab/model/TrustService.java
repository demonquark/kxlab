package edu.bupt.trust.kxlab.model;

import android.os.Parcel;
import android.os.Parcelable;

public class TrustService implements Parcelable {

	private int serviceId;
	private String servicePhoto;
	private String serviceTitle;
	private String serviceDetail;
	
    private TrustService(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	serviceId = in.readInt();
		serviceTitle = in.readString();
		serviceDetail = in.readString();
		servicePhoto = in.readString();
    }

    public TrustService() {
		serviceId = -1;
		serviceTitle = "";
		serviceDetail = "";
		servicePhoto = "";
    }

	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public String getServicePhoto() {
		return servicePhoto;
	}
	public void setServicePhoto(String servicePhoto) {
		this.servicePhoto = servicePhoto;
	}
	public String getServiceTitle() {
		return serviceTitle;
	}
	public void setServiceTitle(String serviceTitle) {
		this.serviceTitle = serviceTitle;
	}
	public String getServiceDetail() {
		return serviceDetail;
	}
	public void setServiceDetail(String serviceDetail) {
		this.serviceDetail = serviceDetail;
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
    	dest.writeInt(serviceId);
    	dest.writeString(serviceTitle);
    	dest.writeString(serviceDetail);
    	dest.writeString(servicePhoto);
    }

}
