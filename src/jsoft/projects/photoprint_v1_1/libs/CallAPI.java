package jsoft.projects.photoprint_v1_1.libs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import jsoft.projects.photoprint_v1_1.OrderHistory;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class CallAPI extends AsyncTask<String, String, JSONObject>{

	InputStream in = null;
	JSONObject jObj = null;
	String json = "";
	
	private OrderHistory _activity;
	// constructor
	public CallAPI(OrderHistory activity) {
		this._activity = activity;
	}
	
	@Override
	protected JSONObject doInBackground(String... params) {

		String urlString = params[0];
		
		try{
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream());
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					in, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			in.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}

		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);			
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		// return JSON String
		return jObj;
	}

	@Override
	protected void onPostExecute(JSONObject result) {
		super.onPostExecute(result);
		if(result != null)
			_activity.SetLayout(result);
	}
	
	

}
