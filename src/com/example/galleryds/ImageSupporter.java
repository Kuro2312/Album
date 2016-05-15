package com.example.galleryds;

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
	
    // Bật chế độ chọn hình ảnh
    public static void turnOnSelectionMode(GridView gridView, ImageAdapter adapter) 
    {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) 
        {
            View view = ImageSupporter.getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }  
 
    // Tắt chế độ chọn hình ảnh
    public static void turnOffSelectionMode(GridView gridView, ImageAdapter adapter) 
    {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = ImageSupporter.getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.INVISIBLE);
            holder.checkbox.setChecked(false);
        }
    }
   
    public static float convertDipToPixels(Context context, float dipValue) 
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }
    
	// Tính toán kích cỡ hợp lý với kích cỡ thể hiện
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) 
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
	
	// Chuyễn hóa thành bitmap có kích cỡ phù hợp
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
	
	// Đổi tên 1 tập tin
	public static boolean renameFile(File file, String newName)
	{		
        File to = new File(file.getParent(), newName);
        
        if (to.exists())
        	return false;
        
        file.renameTo(to);
        return true;
	}
	
	// Xóa 1 tập tin
	public static void deleteFile(File file)
	{
		file.delete();
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
	
	// Xóa cả thư mục và tập tin con bên trong
	public static void deleteWholeFolder(String path, String name)
	{
		File folder = new File(path + File.separator + name);
		
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
	
	// Di chuyển tập tin 
	public static boolean moveFile(String inputPath, String inputFile, String outputPath) 
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

	        // Xóa file gốc
	        File file = new File(inputPath + File.separator + inputFile);
	        file.delete();
	        
	        return true;
	    } 
    	catch (FileNotFoundException fnfe1) 
	    {
	        Log.e("GalleryDS_moveFile", fnfe1.getMessage());
	        return false;
	    }
	    catch (Exception e) 
	    {
	        Log.e("GalleryDS_moveFile", e.getMessage());
	        return false;
	    }
	}
	
	// Lấy View tại 1 vị trí i
	public static View getViewByPosition(int pos, GridView listView)
	{
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
        
        if (pos < firstListItemPosition || pos > lastListItemPosition) 
            return listView.getAdapter().getView(pos, null, listView);
        else 
            return listView.getChildAt(pos - firstListItemPosition);
    }
	
	// Thực hiện thêm 1 phần tử vào 1 mãng đã sắp xếp
	public static void binaryInsertByLastModifiedDate(ArrayList<DataHolder> array, DataHolder data)
    {
    	Date lastModDate = new Date(data.getLastModified());
    	
    	int right = array.size() - 1;
    	int left = 0;
    	
    	if (array.size() == 0)
    	{
    		array.add(data);
    		return;
    	}
    	
    	Date lastModDate1 = new Date(array.get(right).getLastModified());
    	if (lastModDate1.compareTo(lastModDate) <= 0)
    	{
    		array.add(data);
    		return;
    	}
    	
    	lastModDate1 = new Date(array.get(left).getLastModified());
    	if (lastModDate1.compareTo(lastModDate) >= 0)
    	{
    		array.add(0, data);
    		return;
    	}
    	
    	
    	while (left < right)
    	{
    		int median = (right + left) / 2;
    		
    		lastModDate1 = new Date(array.get(median).getLastModified());
    		
    		if (lastModDate1.compareTo(lastModDate) == 0)
    		{
    			array.add(median + 1, data);
    			return;
    		}
    		else if (lastModDate1.compareTo(lastModDate) < 0)
    			left = median + 1;
    		else
    			right = median - 1;
    	}
    	
    	array.add(left , data);
    }
	
	// Hỗ trợ load bitmap
	public static void loadBitmap(Context context, DataHolder data, ImageView imageView)
	{
		if (ImageSupporter.cancelPotentialWork(data, imageView))
		{
	        final BitmapLoadingTask task = new BitmapLoadingTask(imageView);
	        final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), data.getBitmap(), task);
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(data);
	    }
	}
	
	public static boolean cancelPotentialWork(DataHolder data, ImageView imageView) 
	{
	    final BitmapLoadingTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

	    if (bitmapWorkerTask != null) {
	        final DataHolder bitmapData = bitmapWorkerTask.getData();
	        // If bitmapData is not yet set or it differs from the new data
	        if (bitmapData == null || bitmapData != data) {
	            // Cancel previous task
	            bitmapWorkerTask.cancel(true);
	        } else {
	        	
	            // The same work is already in progress
	            return false;
	        }
	    }
	    
	    // No task associated with the ImageView, or an existing task was cancelled
	    return true;
	}
	
	private static BitmapLoadingTask getBitmapWorkerTask(ImageView imageView) 
	{
		if (imageView != null) 
	   	{
	       final Drawable drawable = imageView.getDrawable();
	       
	       if (drawable instanceof AsyncDrawable) 
	       {
	           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
	           return asyncDrawable.getBitmapWorkerTask();
	       }
	    }
	   
	    return null;
	}
}

class AsyncDrawable extends BitmapDrawable
{
    private final WeakReference<BitmapLoadingTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources data, Bitmap bitmap, BitmapLoadingTask bitmapWorkerTask) 
    {
        super(data, bitmap);
        bitmapWorkerTaskReference = new WeakReference<BitmapLoadingTask>(bitmapWorkerTask);
    }

    public BitmapLoadingTask getBitmapWorkerTask() 
    {
        return bitmapWorkerTaskReference.get();
    }
}
