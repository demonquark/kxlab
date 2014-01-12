package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class TrustService implements Parcelable  {

	private ServiceType type;
	private ServiceFlavor flavor;
	private JsonTrustService jsonService;
	private User owner;
	private int ServiceUserNumber;
	
    public TrustService() {
    	type = ServiceType.COMMUNITY;
    	flavor = ServiceFlavor.MYSERVICE;
    	jsonService = new JsonTrustService();
    	owner = new User();
    	ServiceUserNumber = 0;
    }
	
    public TrustService(TrustService otherService) {
    	this();
    	setFromService(otherService);
    }
    
    public TrustService(ServiceFlavor flavor, JsonTrustService service, User owner){
    	this.flavor = flavor;
    	setJsonService(service);
    	setOwner(owner);
    	ServiceUserNumber = 0;
    }

    public void setFromService(TrustService that) {
    	flavor	= that.flavor != null ? that.flavor : ServiceFlavor.SERVICE; 
    	owner 	= that.owner != null ? new User(that.owner) : new User();
		setJsonService(that.jsonService);
	}

	public int getId() {
		return (jsonService != null) ? jsonService.getId() : -1;
	}
	
    public ServiceType getType() {
		return type;
	}

	public ServiceFlavor getFlavor() {
		return flavor;
	}

	public JsonTrustService getJsonService() {
		return jsonService;
	}

	public User getOwner() {
		return owner;
	}

    public void setType(ServiceType type) {
		if(type != null) { 
			this.type = type;
			this.jsonService.servicetype = type.getServerType();
		}
	}
	
	public void setFlavor(ServiceFlavor flavor) {
		this.flavor = flavor;
	}

	public void setJsonService(JsonTrustService jsonService) {
		if(jsonService != null){
			this.type = ServiceType.fromServerType(jsonService.servicetype);
			this.jsonService = jsonService;
		}
	}

	public void setOwner(User owner) {
		if(owner != null) { this.owner = owner; }
	}

	private TrustService(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
    	type 			= ServiceType.fromIndex(in.readInt());
    	flavor 			= ServiceFlavor.fromIndex(in.readInt());
    	jsonService		= in.readParcelable(getClass().getClassLoader());
    	owner			= in.readParcelable(getClass().getClassLoader());
    	ServiceUserNumber = in.readInt();
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
    	dest.writeInt(type.getIndex());
    	dest.writeInt(flavor.getIndex());
    	dest.writeParcelable(jsonService, 0);
    	dest.writeParcelable(owner, 0);
    	dest.writeInt(ServiceUserNumber);
    }

    @Override 
	public boolean equals(Object aThat) {
		if ( this == aThat ) return true;

	    //use instanceof instead of getClass here for two reasons
	    if ( !(aThat instanceof TrustService) ) return false;

	    //cast to native object is now safe
	    TrustService that = (TrustService)aThat;

	    //now a proper field-by-field evaluation can be made
	    return
	    	(this.type == that.type) &&
	    	(this.flavor == that.flavor) &&
	    	((this.jsonService != null) ? this.jsonService.equals(that.jsonService) : that.jsonService == null) &&
	    	((this.owner != null) ? this.owner.equals(that.owner) : that.owner == null) &&
	    	(this.ServiceUserNumber == that.ServiceUserNumber);
	}
    
	public String getServicetitle() {
		return jsonService.servicetitle;
	}

	public String getServicedetail() {
		return jsonService.servicedetail;
	}

	public long getServicecreatetime() {
		return jsonService.servicecreatetime;
	}

	public long getServicelastedittime() {
		return jsonService.servicelastedittime;
	}

	public String getServicecreatetimeString() {
		long date = (jsonService != null) ? jsonService.servicecreatetime : 0;
		return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
	}

	public String getServicelastedittimeString() {
		long date = (jsonService != null) ? jsonService.servicelastedittime : 0;
		return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
	}

	public String getCredibilityScore() {
		return jsonService.credibilityScore;
	}

	public String getUseremail() {
		return jsonService.useremail;
	}

	public int getServicestatus() {
		return jsonService.servicestatus;
	}

	public String getLocalPhoto() {
		return jsonService.localPhoto;
	}

	public void setId(int serviceid) {
		this.jsonService.serviceid = serviceid;
	}

	public void setServicetitle(String servicetitle) {
		this.jsonService.servicetitle = servicetitle;
	}

	public void setServicedetail(String servicedetail) {
		this.jsonService.servicedetail = servicedetail;
	}

	public void setServicecreatetime(long servicecreatetime) {
		this.jsonService.servicecreatetime = servicecreatetime;
	}

	public void setServicelastedittime(long servicelastedittime) {
		this.jsonService.servicelastedittime = servicelastedittime;
	}

	public void setCredibilityScore(String credibilityScore) {
		this.jsonService.credibilityScore = credibilityScore;
	}

	public void setUseremail(String useremail) {
		// TODO: Tie this in with user email ???
		this.jsonService.useremail = useremail;
	}

	public void setServicestatus(int servicestatus) {
		this.jsonService.servicestatus = servicestatus;
	}

	public void setLocalPhoto(String localPhoto) {
		// TODO: Tie this in with jsonService photo ???
		this.jsonService.localPhoto = localPhoto;
	}

	public int getServiceUserNumber() {
		return ServiceUserNumber;
	}

	public void setServiceUserNumber(int serviceUserNumber) {
		ServiceUserNumber = serviceUserNumber;
	}
}
