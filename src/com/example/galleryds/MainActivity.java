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
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeSystem();

        //_gridViewAlbumImages = (GridView) findViewById(R.id.gridView4);
      //Toast.makeText(context, "Create Album Successfully", Toast.LENGTH_SHORT).show();
        // Xử lý lỗi
       // Toast.makeText(context, "Album Have Existed", Toast.LENGTH_SHORT).show();
        
        loadTabs();

        _albumManager.loadAlbums();
        this.loadImages();
        _imageManager.loadFavouriteImages();

        setOnTabChangedListener_MainTabHost();

        registerForContextMenu(_imageManager.getGridViewAll());
        registerForContextMenu(_albumManager.getGridViewAlbum());
        registerForContextMenu(_imageManager.getGridViewFavourite());

        setOnClickListener_btnSelect();
        setOnClickListener_btnFavourite();
        setOnClickListener_btnDelete();
        setOnClickListener_btnAdd();   
        setOnClickListener_GridViewAlbumItem();      
        setTouchEventForTabHost();
    }

    protected void initializeSystem()
    {
    	_albumManager = AlbumManager.CreateInstance(this, (GridView) findViewById(R.id.gridView3));
    	_imageManager = ImageManager.CreateInstance(this, (GridView) findViewById(R.id.gridView1), (GridView) findViewById(R.id.gridView2));
    	
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
                        	_imageManager.turnOffAllImagesSelectionMode();
                            break;
                        case 1: // tab Albums
                        	_albumManager.turnOffSelectionAlbumMode();
                            break;
                        case 2: // tab Favourite
                        	_imageManager.turnOffFavouriteSelectionMode();
                            break;
                    }
                    
                    _inSelectionMode = false;
                } else {
                    switch (currentTab) {
                        case 0: // tab All
                        	_imageManager.turnOnAllImagesSelectionMode();
                            break;
                        case 1: // tab Albums
                        	_albumManager.turnOnSelectionAlbumMode();
                            break;
                        case 2: // tab Favourite
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
                	_imageManager.addToFavourite();
                else
                	_imageManager.removeFromFavourite();
            }
        });
    }
    
    public void setOnClickListener_btnDelete()
    {
    	 btnDelete.setOnClickListener(new View.OnClickListener() {

             @Override
             public void onClick(View v) {
                 if (_mainTabHost.getCurrentTab() == 0)
                 	_imageManager.deleteSelectedImages(_albumManager);
                 else {
                 	_albumManager.deleteSelectedAlbums();
                 }
             }
         });
    }
    
    public void setOnClickListener_btnAdd()
    {
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!_inSelectionMode) {
                    Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                    MainActivity.this.startActivityForResult(albumIntent, ADD_ALBUM);
                }
                else 
                	chooseAlbum();
            }
        });
    }
    
    // Thêm ảnh đã chọn vào album
    public void addSelectedImagesToAlbum(String albumName) {
        int count = _imageManager.getNumberOfImages();

        for (int i = count - 1; i >= 0; i--) {
            View view = ImageSupporter.getViewByPosition(i, _imageManager.getGridViewAll());

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                
            	File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            	File f =  (File) holder.checkbox.getTag();
            	
            	if (f.getParent() != path.getAbsolutePath() + File.pathSeparator + albumName)
            	{
            		ImageSupporter.moveFile(f.getParent(), f.getName(), path.getAbsolutePath() + File.separator + albumName);
            		_albumManager.addImageToAlbum(_imageManager.getImageDataByName(f.getAbsolutePath()), albumName);
            	}
            }
        }
    }   
    
    // Hiện dialog dể chọn album 
    public void chooseAlbum()
    {
    	AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
    	builderSingle.setIcon(R.drawable.ic_launcher);
    	builderSingle.setTitle("Select One Album: ");

    	final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
    	
    	arrayAdapter.addAll(_albumManager.getAlbumList());

    	builderSingle.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	            @Override
    	            public void onClick(DialogInterface dialog, int which) {
    	                dialog.dismiss();
    	            }
    	        });

    	builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
    	            @Override
    	            public void onClick(DialogInterface dialog, int which) {
    	                String strName = arrayAdapter.getItem(which);
    	                MainActivity main = (MainActivity) arrayAdapter.getContext();
    	                main.addSelectedImagesToAlbum(strName);
    	            }
    	        });
    	
    	builderSingle.show();
    }
    
    // Gọi hàm này để chạy sự kiện mới: xem ảnh trong album
    public void viewAllAlbumImages(String albumName)
    {
    	Intent intent = new Intent(this, ViewAlbumImagesActivity.class);
    	
    	Bundle bundle = new Bundle();
    	bundle.putString("AlbumName", albumName);

    	intent.putExtras(bundle);
    	startActivity(intent);
    }
       
    // Nạp toàn bộ ảnh trong máy lên
    public void loadImages() 
    {
        File imageDir = new File(Environment.getExternalStorageDirectory().toString());

        if (imageDir.exists()) 
            dirFolder(imageDir);     
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
        _mainTabHost.setCurrentTab(0);
    }

    // Duyệt và tìm kiếm tất cả tập tin hình ảnh
    public void dirFolder(File file) {
        if (file.getName().equals(".thumbnails"))
            return;

        File[] files = file.listFiles();

        if (files == null)
            return;

        for (File f : files) {
            if (ImageSupporter.isImage(f)) 
            {
                Bitmap b = ImageSupporter.decodeSampledBitmapFromFile(f, 100, 100);
                DataHolder dataHolder = new DataHolder(f, b);
                
                _imageManager.getImageList().add(dataHolder);
                _imageManager.addImageData(f.getAbsolutePath(), dataHolder);
                
                String parentName = f.getParentFile().getName();
                if (_albumManager.containsAlbum(parentName))
                	_albumManager.getAlbumData(parentName).add(dataHolder);
            }

            if (f.isDirectory())
                dirFolder(f);
        }

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
                    _albumManager.createAlbum(name);
                    break;

                case EDIT_ALBUM:
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
                ViewHolder holder = (ViewHolder) ImageSupporter.getViewByPosition(_contextPosition, gridView).getTag();
                
                holder.checkbox.setChecked(true);
                
                if (item.getTitle().equals("Delete"))
                	_imageManager.deleteSelectedImage(_imageManager.getImageDataById(holder.id), _albumManager);
                
                if (item.getTitle().equals("Add to Favourite")) 
                    _imageManager.addImageToFavourite(_imageManager.getImageDataById(holder.id));
                
                if (item.getTitle().equals("Add to Album"))
                	chooseAlbum();
        		
                holder.checkbox.setChecked(true);
                
                break;

            case 1: // tab Albums
            	gridView = _albumManager.getGridViewAlbum();
                AlbumViewHolder holder1 = (AlbumViewHolder) ImageSupporter.getViewByPosition(_contextPosition, gridView).getTag();

                if (item.getTitle().equals("Delete Album"))
                    _albumManager.deleteAlbum(holder1.textview.getText().toString());
                
                if (item.getTitle().equals("Edit")) 
                {
                    Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                    albumIntent.putExtra("Name", holder1.textview.getText().toString());
                    MainActivity.this.startActivityForResult(albumIntent, EDIT_ALBUM);
                }
                
                break;

            case 2: // tab Favourite
                ViewHolder holder2 = (ViewHolder) ImageSupporter.getViewByPosition(_contextPosition, _imageManager.getGridViewFavourite()).getTag();
                _imageManager.removeImageFromFavourite(holder2.id);
                break;
        }
        return true;
    }

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
	    						
	    						if (_sXPoint - X > 50)
	    						{
	    							nextTabID = (currentTabID + 1) % nTabs;
	    							_sXPoint = -1;
	    							_gridViewList.get(nextTabID).setAnimation(ani2);
	    							_gridViewList.get(nextTabID).getAnimation().start();
		    						
	    							_flag = false;
		    						_mainTabHost.setCurrentTab(nextTabID);
	    						}
	    						else if (_sXPoint - X < -50)
	    						{
	    							if (currentTabID - 1 < 0)
	    								nextTabID = nTabs - 1;
	    							else 
	    								nextTabID = currentTabID - 1;
	    							
	    							_sXPoint = -1;
	    							_gridViewList.get(nextTabID).setAnimation(ani4);
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
}