package jsoft.projects.photoprint_v1_1;

import jsoft.projects.photoprint_v1_1.libs.ConnectionMngr;
import jsoft.projects.photoprint_v1_1.libs.UserFunctions;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UserRegistration extends Activity{
	
	Button btnSubmit;
	EditText etFullName, etUsername, etEmail, etPassword;
	TextView tvErrorMsg;
	String fullName, username, email, password;
	ConnectionMngr cm;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_registration);
		cm = new ConnectionMngr(getApplicationContext());
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		etFullName = (EditText) findViewById(R.id.etFullName);
		etUsername = (EditText) findViewById(R.id.etUsername);
		etEmail = (EditText) findViewById(R.id.etEmail);
		etPassword = (EditText) findViewById(R.id.etPassword);
		
		tvErrorMsg = (TextView) findViewById(R.id.tvErrorMsg);
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(!cm.isOnline()){
					Toast toast = Toast.makeText(getApplicationContext(), "Not connected to network", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 150);
					toast.show();
					return;
				}
				
				fullName = etFullName.getText().toString();
				username = etUsername.getText().toString();
				email = etEmail.getText().toString();
				password = etPassword.getText().toString();
				
				UserFunctions uf = new UserFunctions();
				JSONObject msg = uf.registerUser(fullName,  username,  email, password);
				try {
					if(Integer.parseInt(msg.getString("error"))==2){
						String errorMsg = msg.getString("error_msg");
						tvErrorMsg.setText(errorMsg);
					}else{
						finish();
						Intent i = new Intent (getApplicationContext(), MainActivity.class);
						startActivity(i);
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Registration Message", msg.toString());
			}
		});
	}

}
