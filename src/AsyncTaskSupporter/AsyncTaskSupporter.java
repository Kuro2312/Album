package AsyncTaskSupporter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class AsyncTaskSupporter 
{
	public static BitmapLoadingTask getBitmapLoadingTask(ImageView imageView) 
	{
		if (imageView != null) 
	   	{
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncDrawable) 
			{
				final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
				
				return asyncDrawable.getBitmapWorkerTask();
			}
	   	}
		
	   	return null;
	}
	
	public static boolean cancelPotentialWork(String data, ImageView imageView) 
	{
	    final BitmapLoadingTask bitmapLoadingTask = AsyncTaskSupporter.getBitmapLoadingTask(imageView);

	    if (bitmapLoadingTask != null) 
	    {
	        final String bitmapData = bitmapLoadingTask.getData();
	        
	        // Nếu dữ liệu vẫn chưa khởi tạo hay khác với giá trị dữ liệu mới
	        // Hủy task bất đồng bộ trước đó. Ngược lại, tiếp tục công việc
	        if (bitmapData == null || bitmapData != data)
	        	bitmapLoadingTask.cancel(true);
	        else
	            return false;
	    }

	    // Nếu không có task bất đồng bộ nào liên quan tới ImageView hay đã bị hủy, trả v�? true
	    return true;
	}
	
	public static void loadBitmap(Context context, String filePath, ImageView imageView) 
	{
	    if (cancelPotentialWork(filePath, imageView)) 
	    {
	        final BitmapLoadingTask task = new BitmapLoadingTask(imageView);
	        final Bitmap mPlaceHolderBitmap = null;
	        final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), mPlaceHolderBitmap, task);
	       
	        imageView.setImageDrawable(asyncDrawable);
	        task.execute(filePath);
	    }
	}
}
