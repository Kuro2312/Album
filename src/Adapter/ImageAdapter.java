package Adapter;

import java.util.ArrayList;

import com.example.neogalleryds.MainActivity;
import com.example.neogalleryds.R;
import com.example.neogalleryds.R.drawable;
import com.example.neogalleryds.R.id;
import com.example.neogalleryds.R.layout;

import AsyncTaskSupporter.AsyncTaskSupporter;

import com.example.neogalleryds.ViewImageActivity;

import BusinessLayer.ImageSupporter;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
	public int _lastPosition = -1;
	
	public ImageAdapter(Context context, ArrayList<String> data) 
	{
		 super(context, R.layout.image_item, data);
		 
		 // TODO Auto-generated constructor stub
		 this._context = context;
		 this._items = data;
		 
		 REQ_HEIGHT = (int) ImageSupporter.convertDipToPixels(context, 75);
		 REQ_WIDTH = (int) ImageSupporter.convertDipToPixels(context, 75);
	}
	
	public void removeImages(ArrayList<String> imagePaths) {
		for (String path : imagePaths) {
			_items.remove(path);
		}
		notifyDataSetChanged();
	}
	
	public ArrayList<String> getItems() {
		return _items;
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
    	 ImageViewHolder holder;
    	 if (convertView == null) 
    	 {
			 LayoutInflater inflater = ((Activity) _context).getLayoutInflater();
			 convertView = inflater.inflate(R.layout.image_item, parent, false); 
			 
			 holder = new ImageViewHolder();
			 holder.imageview = (ImageView) convertView.findViewById(R.id.imageView0);
			 holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkBox0);	
		        
			 convertView.setTag(holder);
    	 }
    	 else
             holder = (ImageViewHolder) convertView.getTag();

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
		        	// Đóng gói dữ liệu truyền đi
		        	Intent intent = new Intent(_context, ViewImageActivity.class);
		        	
		        	intent.putExtra("filePaths", _items);
		        	intent.putExtra("position", cb.getId());
		        	intent.putExtra("slideshow", false);
		        	intent.putExtra("internal", true);
		        	intent.putExtra("status", ((MainActivity) _context)._isLogined);
		        	_context.startActivity(intent);
					
		        	// Animation để chuyển Activity
					((Activity) _context).overridePendingTransition(R.animator.animator_slide_in_right, R.animator.animator_zoom_out);
		        }
		    }
		});
        
	    holder.filePath = _items.get(position);
	    
	    // Thực hiện load bitmap bất đồng bộ
	    AsyncTaskSupporter.loadBitmap(_context, _items.get(position), holder.imageview);
		holder.checkbox.setChecked(false);
        holder.id = position;
        
        if (position > _lastPosition) {
        	 
            Animation animation = AnimationUtils.loadAnimation(_context, R.animator.up_from_bottom);

            convertView.startAnimation(animation);
        } 
        
        _lastPosition = position;
                
        return convertView;
    }
    
    // Cập toàn bộ dữ liệu 
    public void updateData(ArrayList<String> data)
    {
    	// Xóa toàn bộ dữ liệu
    	_items.clear();
    	
    	// Thêm vào nếu không phải null
    	if (data != null)
    		_items.addAll(data);
    	
    	// Cập nhật giao diện
    	super.notifyDataSetChanged();
    }
    
    public void refresh()
    {
    	super.notifyDataSetChanged();
    }
}
