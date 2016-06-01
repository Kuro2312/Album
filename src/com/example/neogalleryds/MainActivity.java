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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import Adapter.AlbumAdapter;
import Adapter.FolderAdapter;
import Adapter.ImageAdapter;
import Adapter.ViewHolder;
import BusinessLayer.AlbumManager;
import BusinessLayer.FolderManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.LockManager;
import BusinessLayer.MarkManager;
import BusinessLayer.ResourceManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
	private int _lastIndex = -1;
	private int _contextPosition;
	private int _albumContextPosition;
	public static int currentTab = 0;
	
	// Adapter
	private FolderAdapter _folderAdapter;
	public static ImageAdapter _imageAdapter;
	private AlbumAdapter _albumAdapter;
	
	// Các thuộc tính quản lý
	public static FolderManager _folderManager;
	public static AlbumManager _albumManager;
	public static MarkManager _markManager;
	public static LockManager _lockManager;

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
		
		//_lockManager.locksImage(this, _folderManager.getsFolderImages(ImageSupporter.DEFAULT_PICTUREPATH).get(0));
		//_lockManager.locksImage(this, ImageSupporter.DEFAULT_PICTUREPATH + File.separator + );
	}
	
	// Cài đặt sự kiện chọn 1 item trong danh sách thư mục
	public void setOnFolderSelected()
	{			
		// mặc định hiển thị ảnh chụp từ camera
		ArrayList<String> folderPaths = _folderManager.getsFolderPathList();	
		String dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "Camera";
		
		int defaultPos = 0;
		for (int i = 0; i < folderPaths.size(); i++) 
		{
			if (folderPaths.get(i).equals(dcim)) {
				defaultPos = i;
				break;
			}
		}
		
		_imageAdapter.updateData(_folderManager.getsFolderImages(folderPaths.get(defaultPos)));
		_listViewFolder.setItemChecked(defaultPos, true);
		_lastIndex = defaultPos;

		_listViewFolder.setOnItemClickListener(new OnItemClickListener() 
		{	
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	if (_lastIndex != -1)
            		_listViewFolder.setItemChecked(_lastIndex, false);
            	
            	_listViewFolder.setItemChecked(position, true);
            	_lastIndex = position;
            	
            	// Thay dữ liệu vô
        		_imageAdapter.updateData(_folderManager.getsFolderImages((String)_folderAdapter.getItem(position)));
            	
                //_folderAdapter.setChecked(view);
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
                
            	if (_lastIndex != -1)
            		_listViewFolder.setItemChecked(_lastIndex, false);
            	
            	_listViewFolder.setItemChecked(position, true);
            	_lastIndex = position;
            	
            	// Thay dữ liệu vô
        		_imageAdapter.updateData(_albumManager.getsAlbumImages((String) _albumAdapter.getItem(position)));
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
    	
		_radioViewgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            if (_radioAll.isChecked())
	            {
	            	currentTab = 0;
	            	String currentFolder = (String)_folderAdapter.getItem(_listViewFolder.getCheckedItemPosition());
	            	_imageAdapter.updateData(_folderManager.getsFolderImages(currentFolder));
	            	_listViewFolder.setVisibility(View.VISIBLE);
	            	_listViewAlbum.setVisibility(View.GONE);
	            	
	            	btnAddToAlbum.setVisibility(View.VISIBLE);
	            	btnMarkImage.setVisibility(View.VISIBLE);
	            	btnLockImage.setVisibility(View.VISIBLE);
	            	
	            	btnUnmarkImage.setVisibility(View.GONE);
	            	btnUnlockImage.setVisibility(View.GONE);
	            	btnRemoveFromAlbum.setVisibility(View.GONE);
	            }
	            else if (_radioAlbum.isChecked())
	            {
	            	currentTab = 1;
	            	int pos = _listViewAlbum.getCheckedItemPosition();
	            	if (pos == ListView.INVALID_POSITION)
	            		_imageAdapter.updateData(new ArrayList<String>());
	            	else
	            		_imageAdapter.updateData(_albumManager.getsAlbumImages(_albumManager.getsAlbumList().get(pos)));
	            	_listViewAlbum.setVisibility(View.VISIBLE);
	            	_listViewFolder.setVisibility(View.INVISIBLE);
	            	
	            	btnMarkImage.setVisibility(View.VISIBLE);
	            	btnLockImage.setVisibility(View.VISIBLE);
	            	btnRemoveFromAlbum.setVisibility(View.VISIBLE);
	            	
	            	btnUnmarkImage.setVisibility(View.GONE);
	            	btnUnlockImage.setVisibility(View.GONE);
	            	btnAddToAlbum.setVisibility(View.GONE);
	            }
	            else if (_radioMarks.isChecked())
	            {
	            	currentTab = 2;
	            	_imageAdapter.updateData(_markManager.getsMarkedImages());
	            	_listViewFolder.setVisibility(View.GONE);
	            	_listViewAlbum.setVisibility(View.GONE);
	            	
	            	btnUnmarkImage.setVisibility(View.VISIBLE);
	            	
	            	btnLockImage.setVisibility(View.GONE);
	            	btnRemoveFromAlbum.setVisibility(View.GONE);            	
	            	btnMarkImage.setVisibility(View.GONE);
	            	btnUnlockImage.setVisibility(View.GONE);
	            	btnAddToAlbum.setVisibility(View.GONE);
	            }
	            else if (_radioLocks.isChecked())
	            {
	            	currentTab = 3;
	            	_imageAdapter.updateData(_lockManager.getsLockedImages());
	            	_listViewFolder.setVisibility(View.GONE);
	            	_listViewAlbum.setVisibility(View.GONE);
	            	
            		btnUnlockImage.setVisibility(View.VISIBLE);
	            	
	            	btnLockImage.setVisibility(View.GONE);
	            	btnRemoveFromAlbum.setVisibility(View.GONE);            	
	            	btnUnmarkImage.setVisibility(View.GONE);
	            	btnLockImage.setVisibility(View.GONE);
	            	btnAddToAlbum.setVisibility(View.GONE);
	            }
	        }
	    });
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
	 
	// Lấy đối tượng đại diện cho các đối tượng giao diện cần xữ lý
	private void setAllViewInstances()
	{
		// View cho việc thể hiện các chức năng xử lý ảnh và album
		_scrollViewgroup = (ViewGroup) findViewById(R.id.groupView1);
		_scrollView = (HorizontalScrollView) this.findViewById(R.id.horizontalScrollView1);
		
		// View thể hiện các thư mục/album
		_listViewFolder = (ListView) this.findViewById(R.id.listView1);
		_listViewAlbum = (ListView) this.findViewById(R.id.listViewAlbum);
		_listViewAlbum.setVisibility(View.GONE);
		
		// View thể hiện danh sách ảnh
		_gridViewImage = (GridView) this.findViewById(R.id.gridView1);
		
		// View thể hiện danh sách các mục xem ảnh
		_radioViewgroup = (RadioGroup) findViewById(R.id.radioTabGroup);
		_radioAlbum = (RadioButton) findViewById(R.id.radioAlbum);
		_radioAll = (RadioButton) findViewById(R.id.radioAll);
		_radioMarks = (RadioButton) findViewById(R.id.radioMarks);
		_radioLocks = (RadioButton) findViewById(R.id.radioLocks);
		
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
	
	private void setUpOnClickListeners() 
	{
		onClickDeleteImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				deleteImages(getSelectedPaths()); 
			}
		};
		
		onClickMarkImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				_markManager.marksImages(getSelectedPaths());								
			}
		};

		onClickUnmarkImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				_markManager.unmarksImages(getSelectedPaths());
				_imageAdapter.updateData(_markManager.getsMarkedImages());
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
				ArrayList<String> paths = getSelectedPaths();
				removeFromAlbum(paths);				
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

				final Dialog dialog = new Dialog(_this);
				dialog.setContentView(R.layout.slideshow_dialog_layout);
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
			        	intent.putExtra("wait", sbrWait.getProgress() * 500);
			        	intent.putExtra("slide", 3000 / (sbrSpeed.getProgress() + 1));
			        	_this.startActivity(intent);
			        	dialog.dismiss();
					}
				});
				
				Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
				btnCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

				dialog.show();				
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
	
	private ArrayList<String> getSelectedPaths() {
 		int count = _imageAdapter.getCount();
 		ArrayList<String> result = new ArrayList<String>();
 		
        for (int i = 0; i < count; i++) 
        {
            View view = this.getViewByPosition(i, _gridViewImage);
            ViewHolder holder = (ViewHolder) view.getTag();
            if (holder.checkbox.isChecked()) {
            	result.add(holder.filePath);
            }
        }
        
        return result;
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
 	
    private void deleteImages(final ArrayList<String> images) {
    	if (images.size() == 0)
    		return;
    	
		AlertDialog.Builder builder = new AlertDialog.Builder(_this);
		builder.setMessage("Are you sure you want to delete?")
			   .setTitle("Delete");
		
		builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				_imageAdapter.removeImages(images);
				
				if (_radioAll.isChecked()) { 
					
					// Xóa ảnh trong thư mục					
                 	if (_folderManager.deletesImages(images))
                 		Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                 	else
                 		Toast.makeText(getApplicationContext(), "Fail To Delete", Toast.LENGTH_SHORT).show();
				
				} else if (_radioAlbum.isChecked()) {
					
                	// Xóa ảnh trong album			
                 	if (_albumManager.deletesImages(images))
                 		Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                 	else
                 		Toast.makeText(getApplicationContext(), "Fail To Delete", Toast.LENGTH_SHORT).show();
                
				} else if (_radioMarks.isChecked()) {
					
				}
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
    
    private void lockImages(ArrayList<String> images) {
    	if (images.size() == 0)
			return;
		
		_lockManager.locksImages(images);
		
		if (_radioAll.isChecked())
			_folderManager.deletesImages(images);
		else
			_albumManager.deletesImages(images);
		
		_markManager.unmarksImages(images);
    }
    
    private void unlockImages(ArrayList<String> images) {
    	if (images.size() == 0)
			return;
		
		_lockManager.unlocksImages(images);
		
		// Xóa sạch dữ liệu 
		_folderManager.getsFolderImages(ImageSupporter.DEFAULT_PICTUREPATH).clear();
		
		// Duyệt lại dữ liệu
		File file = new File(ImageSupporter.DEFAULT_PICTUREPATH);
		File[] files = file.listFiles();
		
		for (File f : files)
			if (ImageSupporter.isImage(f))
				_folderManager.addsImage(ImageSupporter.DEFAULT_PICTUREPATH, f.getAbsolutePath());
    }
    
    private void removeFromAlbum(ArrayList<String> images) {
    	for (String path : images) {
    		_albumManager.removesImageFromAlbum(path);
    	}
    	int pos = _listViewAlbum.getCheckedItemPosition();
    	_imageAdapter.updateData(_albumManager.getsAlbumImages(_albumManager.getsAlbumList().get(pos)));
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
                            	Toast.makeText(_this, "Failed to rename album", Toast.LENGTH_SHORT).show();
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
    				if (_albumManager.deletesAlbum(name)) {
    					_listViewAlbum.setItemChecked(_albumContextPosition, false);
    					_albumAdapter.updateData(_albumManager.getsAlbumList());
    					_imageAdapter.updateData(new ArrayList<String>());
    					
                    	Toast.makeText(_this, "Album deleted", Toast.LENGTH_SHORT).show();
    				} else
    					Toast.makeText(_this, "Failed to delete album", Toast.LENGTH_SHORT).show();
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
		if (id == R.id.action_settings) {
			return true;
		}
		else if (id == R.id.addNewAlbum)
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
                            	Toast.makeText(_this, "Failed to create album", Toast.LENGTH_SHORT).show();
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
		return super.onOptionsItemSelected(item);
	}
}
