
package com.anjuke.library.uicomponent.scaleablescrollview;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class ScaleAbleScrollView extends ScrollView {
    private ScrollChangedListener listener;
    private onScrollChangedUIUpdater scorllUiUpdater;

    public onScrollChangedUIUpdater getScorllUiUpdater() {
        return scorllUiUpdater;
    }

    public void setScorllUiUpdater(onScrollChangedUIUpdater scorllUiUpdater) {
        this.scorllUiUpdater = scorllUiUpdater;
    }

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
        if (scorllUiUpdater != null) {
            scorllUiUpdater.onScrollChangedUIUpdate(l, t, oldl, oldt);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (touchListener != null)
            return touchListener.onTouchEvent(ev) ? true : super.dispatchTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    // @Override
    // public boolean onTouchEvent(MotionEvent ev) {
    // return true;
    // }

    public interface MotionEventListener {
        boolean onTouchEvent(MotionEvent ev);
    }

    public interface ScrollChangedListener {
        void onScrollChanged(int l, int t, int oldl, int oldt);
    }

    /**
     * ScrollView滚动时，主界面更新接口，由于ScaleAbleCotroller已经实现了ScrollChangedListener， 因此为了更新Activity中UI，需定义另外一个接口监听滚动事件
     * 
     * @param l Current horizontal scroll origin. t Current vertical scroll origin. oldl Previous horizontal scroll
     *            origin. oldt Previous vertical scroll origin.
     */
    public interface onScrollChangedUIUpdater {
        void onScrollChangedUIUpdate(int l, int t, int oldl, int oldt);
    }

    // 不会因为child view获取到焦点而滚动
    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        return 0;
    }
}
