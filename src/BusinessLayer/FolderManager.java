package BusinessLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;

public class FolderManager 
{
	protected HashMap<String, ArrayList<String>> _folderData;
	protected Context _context;
	
	public FolderManager(Context context)
	{
		_context = context;
		initializesData();
	}
	
	// Khởi tạo dữ liệu
	public void initializesData()
	{
		_folderData = new HashMap<String, ArrayList<String>>();
	}
		
	// Lấy dữ liệu danh sách ảnh của 1 thư mục
	public ArrayList<String> getsFolderImages(String folderPath)
	{
		if (_folderData.containsKey(folderPath))
    		return _folderData.get(folderPath);
    	else
    		return null;
	}

	// Lấy danh sách các thư mục
    public ArrayList<String> getsFolderPathList()
    {
    	return new ArrayList(_folderData.keySet()); 
    }

    // Kiểm tra tên 1 thư mục
    public boolean containsFolder(String name)
    {
    	return _folderData.containsKey(name);
    }
    
    // Thêm ảnh vào 1 thư mục (move file)
    public boolean movesImage(String folderPath, String imagePath)
    {	
    	File f = new File(imagePath);
    	String parentPath = f.getParent();
    	
    	// Kiểm tra thông tin thư mục có hợp lệ/tồn tại
    	// Kiểm tra xem ảnh có trong thư mục chưa
    	if (!this.containsFolder(parentPath) || !this.containsFolder(folderPath) || folderPath.equals(parentPath))
    		return false;
    			
		// Di chuyển file
		ImageSupporter.moveFile(parentPath, f.getName(), folderPath);
		
		// Xóa dữ liệu khỏi thư mục cũ nếu có
		this.getsFolderImages(parentPath).remove(imagePath);
		
		// Thêm vào album
    	this.getsFolderImages(folderPath).add(imagePath);
    	
    	return true;
    }

    // Xóa ảnh
    public boolean deletesImage(String imagePath)
    {
    	File f = new File(imagePath);
    	String parentPath = f.getParent();
    	
    	if (!this.containsFolder(parentPath))
    		return false;
    	
    	// Xóa file
    	f.delete();
    	
    	// Loại bỏ dữ liệu
    	this.getsFolderImages(parentPath).remove(imagePath);
    	
    	return true;
    }

    // Sao chép 1 ảnh vào thư mục
    public boolean copiesImage(String folderPath, String imagePath)
    {
    	File f = new File(imagePath);
    	String parentPath = f.getParent();
    	
    	// Kiểm tra thông tin thư mục có hợp lệ/tồn tại
    	// Kiểm tra xem ảnh có trong thư mục chưa
    	if (!this.containsFolder(parentPath) || !this.containsFolder(folderPath) || folderPath.equals(parentPath))
    		return false;
    			
		// Di chuyển file
		ImageSupporter.copyFile(parentPath, f.getName(), folderPath);
		
		// Thêm vào album
    	this.getsFolderImages(folderPath).add(imagePath);
    	
    	return true;
    }

    // Thêm thư mục
    public boolean addsFolder(String folderPath)
    {
    	if (this.containsFolder(folderPath))
    		return false;
    				
    	this._folderData.put(folderPath, new ArrayList<String>());
    	return true;
    }

    // Thêm thông tin ảnh vào 1 thư mục
    public boolean addsImage(String folderPath, String imagePath)
    {
    	if (!this.containsFolder(folderPath))
    		return false;
    	
    	ArrayList<String> data = _folderData.get(folderPath);
    	data.add(imagePath);
    	
    	return true;
    }
}
