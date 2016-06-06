package AsyncTaskSupporter;

import java.util.ArrayList;

import com.example.neogalleryds.MainActivity;

import Adapter.AlbumAdapter;
import Adapter.FolderAdapter;
import Adapter.ImageAdapter;
import BusinessLayer.AlbumManager;
import BusinessLayer.ImageSupporter;
import android.app.Dialog;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

public class DeleteAlbumAsyncTask extends AsyncTask<Object, Void, Boolean> 
{
	private Dialog _dialog;
	private ListView _listViewAlbum;
	private AlbumAdapter _albumAdapter;
	private ImageAdapter _imageAdapter;
	private int _albumContextPosition;
	private String _selectedAlbumName;
	private AlbumManager _albumManager;
	
	public DeleteAlbumAsyncTask(Dialog dialog, ListView listViewAlbum, AlbumAdapter albumAdapter, ImageAdapter imageAdapter)
	{
		_dialog = dialog;
		_listViewAlbum = listViewAlbum;
		_albumAdapter = albumAdapter;
		_imageAdapter = imageAdapter;
	}
	
	protected void onPreExecute()
	{
		if (_dialog != null)
			_dialog.show();
    } 
	
	@Override
	protected Boolean doInBackground(Object... params) 
	{
		String inputPath = (String) params[0];
		_albumManager = (AlbumManager) params[1];
		_albumContextPosition = (Integer) params[2];
		_selectedAlbumName = (String) params[3];
		
		return _albumManager.deletesAlbum(inputPath);
	}
	
	protected void onPostExecute(Boolean result) 
    {
		_listViewAlbum.setItemChecked(_albumContextPosition, false);
		_albumAdapter.updateData(_albumManager.getsAlbumList());
		_imageAdapter.updateData(_albumManager.getsAlbumImages(_selectedAlbumName));
		
		// Xử lý kết quả trả về
			if (_dialog != null)
				_dialog.dismiss();
				
		MainActivity.cancelLoadImage = false;
		if (result == true)
			Toast.makeText(_dialog.getContext(), "Deleted Succesully", Toast.LENGTH_SHORT).show();
		else
			Toast.makeText(_dialog.getContext(), "Fail ! +_+ !", Toast.LENGTH_SHORT).show();
    }
}
