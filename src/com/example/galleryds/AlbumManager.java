package com.example.galleryds;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

public class AlbumManager {

    // Cho việc thao tác trên mục album ảnh
	private GridView _gridViewAlbum;
    protected AlbumAdapter _albumAdapter;
    protected ArrayList<String> _albumList;
    protected HashMap<String, ArrayList<DataHolder>> _albumData;
    
    // Khởi tạo
    public AlbumManager(Context context, GridView gridViewAlbum)
    {
    	_gridViewAlbum = gridViewAlbum;
    	loadAlbums(context);
    }
	
    // Lấy GridView của Album
    public GridView getGridViewAlbum() 
    {
		return _gridViewAlbum;
	}
    
    // Lấy dữ liệu của Album
    public ArrayList<DataHolder> getAlbumData(String albumName) 
    {
    	if (_albumData.containsKey(albumName))
    		return _albumData.get(albumName);
    	
    	return null;
	}

    // Lấy danh sách các album
    public ArrayList<String> getAlbumList()
    {
    	return _albumList; 
    }
    
    public boolean containsAlbum(String albumName)
    {
    	return _albumData.containsKey(albumName);
    }
    
    // Nạp các album lên
    public void loadAlbums(Context context)
    {
        _albumList = ImageSupporter.getAlbumPaths(context);

        if (_albumList == null)
        	_albumList = new ArrayList<String>();
        
        _albumAdapter = new AlbumAdapter(context, _albumList);
        getGridViewAlbum().setAdapter(_albumAdapter);
        
        _albumData = new HashMap<String, ArrayList<DataHolder>>();
        
        for (int i = 0; i < _albumList.size(); i++)
        	_albumData.put(_albumList.get(i), new ArrayList<DataHolder>());
    }

    // Tạo mới Album
    public boolean createAlbum(Context context, String name) 
    {   	
    	try
    	{
	    	// Tập tin chứa ảnh mặc định của thiết bị
	        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	
	        // Kiểm tra có tạo thư mục (Album) thành công không
	        if (ImageSupporter.createFolder(path.getAbsolutePath(), name))
	        {
	           
	            ArrayList<String> data = new ArrayList<String>();
	            data.add(name);
	            
	            // Cập nhật dữ liệu tập tin 
	            ImageSupporter.addNewAlbumPaths(context, data);
	            
	            // Cập nhật adapter và giao diện
	            _albumAdapter.add(name);
	
	            return true;
	        } 
	        
	        return false;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }

    // Đổi tên 1 Album
    public boolean renameAlbum(Context context, String oldName, String newName) 
    {
    	try
    	{
	    	// Tập tin chứa ảnh mặc định của thiết bị 
	        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	        
	        // Thư mục album được chọn
	        File album = new File(path.getAbsolutePath() + File.separator + oldName);
	
	        // Cập nhật trên cơ sở dữ liệu 
	        renameAlbumDataOnFileByName(context, oldName, newName);
	
	    	// Cập nhật trên adapter
	        _albumAdapter.remove(oldName);
	        _albumAdapter.add(newName);
	
	        // Đổi tên thư mục album
	        ImageSupporter.renameFile(album, newName);
	        
	        return true;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }

    // Xóa toàn bộ ảnh trong album
    public boolean deleteWholeAlbum(Context context, String name) 
    {
    	try
    	{
	    	// Tập tin chứa ảnh mặc định của thiết bị 
	        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	
	    	// Thực hiện xóa album và toàn bộ ảnh trong album
	        ImageSupporter.deleteWholeFolder(path.getAbsolutePath(), name);
	        
	        return true;
	    }
        catch (Exception e)
    	{
    		return false;
    	}
    }

    // Xóa album và chuyển ảnh sang chỗ khác
    public boolean deleteAlbum(Context context, String name) 
    {
    	try
    	{
	    	// Lấy thư mục album
	        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	        File folder = new File(path.getAbsolutePath() + File.separator + name);
	
	        // Kiểm tra có tồn tại thư mục đó không
	        if (folder.exists())
	        {
	            File[] files = folder.listFiles();
	
	            // Nếu có tập tin bên trong thực hiện chuyển tập tin ra chỗ khác
	            if (files != null)
	                for (File f : files)
	                    ImageSupporter.moveFile(f.getParent(), f.getName(), path.getAbsolutePath());
	
	            // Thực hiện xóa thư mục
	            folder.delete();	
	            
	            // Xóa trên cơ sỡ dữ liệu
	            removeAlbumDataOnFileByName(context, name);
	            
	            // Xóa trên adapter
		        _albumAdapter.remove(name);
		        
		        return true;
	        }
	        
	        return false;
    	}
    	catch (Exception e)
    	{
    		return false;
    	}
    }
    
    // Xóa trên cơ sở dữ liệu
    protected void removeAlbumDataOnFileByName(Context context, String name)
    {
    	// Lấy đường dẫn các album
    	ArrayList<String> data = ImageSupporter.getAlbumPaths(context);
    	
    	// Tìm và xóa album
    	int i =0;
        for(i = 0; i < data.size(); i++)
            if (data.get(i).equals(name)) 
        		break;
        
        data.remove(i);
      
        // Tìm kiếm và đổi tên mới
        ImageSupporter.saveAlbumPaths(context, data);
    }
    
    // Cập nhật trên cơ sở dữ liệu
    protected void renameAlbumDataOnFileByName(Context context, String oldName, String newName)
    {
	    // Lấy đường dẫn các album
	    ArrayList<String> data = ImageSupporter.getAlbumPaths(context);
	    
	    // Tìm kiếm và đổi tên mới
	    int i =0;
        for(i = 0; i < data.size(); i++)
            if (data.get(i).equals(oldName)) 
        		break;
        
	    data.set(i, newName);
	    
	    // Lưu dữ liệu trên tập tin dữ liệu
	    ImageSupporter.saveAlbumPaths(context, data);
    }


  
    // Bật chế độ chọn hình ảnh
    public void turnOffSelectionAlbumMode()
    {  
        int count = _albumAdapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAlbum);

            AlbumViewHolder holder = (AlbumViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.INVISIBLE);
            holder.checkbox.setChecked(false);
        }
    }

    // Bật chế độ chọn hình ảnh
    public void turnOnSelectionAlbumMode() {
        int count = _albumAdapter.getCount();

        for (int i = 0; i < count; i++) {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAlbum);

            AlbumViewHolder holder = (AlbumViewHolder) view.getTag();

            holder.checkbox.setVisibility(View.VISIBLE);
            holder.checkbox.setChecked(false);
        }
    }
    
    // Xóa album đã được chọn
    public void deleteSelectedAlbums(Context context) 
    {
        int count = _albumAdapter.getCount();

        for (int i = count - 1; i >= 0; i--)
        {
            View view = ImageSupporter.getViewByPosition(i, _gridViewAlbum);

            AlbumViewHolder holder = (AlbumViewHolder) view.getTag();

            if (holder.checkbox.isChecked() == true)
            	this.deleteAlbum(context, holder.textview.getText().toString());
        }
    } 
}
