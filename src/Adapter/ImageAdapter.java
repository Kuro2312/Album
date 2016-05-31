package Adapter;

import java.util.ArrayList;

import com.example.neogalleryds.R;
import com.example.neogalleryds.R.drawable;
import com.example.neogalleryds.R.id;
import com.example.neogalleryds.R.layout;

import AsyncTask.AsyncTaskSupporter;
import BusinessLayer.ImageSupporter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.app.Activity;

public class ImageAdapter extends ArrayAdapter 
{
	private ArrayList<String> _items;
	private Context _context;
	public static int REQ_HEIGHT;
	public static int REQ_WIDTH;
	public static int SELECT_MODE = View.INVISIBLE;
	
	public ImageAdapter(Context context, ArrayList<String> data) 
	{
		 super(context, R.layout.image_item, data);
		 
		 // TODO Auto-generated constructor stub
		 this._context = context;
		 this._items = data;
		 
		 REQ_HEIGHT = (int) ImageSupporter.convertDipToPixels(context, 75);
		 REQ_WIDTH = (int) ImageSupporter.convertDipToPixels(context, 75);
	}

    public Object getItem(int position) 
    {
        return _items.get(position);
    }

    public long getItemId(int position) 
    {
        return position;
    }

    @Override
	public View getView(int position, View convertView, ViewGroup parent) 
	 {
    	 ViewHolder holder;
    	 if (convertView == null) 
    	 {
			 LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
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
    	 holder.checkbox.setVisibility(ImageAdapter.SELECT_MODE);
        
    	 holder.imageview.setOnClickListener(new View.OnClickListener() 
    	 {	
		    public void onClick(View v) 
		    {
		        CheckBox cb = (CheckBox) v.getTag();
		        
		        // Khi nhấn vào 1 ảnh
		        if (cb.getVisibility() == View.VISIBLE) 
		        	 cb.setChecked(!cb.isChecked());
		        else {	        
		        	// Thêm animation chuyển cảnh
		        	//((Activity) _context).overridePendingTransition(R.animator.animator_slide_in_right, R.animator.animator_zoom_out);
		        }
		    }
		});
        
	    holder.filePath = _items.get(position);
	    AsyncTaskSupporter.loadBitmap(_context, _items.get(position), holder.imageview);
		holder.checkbox.setChecked(false);
        holder.id = position;
        
        return convertView;
    }
    
    // Cập toàn bộ dữ liệu 
    public void updateData(ArrayList<String> data)
    {
    	_items.clear();
    	_items.addAll(data);
    	super.notifyDataSetChanged();
    }
    
    public void refresh()
    {
    	super.notifyDataSetChanged();
    }
}
