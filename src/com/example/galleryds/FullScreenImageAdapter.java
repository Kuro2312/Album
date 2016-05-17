package com.example.galleryds;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FullScreenImageAdapter extends PagerAdapter{

	private Activity _activity;
	private ArrayList<String> _filePaths;
	private ArrayList<View> _pages;
	private LayoutInflater _inflater;
	
	private LruCache<String, Bitmap> mMemoryCache;
	
	// constructor
	public FullScreenImageAdapter(Activity activity,
			ArrayList<String> filePaths) {
		_activity = activity;
		_filePaths = filePaths;
		_pages = new ArrayList<View>();
		_inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
		final int cacheSize = maxMemory / 2;
		mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) {
	            return bitmap.getByteCount() / 1024;
	        }
	    };
	}
	
	public void addBitmapToMemCache(String key, Bitmap bitmap) {
	    if (getBitmapFromMemCache(key) == null) {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) {
	    return mMemoryCache.get(key);
	}
	
	
	@Override
	public int getCount() {
		return _filePaths.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
    public Object instantiateItem(ViewGroup container, int position) {
		
        View viewLayout = _inflater.inflate(R.layout.fullscreen_image_layout, container, false); 
        TouchImageView image = (TouchImageView) viewLayout.findViewById(R.id.fullScreenImage);
        
        Display display = _activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        
        Bitmap bitmap = getBitmapFromMemCache(_filePaths.get(position));
        
        if (bitmap == null) {       	
        	new BitmapWorkerTask(image, size.x).execute(_filePaths.get(position));
        }
        else
        	image.setImageBitmap(bitmap);
        
        image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((ViewImageActivity) _activity).toggle();
			}
		});
        
        ((ViewPager) container).addView(viewLayout);
      
        return viewLayout;
	}
	
	@Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
	
	public boolean removeImage(int position) {
		_filePaths.remove(position);
		notifyDataSetChanged();
		if  (_filePaths.size() == 0)
			return false;
		return true;
	}

	@Override
	public int getItemPosition(Object object) {
		if (_pages.contains(object)) {
			return _pages.indexOf(object);
		}
		return POSITION_NONE;
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
		
		ImageView _view;
		int _size;
		String _filePath;
		
		public BitmapWorkerTask(ImageView view, int size) {
			_view = view;
			_size = size;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			_filePath = params[0];
			Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromFile(new File(_filePath), _size, _size);
			return bitmap;
		}
		
		@Override
		protected void onPostExecute(Bitmap result) {
			_view.setImageBitmap(result);
			addBitmapToMemCache(_filePath, result);
		}
		
	}
}
