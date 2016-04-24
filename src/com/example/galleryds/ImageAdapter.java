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
    public int id;
}

class DataHolder
{
	File _file;
	Bitmap _bitmap;
	
	public DataHolder(File file, Bitmap bitmap)
	{
		_file = file; 
		_bitmap = bitmap;
	}
	
	public static ArrayList<DataHolder> convert(ArrayList<File> files, ArrayList<Bitmap> bitmaps)
	{
		if (files.size() != bitmaps.size())
			return null;
		
		ArrayList<DataHolder> result = new ArrayList<DataHolder>();
		for (int i = 0; i < files.size(); i++)		
		{
			result.add(new DataHolder(files.get(i), bitmaps.get(i)));		
		}
		
		return result;
	}
}

public class ImageAdapter extends ArrayAdapter {
	
	private ArrayList<DataHolder> _items;
	Context _context;
	
	public ArrayList<DataHolder> getData ( )
    {
        return _items;
    }
	
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
		        //Intent intent = new Intent();
		        //intent.setAction(Intent.ACTION_VIEW);
		        //intent.setDataAndType(Uri.parse("file://" + arrPath[id]), "image/*");
		        //MainActivity.this.startActivity(intent);
		        CheckBox cb = (CheckBox) v.getTag();
		        
		        if (cb.getVisibility() == View.VISIBLE) {
			        if (cb.isChecked())
			            cb.setChecked(false);
			        else 
			            cb.setChecked(true);
		        } else {
		        
		        	ArrayList<File> files = new ArrayList<File>();		        	
		        	for (DataHolder d : _items) {
		        		files.add(d._file);
		        	}
		        	
		        	Intent intent = new Intent(_context, ViewImageActivity.class);
		        	
		        	intent.putExtra("files", files);
		        	intent.putExtra("position", cb.getId());
		        	_context.startActivity(intent);
		        }
		    }
		});
        
	    holder.checkbox.setTag(_items.get(position)._file);
        holder.imageview.setImageBitmap(_items.get(position)._bitmap);
		holder.checkbox.setChecked(false);
        holder.id = position;
        
        return convertView;
    }
    
    public void remove(int position)
    {
    	ImageSupporter.deleteFile(_items.get(position)._file);
    	super.remove(_items.get(position));
    }
    
    public void remove(DataHolder data)
    {
    	ImageSupporter.deleteFile(data._file);
    	super.remove(data);
    }
    
    public void updateData(ArrayList<DataHolder> data)
    {
    	_items.clear();
    	_items.addAll(data);
    	super.notifyDataSetChanged();
    }
}