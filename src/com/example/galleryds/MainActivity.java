package com.example.galleryds;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;


public class MainActivity extends Activity {

    TabHost _mainTabHost;

    protected AlbumManager _albumManager;
    protected ImageManager _imageManager;

    private boolean _inSelectionMode;
    private int _lastTab;
    private int _contextPosition;
    
    private boolean _addToAlbumViaContext;
    
    private Context _this;

    private static final int ADD_ALBUM = 0;
    private static final int EDIT_ALBUM = 1;

    // Các thành phần trên toolbar
    private LinearLayout llSelect;
    private ImageButton btnSelect;
    private TextView tvSelect;
    
    private View vwFavourite;
    private LinearLayout llFavourite;
    private ImageButton btnFavourite;
    private TextView tvFavourite;
    
    private View vwDelete;
    private LinearLayout llDelete;
    private ImageButton btnDelete;
    private TextView tvDelete;
    
    private View vwAdd;
    private LinearLayout llAdd;
    private ImageButton btnAdd;
    private TextView tvAdd;
    
    private Animation ani1;
    private Animation ani2;
    private Animation ani3;
    private Animation ani4;
  
    private ArrayList<GridView> _gridViewList;
    private float _sXPoint = -1;
    private boolean _flag = true;
    
    private boolean rotated;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        rotated = ImageManager.hasInstance();

        // Khởi tạo dữ liệu cho hệ thống
        initializeSystem();
        
        // Load dữ liệu của các tab thể hiện
        loadTabs();
      
        // Load dữ liệu album
        _albumManager.loadAlbums();
        
        // Chiến lược load dữ liệu bình thường
        // doStrategyLoading_V1();
        
        // Sử dụng load dữ liệu hướng bất đồng bộ
        doStrategyLoading_V2();
        
        int a = _imageManager.getNumberOfImages();
        Toast.makeText(this, String.valueOf(a), Toast.LENGTH_SHORT).show();

        // Tạo các sự kiện lắng nghe cho các View
        setOnTabChangedListener_MainTabHost();

        registerForContextMenu(_imageManager.getGridViewAll());
        registerForContextMenu(_albumManager.getGridViewAlbum());
        registerForContextMenu(_imageManager.getGridViewFavourite());

