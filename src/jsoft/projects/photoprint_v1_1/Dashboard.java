package jsoft.projects.photoprint_v1_1;

/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.adapters.DrawerItem;
import jsoft.projects.photoprint_v1_1.adapters.DrawerListAdapter;
import jsoft.projects.photoprint_v1_1.cart.OrderManager;
import jsoft.projects.photoprint_v1_1.cart.ShoppingCart;
import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * This is the dashboard where user can view their details and get to menu lists
 */
@SuppressLint("DefaultLocale")
public class Dashboard extends Activity {
//	private ProgressDialog dialog = null;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    
    private DrawerListAdapter adapter;
    private ArrayList<DrawerItem> drawerItems;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] navList;
    private TypedArray mIcons;
    private int uid;

    OrderManager om;
    
    SessionMngr session;
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionMngr(getApplicationContext());
        
        uid = session.getIntValues("uid");
        
        setContentView(R.layout.dashboard);

        om = new OrderManager(getApplicationContext());
        
        mTitle = mDrawerTitle = getTitle();
        mIcons = getResources().obtainTypedArray(R.array.menu_icons);
        navList = getResources().getStringArray(R.array.menu_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        om.open();
        int totalCartItems = om.getAllOrderItems(uid).size();
        om.close();
        String user = session.getStringValues("userName");
        navList[2] = user.substring(0,1).toUpperCase()+user.substring(1);
        
        drawerItems = new ArrayList<DrawerItem>();
        
        for(int i = 0; i<navList.length; i++){
        	if(i!=4){
        		drawerItems.add(new DrawerItem(navList[i], mIcons.getResourceId(i, -1)));
        	}
        	else{
        		drawerItems.add(new DrawerItem(navList[i], mIcons.getResourceId(i, -1), true, Integer.toString(totalCartItems))); 
        	}
        }
        
        mIcons.recycle();
        
        adapter = new DrawerListAdapter(getApplicationContext(), drawerItems);
        mDrawerList.setAdapter(adapter);
        
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_websearch:
            // create intent to perform web search for this planet
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
            // catch event that there's no activity to handle intent
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
    	
    	Fragment fragment = null;
    	
    	switch(position){
    		case 0:  	// MyGallery
    			fragment = new MyGalleryFragment();
    			break;
    		case 1:		// Facebook
    			fragment = new MyGalleryFragment();
    			break;
    		case 2:		// User
    			fragment = new MyGalleryFragment();
    			break;
    		case 3:		// Order History
    			fragment = new MyGalleryFragment();
    			break;
    		case 4:		// Cart
    			showCart();
    			break;
    		case 5:		// Logout
    			fragment = new MyGalleryFragment();
    			break;
    			
			default:
				break;
    	}
    	

    	if(fragment != null){
    		FragmentManager fragmentManager = getFragmentManager();
    		fragmentManager.beginTransaction()
    						.replace(R.id.content_frame, fragment)
    						.commit();
    		
    		//mDrawerList. navList[position]
    		
    		mDrawerList.setItemChecked(position, true);
    		mDrawerList.setSelection(position);
    		setTitle(navList[position]);
    		mDrawerLayout.closeDrawer(mDrawerList);
    	}
    	

//    	Toast.makeText(getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
    	
    }

    
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }   
    
    private void showCart(){    	
    	ArrayList<String> selectedItems = new ArrayList<String>();
    	ShoppingCart cart = new ShoppingCart(getApplicationContext());
    	selectedItems = cart.getCartImages();
    	Log.d("selected Images",selectedItems.toString());
    	
    	Intent i = new Intent(this, Details.class);
		startActivity(i);
    }
    
}