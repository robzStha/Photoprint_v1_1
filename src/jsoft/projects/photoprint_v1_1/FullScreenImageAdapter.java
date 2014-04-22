package jsoft.projects.photoprint_v1_1;


import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.cart.OrderDetails;
import jsoft.projects.photoprint_v1_1.cart.OrderManager;
import jsoft.projects.photoprint_v1_1.libs.ImageDecoder;
import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
 
public class FullScreenImageAdapter extends PagerAdapter {
 
    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private ArrayList<String> sizes;
    public ArrayList<Integer> positions = new ArrayList<Integer>();
    public int uid;
    SessionMngr sess;
//    private boolean flagOd= false;
    
    public ArrayList<Integer> sizesIndex = new ArrayList<Integer>();
    public ArrayList<Integer> qty = new ArrayList<Integer>();
    
    // constructor
    public FullScreenImageAdapter(Activity activity,
            ArrayList<String> imagePaths) {
        this._activity = activity;
        _activity.getApplicationContext();
        this._imagePaths = imagePaths;
        
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
        
        OrderManager om = new OrderManager(_activity);
        om.open();
        ArrayList<OrderDetails> od = om.getAllOrderDetails(uid);
        om.close();
        
        //for image sizes and qty
        int temp = _imagePaths.size();
        
        for(int x = 0; x<_imagePaths.size();x++){
//        	System.out.println("Sizes: "+Integer.toString(od.size())+" "+Integer.toString(temp));
        	if(od.size()-temp>=0){
//        		System.out.println("I am in "+temp);
	        	sizesIndex.add(od.get(x).getSize());
	        	qty.add(od.get(x).getQty());
        	}else{
        		sizesIndex.add(0);
        		qty.add(1);
        	}
        	temp--;
    	}
        
    }
 
    @Override
    public int getCount() {
        return this._imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((TableLayout) object);
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        ImageView imgDisplay;
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
        
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this._activity,
	    		android.R.layout.simple_spinner_item, sizes);
        
        final Spinner mSpinner = (Spinner) viewLayout.findViewById(R.id.sizes);
        EditText etQty = (EditText) viewLayout.findViewById(R.id.etQty);
        TextView tvCurrentStatus = (TextView) viewLayout.findViewById(R.id.tvCurrentState);
        
        tvCurrentStatus.setText(Integer.toString(position+1)+" out of "+ Integer.toString(_imagePaths.size())+" image/s");
        
        etQty.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if(s.length()>0){
					qty.set(position, Integer.parseInt(s.toString()));
				}
			}
		});
        
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mSpinner.setAdapter(adapter);
	    
    	mSpinner.setSelection(sizesIndex.get(position));
	    etQty.setText(Integer.toString(qty.get(position)));
	    
	    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
	        	sizesIndex.set(position, i);
	        }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	    
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy); 
	    ImageDecoder id = new ImageDecoder();
		
	    Bitmap bitmap=null;
	    
	    if(isUrl(_imagePaths.get(position))){
	    	URL url;
			try {
				url = new URL(_imagePaths.get(position));
//				File f = fileCache.getFile(_imagePaths.get(position));
//				bitmap = id.decodeBitmapFile(f);
				bitmap = id.decodeBitmapFileFromUrl(url.openConnection().getInputStream());
//				bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    }else{
//	    	BitmapFactory.Options options = new BitmapFactory.Options();
//	        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
	    	File f = new File(_imagePaths.get(position));
	        bitmap = id.decodeBitmapFile(f);
//	        bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
	    }
        
        imgDisplay.setImageBitmap(bitmap);

        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((TableLayout) object);
  
    }
    
    public boolean isUrl(String imgPath){
    	boolean flag = true;
    	URL url = null;
    	try {
			url = new URL(imgPath);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	if(url == null){
    		flag = false;
    	}
    	return flag;
    }
}