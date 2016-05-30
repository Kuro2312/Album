package com.example.neogalleryds;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

class FolderDataHolder
{
	public int id;
	public ImageView imageView;
	public TextView textView;
}

public class FolderAdapter extends ArrayAdapter  {

	private ArrayList<String> _items;
	private Context _context;
	
	protected int _reqWidth;
	protected int _reqHeight;
	public static int[] _folderIcon = { R.drawable.blue_folder, R.drawable.brown_folder, R.drawable.yellow_folder, R.drawable.green_folder, R.drawable.orange_folder}; 
	
	public FolderAdapter(Context context, ArrayList<String> data) 
	{
		 super(context, R.layout.folder_item, data);
		 
		 // TODO Auto-generated constructor stub
		 this._context = context;
		 this._items = data;
		 
		 this._reqWidth = (int) ImageSupporter.convertDipToPixels(context, 50);
		 this._reqHeight = (int) ImageSupporter.convertDipToPixels(context, 50);
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
    	FolderDataHolder holder;
    	
		if (convertView == null) 
		{
			LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.folder_item, parent, false); 
			 
			holder = new FolderDataHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFolder);
			holder.textView = (TextView) convertView.findViewById(R.id.textViewFolder);		
			
			convertView.setTag(holder);
		}
		else
	        holder = (FolderDataHolder) convertView.getTag();
		
        
	    holder.imageView.setTag(holder.textView); 	    
	   	holder.textView.setId(position);
	   	holder.imageView.setId(position); 
   	 
	   	int num = _folderIcon[2];
	   	Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(_context.getResources(), num, _reqWidth, _reqHeight);
        holder.imageView.setImageBitmap(bitmap);
	   	
	   	File file = new File(_items.get(position));
	   	holder.textView.setText(file.getName());
	   	
        holder.id = position;
        
        return convertView;
    }

	public void setChecked(View view) {
		
		 FolderDataHolder dataHolder = (FolderDataHolder) view.getTag();
		 
		 int num = _folderIcon[0];
		 Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(_context.getResources(), num, _reqWidth, _reqHeight);
	     dataHolder.imageView.setImageBitmap(bitmap);
	}
}
