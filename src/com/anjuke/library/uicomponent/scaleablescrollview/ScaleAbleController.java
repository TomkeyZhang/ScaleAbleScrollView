package com.anjuke.library.uicomponent.scaleablescrollview;

import android.app.Activity;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.anjuke.library.uicomponent.scaleablescrollview.ScaleAbleScrollView.MotionEventListener;
import com.anjuke.library.uicomponent.scaleablescrollview.ScaleAbleScrollView.ScrollChangedListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;

public class ScaleAbleController implements OnGestureListener, ScrollChangedListener, AnimatorListener, AnimatorUpdateListener {
    private View topView;
    private int topMiniHeight;
    private int contentHeight;
    private int titleHeight;
    private GestureDetector mGestureDetector;
    private ViewGroup.LayoutParams lp;
    private ObjectAnimator animator;
    private long currentPlayTime = 0l;
    private long totalPlayTime = 800;
    private ScaleAbleScrollView scrollView;
    private View bottomView;
    private Activity activity;
    private float lastY;
    private int[] location = new int[2];
    private float velocityY;
    private ScaleListener scaleListener;
    private boolean isDebug;
    private Rect exceptRect = new Rect();
    private View exceptView;

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
    public ScaleAbleController(Activity activity, int scrollViewId, int topViewId, int bottomViewId, boolean isDebug) {
        this.isDebug = isDebug;
        this.activity = activity;
		this.mGestureDetector = new GestureDetector(activity, this);
		this.scrollView = (ScaleAbleScrollView) activity.findViewById(scrollViewId);
		this.topView = activity.findViewById(topViewId);
		this.bottomView = activity.findViewById(bottomViewId);
		this.lp = topView.getLayoutParams();
		this.topMiniHeight = lp.height;
        this.scaleListener = new ScaleListener() {
            @Override
            public void onScale(int height) {
            }

            @Override
            public void onReachStart() {
            }

            @Override
            public void onReachEnd() {
            }

            @Override
            public void onClickStart() {
            }

            @Override
            public void onClickEnd() {

            }
        };
	}

    public void setScaleListener(ScaleListener scaleListener) {
        this.scaleListener = scaleListener;
    }

