package jsoft.projects.photoprint_v1_1.libs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImageDecoder {

//	private File cacheDir;
//
//	public ImageDecoder(Context context) {
//		if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
//			cacheDir = new File(android.os.Environment.getExternalStorageDirectory(),"TempImages");
//		else
//			cacheDir = context.getCacheDir();
//		if(!cacheDir.exists())
//			cacheDir.mkdirs();
//	}
//	
//	public File getFile(String url){
//		String fileName = String.valueOf(url.hashCode());
//		System.out.println(fileName);
//		File f = new File(cacheDir, fileName);
//		return f;
//	}
//	
//	public void clear(){
//        File[] files=cacheDir.listFiles();
//        if(files==null)
//            return;
//        for(File f:files)
//            f.delete();
//    }
	
	public Bitmap decodeBitmapFile(File f){
    	try {
    	    //Decode image size
    	    BitmapFactory.Options o = new BitmapFactory.Options();
    	    o.inJustDecodeBounds = true;
    	    BitmapFactory.decodeStream(new FileInputStream(f),null,o);

    	    //The new size we want to scale to
    	    final int REQUIRED_SIZE=200;

    	    //Find the correct scale value. It should be the power of 2.
    	    int scale=1;
    	    while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
    	        scale*=2;

    	    //Decode with inSampleSize
    	    BitmapFactory.Options o2 = new BitmapFactory.Options();
    	    o2.inSampleSize=scale;
    	    return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    	} catch (FileNotFoundException e) {}
    	return null;
    }
	
	
	
	public Bitmap decodeBitmapFileFromUrl(InputStream inputStream){
		byte[] byteArr = new byte[0];
		byte[] buffer = new byte[1024];
		int len;
		int count = 0;
		try{
			while ((len = inputStream.read(buffer)) > -1){
				if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
			}
			final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

          //The new size we want to scale to
    	    final int REQUIRED_SIZE=200;

    	    //Find the correct scale value. It should be the power of 2.
    	    int scale=1;
    	    while(options.outWidth/scale/2>=REQUIRED_SIZE && options.outHeight/scale/2>=REQUIRED_SIZE)
    	        scale*=2;
            
            
            options.inSampleSize = calculateInSampleSize(options);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

//            int[] pids = { android.os.Process.myPid() };
//            MemoryInfo myMemInfo = mAM.getProcessMemoryInfo(pids)[0];
//            Log.e("Error Tag", "dalvikPss (decoding) = " + myMemInfo.dalvikPss);

            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > 200 || width > 200) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) 200);
	        final int widthRatio = Math.round((float) width / (float) 200);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }


	    return inSampleSize;
	}
	
}
