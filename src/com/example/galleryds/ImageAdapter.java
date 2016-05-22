package com.example.galleryds;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

class ViewHolder
{
    public ImageView imageview;
    public CheckBox checkbox;
    public String filePath;
    public int id;
}

class DataHolder
{
	private String _filePath;
	private long _modifiedTime;
	private Bitmap _bitmap;
	
	public DataHolder(String filePath, Bitmap bitmap, long lastModified)
	{
		_filePath = filePath; 
		_bitmap = bitmap;
		_modifiedTime = lastModified;
	}

	public boolean setFilePath(String filePath)
	{
		if (filePath == "")
			return false;
		
		_filePath = filePath; 
		return true;
	}
	
	public void loadBitmap()
	{
		_bitmap = ImageSupporter.decodeSampledBitmapFromFile(new File(_filePath), ImageManager.IMAGE_WIDTH, ImageManager.IMAGE_HEIGHT);
	}

	public String getFilePath()
	{
		return _filePath;
	}
	
	public Bitmap getBitmap()
	{
		if (_bitmap == null)
		{
			File infile = new File(_filePath);
			
			String name = infile.getParentFile().getName() + infile.getName();
			
			int pos = name.lastIndexOf(".");
			if (pos > 0)
			    name = name.substring(0, pos) + ".png";
			
			String path = ImageManager.IMAGE_THUMBNAIL_PATH + File.separator + name;
			 
			BitmapFactory.Options bmOptions = new BitmapFactory.Options();
			//bmOptions.inJustDecodeBounds = true;
			_bitmap = BitmapFactory.decodeFile(path, bmOptions);	
		}
		
		return _bitmap;
	}
	
	public void setBitmap(Bitmap bitmap)
	{
		_bitmap = bitmap;
	}
	
	public long getLastModified()
	{
		return _modifiedTime;
	}
}

public class ImageAdapter extends ArrayAdapter 
{
	private ArrayList<DataHolder> _items;
	private Context _context;
	
	public ImageAdapter(Context context, ArrayList<DataHolder> data) 
	{
		 super(context, R.layout.image_item, data);
		 
		 // TODO Auto-generated constructor stub
		 this._context = context;
		 this._items = data;
	}

    public Object getItem(int position) {
    	
        return _items.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) 
	 {
    	 ViewHolder holder;
    	 if (convertView == null) 
    	 {
			 LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
			 convertView = inflater.inflate(R.layout.image_item, parent, false); 
			 
			 holder = new ViewHolder();
			 holder.imageview = (ImageView) convertView.findViewById(R.id.imageView0);
			 holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox0);	
		        
			 convertView.setTag(holder);
    	 }
    	 else
             holder = (ViewHolder) convertView.getTag();

    	 holder.checkbox.setId(position);
    	 holder.imageview.setId(position);
    	 holder.imageview.setTag(holder.checkbox);     
        
    	 holder.imageview.setOnClickListener(new View.OnClickListener() {
		
		    public void onClick(View v) {

		        CheckBox cb = (CheckBox) v.getTag();
		        
		        // Khi nhấn vào 1 ảnh
		        if (cb.getVisibility() == View.VISIBLE) 
		        	 cb.setChecked(!cb.isChecked());
		        else {
		        
		        	ArrayList<String> filePaths = new ArrayList<String>();		        	
		        	for (DataHolder d : _items) {
		        		filePaths.add(d.getFilePath());
		        	}
		        	
		        	// Đóng gói dữ liệu truyền đi
		        	Intent intent = new Intent(_context, ViewImageActivity.class);
		        	
		        	intent.putExtra("filePaths", filePaths);
		        	intent.putExtra("position", cb.getId());
		        	
		        	_context.startActivity(intent);
		        	
		        	// Thêm animation chuyển cảnh
		        	((Activity) _context).overridePendingTransition(R.animator.animator_slide_in_right, R.animator.animator_zoom_out);
		        }
		    }
		});
        
    	if (_items.get(position).getBitmap() == null)
    	{
    		((MainActivity) _context).refreshData();
    	}
    	 
	    holder.filePath = _items.get(position).getFilePath();
	    holder.imageview.setImageBitmap(_items.get(position).getBitmap());
	    //ImageSupporter.loadBitmap(_context, _items.get(position), holder.imageview);
		holder.checkbox.setChecked(false);
        holder.id = position;
        
        return convertView;
    }
    
    // Xóa khỏi adapter
    public void remove(int position)
    {
    	super.remove(_items.get(position));
    }
    
    public void remove(DataHolder data)
    {
    	super.remove(data);
    }
    
    // Cập toàn bộ dữ liệu 
    public void updateData(ArrayList<DataHolder> data)
    {
    	_items.clear();
    	_items.addAll(data);
    	super.notifyDataSetChanged();
    }
}