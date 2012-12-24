package com.tomkey.scaleablescrollview.widget;

import android.app.Activity;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.tomkey.scaleablescrollview.widget.ScaleAbleScrollView.MotionEventListener;
import com.tomkey.scaleablescrollview.widget.ScaleAbleScrollView.ScrollChangedListener;

public class ScaleAbleController implements OnGestureListener, ScrollChangedListener, AnimatorListener {
	View topView;
	int topMiniHeight;
	int contentHeight;
	int titleHeight;
	GestureDetector mGestureDetector;
	LinearLayout.LayoutParams lp;
	ObjectAnimator animator;
	long currentPlayTime = 0l;
	long totalPlayTime = 800;
	ScaleAbleScrollView scrollView;
	View bottomView;
	Activity activity;
	float lastY;
	int[] location = new int[2];
	float velocityY;
	Handler handler = new Handler();

	/**
	 * ScrollView下面必须是一个LinearLayout，LinearLayout的orientation必须是vertical
	 * 
	 * @param activity
	 *            对应的activity
	 * @param scrollViewId
	 *            最上层ScrollView的id
	 * @param topViewId
	 *            上面缩放的view的id
	 * @param bottomViewId
	 *            下面自适应的view的id
	 */
	public ScaleAbleController(Activity activity, int scrollViewId, int topViewId, int bottomViewId) {
		this.activity = activity;
		this.mGestureDetector = new GestureDetector(activity, this);
		this.scrollView = (ScaleAbleScrollView) activity.findViewById(scrollViewId);
		this.topView = activity.findViewById(topViewId);
		this.bottomView = activity.findViewById(bottomViewId);
		this.lp = (LinearLayout.LayoutParams) topView.getLayoutParams();
		this.topMiniHeight = lp.height;
	}

