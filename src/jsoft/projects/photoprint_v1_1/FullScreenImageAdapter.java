package jsoft.projects.photoprint_v1_1;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import jsoft.projects.photoprint_v1_1.cart.OrderDetails;
import jsoft.projects.photoprint_v1_1.cart.OrderManager;
import jsoft.projects.photoprint_v1_1.libs.ImageDecoder;
import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
 
public class FullScreenImageAdapter extends PagerAdapter {
 
    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private ArrayList<String> sizes;
    public ArrayList<Integer> positions = new ArrayList<Integer>();
    public int uid;
    SessionMngr sess;
    SparseBooleanArray msparseBooleanArray;
    int currentPosition;
//    private boolean flagOd= false;
    
    public ArrayList<String> sizesIndex = new ArrayList<String>();
    public ArrayList<Integer> qty = new ArrayList<Integer>();
    EditText etQty;
    ArrayList<EditText> etQtyAll = new ArrayList<EditText>();
    
    public ArrayList<String> price;

    ArrayList<String> checked = new ArrayList<String>();
    HashMap<String, String> checkedQty = new HashMap<String, String>();
    
    // constructor
    public FullScreenImageAdapter(Activity activity,
            ArrayList<String> imagePaths) {
        this._activity = activity;
        _activity.getApplicationContext();
        this._imagePaths = imagePaths;
        msparseBooleanArray = new SparseBooleanArray();
        sess = new SessionMngr(_activity);
        uid = sess.getIntValues("uid");
        
        sizes = new ArrayList<String>();
        sizes.add("3.25 x 4.5");
        sizes.add("3.5 x 5");
        sizes.add("3.5 x 8.1");
        sizes.add("3.9 × 5.8");
        sizes.add("4.7 × 9.3");
        sizes.add("5.8 × 7.9");
        sizes.add("4 x 6");
        
        price = new ArrayList<String>();
        price.add("5");
        price.add("10");
        price.add("15");
        price.add("20");
        price.add("25");
        price.add("30");
        price.add("35");
        
        setDefaultChecked();
        
    }
    
    private void setDefaultChecked(){
    	for(int i = 0;i<_imagePaths.size(); i++){
    		OrderManager om = new OrderManager(_activity);
        	om.open();
        	long oiid = om.getOrderItemIdByImage(_imagePaths.get(i));
        	ArrayList<OrderDetails> od = om.getAllOrderDetails(uid);
        	om.close();
        	
        	if(od.size()>0){
        		for(int j=0;j<od.size();j++){
        			if(!checked.contains(od.get(j).getSize())){
        				checked.add(od.get(j).getSize());
        			}
            		checkedQty.put("et_"+od.get(j).getSize(), Integer.toString(od.get(j).getQty()));
        		}
        	}else{
    		// to check all the first value of checkbox
				checked.add(Long.toString(oiid)+"_0");
				checkedQty.put("et_"+oiid+"_0", "1");
        	}
        	Log.d("Database value", checked.toString());
        	Log.d("Database value", checkedQty.toString());
    	}
    }
 
