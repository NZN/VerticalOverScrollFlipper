package com.nzn.encubadora.verticalviewflipper.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class OverScrollView extends ScrollView {

    public static final int MAX_Y_OVERSCROLL_DISTANCE = 200;

    private int mMaxYOverscrollDistance;
    private int mPreviousY = 0;
    private int mTopLimit = 0;
    private int mBottomLimit = 0;
    private int mLowerBoundary = 0;
    private int mUpperBoundary = 0;

    private boolean mShouldFireOverscoll = false;

    private OverScrollListener mListener = null;

    public OverScrollView(Context context, AttributeSet attrs) {
	super(context, attrs);

	DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	mMaxYOverscrollDistance = (int) (metrics.density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    public void setOverScrollListener(OverScrollListener overScrollListener) {
	this.mListener = overScrollListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	if (event.getAction() == MotionEvent.ACTION_UP) {
	    if (null != mListener && mShouldFireOverscoll)
		mListener.overScrolled(mPreviousY > 0);
	}

	return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
	super.onLayout(changed, l, t, r, b);

	mBottomLimit = r + MAX_Y_OVERSCROLL_DISTANCE;
	mLowerBoundary = mBottomLimit - MAX_Y_OVERSCROLL_DISTANCE;
	mTopLimit = -mMaxYOverscrollDistance;
	mUpperBoundary = mTopLimit + MAX_Y_OVERSCROLL_DISTANCE;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
	if (null != mListener && moved(scrollY)) {
	    if (scrollY >= mLowerBoundary) {
		mListener.switchFooterWarn(true);
		mListener.switchFooterChange(false);

		mShouldFireOverscoll = true;
	    } else if (scrollY < mLowerBoundary && scrollY > mBottomLimit - (mMaxYOverscrollDistance - 50)) {
		mListener.switchFooterWarn(false);
		mListener.switchFooterChange(true);

		mShouldFireOverscoll = false;
	    } else if (scrollY <= mUpperBoundary) {
		mListener.switchHeaderWarn(true);
		mListener.switchHeaderChange(false);

		mShouldFireOverscoll = true;
	    } else if (scrollY > mUpperBoundary && scrollY < mTopLimit + (mMaxYOverscrollDistance - 50)) {
		mListener.switchHeaderWarn(false);
		mListener.switchHeaderChange(true);

		mShouldFireOverscoll = false;
	    } else {
		mListener.switchHeaderWarn(true);
		mListener.switchFooterWarn(true);

		mShouldFireOverscoll = false;
	    }
	}

	mPreviousY = scrollY;
	super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
	    int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
	return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
		mMaxYOverscrollDistance, isTouchEvent);
    }

    private boolean moved(int value) {
	if ((mPreviousY + 10) <= value && (mPreviousY - 10) <= value) {
	    return false;
	}

	return true;
    }

    public interface OverScrollListener {
	public void overScrolled(boolean next);

	public void switchHeaderWarn(boolean on);

	public void switchHeaderChange(boolean on);

	public void switchFooterWarn(boolean on);

	public void switchFooterChange(boolean on);
    }
}