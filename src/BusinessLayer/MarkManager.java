package BusinessLayer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;

public class MarkManager 
{
	protected HashMap<String, String> _markData;	
	protected Context _context;
	
	public MarkManager(Context context)
	{
		_context = context;
		initializesData();
	}
	
	// Khởi tạo dữ liệu
	public void initializesData()
	{
		_markData = new HashMap<String, String>();
				
		ArrayList<String> markedImages = AlbumManager.getsAlbumPaths(_context);
		int n = markedImages.size();
		
		for (int i = 0; i < n; i++)
			if (!_markData.containsKey(markedImages.get(i)))					
				_markData.put(markedImages.get(i), markedImages.get(i));
	}
		
	// Lấy danh sách ảnh đánh dấu
	public ArrayList<String> getsMarkedImages()
	{
		return new ArrayList<String>(_markData.keySet());
	}
	
	// Kiểm tra xem đã đánh dấu chưa
	public boolean isMarkImage(String imagePath)
	{
		return _markData.containsKey(imagePath);
	}
	
	// Đánh dấu ảnh
	public boolean marksImage(String imagePath)
	{
		if (_markData.containsKey(imagePath))
			return false;
		
		_markData.put(imagePath, imagePath);
		
		ArrayList<String> data = new ArrayList<String>();
		data.add(imagePath);
		MarkManager.addsNewMarkedImagePaths(_context, data);
		
		return true;
	}

	// Bỏ đánh dấu ảnh
	public boolean unmarksImage(String imagePath)
	{
		if (!_markData.containsKey(imagePath))
			return false;
		
		_markData.remove(imagePath);
		
		MarkManager.savesMarkedImagePaths(_context, getsMarkedImages());
		return true;
	}

	// Đánh dấu nhiều ảnh
	public boolean marksImages(ArrayList<String> imagePaths)
    {
    	for (String path : imagePaths)
    		marksImage(path);

    	return true;
    }
	
	// Bỏ đánh dấu nhiều ảnh
	public boolean unmarksImages(ArrayList<String> imagePaths)
    {
    	for (String path : imagePaths)
    		unmarksImage(path);

    	return true;
    }
	 
	// Lấy thông tin ảnh đánh dấu
	protected static ArrayList<String> getsMarkedImagePaths(Context context)
	{
		ArrayList<String> result = null;
		
	    try 
	    {
	        InputStream inputStream = context.openFileInput("NeoGalleryDS_Marks.txt");
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
	        return result;
	    }
	    catch (FileNotFoundException e) 
	    {
	    	Log.e("GalleryDS_Marks", "File not found: " + e.toString());
	        return new ArrayList<String>();
	    } catch (IOException e) 
	    {
	    	Log.e("GalleryDS_Marks", "Can not read file: " + e.toString());
	        return new ArrayList<String>();
	    }
	}

	// Lưu thông tin ảnh đánh dấu
	protected static boolean savesMarkedImagePaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("NeoGalleryDS_Marks.txt", Context.MODE_PRIVATE);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();	
			return true;
		}
		catch (Exception e)
		{
			Log.e("GalleryDS_Marks", e.getMessage());
	        return false;
		}
	}
	
	// Lưu thêm thông tin ảnh đánh dấu
	protected static boolean addsNewMarkedImagePaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("NeoGalleryDS_Marks.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();	
			return true;
		}
		catch (Exception e)
		{
			Log.e("GalleryDS_Marks", e.getMessage());
	        return false;
		}
	}
}
