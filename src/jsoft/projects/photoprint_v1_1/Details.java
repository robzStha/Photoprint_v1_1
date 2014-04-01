package jsoft.projects.photoprint_v1_1;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jsoft.projects.photoprint_v1_1.cart.ShoppingCart;
import jsoft.projects.photoprint_v1_1.libs.ConnectionMngr;
import jsoft.projects.photoprint_v1_1.libs.SessionMngr;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

public class Details extends Activity{

	public ImageLoader imageLoader = ImageLoader.getInstance();
	private int uid = 0;
	private ProgressDialog dialog = null;
	private int serverResponseCode = 0;
	public String imgPath = null;
	
	String upLoadServerUri = "http://www.jhamel.com/print/ah_login_api/UploadToServer.php";
	
	SessionMngr session;
	ShoppingCart cart;
	
	String lineEnd = "\r\n";
	String twoHypens = "--";
	String boundary = "*****";
	
	TextView tvMulMsg;

	private List<NameValuePair> nvpSizes;
	private ArrayList<String> selectedItems = null;
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		session = new SessionMngr(getApplicationContext());
		cart = new ShoppingCart(getApplicationContext());
		
		uid = session.getIntValues("uid");
		
		selectedItems = new ArrayList<String>();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		// 	If is in database add to the list;
        selectedItems = cart.getCartImages();
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		adapter = new FullScreenImageAdapter(Details.this, selectedItems);
		Log.d("positions", adapter.positions.toString());
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(position);	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.details_action_bar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		nvpSizes = new ArrayList<NameValuePair>();
		for(int i=0;i<selectedItems.size();i++){
			nvpSizes.add(new BasicNameValuePair("1", "1")); //qty , sizeId
		}
		
