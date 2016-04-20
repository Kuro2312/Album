package com.example.galleryds;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;

public class ImageManager {
	
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
    public ImageManager(Context context, GridView gridView, GridView gridViewFavourite)
    {
    	_gridViewAll = gridView;
    	_gridViewFavourite = gridViewFavourite;
    	_allImageData = new ArrayList<DataHolder>();
        _allMap = new HashMap<String, DataHolder>();
        _allImageAdapter = new ImageAdapter(context, _allImageData);
        _gridViewAll.setAdapter(_allImageAdapter);
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
    
    // Lấy dữ liệu của 1 ảnh
    public DataHolder getImageDataByName(String imageName) 
    {
    	if (_allMap.containsKey(imageName))
    		return _allMap.get(imageName);
    	
    	return null;
	}

    // Lấy danh sách các ảnh
    public ArrayList<DataHolder> getImageList()
    {
    	return _allImageData; 
    }
    
    public boolean containsImage(String imageName)
    {
    	return _allMap.containsKey(imageName);
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
    
    // Xóa 1 ảnh
    public void removeImage(int id)
    {
    	_allImageAdapter.remove(id);
    }
    
    public ImageAdapter getAllImageAdapter()
    {
    	return _allImageAdapter;
    }
    
    // Lấy dữ liệu ảnh
    public DataHolder getImageDataById(int id)
    {
    	return (DataHolder) _allImageAdapter.getItem(id);
    }
    
    // Lấy số lượng ảnh
    public int getNumberOfImages()
    {
    	return _allImageAdapter.getCount();
    }

    // Xóa các ảnh được chọn
    public void deleteSelectedImages(Context context)
    {
        int count = _allImageAdapter.getCount();

        for (int i = count - 1; i >= 0; i--) 
        {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAll);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true)
            {
            	DataHolder data = (DataHolder) _allImageAdapter.getItem(holder.id);
            	_allImageAdapter.remove(holder.id);
            	
            	
            	this.removeImageFromFavourite(context, data);
            }
        }
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
    public void addToFavourite(Context context) 
    {
        int count = _allImageAdapter.getCount();
        ArrayList<String> newFavourite = new ArrayList<String>();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAll);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                holder.checkbox.setChecked(false);

                DataHolder data = (DataHolder) _allImageAdapter.getItem(holder.id);

                if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == false) {
                    newFavourite.add(data._file.getAbsolutePath());
                    _favouriteAdapter.add(data);
                    _favouriteMap.put(data._file.getAbsolutePath(), data._file.getAbsolutePath());
                }

                holder.checkbox.setChecked(true);
            }
        }

        ImageSupporter.addNewFavouriteImagePaths(context, newFavourite);
    }

    // Xóa khỏi favourite
    // Luu lai vào file
    public void removeFromFavourite(Context context) 
    {
        int count = _favouriteAdapter.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewFavourite);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                DataHolder data = (DataHolder) _favouriteAdapter.getItem(holder.id);

                if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == true) {
                    _favouriteAdapter.remove(data);
                    _favouriteMap.remove(data._file.getAbsolutePath());
                }
            }
        }

        ImageSupporter.saveFavouriteImagePaths(context, new ArrayList<String>(_favouriteMap.values()));
    }

    // Nạp các ảnh ưa thích lên
    public void loadFavouriteImages(Context context) 
    {
        ArrayList<String> data = ImageSupporter.getFavouriteImagePaths(context);
        _favouriteData = new ArrayList<DataHolder>();
        _favouriteMap = new HashMap<String, String>();

        _favouriteAdapter = new ImageAdapter(context, _favouriteData);
        _gridViewFavourite.setAdapter(_favouriteAdapter);

        if (data != null)
        {
            for (int i = 0; i < data.size(); i++) 
            {
                DataHolder d = this.getImageDataByName(data.get(i));

                if (d != null) {
                    _favouriteData.add(d);
                    _favouriteMap.put(d._file.getAbsolutePath(), d._file.getAbsolutePath());
                }
            }
        }
    }

    // Thêm ảnh vào mục ưa thích
    public void addImageToFavourite(Context context, DataHolder imageData)
    {
    	 ArrayList<String> newFavourite = new ArrayList<String>();
    	 
    	if (_favouriteMap.containsKey(imageData._file.getAbsolutePath()) == false) 
    	{
            newFavourite.add(imageData._file.getAbsolutePath());
            _favouriteAdapter.add(imageData);
            _favouriteMap.put(imageData._file.getAbsolutePath(), imageData._file.getAbsolutePath());
        }
    	
    	ImageSupporter.addNewFavouriteImagePaths(context, newFavourite);
    }
    
    // Bỏ ảnh khỏi mục ưa thích
    public void removeImageFromFavourite(Context context, int imageId)
    {
    	DataHolder data = (DataHolder) _favouriteAdapter.getItem(imageId);

        if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == true)
        {
            _favouriteAdapter.remove(data);
            _favouriteMap.remove(data._file.getAbsolutePath());
        }

        ImageSupporter.saveFavouriteImagePaths(context, new ArrayList<String>(_favouriteMap.values()));
    }

    // Bỏ ảnh khỏi mục ưa thích
    public void removeImageFromFavourite(Context context, DataHolder data)
    {
        if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == true)
        {
            _favouriteAdapter.remove(data);
            _favouriteMap.remove(data._file.getAbsolutePath());
        }

        ImageSupporter.saveFavouriteImagePaths(context, new ArrayList<String>(_favouriteMap.values()));
    }

}
