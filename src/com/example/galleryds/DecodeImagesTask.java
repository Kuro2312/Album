package com.example.galleryds;

import java.util.ArrayList;

import android.os.AsyncTask;

public class DecodeImagesTask extends AsyncTask<ImageManager, Void, Boolean>{

	protected Boolean doInBackground(ImageManager... imageManager) {
    	
		// Giải mã ảnh cho phù hợp
		ArrayList<DataHolder> data = imageManager[0].getAllImageData();
		int n = data.size();
		
		for (int i = n - 1; i >=0; i--)
			data.get(i).loadBitmap();
    	
    	return true;
    }
	
	protected void onPostExecute(Boolean flag)
	{
		
    }
}



class LoadFavouriteImageTask extends AsyncTask<ImageManager, Void, Integer> {

    protected Integer doInBackground(ImageManager... imageManager) {
    	
    	//Load dữ liệu ảnh ưa thích
    	imageManager[0].loadFavouriteImages();
    	imageManager[0].getGridViewFavourite().setSelection(imageManager[0].getNumberOfFavouriteImages() - 1);
    	return 1;
    }


    protected void onPostExecute(Integer a) {
        
    }
}