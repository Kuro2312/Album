package AsyncTaskSupporter;

import java.io.File;
import java.lang.ref.WeakReference;

import com.example.neogalleryds.MainActivity;

import Adapter.ImageAdapter;
import BusinessLayer.ImageSupporter;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class BitmapLoadingTask extends AsyncTask<String, Void, Bitmap> 
{
    private final WeakReference<ImageView> _imageViewReference;
    private String _data = null;

    public BitmapLoadingTask(ImageView imageView)
    {
        // Dùng WeakReference để chắc chắn ImageView có thể bị d�?n dẹp 
    	// Tránh việc bị Out Of Memory
    	_imageViewReference = new WeakReference<ImageView>(imageView);
    }

    @Override
    protected Bitmap doInBackground(String... params) 
    {	   	
    	// Lấy đư�?ng dẫn tới tập tin ảnh
    	_data = params[0];
    	
    	if (isCancelled() || MainActivity.cancelLoadImage)
            return null;
        
    	// Giải mã ảnh cho phù hợp
        return ImageSupporter.decodeSampledBitmapFromFile(new File(_data), ImageAdapter.REQ_WIDTH, ImageAdapter.REQ_HEIGHT);
    }
    
    protected void onPostExecute(Bitmap bitmap) 
    {
        

        if (_imageViewReference != null && bitmap != null) 
        {
            final ImageView imageView = _imageViewReference.get();
            final BitmapLoadingTask bitmapWorkerTask = AsyncTaskSupporter.getBitmapLoadingTask(imageView);
            
            if (this == bitmapWorkerTask && imageView != null)
                imageView.setImageBitmap(bitmap);
        }
    }
    
    public String getData()
    {
    	return _data;
    }
}
