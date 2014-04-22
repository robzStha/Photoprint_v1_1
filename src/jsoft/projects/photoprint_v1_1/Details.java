package jsoft.projects.photoprint_v1_1;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jsoft.projects.photoprint_v1_1.cart.OrderManager;
import jsoft.projects.photoprint_v1_1.cart.ShoppingCart;
import jsoft.projects.photoprint_v1_1.libs.ConnectionMngr;
import jsoft.projects.photoprint_v1_1.libs.SessionMngr;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	private static ArrayList<String> selectedItems = null;
	private FullScreenImageAdapter adapter;
	private boolean cartFlag = true;
	private ViewPager viewPager;
	private boolean fbImgs=false;
	ConnectionMngr cm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		session = new SessionMngr(getApplicationContext());
		cart = new ShoppingCart(getApplicationContext());
		cm = new ConnectionMngr(getApplicationContext());
		uid = session.getIntValues("uid");
		
		selectedItems = new ArrayList<String>();
		
		Intent  intent = getIntent();
		
		fbImgs = intent.getBooleanExtra("fbImgs", false);
		
		@SuppressWarnings("unchecked")
		ArrayList<String> orderedItems = (ArrayList<String>) intent.getSerializableExtra("orderedItems");
		if(orderedItems !=null ){
			selectedItems = orderedItems;
			cartFlag = false;
		}else{
			selectedItems = cart.getCartImages();
		}
//		Log.d("Selected Items in detail Page: Line no 82", selectedItems.toString());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details);
		
		if(selectedItems.size()>1){
			Toast.makeText(getApplicationContext(), "Please slide to review your images.", Toast.LENGTH_LONG).show();
		}
		
		// 	If is in database add to the list;
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		adapter = new FullScreenImageAdapter(Details.this, selectedItems);
		
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(position);	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		if(cartFlag == true){
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.details_action_bar, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		Log.d("Selected Items at Line no 110 at Details", selectedItems.toString());
		
		nvpSizes = new ArrayList<NameValuePair>();
		for(int i=0;i<adapter.qty.size();i++){
			nvpSizes.add(new BasicNameValuePair(Integer.toString(adapter.qty.get(i)),
					Integer.toString(adapter.sizesIndex.get(i)+1))); //qty , sizeId
		}
		
		cart.updateImgInfo(adapter.sizesIndex, adapter.qty);
		
		switch(item.getItemId()){
			case R.id.action_check_out:
				
				if(!cm.isOnline()){
					Toast toast = Toast.makeText(getApplicationContext(), "Not connected to network", Toast.LENGTH_LONG);
					toast.setGravity(Gravity.TOP, 0, 150);
					toast.show();
					return false;
				}
				if(selectedItems.size()>0){
					dialog = ProgressDialog.show(this, "", "Please wait. Uploading file...",true);
					new Thread(new Runnable(){
						public void run(){
							
								UploadFile(selectedItems, nvpSizes);
						}
					}).start();
					}
				else{
					Toast.makeText(getApplicationContext(), "No items in cart please add some from above links.", Toast.LENGTH_LONG).show();
				}
				break;
			case R.id.action_add_more:
				selectedItems = null;
				Intent intent = new Intent(Details.this, Dashboard.class);
				
				startActivity(intent);
				break;
			case R.id.action_delete_cart:
				OrderManager om = new OrderManager(getApplicationContext());
				om.open();
				om.delOrderDetails();
				om.delOrderItem();
				om.close();
				Intent i = new Intent(Details.this, Dashboard.class);
				startActivity(i);
				break;
			default:
				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	

	protected int UploadFile(ArrayList<String> sourceFileUri, List<NameValuePair> nvpSizes){
		
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
								Toast.makeText(Details.this, msg, Toast.LENGTH_SHORT).show();
								
								ShoppingCart cart = new ShoppingCart(getApplicationContext());
								cart.deleteItems();
								if(fbImgs == true){
									DeleteFbImages();
								}
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
			if(checkForURLFile(file.toString())){
				return true;
			}
			
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
	
	public static void DeleteFbImages(){
		
		for(int i=0 ;i< selectedItems.size();i++){
			String delImg = Environment.getExternalStorageDirectory().toString()+"/fb_images/fb_image"+i+".jpg";
			DeleteFile(delImg);
		}
		
		File path = new File (Environment.getExternalStorageDirectory().toString()+"/fb_images");
		if(path.exists()){
			path.delete();
		}
		
	}
	
	public static void DeleteFile(String fileName){
		File file = new File(fileName);
		if(!file.exists()){
			System.out.println("not exists");
			return;
		}
		if(!file.isDirectory()){
			System.out.println("Deleted");
			file.delete();
			return;
		}
	}
	
	public boolean checkForURLFile(String url){
			try {
			      HttpURLConnection.setFollowRedirects(false);
			      // note : you may also need
			      //        HttpURLConnection.setInstanceFollowRedirects(false)
			      HttpURLConnection con =
			         (HttpURLConnection) new URL(url).openConnection();
			      con.setRequestMethod("HEAD");
			      return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
			    }
			    catch (Exception e) {
			       e.printStackTrace();
			       return false;
			    }
			
		}
}