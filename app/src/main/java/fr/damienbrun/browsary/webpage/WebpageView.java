package fr.damienbrun.browsary.webpage;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.MotionEvent;
import android.webkit.WebView;

public class WebpageView extends WebView {

	// add my own listener
	public interface OnMovedListener {
		public void onMoved(boolean hasMoved);
	}

	private OnMovedListener onMovedListener;

	public static final String EMPTY = "browsary:";

	private boolean mPushlong = false;
	// integer instead of boolean so I allow a little movement
	private int mMoved = 0;

	public WebpageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WebpageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WebpageView(Context context) {
		super(context);
	}

	public WebpageView(Context context, OnMovedListener onMovedListener) {
		super(context);
		this.onMovedListener = onMovedListener;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu) {
		// onCreateContextMenu because onLongClick doesn't work properly with
		// WebView
		mPushlong = true;
		super.onCreateContextMenu(menu);
	}

	// manage my own onMoveListener, because onScrollListener handle a little
	// bit different behavior
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPushlong = false;
			mMoved = 0;
			break;
		case MotionEvent.ACTION_MOVE:
			mMoved++;
			break;
		case MotionEvent.ACTION_UP:
			if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
				// add some text to get message even without any selection
				loadUrl("javascript:alert('" + EMPTY
						+ "'+window.getSelection())");
			} else
				onMovedListener.onMoved((mMoved >= 5));
			break;
		}
		return super.onTouchEvent(event);
	}

	public int getMoved() {
		return mMoved;
	}

	public boolean getPushLong() {
		return mPushlong;
	}
}
