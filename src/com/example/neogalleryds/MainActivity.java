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
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
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
	private View btnDeleteAlbum;
	
	private View btnMarkImage;
	private View btnUnmarkImage;
	
	private View btnLockImage;
	private View btnUnlockImage;
	
	private View btnAddToAlbum;
	private View btnRemoveFromAlbum;
	
	private View btnShareImage;
	private View btnCompressImage;
	
	private View btnEditImage;
	
	private View btnConvertToVideo;
	
	// Các xữ lý chức năng cho sự kiện nhấn nút.
	private OnClickListener onClickSlideshow;
	private OnClickListener onClickCompressImage;
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
	private int _selectedTab = 0;
	
	// Adapter
	private FolderAdapter _folderAdapter;
	private ImageAdapter _imageAdapter;
	private AlbumAdapter _albumAdapter;
	
	// Các thuộc tính quản lý
	private FolderManager _folderManager;
	private AlbumManager _albumManager;
	private MarkManager _markManager;
	private LockManager _lockManager;

	
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
				
		// mặc định hiển thị ảnh chụp từ camera
		ArrayList<String> folderPaths = _folderManager.getsFolderPathList();	
		String dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath()
				+ File.separator + "Camera";
		int defaultPos = 0;
		for (int i = 0; i < folderPaths.size(); i++) {
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
		
		_radioViewgroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
	    {
	        @Override
	        public void onCheckedChanged(RadioGroup group, int checkedId) 
	        {
	            if (_radioAll.isChecked())
	            {
	            	_listViewFolder.setVisibility(View.VISIBLE);
	            	_listViewAlbum.setVisibility(View.GONE);
	            }
	            else if (_radioAlbum.isChecked())
	            {
	            	_listViewAlbum.setVisibility(View.VISIBLE);
	            	_listViewFolder.setVisibility(View.INVISIBLE);
	            }
	            else if (_radioLocks.isChecked())
	            {
	            	_listViewFolder.setVisibility(View.GONE);
	            	_listViewAlbum.setVisibility(View.GONE);
	            }
	            else if (_radioMarks.isChecked())
	            {
	            	_listViewFolder.setVisibility(View.GONE);
	            	_listViewAlbum.setVisibility(View.GONE);
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
				 String parent = file.getAbsolutePath();
				 
				 // Chưa có thư mục này, thêm vào dữ liệu
				 if (!_folderManager.containsFolder(parent))
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
		
		// Delete Album
		btnDeleteAlbum = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.delete_album, "Delete Album", onClickDeleteAlbum);
		
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
		btnCompressImage = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.compress, "Compress", onClickCompressImage);
		
		// Convert To Video
		btnConvertToVideo = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.convert_video, "Convert", onClickSlideshow);
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
	
	// Bật chế độ chọn hình ảnh
    public void turnOnSelectionMode(GridView gridView, ImageAdapter adapter) 
    {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) 
        {
            View view = this.getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }  
 
    // Tắt chế độ chọn hình ảnh
    public void turnOffSelectionMode(GridView gridView, ImageAdapter adapter) 
    {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = this.getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.INVISIBLE);
            holder.checkbox.setChecked(false);
        }
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
	
	private void setUpOnClickListeners() {
		onClickDeleteImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(_this);
 				builder.setMessage("Are you sure you want to delete?")
 					   .setTitle("Delete");
 				
 				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface dialog, int which) {
 						if (_radioAll.isChecked()) { // xoá ảnh
 							ArrayList<String> selectedItems = getSelectedPaths(_gridViewImage, _imageAdapter);
 							_imageAdapter.removeImages(selectedItems);
 		                 	if (_folderManager.deletesImages(selectedItems))
 		                 		Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT);
 						}
 		                 else {
 		                 	//_albumManager.deleteSelectedAlbums(); // Xóa album
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
		};
		
		onClickShareImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (_radioAll.isChecked()) {
					ArrayList<String> paths = getSelectedPaths(_gridViewImage, _imageAdapter);
					if (paths.size() == 0)
						return;
					
					ArrayList<Uri> files = new ArrayList<Uri>();		
					for (String path : paths) {
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
			}
		};
		
		onClickCompressImage = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<String> paths = getSelectedPaths(_gridViewImage, _imageAdapter);
				if (paths.size() == 0)
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
					
					for(int i=0; i < paths.size(); i++) { 
						FileInputStream fi = new FileInputStream(paths.get(i)); 
						origin = new BufferedInputStream(fi, BUFFER); 
						ZipEntry entry = new ZipEntry(paths.get(i).substring(paths.get(i).lastIndexOf("/") + 1)); 
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
		};
	
		onClickSlideshow = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ArrayList<String> paths = getSelectedPaths(_gridViewImage, _imageAdapter);
				if (paths.size() == 0)
					return;
	        	// Đóng gói dữ liệu truyền đi
	        	Intent intent = new Intent(_this, ViewImageActivity.class);
	        	
	        	intent.putExtra("filePaths", paths);
	        	intent.putExtra("position", 0);
	        	intent.putExtra("slideshow", true);
	        	_this.startActivity(intent);
			}
		};
	}
	
	private ArrayList<String> getSelectedPaths(GridView gridView, ImageAdapter adapter) {
 		int count = adapter.getCount();
 		ArrayList<String> result = new ArrayList<String>();
 		
        for (int i = 0; i < count; i++) 
        {
            View view = this.getViewByPosition(i, gridView);
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
