package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

	public static ImageAdapter _albumImagesAdapter = null;
	
    TabHost _mainTabHost;

    // Cho việc thao tác trên mục toàn bô ảnh
    GridView _gridViewAll;
    ArrayList<DataHolder> _allImageData;
    private ImageAdapter _allImageAdapter;

    // Cho việc thao tác trên mục album ảnh
    GridView _gridViewAlbum;
    private AlbumAdapter _albumAdapter;
    ArrayList<String> _albumList;
    
    // Cho việc thao tác trên mục yêu thích
    GridView _gridViewFavourite;
    ArrayList<DataHolder> _favouriteData;
    private ImageAdapter _favouriteAdapter;

    // Các HashMap cần sử dụng 
    HashMap<String, ArrayList<DataHolder>> _albumData;
    HashMap<String, DataHolder> _allMap;
    HashMap<String, String> _favouriteMap;

    //Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _gridViewAll = (GridView) findViewById(R.id.gridView1);
        _gridViewFavourite = (GridView) findViewById(R.id.gridView2);
        _gridViewAlbum = (GridView) findViewById(R.id.gridView3);
        //_gridViewAlbumImages = (GridView) findViewById(R.id.gridView4);

        loadTabs();

        _mainTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (_inSelectionMode) {
                    switch (_lastTab) {
                        case 0: // tab All
                            turnOffSelectionMode(_gridViewAll, _allImageAdapter);
                            break;
                        case 1: // tab Albums
                        	turnOffSelectionAlbumMode(_gridViewAlbum, _albumAdapter);
                            break;
                        case 2: // tab Favourite
                            turnOffSelectionMode(_gridViewFavourite, _favouriteAdapter);
                            break;
                    }
                }
                _lastTab = _mainTabHost.getCurrentTab();
                _inSelectionMode = false;

                populateToolbar();
            }
        });

        loadAlbums();
        loadImages();
        loadFavouriteImages();

        registerForContextMenu(_gridViewAll);
        registerForContextMenu(_gridViewAlbum);
        registerForContextMenu(_gridViewFavourite);

        _inSelectionMode = false;
        _lastTab = 0;

        llSelect = (LinearLayout) findViewById(R.id.llSelect);
        tvSelect = (TextView) findViewById(R.id.tvSelect);
        btnSelect = (ImageButton) findViewById(R.id.btnSelect);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int currentTab = _mainTabHost.getCurrentTab();
                if (_inSelectionMode) {
                    switch (currentTab) {
                        case 0: // tab All
                            turnOffSelectionMode(_gridViewAll, _allImageAdapter);
                            break;
                        case 1: // tab Albums
                        	turnOffSelectionAlbumMode(_gridViewAlbum, _albumAdapter);
                            break;
                        case 2: // tab Favourite
                            turnOffSelectionMode(_gridViewFavourite, _favouriteAdapter);
                            break;
                    }
                    _inSelectionMode = false;
                } else {
                    switch (currentTab) {
                        case 0: // tab All
                            turnOnSelectionMode(_gridViewAll, _allImageAdapter);
                            break;
                        case 1: // tab Albums
                            turnOnSelectionAlbumMode(_gridViewAlbum, _albumAdapter);
                            break;
                        case 2: // tab Favourite
                            turnOnSelectionMode(_gridViewFavourite, _favouriteAdapter);
                            break;
                    }
                    _inSelectionMode = true;
                }

                populateToolbar();
            }
        });

        vwFavourite = (View) findViewById(R.id.vwFavourite);
        llFavourite = (LinearLayout) findViewById(R.id.llFavourite);
        tvFavourite = (TextView) findViewById(R.id.tvFavourite);
        btnFavourite = (ImageButton) findViewById(R.id.btnFavourite);
        btnFavourite.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_mainTabHost.getCurrentTab() == 0)
                    addToFavourite(_gridViewAll, _allImageAdapter);
                else
                    removeFromFavourite(_gridViewFavourite, _favouriteAdapter);
            }
        });

        vwDelete = (View) findViewById(R.id.vwDelete);
        llDelete = (LinearLayout) findViewById(R.id.llDelete);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        btnDelete = (ImageButton) findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (_mainTabHost.getCurrentTab() == 0)
                    deleteSelectedImages(_gridViewAll, _allImageAdapter);
                else {
                	deleteSelectedAlbums(_gridViewAlbum, _albumAdapter);
                }
            }
        });

        vwAdd = (View) findViewById(R.id.vwAdd);
        llAdd = (LinearLayout) findViewById(R.id.llAdd);
        tvAdd = (TextView) findViewById(R.id.tvAdd);
        btnAdd = (ImageButton) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!_inSelectionMode) {
                    Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                    MainActivity.this.startActivityForResult(albumIntent, ADD_ALBUM);
                } else {

                }
            }
        });
        
        _gridViewAlbum.setOnItemClickListener(new OnItemClickListener() {
			
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
            	ViewHolder holder = (ViewHolder) view.getTag();
            	
            	if (holder.checkbox.getVisibility() == View.VISIBLE)
            		holder.checkbox.setChecked(!holder.checkbox.isChecked());
            	
            }
        });
    }

    // Gọi hàm này để chạy sự kiện mới: xem ảnh trong album
    public void viewAllAlbumImages(String albumName)
    {
    	ArrayList dataHolder = _albumData.get(albumName);
    	
    	if (_albumImagesAdapter == null)
    		_albumImagesAdapter = new ImageAdapter(this, dataHolder);
    	else
    		_albumImagesAdapter.updateData(dataHolder);	
    	
    	Intent intent = new Intent(this, ViewAlbumImagesActivity.class);
    	intent.putExtra("AlbumName", albumName);
    	startActivity(intent);
    }
    
    // Thêm vào favourite
    // Ghi thêm vào file
    public void addToFavourite(GridView gridview, ImageAdapter adapter) {
        int count = adapter.getCount();
        ArrayList<String> newFavourite = new ArrayList<String>();

        for (int i = count - 1; i >= 0; i--) {
            View view = getViewByPosition(i, gridview);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                holder.checkbox.setChecked(false);

                DataHolder data = (DataHolder) adapter.getItem(holder.id);

                if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == false) {
                    newFavourite.add(data._file.getAbsolutePath());
                    _favouriteAdapter.add(data);
                    _favouriteMap.put(data._file.getAbsolutePath(), data._file.getAbsolutePath());
                }

                holder.checkbox.setChecked(true);
            }
        }

        ImageSupporter.addNewFavouriteImagePaths(this, newFavourite);
    }

    // Xóa khỏi favourite
    // Luu lai vào file
    public void removeFromFavourite(GridView gridview, ImageAdapter adapter) {
        int count = adapter.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = getViewByPosition(i, gridview);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                DataHolder data = (DataHolder) adapter.getItem(holder.id);

                if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == true) {
                    _favouriteAdapter.remove(data);
                    _favouriteMap.remove(data._file.getAbsolutePath());
                }
            }
        }

        ImageSupporter.saveFavouriteImagePaths(this, new ArrayList<String>(_favouriteMap.values()));
    }

    // Nạp toàn bộ ảnh trong máy lên
    public void loadImages() {
        File imageDir = new File(Environment.getExternalStorageDirectory().toString());

        if (imageDir.exists()) {
            _allImageData = new ArrayList<DataHolder>();
            _allMap = new HashMap<String, DataHolder>();

            dirFolder(imageDir);

            _allImageAdapter = new ImageAdapter(this, _allImageData);
            _gridViewAll.setAdapter(_allImageAdapter);
        }
    }

    // Nạp các ảnh ưa thích lên
    public void loadFavouriteImages() {
        ArrayList<String> data = ImageSupporter.getFavouriteImagePaths(this);
        _favouriteData = new ArrayList<DataHolder>();
        _favouriteMap = new HashMap<String, String>();

        _favouriteAdapter = new ImageAdapter(this, _favouriteData);
        _gridViewFavourite.setAdapter(_favouriteAdapter);

        if (data != null)
        {
            for (int i = 0; i < data.size(); i++) 
            {
                DataHolder d = _allMap.get(data.get(i));

                if (d != null) {
                    _favouriteData.add(d);
                    _favouriteMap.put(d._file.getAbsolutePath(), d._file.getAbsolutePath());
                }
            }
        }
    }

    //ArrayList<String> data;
    // Nạp các album lên
    public void loadAlbums()
    {
        _albumList = ImageSupporter.getAlbumPaths(this);

        if (_albumList == null)
        	_albumList = new ArrayList<String>();
        
        _albumAdapter = new AlbumAdapter(this, _albumList);
        _gridViewAlbum.setAdapter(_albumAdapter);
        
        _albumData = new HashMap<String, ArrayList<DataHolder>>();
        
        for (int i = 0; i < _albumList.size(); i++)
        	_albumData.put(_albumList.get(i), new ArrayList<DataHolder>());
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

    // Tạo mới Album
    public void createAlbum(String name) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // Kiểm tra có tạo folder thành công không
        if (ImageSupporter.createFolder(path.getAbsolutePath(), name)) {
            // Cập nhật giao diện
            ArrayList<String> data = new ArrayList<String>();
            data.add(name);
            ImageSupporter.addNewAlbumPaths(this, data);
            _albumAdapter.add(name);

            Toast.makeText(this, "Create Album Successfully", Toast.LENGTH_SHORT).show();

        } else {
            // Xử lý lỗi
            Toast.makeText(this, "Fail To Create Album", Toast.LENGTH_SHORT).show();
        }
    }

    // Đổi tên 1 Album
    public void renameAlbum(String oldName, String newName) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File album = new File(path.getAbsolutePath() + File.separator + oldName);

        ArrayList<String> data = ImageSupporter.getAlbumPaths(this);
        for(int i = 0; i < data.size(); i++) {
            if (data.get(i).equals(oldName))
                data.set(i, newName);
        }
        ImageSupporter.saveAlbumPaths(this, data);

        _albumAdapter.remove(oldName);
        _albumAdapter.add(newName);

        ImageSupporter.renameFile(album, newName);
    }

    // Xóa toàn bộ ảnh trong album
    public void deleteWholeAlbum(String name) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        ImageSupporter.deleteWholeFolder(path.getAbsolutePath(), name);

        // Cập nhật giao diện
    }

    // Chỉ xóa album và chuyển ảnh sang chỗ khác
    public void deleteAlbum(String name) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File folder = new File(path.getAbsolutePath() + File.separator + name);

        // Kiểm tra có tồn tại folder đó không
        if (folder.exists()) {
            File[] files = folder.listFiles();

            if (files != null)
                for (File f : files)
                    ImageSupporter.moveFile(f.getParent(), f.getName(), path.getAbsolutePath());

            folder.delete();

            Toast.makeText(this, "Successfully", Toast.LENGTH_SHORT).show();
        }

        ArrayList<String> data = ImageSupporter.getAlbumPaths(this);
        for(int i = 0; i < data.size(); i++)
            if (data.get(i).equals(name)) {
                data.remove(i);
                break;
            }
        ImageSupporter.saveAlbumPaths(this, data);
        _albumAdapter.remove(name);
    }

    // Duyệt và tìm kiếm tất cả tập tin hình ảnh
    public void dirFolder(File file) {
        if (file.getName().equals(".thumbnails"))
            return;

        File[] files = file.listFiles();

        if (files == null)
            return;

        for (File f : files) {
            if (ImageSupporter.isImage(f)) {
                Bitmap b = ImageSupporter.decodeSampledBitmapFromFile(f, 100, 100);

                DataHolder dataHolder = new DataHolder(f, b);
                _allImageData.add(dataHolder);
                _allMap.put(f.getAbsolutePath(), dataHolder);
                
                String parentName = f.getParentFile().getName();
                if (_albumData.containsKey(parentName))
                	_albumData.get(parentName).add(dataHolder);
            }

            if (f.isDirectory())
                dirFolder(f);
        }

    }

    // Bật chế độ chọn hình ảnh
    public void turnOnSelectionAlbumMode(GridView gridView, ArrayAdapter adapter) {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = getViewByPosition(i, gridView);

            AlbumViewHolder holder = (AlbumViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }
    
    // Bật chế độ chọn hình ảnh
    public void turnOnSelectionMode(GridView gridView, ArrayAdapter adapter) {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }
    
 // Bật chế độ chọn hình ảnh
    public void turnOffSelectionAlbumMode(GridView gridView, ArrayAdapter adapter) {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = getViewByPosition(i, gridView);

            AlbumViewHolder holder = (AlbumViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.INVISIBLE);
            holder.checkbox.setChecked(false);
        }
    }

    
    // Xóa ảnh đã chọn
    public void deleteSelectedAlbums(GridView gridView, ArrayAdapter adapter) {
        int count = adapter.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = getViewByPosition(i, gridView);

            AlbumViewHolder holder = (AlbumViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
            	deleteAlbum(holder.textview.getText().toString());
            }
        }
    }
    
    // Xóa ảnh đã chọn
    public void deleteSelectedImages(GridView gridView, ArrayAdapter adapter) {
        int count = adapter.getCount();

        for (int i = count - 1; i >= 0; i--) {
            View view = getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true) {
                adapter.remove(holder.id);
            }
        }
    }

    // Tắt chế độ chọn hình ảnh
    public void turnOffSelectionMode(GridView gridView, ArrayAdapter adapter) {
        int count = adapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = getViewByPosition(i, gridView);

            ViewHolder holder = (ViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.INVISIBLE);
            holder.checkbox.setChecked(false);
        }
    }

    public View getViewByPosition(int pos, GridView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
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
                    createAlbum(name);
                    break;

                case EDIT_ALBUM:
                    AlbumViewHolder holder = (AlbumViewHolder) getViewByPosition(_contextPosition, _gridViewAlbum).getTag();
                    renameAlbum(holder.textview.getText().toString(), name);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        switch (_mainTabHost.getCurrentTab()) {
            case 0: inflater.inflate(R.menu.context_menu_all, menu); break;
            case 1: inflater.inflate(R.menu.context_menu_albums, menu); break;
            case 2: inflater.inflate(R.menu.context_menu_favourite, menu); break;
        }
        menu.setHeaderTitle("Choose an action");

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        _contextPosition = info.position;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (_mainTabHost.getCurrentTab()) {
            case 0: // tab All
                ViewHolder holder = (ViewHolder) getViewByPosition(_contextPosition, _gridViewAll).getTag();

                if (item.getTitle().equals("Delete"))
                    _allImageAdapter.remove(holder.id);
                if (item.getTitle().equals("Add to Favourite")) {
                    ArrayList<String> newFavourite = new ArrayList<String>();
                    DataHolder data = (DataHolder) _allImageAdapter.getItem(holder.id);

                    if (_favouriteMap.containsKey(data._file.getAbsolutePath()) == false) {
                        newFavourite.add(data._file.getAbsolutePath());
                        _favouriteAdapter.add(data);
                        _favouriteMap.put(data._file.getAbsolutePath(), data._file.getAbsolutePath());
                    }

                    ImageSupporter.addNewFavouriteImagePaths(this, newFavourite);
                }
                //if (item.getTitle().equals("Add to Album"))

                break;

            case 1: // tab Albums
                AlbumViewHolder holder1 = (AlbumViewHolder) getViewByPosition(_contextPosition, _gridViewAlbum).getTag();

                if (item.getTitle().equals("Delete Album")) {
                    deleteAlbum(holder1.textview.getText().toString());
                }
                if (item.getTitle().equals("Edit")) {
                    Intent albumIntent = new Intent(MainActivity.this, AlbumActivity.class);
                    albumIntent.putExtra("Name", holder1.textview.getText().toString());
                    MainActivity.this.startActivityForResult(albumIntent, EDIT_ALBUM);
                }
                break;

            case 2: // tab Favourite
                ViewHolder holder2 = (ViewHolder) getViewByPosition(_contextPosition, _gridViewFavourite).getTag();
                DataHolder data2 = (DataHolder) _favouriteAdapter.getItem(holder2.id);

                if (_favouriteMap.containsKey(data2._file.getAbsolutePath()) == true) {
                    _favouriteAdapter.remove(data2);
                    _favouriteMap.remove(data2._file.getAbsolutePath());
                }

                ImageSupporter.saveFavouriteImagePaths(this, new ArrayList<String>(_favouriteMap.values()));
                break;
        }
        return true;
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