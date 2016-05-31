package BusinessLayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore.Files;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ImageSupporter 
{
	public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg", "png");
	public static final String DEFAULT_PICTUREPATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath();
	
	// Kiểm tra 1 file có là ảnh
	public static boolean isImage(File file)
	{
		// Lấy thông tin đuôi file (extension)
		String fileName = file.getName();
		String ext = fileName.substring((fileName.lastIndexOf(".") + 1), fileName.length());
		
		// Kiểm tra có trùng với các đuôi ảnh chương trình cho phép không
		return FILE_EXTN.contains(ext.toLowerCase());
	}
	
	// Chuyển đổi dip to pixel
    public static float convertDipToPixels(Context context, float dipValue) 
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    
	// Tính toán kích cỡ hợp lý với kích cỡ thể hiện
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) 
	    {	
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) 
	        {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
	}
	
	// Chuyễn hóa thành bitmap có kích cỡ phù hợp đối với tập tin
	public static Bitmap decodeSampledBitmapFromFile(File file, int reqWidth, int reqHeight) 
	{
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(file.getAbsolutePath(), options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
	}
	
	// Chuyễn hóa thành bitmap có kích cỡ phù hợp với tài nguyên
	public static Bitmap decodeSampledBitmapFromResource(Resources resource, int resourceId, int reqWidth, int reqHeight) 
	{
	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeResource(resource, resourceId, options);

	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeResource(resource, resourceId, options);
	}

	// Di chuyển tập tin 
	public static boolean moveFile(String inputPath, String inputFile, String outputPath) 
	{
		if (!ImageSupporter.copyFile(inputPath, inputFile, outputPath))
			return false;
		
        // Xóa file gốc
        File file = new File(inputPath + File.separator + inputFile);
        file.delete();
        
        return true;
	}

	// Xóa cả thư mục và tập tin con bên trong
	public static void deleteWholeFolder(String path)
	{
		File folder = new File(path);
		
		// Kiểm tra có tồn tại folder đó không
		if (folder.exists()) 
		{
			File[] files = folder.listFiles();
			
			// Xóa tất cả file trong thư mục
		    if (files != null)	    
		    	for (File f : files) 
		    		f.delete();
			
		    // Xóa thư mục
		    folder.delete();
		}
	}

	// Đổi tên 1 tập tin
	public static boolean renameFile(String path, String newName)
	{		
		File oldFile = new File(path);
		
		if (!oldFile.exists())
			return false;
		
        File newFile = new File(oldFile.getParent(), newName);
        
        if (newFile.exists())
        	return false;
        
        oldFile.renameTo(newFile);
        return true;
	}

	// Tạo 1 thư mục
	public static boolean createFolder(String path, String name)
	{
		File folder = new File(path + File.separator + name);
		
		// Kiểm tra có tồn tại folder đó không
		if (!folder.exists()) 
			return folder.mkdir();
		
		return false;
	}

	// Di chuyển tập tin 
	public static boolean copyFile(String inputPath, String inputFile, String outputPath) 
	{
	    InputStream in = null;
	    OutputStream out = null;
	    
	    try {

	        // Nếu chưa có, thì tạo thư mục tới
	        File dir = new File (outputPath); 
	        if (!dir.exists())
	            dir.mkdirs();

	        // Khởi tạo file để đọc & ghi
	        in = new FileInputStream(inputPath + File.separator + inputFile);        
	        out = new FileOutputStream(outputPath + File.separator + inputFile);

	        byte[] buffer = new byte[1024];
	        int read;
	        
	        // Đọc và ghi cho tới hết
	        while ((read = in.read(buffer)) != -1) 
	            out.write(buffer, 0, read);
	        
	        // Kết thúc đóng file
            out.flush();
	        out.close();        
	        in.close();
	        
	        return true;
	    } 
    	catch (FileNotFoundException fnfe1) 
	    {
	        Log.e("GalleryDS_copyFile", fnfe1.getMessage());
	        return false;
	    }
	    catch (Exception e) 
	    {
	        Log.e("GalleryDS_copyFile", e.getMessage());
	        return false;
	    }
	}
}
