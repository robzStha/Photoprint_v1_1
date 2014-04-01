package jsoft.projects.photoprint_v1_1.cart;

import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.content.Context;

/**
 * Acts as a cart.
 * <p>Add to the database and acts as a virtual cart.</p>
 * */
public class ShoppingCart {
	
	Context context;
	SessionMngr session;
	public int uid;
	
	// Constructor
	public ShoppingCart(Context context){
		this.context=context; 
		session = new SessionMngr(this.context);
		uid = session.getIntValues("uid");
	}
	
	public void addToCart(ArrayList<String> selectedItems){
		OrderItems oi = new OrderItems();
		OrderManager om = new OrderManager(context);
		om.open();
		for(int i=0;i<selectedItems.size();i++){
			oi = om.insert(selectedItems.get(i));
			long insertId = oi.getId(); // order item id
			int size = 1;//Integer.parseInt(nvpSizes.get(i).getValue()); 	default value for size
			int qty = 1;//Integer.parseInt(nvpSizes.get(i).getName());		default value for qty
			String mugImg = null;
			om.insert(insertId, size, qty, mugImg);
		}
		om.close();
	}
	
	public int totalCartItems(){
		int totalCartItems=0;
		OrderManager om = new OrderManager(context);
		om.open();
		totalCartItems = om.getAllOrderItems(uid).size();
		om.close();
		return totalCartItems;
	}
	
	public ArrayList<String> getCartImages(){
		ArrayList<OrderItems> oi = new ArrayList<OrderItems>();
		oi = getCartItems();
		
		ArrayList<String> selectedItems = new ArrayList<String>();
    	for(int j=0;j<oi.size();j++){
    		selectedItems.add(oi.get(j).getImgName());
    	}
		return selectedItems;
	}
	
	public ArrayList<OrderItems> getCartItems(){
		OrderManager om = new OrderManager(context);
		om.open();
		ArrayList<OrderItems> oi = new ArrayList<OrderItems>();
		oi = om.getAllOrderItems(uid);
		om.close();
		return oi;
	}

}
