package jsoft.projects.photoprint_v1_1;

import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.cart.ShoppingCart;
import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class MyGalleryFragment extends Fragment {
	
	public ImageLoader imageLoader = ImageLoader.getInstance();
	
	Context context;
	
	public MyGalleryFragment(){}
	
	public String imgPath = null;
//	String upLoadServerUri = "http://www.jhamel.com/print/ah_login_api/UploadToServer.php";
	
	SessionMngr session;
	
	String lineEnd = "\r\n";
	String twoHypens = "--";
	String boundary = "*****";
	
	TextView tvMulMsg;
	
	private ArrayList<String> imageUrls;
	private DisplayImageOptions options;
	private ImageAdapter imageAdapter;
		
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		context = container.getContext();
		
		View rootView = inflater.inflate(R.layout.ac_image_grid, container, false);
		
		final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
        final String orderBy = MediaStore.Images.Media.DATE_TAKEN;
        
        Cursor imagecursor = getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        		columns,
        		null,
        		null,
        		orderBy);
		
        this.imageUrls = new ArrayList<String>();
        
        for (int i = 0; i < imagecursor.getCount(); i++) {
            imagecursor.moveToPosition(i);
            int dataColumnIndex = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            imageUrls.add(imagecursor.getString(dataColumnIndex));
 
        }
 
        options = new DisplayImageOptions.Builder()
            .showStubImage(R.drawable.stub_image)
            .showImageForEmptyUri(R.drawable.image_for_empty_url)
            .cacheInMemory()
            .cacheOnDisc()
            .build();
        
        imageAdapter = new ImageAdapter(context, imageUrls);
        
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview);
        gridView.setAdapter(imageAdapter);
        
		return rootView;
	}
	
	@Override
	public void onStop() {
		imageLoader.stop();
		super.onStop();
	}

	public void btnChoosePhotosClick(View v){
		
		ArrayList<String> selectedItems = imageAdapter.getCheckedItems();
		ShoppingCart cart = new ShoppingCart(context);
		cart.addToCart(selectedItems);
		
		Intent iDetails = new Intent(context, Details.class);
		startActivity(iDetails);
		
		
//		dialog = ProgressDialog.show(context, "", "Uploading file...",true);
//		new Thread(new Runnable(){
//			public void run(){
//				UploadFile(selectedItems);
//			}
//		}).start();
		
	}
	
	public class ImageAdapter extends BaseAdapter{
		
		ArrayList<String> mList;
		LayoutInflater mInflater;
		Context mContext;
		SparseBooleanArray msparseBooleanArray;
		
		public ImageAdapter(Context context, ArrayList<String> imageList) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
			msparseBooleanArray = new SparseBooleanArray();
			mList = new ArrayList<String>();
			this.mList = imageList;
			imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		}
		
		public ArrayList<String> getCheckedItems(){
			ArrayList<String> mTempArray = new ArrayList<String>();
			
			for(int i=0;i<mList.size();i++){
				if(msparseBooleanArray.get(i)){
					mTempArray.add(mList.get(i));
				}
			}
			return mTempArray;
		}
		
		@Override
		public int getCount() {
			return imageUrls.size();
		}
		@Override
		public Object getItem(int position) { //argument is position
			return null;
		}
		@Override 
		public long getItemId(int position) { //argument is position
			return position;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.row_multiphoto_item, null);
			}
			
			CheckBox mCheckBox = (CheckBox) convertView.findViewById(R.id.checkBox1);
			
			final ImageView imageView =(ImageView) convertView.findViewById(R.id.imageView1);
			
			imageLoader.displayImage("file://"+imageUrls.get(position), imageView, options, new SimpleImageLoadingListener() {
	            @Override
	            public void onLoadingComplete(Bitmap loadedImage) {
	                Animation anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
	                imageView.setAnimation(anim);
	                anim.start();
	            }
	        });

	        mCheckBox.setTag(position);
	        mCheckBox.setChecked(msparseBooleanArray.get(position));
	        mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);

	        return convertView;
		}
		OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
			 
	        @Override
	        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	            msparseBooleanArray.put((Integer) buttonView.getTag(), isChecked);
	        }
	    };
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		Button button = (Button) view.findViewById(R.id.button1);
		button.setOnTouchListener(new OnTouchListener() {
			

			@Override
			public boolean onTouch(View view, MotionEvent event) {
				
				if(event.getAction() == MotionEvent.ACTION_UP){
					btnChoosePhotosClick(view);
				}
				return false;
			}
		});
		
	}		
	    public void setTitle(CharSequence title) {
	        String mTitle = title.toString();
	        getActivity().getActionBar().setTitle(mTitle);
	    }
	
}