	/**
	 * 初始化方法
	 */
	public void init() {
		scrollView.setScrollChangedListener(this);
		scrollView.setTouchListener(new MotionEventListener() {
			@Override
			public boolean onTouchEvent(MotionEvent ev) {
				return dispatchTouchEvent(ev);
			}
		});
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				contentHeight = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getMeasuredHeight();
				DisplayMetrics dm = new DisplayMetrics();
				activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
				titleHeight = dm.heightPixels - contentHeight;
			}
		});
	}

	public void setH(int height) {
		lp.height = height;
		topView.setLayoutParams(lp);
	}

	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (lp.height > topMiniHeight)
				topView.scrollTo(0, 0);
			if (topView.getScrollY() == 0 && scrollView.getScrollY() == 0)
				startAnimator();
			Log.d("MotionEvent", "ACTION_UP gallery.getScrollY():" + topView.getScrollY() + " scrollView.getScrollY():"
					+ scrollView.getScrollY());
			break;
		case MotionEvent.ACTION_DOWN:
			cancelAnimator();
			Log.d("MotionEvent", "ACTION_DOWN event.getX():" + event.getX() + " event.getY():" + event.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			velocityY = event.getY() - lastY;
			lastY = event.getY();
			Log.d("MotionEvent", "ACTION_MOVE event.getX():" + event.getX() + " event.getY():" + event.getY());
			break;

		default:
			break;
		}

		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		float y = adjustY(e.getY());
		bottomView.getLocationOnScreen(location);
		if (y < location[1] && y > 0) {
			topView.scrollTo(0, 0);
			scrollView.scrollTo(0, 0);
			if (lp.height == topMiniHeight) {
				cancelAnimator();
				animator = ObjectAnimator.ofInt(this, "h", new int[] { lp.height, contentHeight }).setDuration(
						totalPlayTime - currentPlayTime);
				animator.start();
				animator.addListener(this);
			} else if (lp.height == contentHeight) {
				cancelAnimator();
				animator = ObjectAnimator.ofInt(this, "h", new int[] { lp.height, topMiniHeight }).setDuration(
						totalPlayTime - currentPlayTime);
				animator.start();
				animator.addListener(this);
			}
		}
		Log.d("zqt", "onSingleTapUp e.getY() - titleHeight:" + y + " lp.height:" + lp.height + "location[1]: "
				+ (location[1] - titleHeight));
		return false;
	}

	private float adjustY(float y) {
		return y - titleHeight;
	}

	private void scrollGallery() {
		topView.scrollTo(0, -scrollView.getScrollY() / 2);
		bottomView.getLocationOnScreen(location);
		if (location[1] <= titleHeight) {
			topView.scrollTo(0, -topMiniHeight / 2);
		}
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (e1 == null)
			return false;
		Log.d("onScroll", "distanceY:" + distanceY + " distanceX:" + distanceX + "scrollView.getScrollY():"
				+ scrollView.getScrollY());
		Log.d("onScroll", "adjustY(e1.getY()):" + adjustY(e1.getY()) + " lp.height:" + lp.height);
		Log.d("onScroll", "titleHeight: " + titleHeight + "location:" + location[0] + "-" + location[1]);
		// checkHeight();
		if (scrollView.getScrollY() > 0) {// scrollview未到达顶端,不能缩放gallery的高度
			Log.d("onScroll", "return false");
			// 设置gallery的scrollY的值
			scrollGallery();
			return false;
		}// && location[1] < titleHeight
		float arc = Math.abs(distanceY / distanceX);
		float y = adjustY(e1.getY());
		if (y > lp.height || (y <= lp.height && arc > 1)) {
			// lp.height = lp.height - (int) distanceY;
			// if (txtTV.getTop() <= height && txtTV.getTop() > 0) {
			// lp.topMargin = (int) (lp.topMargin - distanceY);
			// lp.bottomMargin = (int) (lp.bottomMargin - distanceY);
			// } else {
			// lp.topMargin = 0;
			// lp.bottomMargin = 0;
			// }
			boolean result = adjustkHeight(distanceY);
			topView.setLayoutParams(lp);
			if (result)
				return false;
			return true;
		}

		return false;
	}

	private boolean adjustkHeight(float distanceY) {
		Log.d("adjustkHeight", "gallery.getScrollY(): " + topView.getScrollY() + "lp.height" + lp.height
				+ " distanceY: " + distanceY + "scrollView.getScrollY(): " + scrollView.getScrollY());
		lp.height = lp.height - (int) distanceY;
		if (lp.height < topMiniHeight) {
			lp.height = topMiniHeight;
			return true;
		} else if (lp.height > contentHeight) {
			lp.height = contentHeight;
			return true;
		}
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	private void cancelAnimator() {
		if (animator != null && animator.isRunning()) {
			currentPlayTime = animator.getCurrentPlayTime();
			animator.cancel();
			animator = null;
		}
		Log.d("zqt", "cancelAnimator currentPlayTime: " + currentPlayTime);
	}

	private void startAnimator() {
		Log.d("zqt", "startAnimator gallery.getScrollY():" + topView.getScrollY());
		if (lp.height <= topMiniHeight || lp.height >= contentHeight) {
			topView.scrollTo(0, 0);
			return;
		}
		cancelAnimator();

		if (velocityY > 0) {
			animator = ObjectAnimator.ofInt(this, "h", new int[] { lp.height, contentHeight }).setDuration(
					totalPlayTime - currentPlayTime);
			animator.start();
			animator.addListener(this);
			Log.d("zqt", "放大 startAnimator currentPlayTime: " + currentPlayTime);
		} else {
			animator = ObjectAnimator.ofInt(this, "h", new int[] { lp.height, topMiniHeight }).setDuration(
					totalPlayTime - currentPlayTime);
			animator.start();
			animator.addListener(this);
			Log.d("zqt", "缩小 startAnimator currentPlayTime: " + currentPlayTime);
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
		Log.d("onScrollChanged ", "scrollView.getScrollY(): " + scrollView.getScrollY());
		if (scrollView.getScrollY() > 0) {
			// 设置gallery的scrollY的值
			scrollGallery();
		} else if (scrollView.getScrollY() < 0) {
			topView.scrollTo(0, 0);
		}
	}

	@Override
	public void onAnimationStart(Animator animation) {
		scrollView.smoothScrollTo(0, 0);
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		currentPlayTime = 0;
	}

	@Override
	public void onAnimationCancel(Animator animation) {

	}

	@Override
	public void onAnimationRepeat(Animator animation) {

	}
}
