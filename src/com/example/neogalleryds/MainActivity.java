package com.example.neogalleryds;

import java.io.File;
import java.util.ArrayList;


import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	private ViewGroup _scrollViewgroup;
	private ResourceManager _resource;
	private HorizontalScrollView _scrollView;
	private ListView _listViewFolder;
	private FolderAdapter _folderAdapter;
	private ArrayList<String> _data;
	
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
	private OnClickListener onClickConvertToVideo;
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
	
	protected int _lastIndex = -1;
	
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_resource = new ResourceManager(this);
		_scrollViewgroup = (ViewGroup) findViewById(R.id.groupView1);
		_scrollView = (HorizontalScrollView) this.findViewById(R.id.horizontalScrollView1);
		_listViewFolder = (ListView) this.findViewById(R.id.listView1);
		
		File imageDir = new File(Environment.getExternalStorageDirectory().toString());
		_data = new ArrayList<String>();
		
        // Duyệt đệ quy theo chiến lược
        if (imageDir.exists()) 
            dirFolder(imageDir);
        
		_folderAdapter = new FolderAdapter(this, _data);
		_listViewFolder.setAdapter(_folderAdapter);
		_listViewFolder.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		
		_listViewFolder.setOnItemClickListener(new OnItemClickListener() {
			
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	if (_lastIndex != -1)
            		_listViewFolder.setItemChecked(_lastIndex, false);
            	
            	_listViewFolder.setItemChecked(position, true);
            	_lastIndex = position;
            	
                _folderAdapter.setChecked(view);
                
                Toast.makeText(MainActivity.this, "kuro", Toast.LENGTH_SHORT).show();
            }
        });

		populateFunctionBar();
	}
	
	 public void dirFolder(File file) {
	    	
    	// Kiểm tra tên có hợp lệ không
        if (file.getName().startsWith(".") || file.getName().startsWith("com."))
            return;

        File[] files = file.listFiles();

        // Kiễm tra các tập tin con có rỗng không
        if (files == null)
            return;

        for (File f : files) {
        	
        	// Kiểm tra xem có phải ảnh không
            if (ImageSupporter.isImage(f)) 
            {
            	String path = f.getParentFile().getAbsolutePath();
            	
            	if (!_data.contains(path))
            		_data.add(path);
            }

            // Kiểm tra phải thư mục không
            if (f.isDirectory())
                dirFolder(f);
        }

	}
	 
	public void populateFunctionBar()
	{
		initializeAllFunctionButtons();
		_scrollView.setVisibility(View.GONE);
	}
	
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
		btnConvertToVideo = setUpFunctionButton(_scrollViewgroup, id++, R.drawable.convert_video, "Convert", onClickConvertToVideo);
	}
	
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
		icon.setImageBitmap(ResourceManager.getImageFunctionIcon(this.getResources(), resourceID));
		
		// Gán tên chức năng cho nút bấm
		content.setText(name);
		
		btn.setTag(name);
		
		// Gán sự kiện click với chức năng
		btn.setOnClickListener(onClickEvent);
		
		parent.addView(btn);
		
		return btn;
	}
	
	View.OnClickListener onClickDelete = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Toast.makeText(MainActivity.this, String.valueOf(v.getTag()), Toast.LENGTH_SHORT).show();			
		}
	};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
			return true;
		}
		else if (id == R.id.selectMode)
		{
			if (_scrollView.getVisibility() == View.GONE)
				_scrollView.setVisibility(View.VISIBLE);
			else if (_scrollView.getVisibility() == View.VISIBLE)
				_scrollView.setVisibility(View.GONE);
			
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
