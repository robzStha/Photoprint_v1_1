package jsoft.projects.photoprint_v1_1.libs;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jsoft.projects.photoprint_v1_1.Dashboard;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

public class ImageUploader {

	ArrayList<String> sourceFileUri = new ArrayList<String>();
	ArrayList<NameValuePair> nvpSizes = new ArrayList<NameValuePair>();
	Activity activity;
	private String upLoadServerUri;
	private ProgressDialog dialog = null;
	private int serverResponseCode = 0;
	public String imgPath = null;
	
	String lineEnd = "\r\n";
	String twoHypens = "--";
	String boundary = "*****";
	
	public ImageUploader(Activity activity, ArrayList<String> selectedImages,
			ArrayList<NameValuePair> nvpSizes,
			String upLoadServerUri) {
		super();
		this.activity = activity;
		this.sourceFileUri = selectedImages;
		this.nvpSizes = nvpSizes;
		this.upLoadServerUri = upLoadServerUri;
	}

	public ImageUploader(ArrayList<String> selectedImages) {
		super();
		this.sourceFileUri = selectedImages;
	}
	
	public int Upload(){
		String fileName;// = sourceFileUri.get(2).toString();
		
		HttpURLConnection conn = null;
		DataOutputStream dos = null;
		int bytesRead, bytesAvailable, bufferSize;
		byte[] buffer;
		int maxBufferSize = 1*1024*1024;
		File sourceFile = null;
		
			try{
				// Open a URL connection to the Servlet
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
				
				for(int i=0 ; i<sourceFileUri.size() ; i++){
					
					fileName = Environment.getExternalStorageDirectory().toString() + "/fb_images/fb_image"+i+".jpg";
					//fileName = new URL(sourceFileUri.get(i));
					
					sourceFile = new File(fileName);
					
					Log.d("File name:", sourceFile.toString());
					
//					if(!_ifIsFile(sourceFile)){
//						return 0;
//					}
					
					//fileName = sourceFile.toString();
					fileInputStream = new FileInputStream(sourceFile);
					//conn.setRequestProperty("uploaded_file[]", fileName);
				
					dos = new DataOutputStream(conn.getOutputStream());
					
					dos.writeBytes(twoHypens + boundary + lineEnd);
					dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file[]\";filename=\"" + fileName+"\""+lineEnd);
					
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
					activity.runOnUiThread(new Runnable(){
						public void run(){
							String msg = "File Upload Completed.";
							//tvMulMsg.setText(msg);
							Toast toast = Toast.makeText(activity.getApplicationContext(), msg, Toast.LENGTH_SHORT);
							toast.show();
							
							Intent i = new Intent(activity, Dashboard.class);
							activity.startActivity(i);
							
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
                 
                activity.runOnUiThread(new Runnable() {
                    public void run() {
//                    	activity.tvMulMsg.setText("MalformedURLException Exception : check script url.");
                        //Toast.makeText(Gallery.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
                    }
                });
                 
                Log.e("Upload file to server", "error: " + ex.getMessage(), ex); 
            } catch (Exception e) {
                 
            	dialog.dismiss(); 
                e.printStackTrace();
                 
                activity.runOnUiThread(new Runnable() {
                    public void run() {
//                        tvMulMsg.setText("Got Exception with server");
                        //Toast.makeText(getApplicationContext(), "Got Exception with server ", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e); 
            }
			dialog.dismiss();
//			DeleteFbImages();
		return serverResponseCode;
	}
	
	
}
