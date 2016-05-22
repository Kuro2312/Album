package com.example.galleryds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

// Dùng cho chiến lược tải ảnh V2
public class DecodeImagesTask extends AsyncTask<ImageManager, Void, ImageManager>{

	protected ImageManager doInBackground(ImageManager... imageManager) {
    	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		for (int i = n - 1; i >=0; i--) {
			data.get(i).loadBitmap();			
		}
    	
		
    	return imageManager[0];
    }
	
	protected void onPostExecute(ImageManager imageManager)
	{
		imageManager.refresh();
    }
}


// Dùng cho việc xử lý ảnh Favourite
class LoadFavouriteImageTask extends AsyncTask<ImageManager, Void, Integer> {

    protected Integer doInBackground(ImageManager... imageManager) {
    	
    	//Load dữ liệu ảnh ưa thích
    	imageManager[0].loadFavouriteImages();
    	//imageManager[0].getGridViewFavourite().setSelection(imageManager[0].getNumberOfFavouriteImages() - 1);
    	return 1;
    }


    protected void onPostExecute(Integer a) {
        
    }
}

// Tải và xữ lý 300 ảnh gần nhất
class Decode300ImagesTask extends AsyncTask<ImageManager, Void, ImageManager>{

	protected ImageManager doInBackground(ImageManager... imageManager) {
    	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		int j = 0;
		for (int i = n - 1; i >= 0; i--) {
			data.get(i).loadBitmap();
			
			j++;
			if (j >= 300)
				break;
		}
    	
		
    	return imageManager[0];
    }
	
	protected void onPostExecute(ImageManager imageManager)
	{

		imageManager.refresh();

    }
}

// Xử lý ảnh bitmap và chuyển thành thumbnail cho 1024 ảnh đầu tiên
class Decode1024ImagesTask extends AsyncTask<ImageManager, Void, ImageManager>{
	
	protected ImageManager doInBackground(ImageManager... imageManager) {
    	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		int m = n - 1024;
		
		if (m < 0)
			m = 0;
		
		for (int i = n - 1; i >= m; i--) 
		{	
			// Tải ảnh
			data.get(i).loadBitmap();	
				
			// Save ảnh
			ImageManager.saveThumbNail(data.get(i));
		}
		
    	return imageManager[0];
    }
	
	protected void onPostExecute(ImageManager imageManager)
	{
		imageManager.refresh();
    }
}

//Xử lý ảnh bitmap và chuyển thành thumbnail cho 1024 ảnh đầu tiên
class Decode512ImagesTask extends AsyncTask<ImageManager, Void, ImageManager>{
	
	protected int _pos;
	protected boolean _isScrollUp;
	
	public Decode512ImagesTask(int pos, boolean isScrollUp)
	{
		_pos = pos;
		_isScrollUp = isScrollUp;
	}
	
	protected ImageManager doInBackground(ImageManager... imageManager) {
 	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		if (n > 1024 &&  n - 900 > _pos)
		{	
			if (_isScrollUp == true)
			{
				int m = _pos - 512;
				
				if (m < 0)
					m = 0;
				
				for (int i = _pos; i >= m; i--) 
				{	
					// Tải ảnh
					data.get(i).loadBitmap();	
						
					// Save ảnh
					ImageManager.saveThumbNail(data.get(i));
				}
				
				int m1 = (_pos + 1000 < n) ? _pos + 1000 : n;
				for (int i = _pos + 500; i < m1; i++)
					ImageSupporter.deleteThumbNail(data.get(i));
			}
			else if (_isScrollUp == true && _pos - 500 > 0)
			{
				int m = _pos + 512;
				
				if (m >= n)
					m = n;
				
				for (int i = _pos; i < m; i++) 
				{	
					// Tải ảnh
					data.get(i).loadBitmap();	
						
					// Save ảnh
					ImageManager.saveThumbNail(data.get(i));
				}
				
				int m1 = (_pos - 1000 > 0) ? _pos - 1000 : 0;
				for (int i = _pos - 500; i >= m1; i--)
					ImageSupporter.deleteThumbNail(data.get(i));
			}
		}
		
		return imageManager[0];
	}
	
	protected void onPostExecute(ImageManager imageManager)
	{
		imageManager.refresh();
	}
}

// Dùng để dọn dẹp bớt dữ liệu chưa cần tới hay đã dùng qua
class ReleaseDataTask extends AsyncTask<ImageManager, Void, ImageManager>{

	protected int _pos;
	protected boolean _isScrollUp;
	
	public ReleaseDataTask(int pos, boolean isScrollUp)
	{
		_pos = pos;
		_isScrollUp = isScrollUp;
	}
	
	protected ImageManager doInBackground(ImageManager... imageManager) {
    	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		if (_isScrollUp == true)
		{
			if (n - _pos > 300)
				for (int i = _pos + 100; i < n; i++)
					data.get(i).setBitmap(null);
		}
		else
		{
			if (_pos > 300)
				for (int i = _pos - 300; i >= 0; i--)
					data.get(i).setBitmap(null);
		}
    		
    	return imageManager[0];
    }
	
	protected void onPostExecute(ImageManager imageManager)
	{
		imageManager.refresh();
    }
}

