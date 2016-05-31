package BusinessLayer;

import java.util.ArrayList;

import com.example.neogalleryds.R;
import com.example.neogalleryds.R.drawable;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;

// Hỗ trợ việc lấy ảnh từ tài nguyên
public class ResourceManager 
{	
	// Các chức năng có nút bấm (trong thanh toolbar dưới cùng)
	protected static int[] _imageIcon = 
	{ 
		R.drawable.delete_image, R.drawable.delete_album,
		R.drawable.mark_image, R.drawable.unmark_image, 
		R.drawable.add_image_album, R.drawable.remove_image_album,
		R.drawable.lock_image, R.drawable.unlock_image,
		R.drawable.compress, R.drawable.share, R.drawable.convert_video
	};	
	
	protected static int _reqWidth;
	protected static int _reqHeight;
	
	private static void setDefaultSize(Context context)
	{
		// Khởi tạo kích thước mặc định sang pixel
		_reqWidth = (int) ImageSupporter.convertDipToPixels(context, 50);
		_reqHeight = (int) ImageSupporter.convertDipToPixels(context, 50);		
	}

	// Lấy ảnh Bitmap theo mã tài nguyên 
	public static Bitmap getImageFunctionIcon(Context context, int resourceID)
	{
		// Cài đặt kích thước mặc định
		setDefaultSize(context);
		
		int size = _imageIcon.length;
		
		// Tìm kiếm theo mã ID của resource
		// Trả về ảnh Bitmap
		for (int i = 0; i < size; i++)
			if (_imageIcon[i] == resourceID)
				return ImageSupporter.decodeSampledBitmapFromResource(context.getResources(), _imageIcon[i], _reqWidth, _reqHeight);
		
		return null;
	}
	
	// Lấy ảnh Bitmap theo mã tài nguyên 
	public static Bitmap getImageFunctionIcon(Context context, int resourceID, int reqWidth, int reqHeight)
	{
		if (reqWidth <= 0 || reqHeight <= 0 )
			return null;	
		
		_reqWidth = (int) ImageSupporter.convertDipToPixels(context, reqWidth);
		_reqHeight = (int) ImageSupporter.convertDipToPixels(context, reqHeight);	
		
		int size = _imageIcon.length;
		
		// Tìm kiếm theo mã ID của resource
		// Trả về ảnh Bitmap
		for (int i = 0; i < size; i++)
			if (_imageIcon[i] == resourceID)
				return ImageSupporter.decodeSampledBitmapFromResource(context.getResources(), _imageIcon[0], reqWidth, reqHeight);
		
		return null;
	}
}