    public void init() {
        init(0, null);
    }
	/**
	 * 初始化方法
	 */
    public void init(final int extraTitleHeight, final View exceptView) {
        this.exceptView = exceptView;
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
                View root = activity.getWindow().findViewById(Window.ID_ANDROID_CONTENT);
                contentHeight = root.getMeasuredHeight() - extraTitleHeight;
                // DisplayMetrics dm = new DisplayMetrics();
                // activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
                // titleHeight = dm.heightPixels - contentHeight;
                int[] location = new int[2];
                topView.getLocationOnScreen(location);
                titleHeight = location[1];
                // log("zqt", "getLocationOnScreen " + location[1] + "contentHeight=" + contentHeight + "-" +
                // scrollView.getHeight());
                updateExceptRect();
                // log("zqt", outRect.height() - root.getHeight() + "");
			}
		});
	}

    private void updateExceptRect() {
        if (exceptView != null)
            exceptView.getGlobalVisibleRect(exceptRect);
    }
	public void setH(int height) {
		lp.height = height;
		topView.setLayoutParams(lp);
	}

    public boolean dispatchTouchEvent(MotionEvent event) {
        if (exceptRect.contains((int) event.getX(), (int) (event.getY() + titleHeight)))
            return false;// 不处理例外的view的事件
        log("zqt", exceptRect + "- event.getX()" + event.getX() + " event.getY()" + event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                if (lp.height > topMiniHeight)
                    topView.scrollTo(0, 0);
                if (topView.getScrollY() == 0)
                    startAnimatorInActionUp();
                log("MotionEvent", "ACTION_UP gallery.getScrollY():" + topView.getScrollY() + " scrollView.getScrollY():"
                        + scrollView.getScrollY());
                break;
            case MotionEvent.ACTION_DOWN:
                cancelAnimator();
                log("MotionEvent", "ACTION_DOWN event.getX():" + event.getX() + " event.getY():" + event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                velocityY = event.getY() - lastY;
                lastY = event.getY();
                updateExceptRect();
                log("MotionEvent", "ACTION_MOVE event.getX():" + event.getX() + " event.getY():" + event.getY());
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

    private void log(String tag, String msg) {
        if (isDebug)
            Log.d(tag, msg);
    }
	@Override
	public boolean onSingleTapUp(MotionEvent e) {

        float y = e.getY();
		bottomView.getLocationOnScreen(location);
        if (y < location[1] - titleHeight && y > 0) {
            scaleInAnimation();
            return true;
		}
        log("zqt", "onSingleTapUp titleHeight:" + titleHeight + " lp.height:" + lp.height + "location[1]: "
                + location[1] + " y=" + y);
        return false;
	}

    public void scaleInAnimation() {
        topView.scrollTo(0, 0);
        scrollView.scrollTo(0, 0);
        if (lp.height == topMiniHeight) {
            cancelAnimator();
            animator = ObjectAnimator.ofInt(this, "h", new int[] {
                    lp.height, contentHeight
            }).setDuration(
                    totalPlayTime - currentPlayTime);
            animator.start();
            animator.addListener(this);
            animator.addUpdateListener(this);
            scaleListener.onClickStart();
        } else if (lp.height == contentHeight) {
            cancelAnimator();
            animator = ObjectAnimator.ofInt(this, "h", new int[] {
                    lp.height, topMiniHeight
            }).setDuration(
                    totalPlayTime - currentPlayTime);
            animator.start();
            animator.addListener(this);
            animator.addUpdateListener(this);
            scaleListener.onClickEnd();
        }
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
        log("onScroll", "distanceY:" + distanceY + " distanceX:" + distanceX + "scrollView.getScrollY():"
				+ scrollView.getScrollY());
        log("onScroll", "adjustY(e1.getY()):" + adjustY(e1.getY()) + " lp.height:" + lp.height);
        log("onScroll", "titleHeight: " + titleHeight + "location:" + location[0] + "-" + location[1]);
		// checkHeight();
		if (scrollView.getScrollY() > 0) {// scrollview未到达顶端,不能缩放gallery的高度
            log("onScroll", "return false");
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
        log("adjustkHeight", "gallery.getScrollY(): " + topView.getScrollY() + "lp.height" + lp.height
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
        log("zqt", "cancelAnimator currentPlayTime: " + currentPlayTime);
	}

    private void startAnimatorInActionUp() {
        log("zqt", "startAnimator gallery.getScrollY():" + topView.getScrollY());
		if (lp.height <= topMiniHeight || lp.height >= contentHeight) {
			topView.scrollTo(0, 0);
            if (lp.height == topMiniHeight) {
                scaleListener.onReachStart();
            } else {
                scaleListener.onReachEnd();
            }
			return;
		}
		cancelAnimator();

		if (velocityY > 0) {
			animator = ObjectAnimator.ofInt(this, "h", new int[] { lp.height, contentHeight }).setDuration(
					totalPlayTime - currentPlayTime);
			animator.start();
			animator.addListener(this);
            animator.addUpdateListener(this);
            log("zqt", "放大 startAnimator currentPlayTime: " + currentPlayTime);
		} else {
            if (velocityY == 0) {

            }
            animator = ObjectAnimator.ofInt(this, "h", new int[] {
                    lp.height, topMiniHeight
            }).setDuration(
					totalPlayTime - currentPlayTime);
			animator.start();
			animator.addListener(this);
            animator.addUpdateListener(this);
            log("zqt", "缩小 startAnimator currentPlayTime: " + currentPlayTime);
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;
	}

	@Override
	public void onScrollChanged(int l, int t, int oldl, int oldt) {
        log("onScrollChanged ", "scrollView.getScrollY(): " + scrollView.getScrollY() + " topView.getTop()" + topView.getTop());
		if (scrollView.getScrollY() > 0) {
			// 设置gallery的scrollY的值
			scrollGallery();
        } else if (scrollView.getScrollY() <= 0) {
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
        int height = (Integer) ((ObjectAnimator) animation).getAnimatedValue();
        if (height == topMiniHeight) {
            scaleListener.onReachStart();
        } else {
            scaleListener.onReachEnd();
        }
	}
	@Override
    public void onAnimationUpdate(ValueAnimator animation) {
        scaleListener.onScale((Integer) animation.getAnimatedValue());
    }

    @Override
	public void onAnimationCancel(Animator animation) {

	}

	@Override
	public void onAnimationRepeat(Animator animation) {

	}

    public interface ScaleListener {
        void onClickStart();

        void onClickEnd();
        void onReachStart();

        void onReachEnd();

        void onScale(int height);
    }


}
