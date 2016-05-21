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

class Decode1024ImagesTask extends AsyncTask<ImageManager, Void, ImageManager>{

	protected ImageManager doInBackground(ImageManager... imageManager) {
    	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		if (n - 300 > 0)
		{
			int m = n - 1024;
			
			if (m < 0)
				m = 0;
			
			for (int i = n - 1; i >= m; i--) {
				

				
				data.get(i).loadBitmap();	
				Bitmap b = data.get(i).getBitmap();
				data.get(i).setBitmap(null);
					
				if (b != null)
				{
					try {
						
						 File infile = new File(data.get(i).getFilePath());
						 
						 String name = infile.getParentFile().getName() + infile.getName();
						 
							int pos = name.lastIndexOf(".");
							if (pos > 0)
							    name = name.substring(0, pos) + ".png";
							
						 File f = new File(ImageSupporter.DEFAULT_PICTUREPATH + File.separator + "nova" + File.separator + name);
						 //File f1 = new File(ImageSupporter.DEFAULT_PICTUREPATH + File.separator + "nova");
						 
						 f.createNewFile();
	 
							 FileOutputStream out = new FileOutputStream(f);
							 b.compress(Bitmap.CompressFormat.PNG, 100, out);
							 out.flush();
							 out.close();

					} catch (Exception e) {
					     e.printStackTrace();
					}	
				}
			}
		}
		
    	return imageManager[0];
    }
	
	protected void onPostExecute(ImageManager imageManager)
	{
		imageManager.refresh();
    }
}


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
		
		if (_isScrollUp == false)
		{
			if (n - _pos > 300)
				for (int i = _pos + 100; i < _pos + 300; i++)
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