        setOnClickListener_btnSelect();
        setOnClickListener_btnFavourite();
        setOnClickListener_btnDelete();
        setOnClickListener_btnAdd();   
        setOnClickListener_GridViewAlbumItem();      
        //setTouchEventForTabHost();
        
        
        // Tự động xuất hiện dưới cùng danh sách
        _imageManager.getGridViewAll().post(new Runnable() { 
            public void run() { 
            	_imageManager.getGridViewAll().setSelection(_imageManager.getNumberOfImages() - 1);
            } 
        });   
    }

    protected void doStrategyLoading_V1()
    {
    	// Load thông tin ảnh
    	// Giải mã cập nhật
    	loadImages();
    	
    	// Load ảnh ưa thích
    	_imageManager.loadFavouriteImages();
    }
    
    // Chiến lược
    // B1: Tải thông tin tất cả ảnh lên, không giải mã, cho vào adapter (giữ chỗ)
    // B2: Dùng bất đồng bộ để giải mã
    protected void doStrategyLoading_V2()
    {
    	// Load thông tin ảnh
    	if (!rotated) {
    		loadImages_V2();
    	
    	// Giải mã và cập nhật
    	 new DecodeImagesTask().execute(_imageManager);
    	}
    	else
    		_imageManager._allImageAdapter.notifyDataSetChanged();
    	 
    	// Load ảnh ưa thích
    	 new LoadFavouriteImageTask().execute(_imageManager);
    }
    
    protected void initializeSystem()
    {
    	_this = this;
    	_addToAlbumViaContext = false;
    	
    	_albumManager = AlbumManager.getInstance(this, (GridView) findViewById(R.id.gridView3));
    	_imageManager = ImageManager.getInstance(this, (GridView) findViewById(R.id.gridView1), (GridView) findViewById(R.id.gridView2));
    	
    	if (rotated) {
    		_imageManager.onRotateScreen(this, (GridView) findViewById(R.id.gridView1), (GridView) findViewById(R.id.gridView2));
    		_albumManager.onRotateScreen(this, (GridView) findViewById(R.id.gridView3));
    	}
        llSelect = (LinearLayout) findViewById(R.id.llSelect);
        tvSelect = (TextView) findViewById(R.id.tvSelect);
        btnSelect = (ImageButton) findViewById(R.id.btnSelect);
        
        vwFavourite = (View) findViewById(R.id.vwFavourite);
        llFavourite = (LinearLayout) findViewById(R.id.llFavourite);
        tvFavourite = (TextView) findViewById(R.id.tvFavourite);
        btnFavourite = (ImageButton) findViewById(R.id.btnFavourite);
        
        vwAdd = (View) findViewById(R.id.vwAdd);
        llAdd = (LinearLayout) findViewById(R.id.llAdd);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        
        vwDelete = (View) findViewById(R.id.vwDelete);
        llDelete = (LinearLayout) findViewById(R.id.llDelete);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        
        _inSelectionMode = false;
        _lastTab = 0;
        
        ani1 = AnimationUtils.loadAnimation(this, R.animator.out_to_left);
		ani2 = AnimationUtils.loadAnimation(this, R.animator.in_from_right);
		ani3 = AnimationUtils.loadAnimation(this, R.animator.out_to_right);
		ani4 = AnimationUtils.loadAnimation(this, R.animator.in_from_left);
				
		ani1.setStartTime(Animation.START_ON_FIRST_FRAME);
		ani2.setStartTime(Animation.START_ON_FIRST_FRAME);
		ani3.setStartTime(Animation.START_ON_FIRST_FRAME);
		ani4.setStartTime(Animation.START_ON_FIRST_FRAME);
		
		_gridViewList = new ArrayList<GridView>();
		_gridViewList.add(_imageManager.getGridViewAll());
		_gridViewList.add(_albumManager.getGridViewAlbum());
		_gridViewList.add(_imageManager.getGridViewFavourite());
    }
   
    // Thêm ảnh đã chọn vào album
    public void addSelectedImagesToAlbum(String albumName) {
        int count = _imageManager.getNumberOfImages();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _imageManager.getGridViewAll());

            ViewHolder holder = (ViewHolder) view.getTag();

            // Kiểm tra cái nào được chọn
            if (holder.checkbox.isChecked() == true) {
        		_albumManager.addImageToAlbum(_imageManager.getImageDataById(holder.id), albumName);
        		if (_addToAlbumViaContext) {
        			_addToAlbumViaContext = false;
        			holder.checkbox.setChecked(false);
        			return;
        		}
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
                MainActivity main = (MainActivity) arrayAdapter.getContext();
                
                // Thực hiện thêm ảnh vào album
                main.addSelectedImagesToAlbum(strName);
            }
        });
    	
    	builderSingle.show();
    }
      
    // Nạp toàn bộ ảnh trong máy lên
    public void loadImages() 
    {
        File imageDir = new File(Environment.getExternalStorageDirectory().toString());

        // Duyệt đệ quy
        if (imageDir.exists()) 
            dirFolder(imageDir);     
    }

    // Duyệt và tìm kiếm tất cả tập tin hình ảnh
    public void dirFolder(File file) {
    	
    	// Kiểm tra tên có hợp lệ không
        if (file.getName().startsWith(".") || file.getName().startsWith("com.")
        		|| file.getName().equals("thumbnails"))
            return;

        File[] files = file.listFiles();

        // Kiễm tra các tập tin con có rỗng không
        if (files == null)
            return;

        for (File f : files) {
        	
        	// Kiểm tra xem có phải ảnh không
            if (ImageSupporter.isImage(f)) 
            {
            	// Tạo mới dữ liệu của ảnh
                //Bitmap b = ImageSupporter.decodeSampledBitmapFromFile(f, 100, 100);
                DataHolder dataHolder = new DataHolder(f.getAbsolutePath(), null, f.lastModified());
                
                // Nập dữ liệu vào bộ quản lý ảnh
                _imageManager.insertImageData(dataHolder);
                _imageManager.addImageData(f.getAbsolutePath(), dataHolder);
                
                // Nạp dữ liệu vào bộ quản lý album
                String parentName = f.getParentFile().getName();
                if (_albumManager.containsAlbum(parentName))
                	_albumManager.insertImageDataToAlbum(parentName, dataHolder);
            }

            // Kiểm tra phải thư mục không
            if (f.isDirectory())
                dirFolder(f);
        }

    }
    
    // Nạp toàn bộ ảnh trong máy lên
    public void loadImages_V2() 
    {
        File imageDir = new File(Environment.getExternalStorageDirectory().toString());

        // Duyệt đệ quy
        if (imageDir.exists()) 
            dirFolder_V2(imageDir);     
    }

    // Duyệt và tìm kiếm tất cả tập tin hình ảnh
    // Không giải mã ảnh
    public void dirFolder_V2(File file) {
    	
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
            	// Tạo mới dữ liệu của ảnh
                DataHolder dataHolder = new DataHolder(f.getAbsolutePath(), null, f.lastModified());
                
                // Nập dữ liệu vào bộ quản lý ảnh
                _imageManager.insertImageData(dataHolder);
                _imageManager.addImageData(f.getAbsolutePath(), dataHolder);
                
                // Nạp dữ liệu vào bộ quản lý album
                String parentName = f.getParentFile().getName();
                if (_albumManager.containsAlbum(parentName))
                	_albumManager.insertImageDataToAlbum(parentName, dataHolder);
            }

            // Kiểm tra phải thư mục không
            if (f.isDirectory())
                dirFolder(f);
        }

    }
        
    // Gọi hàm này để chạy sự kiện mới: xem ảnh trong album
    public void viewAllAlbumImages(String albumName)
    {
    	Intent intent = new Intent(this, ViewAlbumImagesActivity.class);
    	
    	// Đóng gói dữ liệu gửi sang activity mới
    	Bundle bundle = new Bundle();
    	bundle.putString("AlbumName", albumName);

    	intent.putExtras(bundle);
    	startActivity(intent);
    	
    	// Animation cho chuyển cảnh
    	MainActivity.this.overridePendingTransition(R.animator.animator_slide_in_right, R.animator.animator_zoom_out);
    }
       
    // Tạo nút cho toolbar. Phải thay đổi giá trị _inSelectionMode trước khi gọi
    private void populateToolbar() {
        int currentTab = _mainTabHost.getCurrentTab();
        if (_inSelectionMode) {
            switch (currentTab) {
                case 0: // tab All
                    btnSelect.setImageResource(R.drawable.deselect);
                    tvSelect.setText("Deselect");

                    vwFavourite.setVisibility(View.VISIBLE);
                    llFavourite.setVisibility(View.VISIBLE);
                    btnFavourite.setImageResource(R.drawable.favourite);
                    tvFavourite.setText("Favourite");

                    vwDelete.setVisibility(View.VISIBLE);
                    llDelete.setVisibility(View.VISIBLE);

                    vwAdd.setVisibility(View.VISIBLE);
                    llAdd.setVisibility(View.VISIBLE);
                    btnAdd.setImageResource(R.drawable.add);
                    tvAdd.setText("Add to Album");

                    break;

                case 1: // tab Albums
                    btnSelect.setImageResource(R.drawable.deselect);
                    tvSelect.setText("Deselect");

                    vwFavourite.setVisibility(View.GONE);
                    llFavourite.setVisibility(View.GONE);

                    vwDelete.setVisibility(View.VISIBLE);
                    llDelete.setVisibility(View.VISIBLE);

                    vwAdd.setVisibility(View.GONE);
                    llAdd.setVisibility(View.GONE);

                    break;

                case 2: // tab Favourite
                    btnSelect.setImageResource(R.drawable.deselect);
                    tvSelect.setText("Deselect");

                    vwFavourite.setVisibility(View.VISIBLE);
                    llFavourite.setVisibility(View.VISIBLE);
                    btnFavourite.setImageResource(R.drawable.defavourite);
                    tvFavourite.setText("Remove");

                    vwDelete.setVisibility(View.GONE);
                    llDelete.setVisibility(View.GONE);

                    vwAdd.setVisibility(View.GONE);
                    llAdd.setVisibility(View.GONE);

                    break;
            }
        } else { // not in slection mode

            btnSelect.setImageResource(R.drawable.select);
            tvSelect.setText("Select");

            vwFavourite.setVisibility(View.GONE);
            llFavourite.setVisibility(View.GONE);

            vwDelete.setVisibility(View.GONE);
            llDelete.setVisibility(View.GONE);

            vwAdd.setVisibility(View.VISIBLE);
            llAdd.setVisibility(View.VISIBLE);
            btnAdd.setImageResource(R.drawable.add);
            tvAdd.setText("Add Album");

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        
    	super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED) {

            String name = data.getStringExtra("Name");
            String event = data.getStringExtra("Event");
            String note = data.getStringExtra("Note");

            switch (requestCode) {
                case ADD_ALBUM:
                    _albumManager.createAlbum(name);	// Tạo album mới
                    break;

                case EDIT_ALBUM:
                	// Cập nhật thông tin album
                	GridView gridView = _albumManager.getGridViewAlbum();
                    AlbumViewHolder holder = (AlbumViewHolder) ImageSupporter.getViewByPosition(_contextPosition, gridView).getTag();
                    _albumManager.renameAlbum(holder.textview.getText().toString(), name);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        
    	super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        
        switch (_mainTabHost.getCurrentTab()) 
        {
            case 0: inflater.inflate(R.menu.context_menu_all, menu); break;
            case 1: inflater.inflate(R.menu.context_menu_albums, menu); break;
            case 2: inflater.inflate(R.menu.context_menu_favourite, menu); break;
        }
        
        menu.setHeaderTitle("Choose an action");

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        _contextPosition = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        
    	GridView gridView;
    	switch (_mainTabHost.getCurrentTab()) 
    	{
            case 0: // tab All
            	gridView = _imageManager.getGridViewAll(); 
                final ViewHolder holder = (ViewHolder) ImageSupporter.getViewByPosition(_contextPosition, gridView).getTag();
                
                holder.checkbox.setChecked(true);
                
                if (item.getTitle().equals("Delete")) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(_this);
     				builder.setMessage("Are you sure you want to delete?")
     					   .setTitle("Delete");
     				
     				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
     					
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						_imageManager.deleteSelectedImage(_imageManager.getImageDataById(holder.id));							
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
                
                if (item.getTitle().equals("Add to Favourite")) 
                    _imageManager.addImageToFavourite(_imageManager.getImageDataById(holder.id));
                
                if (item.getTitle().equals("Add to Album")) {
                	_addToAlbumViaContext = true;
                	chooseAlbum();        		
                }
                
                break;

            case 1: // tab Albums
            	gridView = _albumManager.getGridViewAlbum();
                final AlbumViewHolder holder1 = (AlbumViewHolder) ImageSupporter.getViewByPosition(_contextPosition, gridView).getTag();

                if (item.getTitle().equals("Delete Album")) {
                	AlertDialog.Builder builder = new AlertDialog.Builder(_this);
     				builder.setMessage("Are you sure you want to delete?")
     					   .setTitle("Delete");
     				
     				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
     					
     					@Override
     					public void onClick(DialogInterface dialog, int which) {
     						_albumManager.deleteAlbum(holder1.textview.getText().toString());							
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
                
                if (item.getTitle().equals("Edit")) 
                {
                    Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                    albumIntent.putExtra("Name", holder1.textview.getText().toString());
                    MainActivity.this.startActivityForResult(albumIntent, EDIT_ALBUM);
                    MainActivity.this.overridePendingTransition(R.animator.animator_slide_in_right, R.animator.animator_zoom_out);
                }
                
                break;

            case 2: // tab Favourite
                ViewHolder holder2 = (ViewHolder) ImageSupporter.getViewByPosition(_contextPosition, _imageManager.getGridViewFavourite()).getTag();
                _imageManager.removeImageFromFavourite(_imageManager.getFavouriteImageByID(holder2.id));
                break;
        }
        return true;
    }

    // Nạp các tab cần thể hiện cho tabhost
    public void loadTabs() {
    	
        // Lấy TabHost từ Id cho trước
        _mainTabHost = (TabHost) findViewById(R.id.tabhost);
        _mainTabHost.setup();

        // Tạo cấu hình cho tab1 : tab chứa toàn bộ ảnh
        TabSpec spec = _mainTabHost.newTabSpec("tab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("All");
        _mainTabHost.addTab(spec);

        // Tạo cấu hình cho tab2 : tab chứa toàn bộ album ảnh
        spec = _mainTabHost.newTabSpec("tab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Albums");
        _mainTabHost.addTab(spec);

        // Tạo cấu hình cho tab3 : tab chứa toàn bộ ảnh yêu thích
        spec = _mainTabHost.newTabSpec("tab3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Favourite");
        _mainTabHost.addTab(spec);

        //Thiết lập tab mặc định được chọn ban đầu là tab 0
        //_mainTabHost.setCurrentTab(0);
    }

    // Sự kiện cho việc quẹt trái phải để chuyển tab 
    public void setTouchEventForTabHost()
	{
		int numberOfTabs = _mainTabHost.getTabWidget().getChildCount();
        for(int i = 0; i < numberOfTabs; i++)
        {
        	GridView tab = _gridViewList.get(i);
        	tab.setOnTouchListener(new  View.OnTouchListener() {
            	
    			@Override
    			public boolean onTouch(View v, MotionEvent event) {
    				// TODO Auto-generated method stub
    				
    	        	final int X = (int) event.getRawX();
    				final int Y = (int) event.getRawY();
    				
    				switch (event.getAction() & MotionEvent.ACTION_MASK) 
    				{
    					case MotionEvent.ACTION_DOWN:
    						if (_sXPoint == -1)
    							_sXPoint = X;
    						break;
    					case MotionEvent.ACTION_UP:
    						_sXPoint = -1;
    						break;
    					case MotionEvent.ACTION_POINTER_DOWN:
    						break;
    					case MotionEvent.ACTION_POINTER_UP:
    						break;
    					case MotionEvent.ACTION_MOVE:
    						
    						if (_sXPoint != -1)
    						{
	    						int currentTabID =  _mainTabHost.getCurrentTab();
	    						int nTabs = _mainTabHost.getTabWidget().getChildCount();
	    						int nextTabID = -1;
	    						Animation ani = null;
	    						
	    						if (_sXPoint - X > 50)
	    						{
	    							nextTabID = (currentTabID + 1) % nTabs;
	    							ani = ani2;
	    						}
	    						else if (_sXPoint - X < -50)
	    						{
	    							if (currentTabID - 1 < 0)
	    								nextTabID = nTabs - 1;
	    							else 
	    								nextTabID = currentTabID - 1;
	    							   							
	    							ani = ani4;

	    						}
	    						
	    						if (ani != null)
	    						{
		    						_sXPoint = -1;
	    							_gridViewList.get(nextTabID).setAnimation(ani);
	    							_gridViewList.get(nextTabID).getAnimation().start();
		    						
	    							_flag = false;
		    						_mainTabHost.setCurrentTab(nextTabID);
	    						}
    						}
    						break;
    				}
    							
    				return true;
    			}
    		});
        } 
	}
	   
    @Override
    protected void onResume() {

        super.onResume();

        //LoadImages();
    }
  
    public void setOnClickListener_GridViewAlbumItem()
    {
    	_albumManager.getGridViewAlbum().setOnItemClickListener(new OnItemClickListener() {
			
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	ViewHolder holder = (ViewHolder) view.getTag();
            	
            	if (holder.checkbox.getVisibility() == View.VISIBLE)
            		holder.checkbox.setChecked(!holder.checkbox.isChecked());
            	
            }
        });
    }
    
    public void setOnTabChangedListener_MainTabHost(){
    	_mainTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (_inSelectionMode) {
                    switch (_lastTab) {
                        case 0: // tab All
                        	_imageManager.turnOffAllImagesSelectionMode();
                            break;
                        case 1: // tab Albums
                        	_albumManager.turnOffSelectionAlbumMode();
                            break;
                        case 2: // tab Favourite
                        	_imageManager.turnOffFavouriteSelectionMode();
                            break;
                    }
                }
                _lastTab = _mainTabHost.getCurrentTab();
                _inSelectionMode = false;

                populateToolbar();
                
                if (_flag == true)
                {
	                View nView = _gridViewList.get(ConvertIdToIndex(tabId));            
	                
					nView.setAnimation(ani4);
					nView.getAnimation().start();
                }
                
                _flag = true;
            }
        });
    }
    
    public int ConvertIdToIndex(String tabID)
	{
		if (tabID == "tab1" || tabID == "0")
			return 0;
		else if (tabID == "tab2" || tabID == "1")
			return 1;
		else if (tabID == "tab3" || tabID == "2")
			return 2;
		
		return -1;
	}
    
    public void setOnClickListener_btnSelect(){
    	btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentTab = _mainTabHost.getCurrentTab();
                if (_inSelectionMode) {
                    switch (currentTab) {
                        case 0: // tab All
                        	// Tắt chế độ chọn
                        	_imageManager.turnOffAllImagesSelectionMode();
                            break;
                        case 1: // tab Albums
                        	// Tắt chế độ chọn
                        	_albumManager.turnOffSelectionAlbumMode();
                            break;
                        case 2: // tab Favourite
                        	// Tắt chế độ chọn
                        	_imageManager.turnOffFavouriteSelectionMode();
                            break;
                    }
                    
                    _inSelectionMode = false;
                } else {
                    switch (currentTab) {
                        case 0: // tab All
                        	// Bật chế độ chọn
                        	_imageManager.turnOnAllImagesSelectionMode();
                        	
                            break;
                        case 1: // tab Albums
                        	// Bật chế độ chọn
                        	_albumManager.turnOnSelectionAlbumMode();
                            break;
                        case 2: // tab Favourite
                        	// Bật chế độ chọn
                        	_imageManager.turnOnFavouriteSelectionMode();
                            break;
                    }
                    
                    _inSelectionMode = true;
                }

                populateToolbar();
            }
        });
    }
    
    public void setOnClickListener_btnFavourite(){
        btnFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_mainTabHost.getCurrentTab() == 0)
                	_imageManager.addToFavourite();	// thêm ảnh vào mục ưa thích
                else
                	_imageManager.removeFromFavourite(); // bỏ ảnh ra khỏi mục ưa thích
            }
        });
    }
    
    public void setOnClickListener_btnDelete()
    {
    	 btnDelete.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
            	 
            	AlertDialog.Builder builder = new AlertDialog.Builder(_this);
 				builder.setMessage("Are you sure you want to delete?")
 					   .setTitle("Delete");
 				
 				builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
 					
 					@Override
 					public void onClick(DialogInterface dialog, int which) {
 						if (_mainTabHost.getCurrentTab() == 0)
 		                 	_imageManager.deleteSelectedImages(); // Xóa ảnh
 		                 else {
 		                 	_albumManager.deleteSelectedAlbums(); // Xóa album
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
         });
    }
    
    public void setOnClickListener_btnAdd()
    {
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!_inSelectionMode) {
                	// Chuyển sang activity tạo album
                    Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                    
                    // Animation cho chuyện cảnh
                    MainActivity.this.startActivityForResult(albumIntent, ADD_ALBUM);
                    MainActivity.this.overridePendingTransition(R.animator.animator_slide_in_right, R.animator.animator_zoom_out);
                }
                else 
                	chooseAlbum();	// Kích hoạt sự kiện chọn album 
            }
        });
    }
    
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
        return super.onOptionsItemSelected(item);
    }

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		
		if (savedInstanceState != null) {
			_mainTabHost.setCurrentTab(savedInstanceState.getInt("currentTab"));
		}
		
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		
		outState.putInt("currentTab", _mainTabHost.getCurrentTab());
		
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
    
    
}