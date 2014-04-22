package jsoft.projects.photoprint_v1_1.adapters;

import java.util.ArrayList;
import java.util.HashMap;

import jsoft.projects.photoprint_v1_1.R;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderHistoryAdapter extends BaseAdapter{
	
	private Activity activity;
	private ArrayList<HashMap<String, String>> data;
	private static LayoutInflater inflater = null;
	
	public OrderHistoryAdapter(Activity a,
			ArrayList<HashMap<String, String>> d) {
		activity = a;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		data = d;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if(convertView == null){
			v = inflater.inflate(R.layout.order_history_specifications, null);
		}
		
		TextView invoice = (TextView)v.findViewById(R.id.invoice);
		TextView date = (TextView)v.findViewById(R.id.date);
		TextView price = (TextView)v.findViewById(R.id.price);
		TextView imgQty = (TextView)v.findViewById(R.id.imgQty);
		
		HashMap<String, String> info = new HashMap<String, String>();
		info = data.get(position);
		
		invoice.setText(Html.fromHtml("<b>Invoice: </b>"+info.get("fld_invoice")));
		date.setText(Html.fromHtml("<b>Date: </b>"+info.get("fld_date").substring(0,info.get("fld_date").length()-9)));
		price.setText(Html.fromHtml("<b>Price: </b>"+info.get("fld_total_price")));
		imgQty.setText(Html.fromHtml("<b>No of Images: </b>"+info.get("fld_img_qty")));
		
		return v;
	}
}