    @Override
    public int getCount() {
        return this._imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ScrollView) object);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
    	
    	OrderManager om = new OrderManager(_activity);
    	om.open();
    	Long oiid = om.getOrderItemIdByImage(_imagePaths.get(position));
    	om.close();
    	
    	
        ImageView imgDisplay;
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);		
		
		TableLayout tl = (TableLayout)viewLayout.findViewById(R.id.layout_fullsceen_image);
		TableRow th = new TableRow(_activity);
		
		TextView empty = new TextView(_activity);
		TextView thSize= new TextView(_activity);
		TextView thQty = new TextView(_activity);
		TextView thPrice = new TextView(_activity);
		
		empty.setTag("tvChbxs");
		empty.setText("");
		empty.setPadding(5, 0, 5, 0);
		
		thSize.setTag("tvSizes");
		thSize.setText("Sizes");
		thSize.setTypeface(null,Typeface.BOLD);
		thSize.setPadding(5, 0, 5, 0);
		
		thQty.setTag("tvQty");
		thQty.setText("Quantities");
		thQty.setTypeface(null,Typeface.BOLD);
		thQty.setPadding(5, 0, 5, 0);
		
		thPrice.setTag("tvPrices");
		thPrice.setText("Price/piece");
		thPrice.setTypeface(null,Typeface.BOLD);
		thPrice.setPadding(5, 0, 5, 0);
		
		th.addView(empty);
		th.addView(thSize);
		th.addView(thQty);
		th.addView(thPrice);
		
		tl.addView(th);
		
		for(int i=0;i<sizes.size();i++){
			TableRow tr = new TableRow(_activity);
			
			CheckBox cb = new CheckBox(_activity);
			cb.setTag(oiid+"_"+i);
			cb.setOnCheckedChangeListener(mCheckedChangeListener);
			cb.setPadding(5, 0, 5, 0);
			
			TextView tv = new TextView(_activity);
			tv.setTag("tv_"+Integer.toString(i));
			tv.setPadding(5, 0, 5, 0);
			tv.setText(sizes.get(i));
			
			TextView tvPrice = new TextView(_activity);
			tvPrice.setTag("tvPrice_"+Integer.toString(i));
			tvPrice.setPadding(5, 0, 0, 0);
			tvPrice.setText(price.get(i));
			
			EditText et = new EditText(_activity);
			et.setTag("et_"+Long.toString(oiid)+"_"+Integer.toString(i));
			et.setInputType(InputType.TYPE_CLASS_NUMBER);
			et.setEnabled(false);
			et.setPadding(5, 0, 5, 0);
			String tag = Long.toString(oiid)+"_"+Integer.toString(i);
			et.setText(checkedQty.get(tag));
			if(checkedQty.get("et_"+tag) != null)
				et.setText(checkedQty.get("et_"+tag));
			else et.setText("1");
			tr.addView(cb);
			tr.addView(tv);
			tr.addView(et);
			tr.addView(tvPrice);
			
			tl.addView(tr);
			
		}
		
		
		// Set the checkbox is if already checked earlier.
		checkFromList(oiid, viewLayout);
		etChangeListner(oiid, viewLayout);
		
		
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this._activity,
	    		android.R.layout.simple_spinner_item, sizes);
        
        TextView tvCurrentStatus = (TextView) viewLayout.findViewById(R.id.tvCurrentState);
        
        tvCurrentStatus.setText(Integer.toString(position+1)+" out of "+ Integer.toString(_imagePaths.size())+" image/s");
        
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
	    ImageDecoder id = new ImageDecoder();
		
	    Bitmap bitmap=null;
	    
	    if(isUrl(_imagePaths.get(position))){
	    	URL url;
			try {
				url = new URL(_imagePaths.get(position));
				bitmap = id.decodeBitmapFileFromUrl(url.openConnection().getInputStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} 
	    }else{
	    	File f = new File(_imagePaths.get(position));
	        bitmap = id.decodeBitmapFile(f);
	    }
        
        imgDisplay.setImageBitmap(bitmap);

        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ScrollView) object);
  
    }
    
    public boolean isUrl(String imgPath){
    	boolean flag = true;
    	URL url = null;
    	try {
			url = new URL(imgPath);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		}
    	if(url == null){
    		flag = false;
    	}
    	return flag;
    }

    OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener(){

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			String tag = buttonView.getTag().toString();
			int index = checkPresence(tag);
			if(isChecked){
				if(index==-1){
					if(!checked.contains(tag)){
						checked.add(tag);
					}
					enableEditText(_activity.getWindow().getDecorView(), tag);
				}
			}else{
				if(index!=-1){
					removeChecked(index);
					checkedQty.remove("et_"+tag);
					disableEditText(_activity.getWindow().getDecorView(), tag);
				}
			}

		}
    };

    public void checkFromList(long oiid, View view){
		if(checked!=null){
			for(int i = 0; i<checked.size(); i++){
				if(isProperTag(oiid, checked.get(i))){
					CheckBox chbx = (CheckBox) view.findViewWithTag(checked.get(i));
					chbx.setChecked(true);
					enableEditText(view, checked.get(i));
				}
			}
		}
		
    }
    
    private boolean isProperTag(long position, String value){
    	StringTokenizer tokens = new StringTokenizer(value, "_");
    	String viewPos = tokens.nextToken();
    	if(position == Integer.parseInt(viewPos)){
    		return true;
    	}
    	return false;
    }
    
    private int checkPresence(String value){

    	for(int i = 0; i < checked.size(); i++){
    		if(checked.get(i).equals(value)){
    			return i;
    		}
    	}
    	return -1;
    }
    
    private void removeChecked(int index){
    	checked.remove(index);
    }
    
    private void etChangeListner(long oiid, View view){
    	for(int i = 0; i<sizes.size(); i++){
    		etQty = (EditText) view.findViewWithTag("et_"+Long.toString(oiid)+"_"+Integer.toString(i));
    		etQty.addTextChangedListener(new EtTextWatcher(etQty));
    		etQtyAll.add(etQty);
    	}
    	
    }
    
    private class EtTextWatcher implements TextWatcher{

    	private EditText et;
    	
		public EtTextWatcher(EditText et) {
			this.et = et;
		}

		@Override
		public void afterTextChanged(Editable s) {
			String tag = et.getTag().toString();
			checkedQty.put(tag, s.toString());
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
    }
    
    private void enableEditText(View view, String value){
    	EditText et = (EditText)view.findViewWithTag("et_"+value);
    	et.setEnabled(true);
    }
    private void disableEditText(View view, String value){
    	EditText et = (EditText)view.findViewWithTag("et_"+value);
    	et.setText("1");
    	et.setEnabled(false);
    }
}