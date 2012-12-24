package com.tomkey.scaleablescrollview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScaleAbleScrollView extends ScrollView {
	private ScrollChangedListener listener;
	MotionEventListener touchListener;

	public ScaleAbleScrollView(Context context) {
		super(context);
	}

	public ScaleAbleScrollView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public ScaleAbleScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public void setTouchListener(MotionEventListener touchListener) {
		this.touchListener = touchListener;
	}

	public void setScrollChangedListener(ScrollChangedListener listener) {
		this.listener = listener;
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (listener != null)
			listener.onScrollChanged(l, t, oldl, oldt);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (touchListener != null)
			return touchListener.onTouchEvent(ev) ? true : super.dispatchTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}

	public interface MotionEventListener {
		boolean onTouchEvent(MotionEvent ev);
	}

	public interface ScrollChangedListener {
		void onScrollChanged(int l, int t, int oldl, int oldt);
	}
}
