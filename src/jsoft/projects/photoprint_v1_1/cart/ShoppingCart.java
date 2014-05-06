package jsoft.projects.photoprint_v1_1.cart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.content.Context;
import android.util.Log;

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
			String size = Long.toString(insertId)+"_0";//Integer.parseInt(nvpSizes.get(i).getValue()); 	default value for size
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
	
	public void deleteItems(){
		OrderManager om = new OrderManager(context);
		om.open();
		om.delOrderItem();
		om.close();
	}
	
	public ArrayList<OrderDetails> getCartItemDetails(int oiid){
		OrderManager om = new OrderManager(context);
		om.open();
		ArrayList<OrderDetails> od = new ArrayList<OrderDetails>();
		od = om.getAllOrderDetailsByOIID(oiid);
		om.close();
		return od;
	}

	public void updateImgInfo(ArrayList<String> checked, HashMap<String, String> checkedQty){
		OrderManager om = new OrderManager(context);
		om.open();
		ArrayList<OrderItems> oi = om.getAllOrderItems(uid);
		Log.d("Qty", checkedQty.toString());
		Log.d("Checked", checked.toString());
		
		for(int i = 0; i < oi.size(); i++){  // for order items
			long oiid = oi.get(i).getId();
			Log.d("OrderItem Id", Long.toString(oiid));
			if(hasData(oiid, checked)){
				om.delOrderDetailsByOIID(oiid);
				for(int j=0;j<checked.size();j++){
					String tag ="et_"+Long.toString(oiid)+"_"+Integer.toString(j);
					String tempTag = Long.toString(oiid)+"_"+Integer.toString(j);
					if(checkedQty.get(tag) != null){
						Log.d("Qty777", tag);
						om.insert(oiid, tempTag, Integer.parseInt(checkedQty.get(tag)), "");
					}
					else if(hasMoreValues(tempTag, checked)) {
						Log.d("More values", tag);
						om.insert(oiid, tempTag, 1, "");
					}
				}
			}
		}
		om.close();
	}
	
	private boolean hasMoreValues(String tag, ArrayList<String> checked){
		boolean flag = false;
		for(int j=0; j<checked.size(); j++){
			if(tag.equals(checked.get(j))){
				flag=true;
			}
		}
		return flag;
	}
	
	private boolean hasData(long oiid, ArrayList<String> checked){
		boolean flag = false;
		for(int j=0; j<checked.size(); j++){
			StringTokenizer tokens = new StringTokenizer(checked.get(j),"_");
			String position = tokens.nextToken();
//			String chbxPosition = tokens.nextToken();
			if(position.equals(Long.toString(oiid))){
				flag = true;
			}
		}
		
		return flag;
		
	}
}