		switch(item.getItemId()){
			case R.id.action_check_out:
				dialog = ProgressDialog.show(this, "", "Please wait. Uploading file...",true);
				new Thread(new Runnable(){
					public void run(){
						UploadFile(selectedItems, nvpSizes);
					}
				}).start();
				break;
			case R.id.action_add_more:
				Spinner mSpinner = (Spinner) findViewById(R.id.sizes);
//				mSpinner.getSelectedItem();
				System.out.println(mSpinner.getSelectedItemPosition());
				Intent intent = new Intent(Details.this, Dashboard.class);
				startActivity(intent);
				break;
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	

protected int UploadFile(ArrayList<String> sourceFileUri, List<NameValuePair> nvpSizes){
		
		ConnectionMngr cm = new ConnectionMngr(Details.this);
		boolean connection = cm.hasConnection();
		if(!connection){
			Toast.makeText(Details.this, "No Internet Connection. Please connect to the internet and try again", Toast.LENGTH_LONG).show();
			return 0;
		}
		
			String fileName;// = sourceFileUri.get(2).toString();
			
			HttpURLConnection conn = null;
			DataOutputStream dos = null;
			int bytesRead, bytesAvailable, bufferSize;
			byte[] buffer;
			int maxBufferSize = 1*1024*1024;
			File sourceFile = null;
				
				try{
					// Open a URL connection to the Servelet
					FileInputStream fileInputStream = null;
					URL url = new URL(upLoadServerUri);
					
					// Open a HTTP connection to the URL
					conn = (HttpURLConnection)url.openConnection();
					conn.setDoInput(true); // Allow Inputs
					conn.setDoOutput(true); // Allow Outputs
					conn.setUseCaches(false); // Don't use a Cached copy
					conn.setRequestMethod("POST"); //Sets connection method
					conn.setRequestProperty("Connection", "Keep-Alive");
					conn.setRequestProperty("ENCTYPE", "multipart/form-data");					
					conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

					dos = new DataOutputStream(conn.getOutputStream());
					dos.writeBytes(twoHypens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=\"uid\""+lineEnd);
					dos.writeBytes(lineEnd);
					dos.writeBytes(Integer.toString(uid));
					dos.writeBytes(lineEnd);
					
					for(int i=0 ; i<sourceFileUri.size() ; i++){
						
						sourceFile = new File(sourceFileUri.get(i));
						
						if(!_ifIsFile(sourceFile)){
							return 0;
						}
						
						fileName = sourceFile.toString();
						fileInputStream = new FileInputStream(sourceFile);
						//conn.setRequestProperty("uploaded_file[]", fileName);
					
						String qty = nvpSizes.get(i).getName().toString();
						String size = nvpSizes.get(i).getValue().toString();
						dos.writeBytes(twoHypens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"size[]\""+lineEnd);
						dos.writeBytes(lineEnd);
						dos.writeBytes(size);
						dos.writeBytes(lineEnd);
						dos.writeBytes(twoHypens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"qty[]\""+lineEnd);
						dos.writeBytes(lineEnd);
						dos.writeBytes(qty);
						dos.writeBytes(lineEnd);
						dos.writeBytes(twoHypens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; data-size=\""+size+"\";data-qty=\""+qty+"\"; name=\"uploaded_file[]\";filename=\"" + fileName+"\""+lineEnd);
						//dos.writeBytes("Content-Disposition: form-data; name=\"uid\";filename=\"" + "rabin"+"\""+lineEnd);
						dos.writeBytes(lineEnd);
						
						// create a buffer of maximum size
						bytesAvailable = fileInputStream.available();
						
						bufferSize = Math.min(bytesAvailable, maxBufferSize);
						buffer = new byte[bufferSize];
						
						// read file and write it into form
						bytesRead = fileInputStream.read(buffer, 0, bufferSize);
						
						while(bytesRead>0){
							dos.write(buffer, 0, bufferSize);
							bytesAvailable = fileInputStream.available();
							bufferSize = Math.min(bytesAvailable, maxBufferSize);
							bytesRead = fileInputStream.read(buffer, 0, bufferSize);
						}
						//dos.writeBytes(lineEnd);
						dos.writeBytes(twoHypens + boundary + lineEnd);
						dos.writeBytes("Content-Disposition: form-data; name=\"uid\";value=\"rabin shrestha\""+lineEnd);
						dos.writeBytes("Content-Type: text/plain; charset=US-ASCII" + lineEnd);
						dos.writeBytes("Content-Transfer-Encoding: 8bit" + lineEnd);
						
						dos.writeBytes(lineEnd);
					}
					// send multipart form data necessary after file data
					dos.writeBytes(twoHypens+boundary+twoHypens+lineEnd);
					
					// Responses from the server(code and message)
					serverResponseCode = conn.getResponseCode();
					String serverResponseMessage = conn.getResponseMessage();
					
					Log.i("uploadFile","HTTP Response is : "
	                        + serverResponseMessage + ": " + serverResponseCode);
					if(serverResponseCode == 200){ //if http response is created
						runOnUiThread(new Runnable(){
							public void run(){
								String msg = "File Upload Completed.";
								Toast toast = Toast.makeText(Details.this, msg, Toast.LENGTH_SHORT);
								toast.show();
								dialog.dismiss();
								
								Intent i = new Intent(Details.this, Dashboard.class);
								startActivity(i);								
							}
						});
					}
					//close the streams //
	                fileInputStream.close();
	                dos.flush();
	                dos.close();
				}
				catch (MalformedURLException ex) {
	                
	                dialog.dismiss(); 
	                ex.printStackTrace();
	                 
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                        tvMulMsg.setText("MalformedURLException Exception : check script url.");
	                        //Toast.makeText(Gallery.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
	                    }
	                });
	                 
	                Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
	            } catch (Exception e) {
	                 
	            	dialog.dismiss(); 
	                e.printStackTrace();
	                 
	                runOnUiThread(new Runnable() {
	                    public void run() {
	                        tvMulMsg.setText("Got Exception with server");
	                        //Toast.makeText(context, "Got Exception with server ", Toast.LENGTH_SHORT).show();
	                    }
	                });
	                Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e); 
	            }
				dialog.dismiss();      
			return serverResponseCode;
		}

		public boolean _ifIsFile(File file){
			if(!file.isFile()){
				dialog.dismiss();
				
				Log.e("uploadFile", "Source File not exist : " + imgPath);
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						tvMulMsg.setText("Source File not exist : " + imgPath);
					}
				});
				return false;
			}
			return true;
		}
	
}
