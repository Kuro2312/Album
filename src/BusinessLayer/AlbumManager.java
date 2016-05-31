package BusinessLayer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class AlbumManager {

	protected HashMap<String, ArrayList<String>> _albumData;
	protected Context _context;
		
	public AlbumManager(Context context)
	{
		_context = context;
		initializesData();
	}
	
	// Khởi tạo dữ liệu
	public void initializesData()
	{
		_albumData = new HashMap<String, ArrayList<String>>();
		
		ArrayList<String> albumList = AlbumManager.getsAlbumPaths(_context);
		int n = albumList.size();
		
		for (int i = 0; i < n; i++)
			if (!_albumData.containsKey(albumList.get(i)))					
				_albumData.put(albumList.get(i), new ArrayList<String>());
	}
	
	// Lấy danh sách các album
    public ArrayList<String> getsAlbumList()
    {
    	return new ArrayList(_albumData.keySet()); 
    }
    
    // Kiểm tra có tồn tại album đó không 
    public boolean containsAlbum(String albumName)
    {
    	return _albumData.containsKey(albumName);
    }
    
    // Lấy dữ liệu danh sách ảnh của 1 Album
    public ArrayList<String> getsAlbumImages(String albumName) 
    {
    	if (_albumData.containsKey(albumName))
    		return _albumData.get(albumName);
    	else
    		return null;
	}

    // Lấy thông tin album
    protected static ArrayList<String> getsAlbumPaths(Context context)
	{
		ArrayList<String> result = null;
		
	    try 
	    {
	        InputStream inputStream = context.openFileInput("NeoGalleryDS_Album.txt");
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
	    	Log.e("NeoGalleryDS_Album", "File not found: " + e.toString());
	        return new ArrayList<String>();
	    } catch (IOException e) 
	    {
	    	Log.e("NeoGalleryDS_Album", "Can not read file: " + e.toString());
	        return new ArrayList<String>();
	    }
	}

	// Lưu thông tin album
	protected static boolean savesAlbumPaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("NeoGalleryDS_Album.txt", Context.MODE_PRIVATE);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();
			
			return true;
		}
		catch (Exception e)
		{
			Log.e("NeoGalleryDS_Album", e.getMessage());
	        return false;
		}
	}
		
	// Lưu thêm thông tin album
	protected static boolean addsNewAlbumPaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("NeoGalleryDS_Album.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();
			
			return true;
		}
		catch (Exception e)
		{
			Log.e("NeoGalleryDS_Album", e.getMessage());
	        return false;
		}
	}

    // Xóa toàn bộ ảnh trong album
    public boolean deletesAlbum(String name) 
    {
    	try
    	{
	    	// Thực hiện xóa album và toàn bộ ảnh trong album
	        ImageSupporter.deleteWholeFolder(ImageSupporter.DEFAULT_PICTUREPATH + File.separator + name);
	        
	        // Xóa album
	        _albumData.remove(name);
	      
	        // Lưu dữ liệu
	        AlbumManager.savesAlbumPaths(_context, this.getsAlbumList());
	        
	        return true;
	    }
        catch (Exception e)
    	{
    		return false;
    	}
    }
    
    // Đổi tên 1 Album
    public boolean renamesAlbum(String oldName, String newName) 
    {
    	try
    	{
	        // Đổi tên thư mục album
	        if (ImageSupporter.renameFile(ImageSupporter.DEFAULT_PICTUREPATH + File.separator + oldName, newName))
	        {
	        	// Thay đổi dữ liệu
	        	ArrayList<String> data = _albumData.remove(oldName);
	        	_albumData.put(newName, data);
			    
			    // Lưu dữ liệu trên tập tin dữ liệu
			    AlbumManager.savesAlbumPaths(_context, data);
			    
		        return true;
	        }
	        
	        return false;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }

    // Tạo mới Album
    public boolean createsAlbum(String name) 
    {   	
    	try
    	{
	        // Kiểm tra có tạo thư mục (Album) thành công không
	        if (ImageSupporter.createFolder(ImageSupporter.DEFAULT_PICTUREPATH, name))
	        {	    
	        	if (!_albumData.containsKey(name))
	        	{
		            ArrayList<String> data = new ArrayList<String>();
		            data.add(name);
		            
		            // Cập nhật adapter và giao diện
		            _albumData.put(name, new ArrayList<String>());
		            
		            // Cập nhật dữ liệu tập tin 
		            AlbumManager.addsNewAlbumPaths(_context, data);
		                       
		            return true;
	        	}
	        } 
	        
	        return false;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }
  
    // Bỏ ảnh khỏi album
    public boolean removesImageFromAlbum(String albumName, String imagePath)
    {   
    	ArrayList<String> albumImages = this.getsAlbumImages(albumName);
    	
    	if (albumImages == null)
    		return false;
    	
    	// Xóa dữ liệu khỏi album
    	if (!albumImages.remove(imagePath))
    		return false;
    	
        // Di chuyển file
    	File f = new File(imagePath); 
        return f.delete();
    }
    
    // Bỏ ảnh khỏi album
    public boolean removesImageFromAlbum(String imagePath)
    {   
    	File f = new File(imagePath); 
    	
    	String parent = f.getParentFile().getName();
    	ArrayList<String> albumImages = this.getsAlbumImages(parent);
    	
    	if (albumImages == null)
    		return false;
    	
    	// Xóa dữ liệu khỏi album
    	if (!albumImages.remove(imagePath))
    		return false;
    	
        // Xóa file
    	return f.delete();
    }
        
    // Thêm ảnh vào album
    public void addsImageToAlbum(String albumName, String imagePath)
    {	
    	File f = new File(imagePath);
    	File parent = f.getParentFile();
    	String albumPath = ImageSupporter.DEFAULT_PICTUREPATH + File.separator + albumName;
    	
    	// Kiểm tra xem ảnh có trong album chưa
    	if (!this.containsAlbum(albumName) || albumPath.equals(parent.getAbsolutePath()))
    		return;
		
		// Sao chép file
		ImageSupporter.copyFile(parent.getAbsolutePath(), f.getName(), albumPath, null);
		
		// Thêm vào album
    	this.getsAlbumImages(albumName).add(imagePath);
    }
    
    // Kiểm tra xem 1 ảnh có nằm trong album 2 yếu tố
    // Tên thư mục chứa ảnh có là tên album ?
    // Đường dẫn thư mục của thư mục chứa ảnh phải đường dẫn tới mục Picture mặc định của thiết bị
    public boolean containsImage(String imagePath)
    {
    	File f = new File(imagePath);
    	File parent = f.getParentFile();

    	return parent.getParent().equals(ImageSupporter.DEFAULT_PICTUREPATH) && this.containsAlbum(parent.getName());
    }
    
    // Thêm thông tin ảnh vào
    public boolean addImage(String imagePath)
    {
    	// Kiểm tra có phải ảnh album
    	if (!containsImage(imagePath))
    		return false;
    	
    	File f = new File(imagePath);
    	String parent = f.getParentFile().getName();
    	
    	// Thêm vào dữ liệu
    	this.getsAlbumImages(parent).add(imagePath);
    		
    	return true;
    }
    
    // Xoá nhiều ảnh
    public boolean deletesImages(ArrayList<String> imagePaths) 
    {
    	for (String path : imagePaths)
    		if(!removesImageFromAlbum(path))
    			return false;

    	return true;
    }
}
