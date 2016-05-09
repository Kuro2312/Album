package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;

import com.example.galleryds.util.SystemUiHider;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class ViewImageActivity extends Activity {
	/**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private ViewPager _viewPager;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            _viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    
    
    

	private ArrayList<String> _filePaths;
	private FullScreenImageAdapter _adapter;
	
	private ImageButton btnFavourite;
	private ImageButton btnAdd;
	private ImageButton btnDelete;
    
	private ImageManager _imageManager;
	private AlbumManager _albumManager;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        _viewPager = (ViewPager) findViewById(R.id.pager);
        
        /////////////////////////////////////////////////////////////
        
		_viewPager.setPageMargin(10);
		
		Intent i = getIntent();
		int position = i.getIntExtra("position", 0);
		_filePaths = (ArrayList<String>) i.getSerializableExtra("filePaths");

		
		_adapter = new FullScreenImageAdapter(this, _filePaths);
		_viewPager.setAdapter(_adapter);		
		_viewPager.setCurrentItem(position);
		
		_imageManager = ImageManager.getInstance();
		_albumManager = AlbumManager.getInstance();
		
		btnFavourite = (ImageButton) findViewById(R.id.btnFavourite3);
		btnFavourite.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int pos = _viewPager.getCurrentItem();
				
				ArrayList<DataHolder> allImageData = _imageManager.getAllImageData();
				for (DataHolder data : allImageData) {
					if (data.getFilePath().equals(_filePaths.get(pos))) {
						_imageManager.addImageToFavourite(data);
						break;
					}
				}
			}
		});
		
		btnAdd = (ImageButton) findViewById(R.id.btnAdd3);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {	
				chooseAlbum();
			}
		});
		
		btnDelete = (ImageButton) findViewById(R.id.btnDelete3);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int pos = _viewPager.getCurrentItem();
				
				ArrayList<DataHolder> allImageData = _imageManager.getAllImageData();
				for (DataHolder data : allImageData) {
					if (data.getFilePath().equals(_filePaths.get(pos))) {
						_imageManager.deleteSelectedImage(data);
						int newPos;
						if (pos == _filePaths.size() - 1)
							newPos = pos - 1;
						else
							newPos = pos + 1;
						if (!_adapter.removeImage(pos)) // nếu đã xoá hết ảnh
							finish();					
						break;
					}
				}				
			}
		});		
    }
    
 // Thêm ảnh đã chọn vào album
    public void addSelectedImagesToAlbum(String albumName) {
    	
    	int pos = _viewPager.getCurrentItem();
		
		ArrayList<DataHolder> allImageData = _imageManager.getAllImageData();
		for (DataHolder data : allImageData) {
			if (data.getFilePath().equals(_filePaths.get(pos))) {
				_albumManager.addImageToAlbum(data, albumName);
				break;
			}
		} 
    }   
    
    // Hiện dialog dể chọn album 
    public void chooseAlbum()
    {
    	// Tạo 1 alert (thông báo)
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    	builderSingle.setIcon(R.drawable.ic_launcher);
    	builderSingle.setTitle("Select an Album: ");

    	final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
    	
    	// Nạp dữ liệu cho thông báo qua adapter 
    	arrayAdapter.addAll(_albumManager.getAlbumList());

    	// Tạo sự kiện cho nút hủy thông báo
    	builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
    	{
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
    	});

    	// Tạo sự kiện cho nút chọn album
    	builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() 
    	{
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                ViewImageActivity context = (ViewImageActivity) arrayAdapter.getContext();
                
                // Thực hiện thêm ảnh vào album
                context.addSelectedImagesToAlbum(strName);
            }
        });
    	
    	builderSingle.show();
    }
    
    /////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    public void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        _viewPager.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

}
