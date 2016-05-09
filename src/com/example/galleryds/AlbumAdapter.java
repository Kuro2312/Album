package com.example.galleryds;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

class AlbumViewHolder extends ViewHolder
{
    public TextView textview;
}

public class AlbumAdapter extends ArrayAdapter  {

	private ArrayList<String> _items;
	private Context _context;
	
	public AlbumAdapter(Context context, ArrayList<String> data) 
	{
		 super(context, R.layout.album_item, data);
		 
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
    	AlbumViewHolder holder;
		if (convertView == null) 
		{
			LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.album_item, parent, false); 
			 
			holder = new AlbumViewHolder();
			holder.imageview = (ImageView) convertView.findViewById(R.id.imageView1);
			holder.textview = (TextView) convertView.findViewById(R.id.textView1);	
			holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox1);	
			
			convertView.setTag(holder);
		}
		else
	        holder = (AlbumViewHolder) convertView.getTag();
		
		
		holder.imageview.setOnClickListener(new View.OnClickListener() {
			
		    public void onClick(View v) {

		        CheckBox cb = (CheckBox) v.getTag();
		        
		        if (cb.getVisibility() == View.VISIBLE)
		        	cb.setChecked(!cb.isChecked());
		        else if(_context instanceof MainActivity)
		        	((MainActivity) _context).viewAllAlbumImages(_items.get(cb.getId()));  
		    }
		});
        
	    holder.imageview.setTag(holder.checkbox); 
	    
	   	holder.textview.setId(position);
	   	holder.imageview.setId(position); 
	   	holder.checkbox.setId(position);
   	 
        //holder.imageview.setImageBitmap(_items.get(position)._bitmap);
	   	holder.textview.setText(_items.get(position));
        holder.id = position;
        
        return convertView;
    }
}
