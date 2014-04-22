package jsoft.projects.photoprint_v1_1.libs;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionMngr {

	private static Context _cntx;
	
	public ConnectionMngr(Context cntx){
		_cntx = cntx;
	}
	
//	public boolean hasConnection(){
//		
//		ConnectivityManager cm = (ConnectivityManager)_cntx.getSystemService(Context.CONNECTIVITY_SERVICE);
//		if(cm != null){
//			NetworkInfo[] info = cm.getAllNetworkInfo();
//			if(info != null){
//				for (int i = 0;i<info.length;i++){
//					if(info[i].getState() == NetworkInfo.State.CONNECTED){
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}
	
	public boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) _cntx.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if(networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}
}
