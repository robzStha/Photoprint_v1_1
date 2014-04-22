package jsoft.projects.photoprint_v1_1;

import jsoft.projects.photoprint_v1_1.libs.SessionMngr;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


public class MainActivity extends Activity {

	SessionMngr session;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session = new SessionMngr(getApplicationContext());
		
		if(session.IsLoggedIn()){
			openDashboard();
		}
		finish();
	}	
	
	public void openDashboard(){
		Intent dashboard = new Intent(getApplicationContext(), Dashboard.class);
		
		// Close all views before launching Dashboard
		dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		dashboard.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(dashboard);
	}

}