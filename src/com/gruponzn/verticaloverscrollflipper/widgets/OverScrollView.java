package com.gruponzn.verticaloverscrollflipper.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.ScrollView;

import com.gruponzn.verticaloverscrollflipper.widgets.listeners.OverScrollListener;

public class OverScrollView extends ScrollView {

	public static final int MAX_Y_OVERSCROLL_DISTANCE = 200;

	private int touchSlop = -1;

	private int mMaxYOverscrollDistance;
	private int mPreviousY = 0;
	private int mTopLimit = 0;
	private int mBottomLimit = 0;
	private int mLowerBoundary = 0;
	private int mUpperBoundary = 0;

	private boolean mIsTouched = false;
	private boolean mShouldFireOverscoll = false;
	private boolean mBottomReached = false;
	private boolean mOverScrollAllowed = false;

	private OverScrollListener mListener = null;

	public OverScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		mMaxYOverscrollDistance = (int) (metrics.density * MAX_Y_OVERSCROLL_DISTANCE);
		mTopLimit = -mMaxYOverscrollDistance;
		mUpperBoundary = mTopLimit + MAX_Y_OVERSCROLL_DISTANCE;

		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
	}

	public void setOverScrollListener(OverScrollListener overScrollListener) {
		this.mListener = overScrollListener;
	}

	public void allowOverScroll() {
		this.mOverScrollAllowed = true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mIsTouched = true;

		if (event.getAction() == MotionEvent.ACTION_UP) {
			mIsTouched = false;

			if (null != mListener && mShouldFireOverscoll) {
				mBottomReached = false;
				mIsTouched = true;
				mShouldFireOverscoll = false;
				mListener.overScrolled(mPreviousY > 0);
			}
		}

		return super.onTouchEvent(event);
	}

	@Override
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
		if (null != mListener && moved(scrollY)) {
			if (scrollY <= mUpperBoundary) {
				mShouldFireOverscoll = scrollY <= mPreviousY;
			} else if (scrollY > mUpperBoundary && scrollY < mTopLimit + (mMaxYOverscrollDistance - 50)) {
				mListener.switchHeader(true);
				mShouldFireOverscoll = false;
			} else if (scrollY >= mLowerBoundary) {
				mShouldFireOverscoll = mBottomReached;
			} else if (scrollY < mLowerBoundary && scrollY > mBottomLimit - (mMaxYOverscrollDistance - 50)) {
				mListener.switchFooter(true);
				mShouldFireOverscoll = false;
			} else {
				mShouldFireOverscoll = false;
				mListener.switchHeader(false);
				mListener.switchFooter(false);
			}
		}

		mPreviousY = scrollY;
		super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	}

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX,
			int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {

		if (mBottomLimit < scrollY) {
			mBottomLimit = scrollY;
			mLowerBoundary = mBottomLimit - MAX_Y_OVERSCROLL_DISTANCE;
			mBottomReached = false;
		} else if (mBottomLimit > scrollRangeY) {
			mBottomReached = true;
		}

		if (!mOverScrollAllowed)
			return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
					maxOverScrollY, isTouchEvent);

		return super.overScrollBy(deltaX, deltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX,
				mIsTouched ? mMaxYOverscrollDistance : maxOverScrollY + MAX_Y_OVERSCROLL_DISTANCE, isTouchEvent);
	}

	private boolean moved(int value) {
		if ((mPreviousY + touchSlop) <= value && (mPreviousY - touchSlop) <= value)
			return false;

		return true;
	}
}