package com.example.neogalleryds;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

class IconData
{
	public String _function;
	public Bitmap _bitmap;
	
	public IconData(String function, Bitmap bitmap)
	{
		this._bitmap = bitmap;
		this._function = function;
	}
}
public class ResourceManager 
{
	protected int[] _actionbarIcon = { R.drawable.add_album, R.drawable.select };
	protected int[] _imageIcon = { R.drawable.delete, R.drawable.add_favourite, R.drawable.add_image_album, R.drawable.lock_image };
	protected int[] _albumIcon = { R.drawable.delete_album };
	protected int[] _favouriteIcon = { R.drawable.delete, R.drawable.remove_favourite, R.drawable.lock_image};
	protected int[] _albumImageIcon = { R.drawable.delete, R.drawable.add_favourite, R.drawable.lock_image, 
			 R.drawable.remove_image_album };				
	protected int _reqWidth;
	protected int _reqHeight;
	
	public ResourceManager(Context context)
	{
		_reqWidth = (int) ImageSupporter.convertDipToPixels(context, 50);
		_reqHeight = (int) ImageSupporter.convertDipToPixels(context, 50);		
	}
	
	public ArrayList<IconData> getActionBarIcon(Resources resource)
	{
		ArrayList<IconData> data = new ArrayList<IconData>();
		
		Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _actionbarIcon[0], _reqWidth, _reqHeight);
		data.add(new IconData("Add Album", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _actionbarIcon[1], _reqWidth, _reqHeight);
		data.add(new IconData("Select", bitmap));
		
		return data;
	}

	public ArrayList<IconData> getImageFunctionIcon(Resources resource)
	{
		ArrayList<IconData> data = new ArrayList<IconData>();
		
		Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _imageIcon[0], _reqWidth, _reqHeight);
		data.add(new IconData("Delete Images", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _imageIcon[1], _reqWidth, _reqHeight);
		data.add(new IconData("Add To Favourite", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _imageIcon[2], _reqWidth, _reqHeight);
		data.add(new IconData("Add To Album", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _imageIcon[3], _reqWidth, _reqHeight);
		data.add(new IconData("Hide Images", bitmap));
		
		return data;
	}

	public ArrayList<IconData> getAlbumFunctionIcon(Resources resource)
	{
		ArrayList<IconData> data = new ArrayList<IconData>();
		
		Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _albumIcon[0], _reqWidth, _reqHeight);
		data.add(new IconData("Delete Album", bitmap));
		
		return data;
	}
	
	public ArrayList<IconData> getFavouriteFunctionIcon(Resources resource)
	{
		ArrayList<IconData> data = new ArrayList<IconData>();
		
		Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _favouriteIcon[0], _reqWidth, _reqHeight);
		data.add(new IconData("Delete Images", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _favouriteIcon[1], _reqWidth, _reqHeight);
		data.add(new IconData("Remove From Favourite", bitmap));;
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _favouriteIcon[2], _reqWidth, _reqHeight);
		data.add(new IconData("Hide Images", bitmap));
		
		return data;
	}

	public ArrayList<IconData> getAlbumImageFunctionIcon(Resources resource)
	{
		ArrayList<IconData> data = new ArrayList<IconData>();
		
		Bitmap bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _albumImageIcon[0], _reqWidth, _reqHeight);
		data.add(new IconData("Delete Images", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _albumImageIcon[1], _reqWidth, _reqHeight);
		data.add(new IconData("Add To Favourite", bitmap));;
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _albumImageIcon[2], _reqWidth, _reqHeight);
		data.add(new IconData("Hide Images", bitmap));
		
		bitmap = ImageSupporter.decodeSampledBitmapFromResource(resource, _albumImageIcon[2], _reqWidth, _reqHeight);
		data.add(new IconData("Remove From Album", bitmap));
		
		return data;
	}
}
