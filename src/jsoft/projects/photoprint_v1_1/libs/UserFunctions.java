
package jsoft.projects.photoprint_v1_1.libs;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.content.Context;

public class UserFunctions {
	
	private JSONParser jsonParser;
	private static final String BASE_URL = "http://www.jhamel.com/print/";
	
	private static String loginURL = BASE_URL+"ah_login_api/";
	private static String registerURL = BASE_URL+"ah_login_api/";
	
	private static String login_tag = "login";
	private static String register_tag = "register";
	
	// constructor
	public UserFunctions(){
		jsonParser = new JSONParser();
	}
	
	/**
	 * function make Login Request
	 * @param email
	 * @param password
	 * */
	public JSONObject loginUser(String email, String password){
		// Building Parameters
		
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("tag", login_tag));
		pairs.add(new BasicNameValuePair("email", email));
		pairs.add(new BasicNameValuePair("password", password));
		
		//Log.d("List", params.toString());
		JSONObject json = jsonParser.getJSONFromUrl(loginURL, pairs);
		// return json
//		Log.d("JSON", json.toString());
		return json;
	}
	
	/**
	 * function make Login Request
	 * @param name
	 * @param email
	 * @param password
	 * */
	public JSONObject registerUser(String fullName, String username, String email, String password){
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("tag", register_tag));
		params.add(new BasicNameValuePair("fullName", fullName));
		params.add(new BasicNameValuePair("username", username));
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("password", password));
		
		// getting JSON Object
		JSONObject json = jsonParser.getJSONFromUrl(registerURL, params);
		return json;
	}
	
	/**
	 * Function get Login status
	 * */
	public boolean isUserLoggedIn(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		int count = db.getRowCount();
		if(count > 0){
			// user logged in
			return true;
		}
		return false;
	}
	
	/**
	 * Function to logout user
	 * Reset Database
	 * */
	public boolean logoutUser(Context context){
		DatabaseHandler db = new DatabaseHandler(context);
		db.resetTables();
		return true;
	}
	
//	public JSONObject orderHistory(int uid){
//		ohURL = ohURL+"/"+Integer.toString(uid);
//		JSONObject json = jsonParser.getJSONFromUrl(ohURL);
//		System.out.println(json);
//		return json;
//	}
//	
}
