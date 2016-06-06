package com.example.neogalleryds;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import Adapter.AlbumAdapter;
import Adapter.FolderAdapter;
import Adapter.ImageAdapter;
import Adapter.ImageViewHolder;
import AsyncTaskSupporter.AddImagesToAlbumAsyncTask;
import AsyncTaskSupporter.DeleteAlbumAsyncTask;
import AsyncTaskSupporter.DeleteImagesInAlbumAsyncTask;
import AsyncTaskSupporter.DeleteImagesInFolderAsyncTask;
import AsyncTaskSupporter.DeleteLockedImagesAsyncTask;
import AsyncTaskSupporter.LockImagesInAlbumAsyncTask;
import AsyncTaskSupporter.LockImagesInFolderAsyncTask;
import AsyncTaskSupporter.MarkImagesAsyncTask;
import AsyncTaskSupporter.RemoveImagesFromAlbumAsyncTask;
import AsyncTaskSupporter.UnlockImagesAsyncTask;
import AsyncTaskSupporter.UnmarkImagesAsyncTask;
import BusinessLayer.AlbumManager;
import BusinessLayer.FolderManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.LockManager;
import BusinessLayer.MarkManager;
import BusinessLayer.ResourceManager;
import BusinessLayer.UserInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

public class MainActivity extends Activity {
	
	// Thuộc tính lưu đối tượng giao diện
	private ViewGroup _scrollViewgroup;
	private HorizontalScrollView _scrollView;
	
	private ListView _listViewFolder;
	private ListView _listViewAlbum;
	
	private GridView _gridViewImage;
	
	private RadioGroup _radioViewgroup;
	private RadioButton _radioLocks;
	private RadioButton _radioMarks;
	private RadioButton _radioAll;
	private RadioButton _radioAlbum;
	
	private View btnDeleteImage;
	
	private View btnMarkImage;
	private View btnUnmarkImage;
	
	private View btnLockImage;
	private View btnUnlockImage;
	
	private View btnAddToAlbum;
	private View btnRemoveFromAlbum;
	
	private View btnShareImage;
	private View btnZipAndShare;
	
	private View btnEditImage;
	
	private View btnSlideshow;
	
	private ProgressBar _progressBar;
	
	// Các xữ lý chức năng cho sự kiện nhấn nút.
	private OnClickListener onClickSlideshow;
	private OnClickListener onClickZipAndShare;
	private OnClickListener onClickShareImage;
	private OnClickListener onClickRemoveFromAlbum;
	private OnClickListener onClickAddToAlbum;
	private OnClickListener onClickUnlockImage;
	private OnClickListener onClickLockImage;
	private OnClickListener onClickUnmarkImage;
	private OnClickListener onClickMarkImage;
	private OnClickListener onClickDeleteAlbum;
	private OnClickListener onClickDeleteImage;
	
	// Thuộc tính cho việc xữ lý chức năng và lưu dữ liệu
	private Activity _this = this;
	private int _lastIndexFolder = -1;
	private int _lastIndexAlbum = -1;
	private String _selectedAlbumName = "";
	private String _selectedFolderPath = "";
	private int _contextPosition;
	private int _albumContextPosition;
	public static int currentTab = 0;
	public static boolean cancelLoadImage = false;
	private static int SLIDE_SHOW = 2984;
	
	// Adapter
	private FolderAdapter _folderAdapter;
	private ImageAdapter _imageAdapter;
	private AlbumAdapter _albumAdapter;
	
	// Các thuộc tính quản lý
	private FolderManager _folderManager;
	private AlbumManager _albumManager;
	private MarkManager _markManager;
	private LockManager _lockManager;
	public  boolean _isLogined = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// B1: Lấy các đối tượng giao diện
		setAllViewInstances();
		
		// B2: Phát sinh thanh công cụ
		populateFunctionBar();
		
		// B3: Lấy dữ liệu
		loadData();
        
		// B4: Cài đặt dữ liệu lên giao diện
		_imageAdapter = new ImageAdapter(this, new ArrayList<String>());
		_gridViewImage.setAdapter(_imageAdapter);
			
		_folderAdapter = new FolderAdapter(this, _folderManager.getsFolderPathList());
		_listViewFolder.setAdapter(_folderAdapter);
		_listViewFolder.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		_albumAdapter = new AlbumAdapter(this, _albumManager.getsAlbumList());
		_listViewAlbum.setAdapter(_albumAdapter);
		_listViewAlbum.setChoiceMode(ListView.CHOICE_MODE_SINGLE);		
		
