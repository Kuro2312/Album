package com.example.neogalleryds;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import BusinessLayer.AlbumManager;
import BusinessLayer.FolderManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.LockManager;
import BusinessLayer.MarkManager;
import FullscreenImage.CustomScroller;
import FullscreenImage.FullscreenImageAdapter;
import FullscreenImage.MyViewPager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Toast;

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
    private MyViewPager _viewPager;
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
	private FullscreenImageAdapter _adapter;
	
	private ImageButton btnMark;
	private ImageButton btnAdd;
	private ImageButton btnDelete;
	private Context _this;
	
	// Các thuộc tính quản lý
	private FolderManager _folderManager;
	private AlbumManager _albumManager;
	private MarkManager _markManager;
	
	// dành cho slideshow
	private Timer timer;
	private int page;
	private int maxPage;
	
	private int curTab = MainActivity.currentTab;

    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        
        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        _viewPager = (MyViewPager) findViewById(R.id.pager);
        
        /////////////////////////////////////////////////////////////
        
        _this = this;
        loadData();
        
		_viewPager.setPageMargin(10);
		
		Intent intent = getIntent();
		boolean slideshow = false;
		int wait = -1;
		int slide = -1;
		
		int position;
		
		boolean internal = intent.getBooleanExtra("internal", false);
		if (internal == true) { // nếu được gọi từ bên trong app
			
			position = intent.getIntExtra("position", 0);
			_filePaths = (ArrayList<String>) intent.getSerializableExtra("filePaths");
			slideshow = intent.getBooleanExtra("slideshow", false);
			wait = intent.getIntExtra("wait", 500);
			slide = intent.getIntExtra("slide", 2000);
			
			
			
		} else {
			
			curTab = -1;
			
			Uri receivedUri = intent.getData();
			File initFile = new File(receivedUri.getPath());
			String initPath = initFile.getAbsolutePath();
			if (_albumManager.containsImage(initPath)) { // nếu ảnh này nằm trong album
				curTab = 1;
				_filePaths = _albumManager.getsAlbumImages(initFile.getParentFile().getName());
			} else {
				curTab = 0;
				_filePaths = _folderManager.getsFolderImages(initFile.getParent());
			}
			
			Collections.sort(_filePaths, String.CASE_INSENSITIVE_ORDER);

			position = _filePaths.indexOf(initPath);			
		}
		
		_adapter = new FullscreenImageAdapter(this, _filePaths);
		_viewPager.setAdapter(_adapter);		
		_viewPager.setCurrentItem(position);

		btnMark = (ImageButton) findViewById(R.id.btnMark);
		btnMark.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path = _filePaths.get(_viewPager.getCurrentItem());
				_markManager.marksImage(path);
			}
		});
		
		btnAdd = (ImageButton) findViewById(R.id.btnAdd);
		btnAdd.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String path = _filePaths.get(_viewPager.getCurrentItem());
				ArrayList<String> paths = new ArrayList<String>();
				paths.add(path);
				chooseAlbum(paths);
			}
		});
		
		btnDelete = (ImageButton) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(_this);
				builder.setMessage("Are you sure you want to delete?")
					   .setTitle("Delete");
				
				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						int pos = _viewPager.getCurrentItem();
						String path = _filePaths.get(pos);
						ArrayList<String> paths = new ArrayList<String>();
						paths.add(path);
						
						if (MainActivity._imageAdapter != null)
							MainActivity._imageAdapter.removeImages(paths);
						
						switch (curTab) {
						case 0: // tab All
							if (_folderManager.deletesImages(paths)) {
								_markManager.unmarksImages(paths);
		                 		Toast.makeText(_this, "Deleted", Toast.LENGTH_SHORT).show();
							}
		                 	else
		                 		Toast.makeText(_this, "Fail To Delete", Toast.LENGTH_SHORT).show();
							break;
						case 1: // tab Albums
							if (_albumManager.deletesImages(paths)) {
								_markManager.unmarksImages(paths);
								Toast.makeText(_this, "Deleted", Toast.LENGTH_SHORT).show();
							}	                 		
		                 	else
		                 		Toast.makeText(_this, "Fail To Delete", Toast.LENGTH_SHORT).show();
							break;
						}

						if (!_adapter.removeImage(pos)) // nếu đã xoá hết ảnh
							finish();		
					}
				});
				
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();				
					}
				});
				
				builder.show();
			}
		});

		if (slideshow == true) {
			_viewPager.disablePaging();
			page = 0;
			maxPage = _filePaths.size();
			try {
			    Field mScroller;
			    mScroller = ViewPager.class.getDeclaredField("mScroller");
			    mScroller.setAccessible(true);
			    Interpolator sInterpolator = new DecelerateInterpolator();
			    CustomScroller scroller = new CustomScroller(_this, sInterpolator, slide);
			    mScroller.set(_viewPager, scroller);
			} catch (NoSuchFieldException e) {
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			}
			pageSwitcher(slide + wait);
		}
    }
    
 // Thực hiện nạp dữ liệu
 	// Khi xoay màn hình, thay đổi cấu hình, resume
 	public void loadData()
 	{		
 		// Tạo mới để cập nhật dữ liệu nếu cần
 		if (MainActivity._albumManager == null) {
	 		_folderManager = new FolderManager(this);
	 		_albumManager = new AlbumManager(this);
	 		_markManager = new MarkManager(this);
 		} else {
 			_folderManager = MainActivity._folderManager;
 			_albumManager = MainActivity._albumManager;
 			_markManager = MainActivity._markManager;
 		}
 		
 		File imageDir = new File(Environment.getExternalStorageDirectory().toString());
 		
         // Duyệt đệ quy theo chiến lược
 		if (imageDir.exists()) 
 			dirFolder(imageDir);
 	}
 	
 	// Duyệt toàn bộ cây thư mục
 	// Tìm thư mục có ảnh và danh sách ảnh
 	public void dirFolder(File file) 
 	{   	
 		 // Kiểm tra tên có hợp lệ không
 		 if (file.getName().startsWith(".") || file.getName().startsWith("com."))
 			 return;

 		 File[] files = file.listFiles();

 		 // Kiễm tra các tập tin con có rỗng không
 		 if (files == null)
 			 return;

 		 for (File f : files) 
 		 {	
 			 // Kiểm tra xem có phải ảnh không
 			 if (ImageSupporter.isImage(f))       
 			 {
 				 String filePath = file.getAbsolutePath();
 				 String parent = file.getParent();

 				 // Chưa có thư mục này, thêm vào dữ liệu
 				 if ((parent.equals(ImageSupporter.DEFAULT_PICTUREPATH) 
 						 && !_albumManager.containsAlbum(file.getName())) 
 					|| (!parent.equals(ImageSupporter.DEFAULT_PICTUREPATH) 
 						 &&!_folderManager.containsFolder(filePath)))
 					 _folderManager.addsFolder(file.getAbsolutePath());

 				 // Thêm ảnh vào dữ liệu
 				 if (_albumManager.containsImage(f.getAbsolutePath()))
 					 _albumManager.addImage(f.getAbsolutePath());
 				 else
 					 _folderManager.addsImage(file.getAbsolutePath(), f.getAbsolutePath());	
 			 }

 			 // Kiểm tra phải thư mục không
 			 if (f.isDirectory())
 				 dirFolder(f);
 		 }
 	}
    
 // Hiện dialog dể chọn album 
    private void chooseAlbum(final ArrayList<String> images)
    {
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    	builderSingle.setIcon(R.drawable.icon);
    	builderSingle.setTitle("Select an Album: ");

    	final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
    	
    	// Nạp dữ liệu cho thông báo qua adapter 
    	arrayAdapter.addAll(_albumManager.getsAlbumList());

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
                
                for (String path: images) {
                	_albumManager.addsImageToAlbum(strName, path);
                }
                
            }
        });
    	
    	builderSingle.show();
    }
    
    public void pageSwitcher(int miliseconds) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new RemindTask(), 0, miliseconds);
    }

        // this is an inner class...
    class RemindTask extends TimerTask {

        @Override
        public void run() {

            runOnUiThread(new Runnable() {
                public void run() {

                    if (page >= maxPage) { 
                        timer.cancel();
                    } else {
                        _viewPager.setCurrentItem(page++);
                    }
                }
            });

        }
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
