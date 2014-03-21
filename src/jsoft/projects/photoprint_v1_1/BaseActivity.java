package jsoft.projects.photoprint_v1_1;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
 
public abstract class BaseActivity extends Activity {
 
    public ImageLoader imageLoader = ImageLoader.getInstance();
 
}