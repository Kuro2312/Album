package FullscreenImage;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

public class TouchImageView extends ImageView {

	Matrix _matrix;

	// các trạng thái
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int _state = NONE;

	
	PointF _last = new PointF();
	PointF _start = new PointF();
	float _minScale = 1f;
	float _maxScale = 4f;
	float[] _m;

	int _viewWidth, _viewHeight;
	static final int CLICK = 3; // tượng trưng
	float _savedScale = 1f;
	protected float _origWidth, _origHeight;
	int _oldMeasuredWidth, _oldMeasuredHeight;

	ScaleGestureDetector _scaleDetector;

	Context _context;
	
	public TouchImageView(Context context) {
		super(context);
		initialize(context);
	}

	public TouchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		super.setClickable(true);
		this._context = context;
		_scaleDetector = new ScaleGestureDetector(context, new ScaleListener(this));
		_matrix = new Matrix();
		_m = new float[9];
		setImageMatrix(_matrix);
		setScaleType(ScaleType.MATRIX);

		setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				_scaleDetector.onTouchEvent(event);
				PointF current = new PointF(event.getX(), event.getY());

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					_last.set(current);
					_start.set(_last);
					_state = DRAG;
					break;

				case MotionEvent.ACTION_MOVE:
					if (_state == DRAG) {
						float deltaX = current.x - _last.x;
						float deltaY = current.y - _last.y;
						
						float fixTransX = getFixDragTrans(deltaX, _viewWidth, _origWidth * _savedScale);
						float fixTransY = getFixDragTrans(deltaY, _viewHeight, _origHeight * _savedScale);
						
						if (fixTransX != 0 || fixTransY != 0)
							_matrix.postTranslate(fixTransX, fixTransY);
						fixTrans();
						_last.set(current.x, current.y);
					}
					break;

				case MotionEvent.ACTION_UP:
					_state = NONE;
					int dX = (int) Math.abs(current.x - _start.x);
					int dY = (int) Math.abs(current.y - _start.y);
					if (dX < CLICK && dY < CLICK)
						performClick();
					break;

				case MotionEvent.ACTION_POINTER_UP:
					_state = NONE;
					break;
				}

				setImageMatrix(_matrix);
				invalidate();
				return true;
			}

		});
	}

	public void setMaxZoom(float x) {
		_maxScale = x;
	}

	private class ScaleListener extends
			ScaleGestureDetector.SimpleOnScaleGestureListener {
		
		TouchImageView _image;
		
		public ScaleListener(TouchImageView image) {
			_image = image;
		}
		
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector) {
			_state = ZOOM;
			return true;
		}

		@Override
		public boolean onScale(ScaleGestureDetector detector) {
			float scaleFactor = detector.getScaleFactor();
			float origScale = _savedScale;
			_savedScale *= scaleFactor;
			
			if (_savedScale > _maxScale) {
				_savedScale = _maxScale;
				scaleFactor = _maxScale / origScale;
			} else if (_savedScale < _minScale) {
				_savedScale = _minScale;
				scaleFactor = _minScale / origScale;
			}
			
			// nếu zoom out xuống còn x1.0
			if (_origWidth * _savedScale <= _viewWidth || _origHeight * _savedScale <= _viewHeight)
				_matrix.postScale(scaleFactor, scaleFactor, _viewWidth / 2, _viewHeight / 2);
			else {// vẫn còn zoom in
				_matrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
			}
			fixTrans();
			return true;
		}
	}
	
	// điều chỉnh để ảnh không rời mép màn hình
	void fixTrans() {
		_matrix.getValues(_m);
		float transX = _m[Matrix.MTRANS_X];
		float transY = _m[Matrix.MTRANS_Y];

		float fixTransX = getFixTrans(transX, _viewWidth, _origWidth * _savedScale);
		float fixTransY = getFixTrans(transY, _viewHeight, _origHeight * _savedScale);

		if (fixTransX != 0 || fixTransY != 0)
			_matrix.postTranslate(fixTransX, fixTransY);
	}

	
	float getFixTrans(float trans, float viewSize, float contentSize) {
		float minTrans, maxTrans;

		if (contentSize <= viewSize) { // nếu không zoom
			minTrans = 0;
			maxTrans = viewSize - contentSize;
		} else {
			minTrans = viewSize - contentSize;
			maxTrans = 0;
		}

		// nếu lố thì kéo ngược lại để chạm mép
		if (trans < minTrans)
			return -trans + minTrans;
		if (trans > maxTrans)
			return -trans + maxTrans;
		return 0;
	}

	// nếu ko zoom thì ko di chuyển ảnh
	float getFixDragTrans(float delta, float viewSize, float contentSize) {
		if (contentSize <= viewSize) {
			return 0;
		}
		return delta;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		_viewWidth = MeasureSpec.getSize(widthMeasureSpec);
		_viewHeight = MeasureSpec.getSize(heightMeasureSpec);

		if (_oldMeasuredWidth == _viewWidth && _oldMeasuredHeight == _viewHeight
				|| _viewWidth == 0 || _viewHeight == 0)
			return;
		_oldMeasuredHeight = _viewHeight;
		_oldMeasuredWidth = _viewWidth;

		
		Drawable drawable = getDrawable();
		if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0)
			return;
		// lấy kích thước gốc của ảnh
		int bmWidth = drawable.getIntrinsicWidth();
		int bmHeight = drawable.getIntrinsicHeight();
		
		float scale;
		// tính tỉ lệ giữa kích thước màn hình và ảnh
		float scaleX = (float) _viewWidth / (float) bmWidth;
		float scaleY = (float) _viewHeight / (float) bmHeight;
		scale = Math.min(scaleX, scaleY);		
		
		// tính phần màn hình thừa ra
		float redundantYSpace = (float) _viewHeight - (scale * (float) bmHeight);
		float redundantXSpace = (float) _viewWidth - (scale * (float) bmWidth);
		redundantYSpace /= (float) 2;
		redundantXSpace /= (float) 2;
		
		if (_savedScale == 1) {
			// phong to ảnh sát mép màn hình
			_matrix.setScale(scale, scale);
			// đưa ảnh ra giữa màn hình
			_matrix.postTranslate(redundantXSpace, redundantYSpace);

			// kích thước ảnh toàn màn hình
			_origWidth = _viewWidth - 2 * redundantXSpace;
			_origHeight = _viewHeight - 2 * redundantYSpace;		
		} else {
			
			float[] temp = new float[9];
			_matrix.getValues(temp);			
			float transX = temp[Matrix.MTRANS_X];
			float transY = temp[Matrix.MTRANS_Y];
						
			// cập nhật scale
			_savedScale = (_origWidth * _savedScale) / (_viewWidth - 2 * redundantXSpace); 
			
			_matrix.setScale(scale * _savedScale, scale * _savedScale);
		
			_matrix.postTranslate(transX + (_viewWidth - _viewHeight) /2, transY + (_viewHeight - _viewWidth)/2 );
			
			_origWidth = _viewWidth - 2 * redundantXSpace;
			_origHeight = _viewHeight - 2 * redundantYSpace;
		}
		setImageMatrix(_matrix);
		fixTrans();
		
	}

}

