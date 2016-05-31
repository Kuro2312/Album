package BusinessLayer;

import java.util.HashMap;

import android.content.Context;

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
	}
}
