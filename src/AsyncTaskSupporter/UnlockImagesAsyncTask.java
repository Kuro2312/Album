package AsyncTaskSupporter;

import java.io.File;
import java.util.ArrayList;

import Adapter.FolderAdapter;
import Adapter.ImageAdapter;
import BusinessLayer.FolderManager;
import BusinessLayer.ImageSupporter;
import BusinessLayer.LockManager;
import BusinessLayer.MarkManager;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.Toast;

public class UnlockImagesAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ImageAdapter _imageAdapter;
	private FolderAdapter _folderAdapter;
	private ArrayList<String> _folderList;
	private ArrayList<String> _lockedList;
	
	public UnlockImagesAsyncTask(Dialog dialog, ImageAdapter imageAdapter, FolderAdapter folderAdapter)
	{
		_dialog = dialog;
		_imageAdapter = imageAdapter;
		_folderAdapter = folderAdapter;
	}
	
	protected void onPreExecute()
	{
		if (_dialog != null)
			_dialog.show();
    } 
	
	@Override
	protected Boolean doInBackground(Object... params) 
	{
		try
		{		
			LockManager lockManager = (LockManager) params[0];
			FolderManager folderManager = (FolderManager) params[1];
			ArrayList<String> imagePaths = (ArrayList<String>) params[2];

			int n = imagePaths.size();
			
			lockManager.unlocksImages(imagePaths);
			
			// Xóa sạch dữ liệu 
			// Nếu chưa có trong danh sách thư mục thì tạo mới
			if (folderManager.containsFolder(ImageSupporter.DEFAULT_PICTUREPATH))
				folderManager.getsFolderImages(ImageSupporter.DEFAULT_PICTUREPATH).clear();
			else
				folderManager.addsFolder(ImageSupporter.DEFAULT_PICTUREPATH);
			
			// Duyệt lại dữ liệu
			File file = new File(ImageSupporter.DEFAULT_PICTUREPATH);
			File[] files = file.listFiles();
			
			// Cập nhật ảnh trong thư mục Picture mặc định
			for (File f : files)
				if (ImageSupporter.isImage(f))
					folderManager.addsImage(ImageSupporter.DEFAULT_PICTUREPATH, f.getAbsolutePath());	
			
			_folderList = folderManager.getsFolderPathList();
			_lockedList = lockManager.getsLockedImages();
			
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
	
	protected void onPostExecute(Boolean result) 
    {
		// Xử lý kết quả trả v�?
		if (_dialog != null)
			_dialog.dismiss();
		
		// Cập nhật dữ liệu trên các Adapter
		_folderAdapter.updateData(_folderList);
		_imageAdapter.updateData(_lockedList);
		
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Unlocked Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