		// B5: Cài đặt sự kiện xử lý
		setOnFolderSelected();
		setOnAlbumSelected();
		setOnTabChange();
		//_progressBar.setVisibility(View.VISIBLE);
		
		//setProgressingDialog();
	}
	
	// Xử lý cập nhật dữ liệu sau khi quay lại ứng dụng
	protected void onRestart()
	{
		super.onRestart();
		
		// Tải lại dử liệu
		loadData();
		
		// Cập nhật dữ liệu Album và Folder
		_folderAdapter.updateData(_folderManager.getsFolderPathList());
		_albumAdapter.updateData(_albumManager.getsAlbumList());
		
		// Cập nhật dữ liệu trên imageAdpater và gridView
		if (_radioAll.isChecked())
			_imageAdapter.updateData(_folderManager.getsFolderImages(_selectedFolderPath));
		else if (_radioAlbum.isChecked())
			_imageAdapter.updateData(_albumManager.getsAlbumImages(_selectedAlbumName));
		else if (_radioMarks.isChecked())
			_imageAdapter.updateData(_markManager.getsMarkedImages());
		else if (_radioLocks.isChecked())
			_imageAdapter.updateData(_lockManager.getsLockedImages());
		else
			_imageAdapter.updateData(new ArrayList<String>());	
	}
	
	public void showsSignUpDialog()
	{
		final Dialog dialog = new Dialog(_this);
		dialog.setContentView(R.layout.signup_dialog);
		dialog.setTitle("Sign Up");
	
		final EditText txtEmail = (EditText) dialog.findViewById(R.id.editTextEmail);
		final EditText txtPass = (EditText) dialog.findViewById(R.id.editTextPass);
		
		Button btnOk = (Button) dialog.findViewById(R.id.btnOk1);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String email = txtEmail.getText().toString();
				String pass = txtPass.getText().toString();
				
				if (pass == null || email == null || email.equals("") || pass.equals(""))	
					Toast.makeText(_this, "Empty data", Toast.LENGTH_SHORT).show();
				else
				{
					UserInfo.savesUserInfo(_this, email, pass);
					_isLogined = true;
                	dialog.dismiss();
                	
        			if (_radioLocks.isChecked())
        				_imageAdapter.updateData(_lockManager.getsLockedImages());
				}
			}
		});
		
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel1);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		dialog.show();	
	}
	
	public void showsLoginDialog()
	{	
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Login");
        alertDialog.setMessage("Enter password");
        
        final EditText input = new EditText(MainActivity.this);  
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                              LinearLayout.LayoutParams.MATCH_PARENT,
                              LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String pass = input.getText().toString();
                        if (UserInfo.checksPassword(_this, pass)) {
                        	_isLogined = true;
                        	dialog.dismiss();
                        	
                			if (_radioLocks.isChecked())
                				_imageAdapter.updateData(_lockManager.getsLockedImages());
                        	
                        	Toast.makeText(_this, "Logined Successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                        	Toast.makeText(_this, "Login Fail!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
	}
	
	// Cài đặt sự kiện chọn 1 item trong danh sách thư mục
	public void setOnFolderSelected()
	{		
		// Xử lý cài đặt mặc định thê hiện
		ArrayList<String> folderPaths = _folderManager.getsFolderPathList();
		
		if (folderPaths.size() > 0)
		{
			_selectedFolderPath = folderPaths.get(0);
			_lastIndexFolder = 0;
		}
		
		// Gán dữ liệu
		_imageAdapter.updateData(_folderManager.getsFolderImages(_selectedFolderPath));
		_listViewFolder.setItemChecked(_lastIndexFolder, true);

		_listViewFolder.setOnItemClickListener(new OnItemClickListener() 
		{	
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	if (_lastIndexFolder != -1)
            		_listViewFolder.setItemChecked(_lastIndexFolder, false);
            	
            	_listViewFolder.setItemChecked(position, true);
            	_lastIndexFolder = position;
            	_selectedFolderPath = (String)_folderAdapter.getItem(position);
            	
            	// Thay dữ liệu vô
        		_imageAdapter.updateData(_folderManager.getsFolderImages(_selectedFolderPath));
            }
        });
	}
	
	// Cài đặt sự kiện chọn 1 item trong danh sách album
	public void setOnAlbumSelected()
	{
		_listViewAlbum.setOnItemClickListener(new OnItemClickListener() 
		{	
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	if (_lastIndexAlbum != -1)
            		_listViewAlbum.setItemChecked(_lastIndexAlbum, false);
            	
            	_listViewAlbum.setItemChecked(position, true);
            	_lastIndexAlbum = position;
            	_selectedAlbumName = (String) _albumAdapter.getItem(position);
            	
            	// Thay dữ liệu vô
        		_imageAdapter.updateData(_albumManager.getsAlbumImages(_selectedAlbumName));
            }
        });
	}
	
	// Cài đặt sự kiện thay đổi tab
	public void setOnTabChange()
	{
		btnAddToAlbum.setVisibility(View.VISIBLE);
    	btnMarkImage.setVisibility(View.VISIBLE);
    	btnLockImage.setVisibility(View.VISIBLE);
    	
    	btnUnmarkImage.setVisibility(View.GONE);
    	btnUnlockImage.setVisibility(View.GONE);
    	btnRemoveFromAlbum.setVisibility(View.GONE);

    	// Cập nhật dữ liệu và giao diện khi thay đổi tab
		_radioViewgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            if (_radioAll.isChecked())
	            	onTabAllSelect();
	            else if (_radioAlbum.isChecked())
	            	onTabAlbumSelect();
	            else if (_radioMarks.isChecked())
	            	onTabMarksSelect();
	            else if (_radioLocks.isChecked())
	        		onTabLocksSelect();
	        }
	    });
	}
	
	// Khi tab All được chọn
	public void onTabAllSelect()
	{
		// Cập nhật vị trí tab
    	currentTab = 0;
    	
    	// Cập nhật dữ liệu 
    	_imageAdapter.updateData(_folderManager.getsFolderImages(_selectedFolderPath));
    	
    	// Cập nhật giao diện
    	_listViewFolder.setVisibility(View.VISIBLE);
    	_listViewAlbum.setVisibility(View.INVISIBLE);
    	
    	btnDeleteImage.setVisibility(View.VISIBLE);
    	btnAddToAlbum.setVisibility(View.VISIBLE);
    	btnMarkImage.setVisibility(View.VISIBLE);
    	btnLockImage.setVisibility(View.VISIBLE);
    	
    	btnUnmarkImage.setVisibility(View.GONE);
    	btnUnlockImage.setVisibility(View.GONE);
    	btnRemoveFromAlbum.setVisibility(View.GONE);
	}
	
	// Khi tab Album được chọn
	public void onTabAlbumSelect()
	{
		// Cập nhật vị trí tab
    	currentTab = 1;
    	
    	// Cập nhật dữ liệu 
		_imageAdapter.updateData(_albumManager.getsAlbumImages(_selectedAlbumName));
    	
		_listViewFolder.setVisibility(View.GONE);
    	_listViewAlbum.setVisibility(View.VISIBLE);
    	
    	
    	btnDeleteImage.setVisibility(View.VISIBLE);
    	btnMarkImage.setVisibility(View.VISIBLE);
    	btnLockImage.setVisibility(View.VISIBLE);
    	btnRemoveFromAlbum.setVisibility(View.VISIBLE);
    	
    	btnUnmarkImage.setVisibility(View.GONE);
    	btnUnlockImage.setVisibility(View.GONE);
    	btnAddToAlbum.setVisibility(View.GONE);
    }
	
	// Khi tab Marks được chọn
	public void onTabMarksSelect()
	 {
    	// Cập nhật vị trí tab
    	currentTab = 2;
    	
    	// Cập nhật dữ liệu 
    	_imageAdapter.updateData(_markManager.getsMarkedImages());
    	
    	// Cập nhật giao diện
    	_listViewFolder.setVisibility(View.GONE);
    	_listViewAlbum.setVisibility(View.GONE);
    	
    	btnUnmarkImage.setVisibility(View.VISIBLE);
    	
    	btnDeleteImage.setVisibility(View.GONE);
    	btnLockImage.setVisibility(View.GONE);
    	btnRemoveFromAlbum.setVisibility(View.GONE);            	
    	btnMarkImage.setVisibility(View.GONE);
    	btnUnlockImage.setVisibility(View.GONE);
    	btnAddToAlbum.setVisibility(View.GONE);
    }
	
	// Khi tab Locks được chọn
	public void onTabLocksSelect()
	{		
    	// Cập nhật vị trí tab
    	currentTab = 3;
    	
    	// Cập nhật dữ liệu 
    	if (_isLogined == true)
    		_imageAdapter.updateData(_lockManager.getsLockedImages());
    	else
    	{
    		_imageAdapter.updateData(new ArrayList<String>());
    		Toast.makeText(_this, "Locked images isn't showed if you don't login", Toast.LENGTH_SHORT).show();
    	}
    	
    	// Cập nhật giao diện
    	_listViewFolder.setVisibility(View.GONE);
    	_listViewAlbum.setVisibility(View.GONE);
    	
    	btnDeleteImage.setVisibility(View.VISIBLE);
		btnUnlockImage.setVisibility(View.VISIBLE);
    	
    	btnLockImage.setVisibility(View.GONE);
    	btnRemoveFromAlbum.setVisibility(View.GONE);
    	btnMarkImage.setVisibility(View.GONE);
    	btnUnmarkImage.setVisibility(View.GONE);
    	btnLockImage.setVisibility(View.GONE);
    	btnAddToAlbum.setVisibility(View.GONE);
    }
	
	// Thực hiện nạp dữ liệu
	// Khi xoay màn hình, thay đổi cấu hình, resume
	public void loadData()
	{		
		// Tạo mới để cập nhật dữ liệu nếu cần
		_folderManager = new FolderManager(this);
		_albumManager = new AlbumManager(this);
		_markManager = new MarkManager(this);
		_lockManager = new LockManager(this);
		
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
				 if (!_albumManager.isAlbum(filePath) && !_folderManager.containsFolder(filePath))
					 _folderManager.addsFolder(file.getAbsolutePath());

				 // Thêm ảnh vào dữ liệu
				 if (_folderManager.containsFolder(filePath))
					 _folderManager.addsImage(file.getAbsolutePath(), f.getAbsolutePath());	
				 else
					 _albumManager.addImage(f.getAbsolutePath());					 
			 }

			 // Kiểm tra phải thư mục không
			 if (f.isDirectory())
				 dirFolder(f);
		 }
	}
	 
	// Lấy đối tượng đại diện cho các đối tượng giao diện cần xữ lý
	private void setAllViewInstances()
	{
		// View cho việc thể hiện các chức năng xử lý ảnh và album
		_scrollViewgroup = (ViewGroup) findViewById(R.id.listViewFunctions);
		_scrollView = (HorizontalScrollView) this.findViewById(R.id.horizontalScrollView1);
		
		// View thể hiện các thư mục/album
		_listViewFolder = (ListView) this.findViewById(R.id.listViewFolder);
		_listViewAlbum = (ListView) this.findViewById(R.id.listViewAlbum);
		_listViewAlbum.setVisibility(View.INVISIBLE);
		
		// View thể hiện danh sách ảnh
		_gridViewImage = (GridView) this.findViewById(R.id.gridViewImage);
		
		// View thể hiện danh sách các mục xem ảnh
		_radioViewgroup = (RadioGroup) findViewById(R.id.radioTabGroup);
		_radioAlbum = (RadioButton) findViewById(R.id.radioAlbum);
		_radioAll = (RadioButton) findViewById(R.id.radioAll);
		_radioMarks = (RadioButton) findViewById(R.id.radioMarks);
		_radioLocks = (RadioButton) findViewById(R.id.radioLocks);
		
		_progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
		
		registerForContextMenu(_gridViewImage);
		registerForContextMenu(_listViewAlbum);
	}
	
	// Xây dựng thanh công cụ chức năng
	public void populateFunctionBar()
	{
		setUpOnClickListeners();
		initializeAllFunctionButtons();
		
		_scrollView.setVisibility(View.GONE);		
	}
	
	// Xây các nút trong thanh công cụ chức năng
	public void initializeAllFunctionButtons()
	{
		int id = 0;
		
		// Delete Image
		btnDeleteImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.delete_image, "Delete Image", onClickDeleteImage);
		
		// Mark Image
		btnMarkImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.mark_image, "Mark Image", onClickMarkImage);
		
		// Unmark Image
		btnUnmarkImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.unmark_image, "Unmark Image", onClickUnmarkImage);
		
		// Lock Image
		btnLockImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.lock_image, "Lock Image", onClickLockImage);
		
		// Unlock Image
		btnUnlockImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.unlock_image, "Unlock Image", onClickUnlockImage);
		
		// Add To Album
		btnAddToAlbum = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.add_image_album, "Add To Album", onClickAddToAlbum);
		
		// Remove From Album
		btnRemoveFromAlbum = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.remove_image_album, "Remove", onClickRemoveFromAlbum);
		
		// Share
		btnShareImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.share, "Share", onClickShareImage);
		
		// Compress
		btnZipAndShare = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.compress, "Zip and Share", onClickZipAndShare);
		
		// Convert To Video
		btnSlideshow = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.convert_video, "Slideshow", onClickSlideshow);
	}
	
	// Xây dựng 1 nút chức năng
	public View setUpFunctionButton(ViewGroup parent, int id, int resourceID, String name, View.OnClickListener onClickEvent)
	{
		// Khởi tạo với layout cho trước
		View btn = getLayoutInflater().inflate(R.layout.function_item, null);	
		
		// Gán ID
		btn.setId(id);
		
		// Lấy thành phần con trong View
		ImageView icon = (ImageView) btn.findViewById(R.id.imageViewFunction);
		TextView content = (TextView) btn.findViewById(R.id.textViewFunction);
		
		// Gán ảnh icon cho nút bấm
		icon.setImageBitmap(ResourceManager.getImageFunctionIcon(this, resourceID));
		
		// Gán tên chức năng cho nút bấm
		content.setText(name);
		
		btn.setTag(name);
		
		// Gán sự kiện click với chức năng
		btn.setOnClickListener(onClickEvent);
		
		parent.addView(btn);
		
		return btn;
	}
    
    // Lấy View tại 1 vị trí i
 	public View getViewByPosition(int pos, GridView listView)
 	{
         final int firstListItemPosition = listView.getFirstVisiblePosition();
         final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;
         
         if (pos < firstListItemPosition || pos > lastListItemPosition) 
             return listView.getAdapter().getView(pos, null, listView);
         else 
             return listView.getChildAt(pos - firstListItemPosition);
     }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// Lấy danh sách các ảnh được chọn
	private ArrayList<String> getSelectedPaths() 
	{
 		int count = _imageAdapter.getCount();
 		ArrayList<String> result = new ArrayList<String>();
 		
        for (int i = 0; i < count; i++) 
        {
            ImageViewHolder holder = (ImageViewHolder) this.getViewByPosition(i, _gridViewImage).getTag();
            
            if (holder.checkbox.isChecked())
            	result.add(holder.filePath);
        }
        
        return result;
 	}
	
	// Khởi tạo sự kiện cho các nút chức năng
	private void setUpOnClickListeners() 
	{
		// Nút xóa ảnh
		onClickDeleteImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteImages(getSelectedPaths()); 
			}
		};
		
		// Nút đánh dấu ảnh
		onClickMarkImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {								
				marksImages(getSelectedPaths());
			}
		};
		
		onClickUnmarkImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				unmarksImages(getSelectedPaths());
			}
		};
		
		onClickAddToAlbum = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				chooseAlbum(getSelectedPaths());				
			}
		};
		
		onClickRemoveFromAlbum = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				removeFromAlbum(getSelectedPaths());				
			}
		};
		
		onClickShareImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				shareImages(getSelectedPaths());
			}				
		};
		
		onClickZipAndShare = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				zipAndShareImages(getSelectedPaths());							
			}
		};
	
		onClickSlideshow = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSlideShow();			
			}
		};
		
		onClickLockImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lockImages(getSelectedPaths());			
			}
		};
		
		onClickUnlockImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				unlockImages(getSelectedPaths());									
			}
		};
	}
	
	// Tạo dialog thê hiện dấu hiệu đang xử lý
	private Dialog getProgressingDialog()
	{
		Dialog dialog = new Dialog(MainActivity.this);
		dialog.setContentView(R.layout.progressing_dialog);
		dialog.setCanceledOnTouchOutside(false);
		dialog.setTitle("Processing");
		return dialog;
	}
	
	// Thực hiện đánh dấu nhiều ảnh
	private void marksImages(ArrayList<String> images)
	{
		if (images.size() == 0)
			return;
		
		MainActivity.cancelLoadImage = true;
		new MarkImagesAsyncTask(getProgressingDialog()).execute(_markManager, images);
	}
	
	// Thực hiện bỏ đánh dấu nhiều ảnh
	private void unmarksImages(ArrayList<String> images)
	{
		if (images.size() == 0)
			return;
		
		MainActivity.cancelLoadImage = true;
		new UnmarkImagesAsyncTask(getProgressingDialog(), _imageAdapter).execute(_markManager, images);
	}
	
	// Thực hiện xóa nhiều ảnh
	private void deleteImages(final ArrayList<String> images) 
    {
    	if (images.size() == 0)
    		return;
    	
    	cancelLoadImage = true;
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(_this);
		builder.setMessage("Are you sure you want to delete?")
			   .setTitle("Delete");
		
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		{		
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// Xử lý xóa ảnh theo tứng mục
				if (_radioAll.isChecked()) 			
					new DeleteImagesInFolderAsyncTask(getProgressingDialog(), _imageAdapter).execute(_folderManager, _markManager, images);
				else if (_radioAlbum.isChecked())	
					new DeleteImagesInAlbumAsyncTask(getProgressingDialog(), _imageAdapter).execute(_albumManager, _markManager, images);	
				else if (_radioLocks.isChecked()) 
					new DeleteLockedImagesAsyncTask(getProgressingDialog(), _imageAdapter).execute(_lockManager, images);
				else
					return;
			}
		});
		
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() 
		{	
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				cancelLoadImage = false;
			}
		});
		
		builder.show();
	}
	 
	// Thực hiện bỏ ảnh khỏi album
    private void removeFromAlbum(ArrayList<String> images) 
    {
    	MainActivity.cancelLoadImage = true;
    	new RemoveImagesFromAlbumAsyncTask(getProgressingDialog(), _imageAdapter).execute(_albumManager, _markManager, images);
    }
    
 	// Hiện dialog dể chọn album 
    // Thực hiện thêm ảnh vào album
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
            public void onClick(DialogInterface dialog, int which) 
            {	
                String albumName = arrayAdapter.getItem(which);
                MainActivity.cancelLoadImage = true;
                new AddImagesToAlbumAsyncTask(getProgressingDialog()).execute(_albumManager, albumName, images);               
            }
        });
    	
    	builderSingle.show();
    }
 	   
    // Thực hiện khóa ảnh
    private void lockImages(ArrayList<String> images) 
    {  	
    	if (_isLogined == false)
    	{
    		Toast.makeText(_this, "Can't do this function if you don't login", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if (images.size() == 0)
			return;
    	
    	MainActivity.cancelLoadImage = true;
    		  	
		if (_radioAll.isChecked())
			new LockImagesInFolderAsyncTask(getProgressingDialog(), _imageAdapter).execute(_lockManager, _folderManager, _markManager, images);
		else if (_radioAlbum.isChecked())
			new LockImagesInAlbumAsyncTask(getProgressingDialog(), _imageAdapter).execute(_lockManager, _albumManager, _markManager, images);
    }
    
    // Thực hiện bỏ khóa ảnh
    private void unlockImages(ArrayList<String> images) 
    {
    	if (_isLogined == false)
    	{
    		Toast.makeText(_this, "Can't do this function if you don't login", Toast.LENGTH_SHORT).show();
    		return;
    	}
    	
    	if (images.size() == 0)
			return;
		
    	MainActivity.cancelLoadImage = true;
    	
    	new UnlockImagesAsyncTask(getProgressingDialog(), _imageAdapter, _folderAdapter).execute(_lockManager, _folderManager, images);
    }
    
	private void doSlideShow()
	{
		cancelLoadImage = true;
		
		final Dialog dialog = new Dialog(_this);
		dialog.setContentView(R.layout.slideshow_dialog);
		dialog.setTitle("Settings");
	
		final TextView txtWait = (TextView) dialog.findViewById(R.id.txtWait);
		final SeekBar sbrWait = (SeekBar) dialog.findViewById(R.id.sbrWait);
		sbrWait.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {					
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}					
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				float wait = ((float) progress) / 2;
				txtWait.setText("Wait: " + wait + "s");
			}
		});
		
		final TextView txtSpeed = (TextView) dialog.findViewById(R.id.txtSpeed);
		final SeekBar sbrSpeed = (SeekBar) dialog.findViewById(R.id.sbrSpeed);
		sbrSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {					
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {}					
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				
				txtSpeed.setText("Spped: " + (progress + 1));
			}
		});
		
		Button btnOk = (Button) dialog.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> paths = getSelectedPaths();
				
	        	// Đóng gói dữ liệu truyền đi
	        	Intent intent = new Intent(_this, ViewImageActivity.class);
	        	
	        	if (paths.size() == 0)
	        		intent.putExtra("filePaths", _imageAdapter.getItems());
	        	else
	        		intent.putExtra("filePaths", paths);
	        	intent.putExtra("position", 0);
	        	intent.putExtra("slideshow", true);
	        	intent.putExtra("internal", true);
	        	intent.putExtra("wait", sbrWait.getProgress() * 500);
	        	intent.putExtra("slide", 3000 / (sbrSpeed.getProgress() + 1));
	        	//_this.startActivity(intent);
	        	_this.startActivityForResult(intent, SLIDE_SHOW);
	        	dialog.dismiss();
			}
		});
		
		Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				cancelLoadImage = false;
				dialog.dismiss();
			}
		});
		
		dialog.show();	
	}
	
 	private String getZipFileName() {
		String folder = ImageSupporter.DEFAULT_PICTUREPATH + File.separator + "zipped";
		File f = new File(folder);
		f.mkdirs();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		sdf.setTimeZone(TimeZone.getDefault());
		String time = sdf.format(new Date());
		
		return folder + File.separator + "GalleryDS_" + time + ".zip";
	}

    private void shareImages(ArrayList<String> images) {
    	if (images.size() == 0)
			return;
		
		ArrayList<Uri> files = new ArrayList<Uri>();		
		for (String path : images) {
			files.add(Uri.fromFile(new File(path)));
		}
		
		Intent intent;
        
        if (files.size() == 1) {
        	intent = new Intent(Intent.ACTION_SEND);
        	intent.putExtra(Intent.EXTRA_STREAM, files.get(0));
        }
        else {
        	intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        	intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
        }
        
        intent.setType("image/*");
        _this.startActivity(intent);
    }
    
    private void zipAndShareImages(ArrayList<String> images) {
    	if (images.size() == 0)
			return;
		
		final int BUFFER = 2048; 
		String zipFile = getZipFileName();
		File zipped = new File(zipFile);		
				
		try {
			zipped.createNewFile();
			
			BufferedInputStream origin = null; 
			FileOutputStream dest = new FileOutputStream(zipFile);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			
			byte data[] = new byte[BUFFER];
			
			for(int i=0; i < images.size(); i++) { 
				FileInputStream fi = new FileInputStream(images.get(i)); 
				origin = new BufferedInputStream(fi, BUFFER); 
				
				ZipEntry entry = new ZipEntry(images.get(i).substring(images.get(i).lastIndexOf("/") + 1)); 
				out.putNextEntry(entry); 
				
	        	int count; 
	        	while ((count = origin.read(data, 0, BUFFER)) != -1) { 
	        		out.write(data, 0, count); 
	        	} 
	        	origin.close(); 
			} 
	 
			out.close(); 
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(zipped));        
        intent.setType("application/zip");
        
        _this.startActivity(intent);	
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        
    	super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        _contextPosition = info.position;
        
        if (_radioAll.isChecked())
            inflater.inflate(R.menu.context_menu_all, menu);
        if (_radioAlbum.isChecked()) {
        	if (v instanceof ListView) {
        		inflater.inflate(R.menu.context_menu_album_item, menu);
        		_contextPosition = -1;
        		_albumContextPosition = info.position;
        	}
        	else
        		inflater.inflate(R.menu.context_menu_albums, menu);
        }
        if (_radioMarks.isChecked())
            inflater.inflate(R.menu.context_menu_marks, menu);
        if (_radioLocks.isChecked())
            inflater.inflate(R.menu.context_menu_locks, menu);            
        
        menu.setHeaderTitle("Choose an action");    
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	ArrayList<String> paths = new ArrayList<String>();
    	String path = null;
    	if (_contextPosition != -1) {
    		path = (String) _imageAdapter.getItem(_contextPosition);   
    		paths.add(path);
    	}		
		
    	if (item.getTitle().equals("Delete")) {			
			deleteImages(paths);
		}

    	if (item.getTitle().equals("Mark")) {
    		_markManager.marksImage(path);
    	}
    	
    	if (item.getTitle().equals("Unmark")) {
    		_markManager.unmarksImage(path);
    		_imageAdapter.updateData(_markManager.getsMarkedImages());
    	}
    	
    	if (item.getTitle().equals("Share")) {
    		shareImages(paths);
    	}
    	
    	if (item.getTitle().equals("Zip and share")) {
    		zipAndShareImages(paths);
    	}
    	
    	if (item.getTitle().equals("Lock")) {
    		lockImages(paths);
    	}
    	
    	if (item.getTitle().equals("Unlock")) {
    		unlockImages(paths);
    	}
    	
    	if (item.getTitle().equals("Add to album")) {
    		chooseAlbum(paths);
    	}
    	
    	if (item.getTitle().equals("Remove from album")) {
    		removeFromAlbum(paths);
    	}
    	
    	
    	if (item.getTitle().equals("Rename")) {
    		
    		final String oldName = _albumManager.getsAlbumList().get(_albumContextPosition);
    		
    		AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("Rename album");
            alertDialog.setMessage("Enter new name");
            
            final EditText input = new EditText(MainActivity.this);  
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                  LinearLayout.LayoutParams.MATCH_PARENT,
                                  LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            String newName = input.getText().toString();
                            if (_albumManager.renamesAlbum(oldName, newName)) {
                            	_albumAdapter.updateData(_albumManager.getsAlbumList());
                            	Toast.makeText(_this, "Album renamed", Toast.LENGTH_SHORT).show();
                            }
                            else
                            	Toast.makeText(_this, "Existed name! Fail to rename album!", Toast.LENGTH_SHORT).show();
                        }
                    });
            
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();
    	}
    	
    	if (item.getTitle().equals("Delete album")) {
    		AlertDialog.Builder builder = new AlertDialog.Builder(_this);
    		builder.setMessage("Are you sure you want to delete?")
    			   .setTitle("Delete");
    		
    		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
    			
    			@Override
    			public void onClick(DialogInterface dialog, int which) 
    			{
    				String name = _albumManager.getsAlbumList().get(_albumContextPosition);
    				Dialog progressDialog = MainActivity.this.getProgressingDialog();
    				
    				cancelLoadImage = true;
    				new DeleteAlbumAsyncTask(progressDialog, _listViewAlbum, _albumAdapter, _imageAdapter).execute(name, _albumManager, _albumContextPosition, _selectedAlbumName);
    				
    				
    				/*if (_albumManager.deletesAlbum(name)) {
    					_listViewAlbum.setItemChecked(_albumContextPosition, false);
    					_albumAdapter.updateData(_albumManager.getsAlbumList());
    					_imageAdapter.updateData(new ArrayList<String>());
    					
                    	Toast.makeText(_this, "Album deleted", Toast.LENGTH_SHORT).show();
    				} else
    					Toast.makeText(_this, "Failed to delete album", Toast.LENGTH_SHORT).show();*/
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
    	
        return true;
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		if (id == R.id.addNewAlbum)
		{
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            alertDialog.setTitle("New Album");
            alertDialog.setMessage("Enter Album name");
            
            final EditText input = new EditText(MainActivity.this);  
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                  LinearLayout.LayoutParams.MATCH_PARENT,
                                  LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            alertDialog.setView(input);

            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int which) {
                            String name = input.getText().toString();
                            if (_albumManager.createsAlbum(name)) {
                            	_albumAdapter.updateData(_albumManager.getsAlbumList());
                            	Toast.makeText(_this, "Album created", Toast.LENGTH_SHORT).show();
                            }
                            else
                            	Toast.makeText(_this, "Existed name! Fail to create album!", Toast.LENGTH_SHORT).show();
                        }
                    });
            
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            alertDialog.show();			
			return true;
		}
		else if (id == R.id.selectMode)
		{
			if (_scrollView.getVisibility() == View.GONE)
			{
				_scrollView.setVisibility(View.VISIBLE);		
				//Animation animation = AnimationUtils.loadAnimation(_this, R.animator.up_from_bottom);
				//_scrollView.startAnimation(animation);
				
				
				ImageAdapter.SELECT_MODE = View.VISIBLE;
				//_imageAdapter.refresh();
			}
			else if (_scrollView.getVisibility() == View.VISIBLE)
			{
				_scrollView.setVisibility(View.GONE);
				ImageAdapter.SELECT_MODE = View.INVISIBLE;
				//_imageAdapter.refresh();
			}
			
			return true;
		}
		else if (id == R.id.login)
		{
			if (_isLogined == false && UserInfo.isEmpty(_this))
    			showsSignUpDialog();
    		else if (_isLogined == false)
    			showsLoginDialog();
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == SLIDE_SHOW) {
			cancelLoadImage = false;
		}
		
	}
	
	
}
