package com.example.galleryds;

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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class ImageManager {
	
	// Singleton
	protected static ImageManager _instance = null;
	public static int IMAGE_HEIGHT= 75;
	public static int IMAGE_WIDTH = 75;
	
	// Cho việc thao tác trên mục toàn bô ảnh
    protected GridView _gridViewAll;
    protected ArrayList<DataHolder> _allImageData;
    protected ImageAdapter _allImageAdapter;
    protected HashMap<String, DataHolder> _allMap;  
    
    // Cho việc thao tác trên mục yêu thích
    protected GridView _gridViewFavourite;
    protected ArrayList<DataHolder> _favouriteData;
    protected ImageAdapter _favouriteAdapter = null;
    protected HashMap<String, String> _favouriteMap = null;  		
    
    // Khởi tạo
    protected ImageManager(Context context, GridView gridView, GridView gridViewFavourite)
    {
    	_gridViewAll = gridView;
    	_gridViewFavourite = gridViewFavourite;
    	
    	_allImageData = new ArrayList<DataHolder>();
        _allMap = new HashMap<String, DataHolder>();
        
        _allImageAdapter = new ImageAdapter(context, _allImageData);
        _gridViewAll.setAdapter(_allImageAdapter);    	
        
        IMAGE_HEIGHT = (int) ImageSupporter.convertDipToPixels(context, IMAGE_HEIGHT);
		IMAGE_WIDTH = (int) ImageSupporter.convertDipToPixels(context, IMAGE_WIDTH);
    }
    
    public static ImageManager getInstance(Context context, GridView gridView, GridView gridViewFavourite)
    {
    	if (_instance == null)
    		_instance = new ImageManager(context, gridView, gridViewFavourite);
    	
    	return _instance;
    }
    
    public static ImageManager getInstance()
    {
    	return _instance;
    }
    
    public ArrayList<DataHolder> getAllImageData()
    {
    	return _allImageData;
    }
    
    // Lấy context
    protected Context getContext()
    {
    	return _allImageAdapter.getContext();
    }
    
    // Lấy GridView của tất cả ảnh
    public GridView getGridViewAll() 
    {
		return _gridViewAll;
	}
    
    // Lấy GridView của tất cả ảnh
    public GridView getGridViewFavourite() 
    {
		return _gridViewFavourite;
	}
   
    // Thêm dữ liệu của 1 ảnh
    public boolean addImageData(String filePath, DataHolder data)
    {
    	try
    	{
    		if (!_allMap.containsKey(filePath))
    			_allMap.put(filePath, data);
    		
    		return true;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }
    
    // Lấy dữ liệu ảnh
    public DataHolder getImageDataById(int id)
    {
    	return (DataHolder) _allImageAdapter.getItem(id);
    }
    
    // Lấy dữ liệu ảnh
    protected DataHolder getImageDataByName(String name)
    {
    	if (_allMap.containsKey(name))
    		return _allMap.get(name);
    	
    	return null;
    }
    
    // Lấy dữ liệu ảnh ưu thích
    public DataHolder getFavouriteImageByID(int id)
    {
    	return (DataHolder) _favouriteAdapter.getItem(id);
    }
    
    // Lấy số lượng ảnh
    public int getNumberOfImages()
    {
    	return _allImageData.size();
    }
    
    // Lấy số lượng ảnh ưa thích
    public int getNumberOfFavouriteImages()
    {
    	return _favouriteData.size();
    }

    // Xóa các ảnh được chọn
    public void deleteSelectedImages()
    {
        int count = _allImageAdapter.getCount();

        for (int i = count - 1; i >= 0; i--) 
        {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAll);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true)
            	this.deleteSelectedImage((DataHolder) _allImageAdapter.getItem(holder.id));
        }
    }
    
    // Xóa 1 ảnh được chọn
    public void deleteSelectedImage(DataHolder data)
    {
    	File f = new File(data.getFilePath());
    	
    	// Xóa trên adapter của mục All
    	_allImageAdapter.remove(data);
    	
    	// Xóa trên dữ liệu lưu dạng map
    	_allMap.remove(data.getFilePath());
    	
    	// Xóa trên dữ liệu mục Favourite
    	this.removeImageFromFavourite(data);
    	
    	// Xóa ảnh khỏi dữ liệu album
    	AlbumManager.getInstance().removeImageFromAlbum(data,  f.getParentFile().getName());
    	
    	// Xóa ảnh thực sự
    	ImageSupporter.deleteFile(f);
    }

    // Bật chế độ chọn ảnh
    public void turnOnAllImagesSelectionMode()
    {
    	ImageSupporter.turnOnSelectionMode(_gridViewAll, _allImageAdapter);
    }

    // Tắt chế độ chọn ảnh
    public void turnOffAllImagesSelectionMode()
    {
    	ImageSupporter.turnOffSelectionMode(_gridViewAll, _allImageAdapter);
    }

    // Bật chế độ chọn ảnh
    public void turnOnFavouriteSelectionMode()
    {
    	ImageSupporter.turnOnSelectionMode(_gridViewFavourite, _favouriteAdapter);
    }

    // Tắt chế độ chọn ảnh
    public void turnOffFavouriteSelectionMode()
    {
    	ImageSupporter.turnOffSelectionMode(_gridViewFavourite, _favouriteAdapter);
    }
    
    // Thêm vào favourite
    // Ghi thêm vào file
    public void addToFavourite() 
    {
        int count = _allImageAdapter.getCount();
        ArrayList<String> newFavourite = new ArrayList<String>();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAll);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                holder.checkbox.setChecked(false);

                DataHolder data = (DataHolder) _allImageAdapter.getItem(holder.id);
                String path = data.getFilePath();
                
                if (_favouriteMap.containsKey(path) == false) {
                    newFavourite.add(path);
                    _favouriteAdapter.add(data);
                    _favouriteMap.put(path, path);
                }

                holder.checkbox.setChecked(true);
            }
        }

        ImageManager.addNewFavouriteImagePaths(getContext(), newFavourite);
    }

    // Thêm ảnh vào mục ưa thích
    public void addImageToFavourite(DataHolder data)
    {
    	 ArrayList<String> newFavourite = new ArrayList<String>();
    	 String path = data.getFilePath();
    	 
    	 if (_favouriteMap.containsKey(path) == false) 
    	 {
             newFavourite.add(path);
             _favouriteAdapter.add(data);
             _favouriteMap.put(path, path);
         }
    	
    	 ImageManager.addNewFavouriteImagePaths(getContext(), newFavourite);
    }
       
    // Xóa khỏi favourite
    // Luu lai vào file
    public void removeFromFavourite() 
    {
        int count = _favouriteAdapter.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewFavourite);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                DataHolder data = (DataHolder) _favouriteAdapter.getItem(holder.id);
                String path = data.getFilePath();
                
                if (_favouriteMap.containsKey(path) == true) {
                    _favouriteAdapter.remove(data);
                    _favouriteMap.remove(path);
                }
            }
        }

        ImageManager.saveFavouriteImagePaths(getContext(), new ArrayList<String>(_favouriteMap.values()));
    }
 
    // Bỏ ảnh khỏi mục ưa thích
    public void removeImageFromFavourite(DataHolder data)
    {
        if (_favouriteMap.containsKey(data.getFilePath()) == true)
        {
            _favouriteAdapter.remove(data);
            _favouriteMap.remove(data.getFilePath());
        }

        // Lưu dữ liệu
        ImageManager.saveFavouriteImagePaths(getContext(), new ArrayList<String>(_favouriteMap.values()));
    }
    
    // Nạp các ảnh ưa thích lên
    public void loadFavouriteImages() 
    {
        ArrayList<String> data = ImageManager.getFavouriteImagePaths(getContext());
        _favouriteData = new ArrayList<DataHolder>();
        _favouriteMap = new HashMap<String, String>();

        _favouriteAdapter = new ImageAdapter(getContext(), _favouriteData);
        _gridViewFavourite.setAdapter(_favouriteAdapter);

        if (data != null)
        {
            for (int i = 0; i < data.size(); i++) 
            {
                DataHolder d = this.getImageDataByName(data.get(i));
             
                if (d != null) {
                    _favouriteData.add(d);
                    _favouriteMap.put(d.getFilePath(), d.getFilePath());
                }
            }
        }
    }
    
    // Áp dụng insert sort với độ phức tạp O(n)
    // Tiện cho việc tìm kiếm nhị phân
    public void insertImageData(DataHolder data)
    { 	
    	ImageSupporter.binaryInsertByLastModifiedDate(_allImageData, data);
    }

	// Lấy thông tin ảnh ưa thích
	protected static ArrayList<String> getFavouriteImagePaths(Context context)
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
	        return result;
	    }
	    catch (FileNotFoundException e) 
	    {
	    	Log.e("GalleryDS_Favourite", "File not found: " + e.toString());
	        return null;
	    } catch (IOException e) 
	    {
	    	Log.e("GalleryDS_Favourite", "Can not read file: " + e.toString());
	        return null;
	    }
	}

	// Lưu thông tin ảnh ưa thích
	protected static boolean saveFavouriteImagePaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("GalleryDS_Favourite.txt", Context.MODE_PRIVATE);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();	
			return true;
		}
		catch (Exception e)
		{
			Log.e("GalleryDS_Favourite", e.getMessage());
	        return false;
		}
	}
	
	// Lưu thêm thông tin ảnh ưa thích
	protected static boolean addNewFavouriteImagePaths(Context context, ArrayList<String> data)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput("GalleryDS_Favourite.txt", Context.MODE_PRIVATE | Context.MODE_APPEND);
			
			if (data != null)
				for (int i = 0; i < data.size(); i++) 
					fos.write((data.get(i) + "\n").getBytes());
			
			fos.close();	
			return true;
		}
		catch (Exception e)
		{
			Log.e("GalleryDS_Favourite", e.getMessage());
	        return false;
		}
	}

	public void refresh() {
		// TODO Auto-generated method stub
		this._allImageAdapter.notifyDataSetChanged();
	}	
}
