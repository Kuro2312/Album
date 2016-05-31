package BusinessLayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class LockManager {

	protected HashMap<String, String> _lockData;	
	protected Context _context;
	
	public LockManager(Context context)
	{
		_context = context;
		initializesData();
	}
	
	// Khởi tạo dữ liệu
	public void initializesData()
	{
		_lockData = new HashMap<String, String>();
		
		loadLockImages();
	}
	
	// Nạp danh sách ảnh bị khóa
	public void loadLockImages()
	{
		File mydir = _context.getDir("NeoGalleryDS_Locks", Context.MODE_PRIVATE);
		
		File[] files = mydir.listFiles();
		
		// Duyệt ảnh trong internal storage
		for (File f : files) 
		{
			if (ImageSupporter.isImage(f))
				_lockData.put(f.getAbsolutePath(), f.getAbsolutePath());
		}	
	}
	
	// Lấy danh sách ảnh bị khóa
	public ArrayList<String> getsLockedImages()
	{
		return new ArrayList<String>(_lockData.keySet());
	}
	
	// Khóa ảnh
	public boolean locksImage(String imagePath)
	{
		// Kiểm tra thư mục có tồn tại, nếu chưa thì tạo
		File mydir = _context.getDir("NeoGalleryDS_Locks", Context.MODE_PRIVATE);
		
		if (!mydir.exists())
			mydir.mkdir();
	
		// Tạo và lưu tập tin
		File inFile = new File(imagePath);
		
		// Di chuyển 
		String newName = this.generateFileName(inFile.getName());
		ImageSupporter.moveFile(inFile.getParent(), inFile.getName(), mydir.getAbsolutePath(), newName);
		
		// Thêm vào dữ liệu
		String newPath = mydir.getAbsolutePath() + File.separator + newName;
		_lockData.put(newPath, newPath);
		
		return true;
	} 

	// Bỏ khóa ảnh
	public boolean unlocksImage(String imagePath)
	{
		// Kiểm tra thư mục có tồn tại, nếu chưa thì tạo
		File mydir = _context.getDir("NeoGalleryDS_Locks", Context.MODE_PRIVATE);
		
		if (!mydir.exists())
			return false;
		
		// Tạo và lưu tập tin
		File file = new File(imagePath);
		
		// Di chuyển 
		ImageSupporter.moveFile(file.getParent(), file.getName(), ImageSupporter.DEFAULT_PICTUREPATH);
		
		// Xóa trong dữ liệu
		if (!_lockData.containsKey(imagePath))
			return false;
		
		_lockData.remove(imagePath);
		
		return true;
	}
	
	// Phát sinh tên không trùng
	public String generateFileName(String fileName)
	{
		return fileName + String.valueOf(System.currentTimeMillis());
	}
}


/*try 
{
	//out.close();
} 
catch (IOException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
} */

// Mã file
//String imageID = inFile.getParentFile().getName() + inFile.getName();

// Lấy định dang
//String extension = LockManager.getsExtensionOfImage(imagePath);

//BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);	

//File fileWithinMyDir = new File(mydir, imageID);
//out = new FileOutputStream(fileWithinMyDir);	
//bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
