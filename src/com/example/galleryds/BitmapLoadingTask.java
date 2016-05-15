package com.example.galleryds;

import java.io.File;
import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

public class BitmapLoadingTask extends AsyncTask<DataHolder, Void, Bitmap> 
{
    private final WeakReference<ImageView> _imageViewReference;
    private DataHolder _data = null;

    public BitmapLoadingTask(ImageView imageView)
    {
        // Dùng WeakReference để chắc chắn ImageView có thể bị dọn dẹp 
    	_imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(DataHolder... params) 
    {	
    	// Lấy đường dẫn tới tập tin ảnh
    	_data = params[0];
        
    	// Giải mã ảnh cho phù hợp
    	_data.loadBitmap();
        return _data.getBitmap();
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) 
    {
    	// Khi hoàn thành kiểm tra xem ImageView còn tồn tại và ảnh Bitmap có tồn tại không
    	// Nếu không cài đặt bitmap
        if (_imageViewReference != null && bitmap != null)
        {
            final ImageView imageView = _imageViewReference.get();
            if (imageView != null)
                imageView.setImageBitmap(bitmap);
        }
    }
    
    public DataHolder getData()
    {
    	return _data;
    }
}
