package jsoft.projects.photoprint_v1_1.adapters;

public class DrawerItem {

	private String menu;
	private int icon;
	private String count="0";
	
	private boolean isCounterVisible=false;
	
	public DrawerItem(){}
	
	public DrawerItem(String menu, int icon){
		this.menu = menu;
		this.icon = icon;
	}
	
	public DrawerItem(String menu, int icon, boolean isCounterVisible, String count){
		this.menu = menu;
		this.icon = icon;
		this.isCounterVisible = isCounterVisible;
		this.count = count;
	}
	
	public String getMenu(){
		return this.menu;
	}
	
	public int getIcon(){
		return this.icon;
	}
	
	public String getCount(){
		return this.count;
	}
	
	public boolean getCounterVisibility(){
		return this.isCounterVisible;
	}
	
	public void setMenu(String menu){
		this.menu = menu;
	}
	
	public void setIcon(int icon){
		this.icon = icon;
	}
	
	public void setCount(String count){
		this.count = count;
	}
	
	public void setCounterVisibility(boolean isCounterVisible){
		this.isCounterVisible = isCounterVisible;
	}
}
