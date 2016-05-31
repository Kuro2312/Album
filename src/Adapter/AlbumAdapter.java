package Adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import com.example.neogalleryds.R;
import com.example.neogalleryds.R.drawable;
import com.example.neogalleryds.R.id;
import com.example.neogalleryds.R.layout;

import BusinessLayer.ImageSupporter;
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

class AlbumDataHolder
{
	public int id;
	public ImageView imageView;
	public TextView textView;
	public CheckBox checkBox;
}

public class AlbumAdapter extends ArrayAdapter  {

	private ArrayList<String> _items;
	private Context _context;
	
	protected int _reqWidth;
	protected int _reqHeight;
	public static int[] _folderIcon = { R.drawable.blue_folder, R.drawable.brown_folder, R.drawable.yellow_folder, R.drawable.green_folder, R.drawable.orange_folder}; 
	
	public AlbumAdapter(Context context, ArrayList<String> data) 
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
    	AlbumDataHolder holder;
    	
		if (convertView == null) 
		{
			LayoutInflater inflater = ((Activity)_context).getLayoutInflater();
			convertView = inflater.inflate(R.layout.folder_item, parent, false); 
			 
			holder = new AlbumDataHolder();
			holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewFolder);
			holder.textView = (TextView) convertView.findViewById(R.id.textViewFolder);		
			
			convertView.setTag(holder);
		}
		else
	        holder = (AlbumDataHolder) convertView.getTag();
		
        
	    holder.imageView.setTag(holder.textView); 	    
	   	holder.textView.setId(position);
	   	holder.imageView.setId(position); 
   	 
	   	int num = _folderIcon[3];
	   	Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(_context.getResources(), num, _reqWidth, _reqHeight);
        holder.imageView.setImageBitmap(bitmap);
	   	
	   	holder.textView.setText(_items.get(position));
	   	
        holder.id = position;
        
        return convertView;
    }
}
