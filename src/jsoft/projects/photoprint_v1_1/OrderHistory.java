package jsoft.projects.photoprint_v1_1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jsoft.projects.photoprint_v1_1.adapters.OrderHistoryAdapter;
import jsoft.projects.photoprint_v1_1.libs.CallAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class OrderHistory extends Activity{
	
	private static final String TAG_DATE = "fld_date";
	private static final String TAG_INVOICE = "fld_invoice";
	private static final String TAG_IMG_QTY= "fld_img_qty";
	private static final String TAG_PRICE= "fld_total_price";
	
	String[] from;
	int[] to;
	
	ListView list;
	OrderHistoryAdapter adapter;
	
	int uid;
	private static final String BASE_URL = "http://www.jhamel.com/print/";
	private static String ohURL = BASE_URL+"restapi/orderHistory/";
	ArrayList<HashMap<String, ArrayList<String>>> images = new ArrayList<HashMap<String,ArrayList<String>>>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_history_listview);
		
		Intent i = getIntent();
		uid = i.getIntExtra("uid", 0);
		
		String url = ohURL+Integer.toString(uid);
		System.out.println("URL=>"+url);
		CallAPI callApi = new CallAPI(this);
		callApi.execute(url);
	}
	
	public void SetLayout(JSONObject result){
		
		OrderHistoryData data = new OrderHistoryData();
		from = new String[] { TAG_INVOICE, TAG_DATE, TAG_IMG_QTY, TAG_PRICE };
		to = new int[] {R.id.invoice, R.id.date, R.id.imgQty, R.id.price};
		
		ArrayList<HashMap<String, String>> infoList = new ArrayList<HashMap<String,String>>();
		if(result !=null){
			@SuppressWarnings("unchecked")
			Iterator<String> it = result.keys();
			int index = 0;
			while(it.hasNext()){
				String key = it.next();
				try{
					if(result.get(key) instanceof JSONArray){
						JSONArray ja = result.getJSONArray(key);
						int size = ja.length();
						for(int i = 0; i<size;i++){
							data = parseJson(ja.getJSONObject(i));
						}
					}else if(result.get(key) instanceof JSONObject){
						data = parseJson(result.getJSONObject(key));
						
						ArrayList<String> imgList = new ArrayList<String>(); 
						imgList = imageParser(result.getJSONObject(key).getJSONObject("fld_orders"));

						HashMap<String, ArrayList<String>> imgMap = new HashMap<String, ArrayList<String>>();
						imgMap.put(Integer.toString(index), imgList);
						images.add(imgMap);
						index++;
						HashMap<String, String> map = new HashMap<String, String>();
						
						map.put(TAG_INVOICE, data.getInvoice());
						map.put(TAG_DATE, data.getDate());
						map.put(TAG_IMG_QTY, data.getImgQty());
						map.put(TAG_PRICE, data.getPrice());
						
						infoList.add(map);
					}else{
						System.out.println(key+" : "+ result.getString(key));
					}
				}catch(Throwable e){
					try{
						System.out.println(key+" : "+ result.getString(key));
					}catch (Exception ee){
						e.printStackTrace();
					}
				}
			}
		}
		list = (ListView)findViewById(R.id.lvOrderHistory);
		
		adapter = new OrderHistoryAdapter(this, infoList);
		list.setAdapter(adapter);
		
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				Log.d("images of "+position, images.get(position).toString());
				ArrayList<String> orderedItems = images.get(position).get(Integer.toString(position));
				Log.d("ArrayList of images ", orderedItems.toString());
				
				Intent i = new Intent(OrderHistory.this, Details.class);
				i.putExtra("orderedItems", orderedItems);
				startActivity(i);
				
			}
			
		});
	}
	
	public OrderHistoryData parseJson(JSONObject jsonObj){

		OrderHistoryData ohd = new OrderHistoryData();
		
		try {
			String invoice = jsonObj.getString(TAG_INVOICE);
			String date = jsonObj.getString(TAG_DATE);
			String price = "100";
			String imgQty= Integer.toString(jsonObj.getJSONObject("fld_orders").length());
			
//			Log.d("Images: : : ", jsonObj.getJSONObject("fld_orders").toString());
			ohd.setDate(date);
			ohd.setImgQty(imgQty);
			ohd.setInvoice(invoice);
			ohd.setPrice(price);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ohd;
	}
	
	public ArrayList<String> imageParser(JSONObject images){
		ArrayList<String> urls = new ArrayList<String>();
		if(images!=null){
			@SuppressWarnings("unchecked")
			Iterator<String> it = images.keys();
			while(it.hasNext()){
				String key = it.next();
				try {
					JSONObject jo = images.getJSONObject(key);
					urls.add(BASE_URL+jo.get("fld_loc")+""+jo.get("fld_name"));
//					System.out.println(jo.get("fld_loc")+""+jo.get("fld_name"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return urls;
	}

	
}
