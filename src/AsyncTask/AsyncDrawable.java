package AsyncTask;

import java.lang.ref.WeakReference;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

public class AsyncDrawable extends BitmapDrawable
{
    private final WeakReference<BitmapLoadingTask> bitmapLoadingTaskReference;

    public AsyncDrawable(Resources data, Bitmap bitmap, BitmapLoadingTask bitmapWorkerTask) 
    {
        super(data, bitmap);
        bitmapLoadingTaskReference = new WeakReference<BitmapLoadingTask>(bitmapWorkerTask);
    }

    public BitmapLoadingTask getBitmapWorkerTask() 
    {
        return bitmapLoadingTaskReference.get();
    }
}