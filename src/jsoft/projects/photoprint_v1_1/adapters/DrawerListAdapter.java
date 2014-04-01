package jsoft.projects.photoprint_v1_1.adapters;

import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerListAdapter extends BaseAdapter{

	private Context context;
	private ArrayList<DrawerItem> drawerItems;
	
	public DrawerListAdapter(Context context, ArrayList<DrawerItem> drawerItems){
		this.context = context;
		this.drawerItems = drawerItems;
	}
	
	@Override
	public int getCount() {
		return drawerItems.size();
	}

	@Override
	public Object getItem(int position) {
		return drawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView == null){
			LayoutInflater mInflater = (LayoutInflater) 
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.drawer_list_item, null);
		}
		
		ImageView ivIcon = (ImageView) convertView.findViewById(R.id.icon);
		TextView tvMenu = (TextView) convertView.findViewById(R.id.text1);
		TextView tvCount = (TextView) convertView.findViewById(R.id.counter);
		
		
		ivIcon.setImageResource(drawerItems.get(position).getIcon());
		tvMenu.setText(drawerItems.get(position).getMenu());
		
		if(drawerItems.get(position).getCounterVisibility()){
			tvCount.setText(drawerItems.get(position).getCount());
		}else{
			tvCount.setVisibility(View.GONE);
		}
		
		return convertView;
	}

}
