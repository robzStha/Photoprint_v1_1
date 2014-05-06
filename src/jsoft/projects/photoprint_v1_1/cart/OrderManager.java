package jsoft.projects.photoprint_v1_1.cart;

import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class OrderManager {

	Context context;
	SessionMngr sessionMngr;
	private int uid;
	
	private SQLiteDatabase db;
	private MySQLiteHelper dbHelper;
	
	private String[] tblOrderItemsCols = {MySQLiteHelper.COLUMN_OIID, MySQLiteHelper.FLD_UID, MySQLiteHelper.FLD_IMG};
	private String[] tblOrderDetailsCols = {MySQLiteHelper.COLUMN_ODID, MySQLiteHelper.FLD_UID, MySQLiteHelper.FLD_ORDER_ITEM,
						MySQLiteHelper.FLD_SIZE, MySQLiteHelper.FLD_QTY,
						MySQLiteHelper.FLD_MUGPRINT_IMG};
	
	public OrderManager(Context context){
		this.context = context;
		sessionMngr = new SessionMngr(context);
		uid = sessionMngr.getIntValues("uid");
		dbHelper = new MySQLiteHelper(context);
	}
	
	public void open() throws SQLException{
		db = dbHelper.getWritableDatabase();
	}
	
	public void close(){
		dbHelper.close();
	}
	
	/*
	 * Inserts the order item
	 * */
	
	public OrderItems insert(String imgName){
		ContentValues valsOI = new ContentValues();
		
		valsOI.put(MySQLiteHelper.FLD_IMG, imgName);
		valsOI.put(MySQLiteHelper.FLD_UID, uid);
		long insertId = db.insert(MySQLiteHelper.TBL_ORDER_ITEMS, null, valsOI);
		
		Cursor cursor  = db.query(MySQLiteHelper.TBL_ORDER_ITEMS, tblOrderItemsCols,
				MySQLiteHelper.COLUMN_OIID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		OrderItems orderItems = cursorToOrderItems(cursor);
		cursor.close();
		return orderItems;
	}
	
	
	/* Get all the ordered items of an specific user*/
	public ArrayList<OrderItems> getAllOrderItems(int uid){
		ArrayList<OrderItems> oi = new ArrayList<OrderItems>();
		
		String where = "user_id = "+uid;
		String sortBy = MySQLiteHelper.COLUMN_OIID + " DESC";
		Cursor cursor = db.query(MySQLiteHelper.TBL_ORDER_ITEMS,
				tblOrderItemsCols,
				where, null, null, null, sortBy);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			OrderItems orderItems = cursorToOrderItems(cursor);
			oi.add(orderItems);
			cursor.moveToNext();
		}
		cursor.close();
		return oi;
	}
	
	public ArrayList<OrderDetails> getAllOrderDetailsByOIID(int oiid){
		ArrayList<OrderDetails> od = new ArrayList<OrderDetails>();
		String where = MySQLiteHelper.COLUMN_OIID+"="+oiid;
		Cursor cursor = db.query(MySQLiteHelper.TBL_ORDER_DETAILS, tblOrderDetailsCols, where, null, null, null, null);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			OrderDetails orderDetails = cursorToOrderDetails(cursor);
			od.add(orderDetails);
			cursor.moveToNext();
		}
		return od;
	}
	
	
	/** Delete the ordered items **/
	public void delOrderItem(){
		db.delete(MySQLiteHelper.TBL_ORDER_ITEMS, MySQLiteHelper.FLD_UID +" = "+ uid, null);
		delOrderDetails();
	}
	
	private OrderItems cursorToOrderItems(Cursor cursor){
		OrderItems oi = new OrderItems();
		oi.setId(cursor.getLong(0)); // 0 for id;
		oi.setUid(cursor.getInt(1)); // 1 for user id
		oi.setImgName(cursor.getString(2)); // 2 for name;
		return oi;		
	}
	
	public OrderDetails insert(long orderItemId, String size, int qty, String mugImg){
		ContentValues valOD = new ContentValues();
		valOD.put(MySQLiteHelper.FLD_ORDER_ITEM, orderItemId);
		valOD.put(MySQLiteHelper.FLD_UID,uid);
		valOD.put(MySQLiteHelper.FLD_SIZE, size);
		valOD.put(MySQLiteHelper.FLD_QTY, qty);
		valOD.put(MySQLiteHelper.FLD_MUGPRINT_IMG, mugImg);
		long insertId = db.insert(MySQLiteHelper.TBL_ORDER_DETAILS, null, valOD);
		
		Cursor cursor = db.query(MySQLiteHelper.TBL_ORDER_DETAILS,
						tblOrderDetailsCols,
						MySQLiteHelper.COLUMN_ODID +" = "+ insertId,
						null, null, null, null);
		cursor.moveToFirst();
		OrderDetails orderDetails = cursorToOrderDetails(cursor);
		cursor.close();
		return orderDetails;
	}
	
	public OrderDetails cursorToOrderDetails(Cursor cursor){
		OrderDetails od = new OrderDetails();
		od.setId(cursor.getInt(0));
		od.setUid(cursor.getInt(1)); // 1 for user id
		od.setOrderItemId(cursor.getInt(2));
		od.setSize(cursor.getString(3));
		od.setQty(cursor.getInt(4));
		od.setMugImg(cursor.getString(5));
		return od;
	}
	
	public ArrayList<OrderDetails> getAllOrderDetails(int uid){
		ArrayList<OrderDetails> od = new ArrayList<OrderDetails>();
		String where = "user_id = "+uid;
		String sortBy = MySQLiteHelper.COLUMN_OIID + " DESC";
		Cursor cursor = db.query(MySQLiteHelper.TBL_ORDER_DETAILS, tblOrderDetailsCols, where, null, null, null, sortBy);
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			OrderDetails orderDetails = cursorToOrderDetails(cursor);
			od.add(orderDetails);
			cursor.moveToNext();
		}
		cursor.close();
		return od;
	}
	public void delOrderDetails(){
//		long id = od.getId();
		db.delete(MySQLiteHelper.TBL_ORDER_DETAILS, MySQLiteHelper.FLD_UID+" = "+ uid, null);
	}
	
	public void delOrderDetailsByOIID(long oiid){
		String where = MySQLiteHelper.FLD_UID+"="+uid+" and "+MySQLiteHelper.FLD_ORDER_ITEM+"="+oiid;
		db.delete(MySQLiteHelper.TBL_ORDER_DETAILS, where, null);
	}
	
//	public void updateImageInfo(int size, int qty, long id){
//		String where = "_id = "+id;
//		ContentValues cv = new ContentValues();
//		cv.put("fld_size", size);
//		cv.put("fld_qty", qty);
//		db.update(MySQLiteHelper.TBL_ORDER_DETAILS, cv, where, null);
//	}
	
	public void updateImageInfo(String size, String qty, long id, String dbSize){
		String where = "_id = "+id;
		ContentValues cv = new ContentValues();
		cv.put("fld_size", size);
		cv.put("fld_qty", qty);
		db.update(MySQLiteHelper.TBL_ORDER_DETAILS, cv, where, null);
	}
	
	public long getOrderItemIdByImage(String image){
		long id = 0;
		String where =  MySQLiteHelper.FLD_IMG +" = '"+image+"'";
		String sortBy = MySQLiteHelper.COLUMN_OIID + " DESC";
		Cursor cursor = db.query(MySQLiteHelper.TBL_ORDER_ITEMS,
				tblOrderItemsCols,
				where, null, null, null, sortBy);
		if(cursor.getCount() > 0) {
			cursor.moveToFirst();
			id = cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_OIID));
		}
		return id;
	}
	
}
