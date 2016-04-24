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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.Files;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class ImageSupporter 
{
    // Bật chế độ chọn hình ảnh
    public static void turnOnSelectionMode(GridView gridView, ImageAdapter adapter) 
    {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
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
	
	// Kiểm tra 1 file có là ảnh
	/*public static boolean isImage(File file)
	{
		try
		{
			Bitmap b = BitmapFactory.decodeFile(file.getAbsolutePath());
			
			if (b != null)
				return true;
			
			return false;
		}
		catch (Exception e)
		{
			return false;
		}		
	}*/
	
	public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg",
			"png");
	
	// Kiểm tra 1 file có là ảnh
		public static boolean isImage(File file)
		{
			String fileName = file.getName();
			String ext = fileName.substring((fileName.lastIndexOf(".") + 1),
					fileName.length());
			
			if (FILE_EXTN.contains(ext.toLowerCase()))
				return true;
			return false;
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
			
		    if (files != null)	    
		    	for (File f : files) 
		    		f.delete();
			
		    folder.delete();
		}
	}
	
	// Lấy thông tin ảnh ưa thích
	public static ArrayList<String> getFavouriteImagePaths(Context context)
	{
		ArrayList<String> result = null;
		
	    try 
	    {
	        InputStream inputStream = context.openFileInput("GalleryDS_Favourite.txt");
	        result = new ArrayList<String>();
	        
	        if ( inputStream != null )
	        {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";

	            while ((receiveString = bufferedReader.readLine()) != null) 
	                result.add(receiveString);

	            inputStream.close();
	        }
	    }
	    catch (FileNotFoundException e) {
	        //Log.e(TAG, "File not found: " + e.toString());
	    } catch (IOException e) {
	        //Log.e(TAG, "Can not read file: " + e.toString());
	    }

	    return result;
	}

	// Lưu thông tin ảnh ưa thích
	public static void saveFavouriteImagePaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("GalleryDS_Favourite.txt", Context.MODE_PRIVATE);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();		
		}
		catch (Exception e)
		{
			
		}
	}
	
	// Lưu thêm thông tin ảnh ưa thích
	public static void addNewFavouriteImagePaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("GalleryDS_Favourite.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();		
		}
		catch (Exception e)
		{
			
		}
	}
	
	// Lấy thông tin ảnh ưa thích
	public static ArrayList<String> getAlbumPaths(Context context)
	{
		ArrayList<String> result = null;
		
	    try 
	    {
	        InputStream inputStream = context.openFileInput("GalleryDS_Album.txt");
	        result = new ArrayList<String>();
	        
	        if ( inputStream != null )
	        {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";

	            while ((receiveString = bufferedReader.readLine()) != null) 
	                result.add(receiveString);

	            inputStream.close();
	        }
	    }
	    catch (FileNotFoundException e) {
	        //Log.e(TAG, "File not found: " + e.toString());
	    } catch (IOException e) {
	        //Log.e(TAG, "Can not read file: " + e.toString());
	    }

	    return result;
	}

	// Lưu thông tin ảnh ưa thích
	public static void saveAlbumPaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("GalleryDS_Album.txt", Context.MODE_PRIVATE);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();		
		}
		catch (Exception e)
		{
			
		}
	}
		
	// Lưu thêm thông tin ảnh ưa thích
	public static void addNewAlbumPaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("GalleryDS_Album.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();		
		}
		catch (Exception e)
		{
			
		}
	}
	
	// Test thử
	public static ArrayList<String> foo(Context context)
	{
		ArrayList<String> arr = new ArrayList<String>();
		arr.add("Kuro");
		arr.add("Shiro");
		arr.add("Hiraki");
		arr.add("Nova");
		
		saveFavouriteImagePaths(context, arr);
		
		return getFavouriteImagePaths(context);
	}
	
	// Di chuyển tập tin 
	public static void moveFile(String inputPath, String inputFile, String outputPath) 
	{
	    InputStream in = null;
	    OutputStream out = null;
	    
	    try {

	        // Nếu chưa có, thì tạo thư mục tới
	        File dir = new File (outputPath); 
	        if (!dir.exists())
	            dir.mkdirs();


	        in = new FileInputStream(inputPath + File.separator + inputFile);        
	        out = new FileOutputStream(outputPath + File.separator + inputFile);

	        byte[] buffer = new byte[1024];
	        int read;
	        
	        while ((read = in.read(buffer)) != -1) 
	            out.write(buffer, 0, read);
	        
            out.flush();
	        out.close();        
	        in.close();

	        // Xóa file gốc
	        File file = new File(inputPath + File.separator + inputFile);
	        file.delete();
	    } 

    	catch (FileNotFoundException fnfe1) 
	    {
	        //Log.e("tag", fnfe1.getMessage());
	    }
	    catch (Exception e) 
	    {
	        //Log.e("tag", e.getMessage());
	    }
	}
	
	public static View getViewByPosition(int pos, GridView listView)
	{
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) 
        {
            return listView.getAdapter().getView(pos, null, listView);
        } 
        else 
        {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }
}
