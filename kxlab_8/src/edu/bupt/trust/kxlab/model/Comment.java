package edu.bupt.trust.kxlab.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;

public class Comment implements Parcelable  {

	private int commentid;
	private int serviceid;
	private String useremail;
	private String reuseremail;
	private String commentscore;
	private String commentdetail;
	private long commenttime;
	private int recommentid;
	private int rootcommentid;
	private ArrayList<Comment> detailComments;
	
	private Comment(Parcel in) {
    	// Note: you need to read the items in the same order that you wrote them
		commentid = in.readInt();
		serviceid = in.readInt();
		useremail = in.readString();
		reuseremail = in.readString();
		commentscore = in.readString();
		commentdetail = in.readString();
		commenttime = in.readLong();
		recommentid = in.readInt();
		rootcommentid = in.readInt();
		detailComments = new ArrayList <Comment> ();
		in.readList(detailComments, null);
    }

    public Comment() {
    	commentid = -1;
    	serviceid = -1;
    	useremail = "";
    	reuseremail = "";
    	commentscore = "";
    	commentdetail = "";
    	commenttime = -1;
    	recommentid = -1;
    	rootcommentid = -1;
    	detailComments = new ArrayList<Comment>();
    }
    
    // this is used to regenerate your object.
    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel in) { return new Comment(in); }
        public Comment[] newArray(int size) { return new Comment[size]; }
    };

    @Override public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    	// Note: you need to write the items in the same order that you intend to read them
    	dest.writeInt(commentid);
    	dest.writeInt(serviceid);
    	dest.writeString(useremail);
    	dest.writeString(reuseremail);
    	dest.writeString(commentscore);
    	dest.writeString(commentdetail);
    	dest.writeLong(commenttime);
    	dest.writeInt(recommentid);
    	dest.writeInt(rootcommentid);    	
    }
    
	public int getCommentid() {
		return commentid;
	}
	public void setCommentid(int commentid) {
		this.commentid = commentid;
	}
	public int getServiceid() {
		return serviceid;
	}
	public void setServiceid(int serviceid) {
		this.serviceid = serviceid;
	}
	public String getUseremail() {
		return useremail;
	}
	public void setUseremail(String useremail) {
		this.useremail = useremail;
	}
	public String getReuseremail() {
		return reuseremail;
	}
	public void setReuseremail(String reuseremail) {
		this.reuseremail = reuseremail;
	}
	public String getCommentscore() {
		return commentscore;
	}
	public void setCommentscore(String commentscore) {
		this.commentscore = commentscore;
	}
	public String getCommentdetail() {
		return commentdetail;
	}
	public void setCommentdetail(String commentdetail) {
		this.commentdetail = commentdetail;
	}
	public long getCommenttime() {
		return commenttime;
	}
	public void setCommenttime(int commenttime) {
		this.commenttime = commenttime;
	}
	public int getRecommentid() {
		return recommentid;
	}
	public void setRecommentid(int recommentid) {
		this.recommentid = recommentid;
	}
	public int getRootcommentid() {
		return rootcommentid;
	}
	public void setRootcommentid(int rootcommentid) {
		this.rootcommentid = rootcommentid;
	}

	public List<Comment> getDetailComments() {
		return detailComments;
	}

	public void setDetailComments(ArrayList<Comment> detailComments) {
		this.detailComments = detailComments;
	}

	public void setCommenttime(long commenttime) {
		this.commenttime = commenttime;
	}
	
	public String getCommenttimeString(){
		return new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.US).format(commenttime);
	}
	
}
