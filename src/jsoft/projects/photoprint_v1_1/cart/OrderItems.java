package jsoft.projects.photoprint_v1_1.cart;

public class OrderItems{
	
	private long id;
	private int uid;
	private String imgName;
	private String imgLoc;
	
	public long getId(){
		return id;
	}

	public int getUid(){
		return uid;
	}
	
	public void setUid(int uid){
		this.uid = uid;
	}
	
	public String getImgName(){
		return imgName;
	}
	
	public String getImgLoc(){
		return imgLoc;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public void setImgName(String imgName){
		this.imgName = imgName;
	}
	
	public void setImgLoc(String imgLoc){
		this.imgLoc = imgLoc;
	}
}
