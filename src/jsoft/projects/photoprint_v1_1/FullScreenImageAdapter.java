package jsoft.projects.photoprint_v1_1;


import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
 
public class FullScreenImageAdapter extends PagerAdapter {
 
    private Activity _activity;
    private ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private ArrayList<String> sizes;
    public ArrayList<Integer> positions = new ArrayList<Integer>();
    
    
    // constructor
    public FullScreenImageAdapter(Activity activity,
            ArrayList<String> imagePaths) {
        this._activity = activity;
        _activity.getApplicationContext();
        this._imagePaths = imagePaths;
        
        sizes = new ArrayList<String>();
        sizes.add("3.25 x 4.5");
        sizes.add("3.5 x 5");
        sizes.add("3.5 x 8.1");
        sizes.add("3.9 × 5.8");
        sizes.add("4.7 × 9.3");
        sizes.add("5.8 × 7.9");
        sizes.add("4 x 6");
    }
 
    @Override
    public int getCount() {
        return this._imagePaths.size();
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
     
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

    	final int pos = position;
    	positions.add(position, position);
    	Log.d("Position Inside",positions.toString());
        ImageView imgDisplay;
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
  
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this._activity,
	    		android.R.layout.simple_spinner_item, sizes);
        
        final Spinner mSpinner = (Spinner) viewLayout.findViewById(R.id.sizes);
	    
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    mSpinner.setAdapter(adapter);
	    
	    mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
	        @Override
	        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
	            mSpinner.setSelection(i, true);
	            Log.d("selected values: ",mSpinner.getSelectedItem().toString());
	            Log.d("Position: ",Integer.toString(pos));
	        }

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	    
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(_imagePaths.get(position), options);
        imgDisplay.setImageBitmap(bitmap);

        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }
    
//    public OnItemSelectedListener updateCart(){
//    	return new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> parent, View view,
//					int position, long id) {
////				ShoppingCart cart = new ShoppingCart(context);
//				Toast.makeText(context, parent.getItemAtPosition(position).toString()+" Id: "+Long.toString(parent.getPositionForView(view)), Toast.LENGTH_SHORT).show();
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> parent) {
//				// TODO Auto-generated method stub
//				
//			}
//		};
//    }
}