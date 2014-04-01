package jsoft.projects.photoprint_v1_1.cart;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper{
	
	public static final String TBL_ORDER_ITEMS = "tbl_order_items";
	public static final String COLUMN_OIID = "_id";
	public static final String FLD_UID = "user_id";
	public static final String FLD_IMG = "fld_img";
	
	public static final String TBL_ORDER_DETAILS = "tbl_order_details";
	public static final String COLUMN_ODID = "_id";
	public static final String FLD_ORDER_ITEM = "fld_orderitem";
	public static final String FLD_SIZE = "fld_size";
	public static final String FLD_QTY = "fld_qty";
	public static final String FLD_MUGPRINT_IMG = "fld_mugprint_img";
	
	private static final String DATABASE_NAME = "db_print.db";
	private static final int DATABASE_VERSION = 1;
	
	// database creation sql statement
	
	private static final String CREATE_TBL_ORDER_ITEMS = "create table "
			+ TBL_ORDER_ITEMS + "(" + COLUMN_OIID
			+ " integer primary key autoincrement, " + FLD_UID
			+ " integer not null, " + FLD_IMG
			+ " string not null)";
	
	public static final String CREATE_TBL_ORDER_DETAILS	= "create table "
			+ TBL_ORDER_DETAILS + "(" + COLUMN_ODID
			+ " integer primary key autoincrement, " + FLD_UID
			+ " integer not null, "  + FLD_ORDER_ITEM
			+ " integer not null, " + FLD_SIZE
			+ " integer not null, " + FLD_QTY
			+ " integer not null, " + FLD_MUGPRINT_IMG
			+ " string)";
	
	public MySQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}	

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TBL_ORDER_ITEMS);
		db.execSQL(CREATE_TBL_ORDER_DETAILS);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(), "Upgrading database from "+ oldVersion + " to "+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS "+ TBL_ORDER_ITEMS);
		db.execSQL("DROP TABLE IF EXISTS "+ TBL_ORDER_DETAILS);
		onCreate(db);
	}

}
