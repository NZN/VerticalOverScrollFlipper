package com.gruponzn.verticaloverscrollflipper;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.gruponzn.verticaloverscrollflipper.receivers.listeners.ItemChangeListener;
import com.gruponzn.verticaloverscrollflipper.utils.GoogleTrackerUtil;
import com.gruponzn.verticaloverscrollflipper.utils.PreferencesUtil;
import com.gruponzn.verticaloverscrollflipper.widgets.listeners.OverScrollListener;

public class OverScrollUtil implements OverScrollListener {

	private static OverScrollUtil INSTANCE;

	public static final String FLAG_LIST_CALLER = "overscroll.FLAG_ARTICLE_LIST_CALLER";
	public static final String ACTION_ITEM_RESPONSE = "overscroll.itemresponse.ACTION_ITEM_RESPONSE";
	public static final String ACTION_ITEM_REQUEST = "overscroll.itemrequest.ACTION_ITEM_REQUEST";
	public static final String ACTION_SELECTION = "overscroll.itemselection.ACTION_SELECTION";
	public static final String ITEM_POSITION = "overscroll.ITEM_POSITION";

	public static final int MAX_NUMBER_OF_ARTICLES = 1;

	public enum Fetching {
		NEXT, PREVIOUS, IDLE, STANDALONE;
	}

	private Animation upIn;
	private Animation upOut;
	private Animation downIn;
	private Animation downOut;

	private AnimationListener inNullifierListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mViewFlipper.setInAnimation(null);
		}
	};

	private AnimationListener outNullifierListener = new AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			mViewFlipper.setOutAnimation(null);
		}
	};

	private Activity mActivity;
	private String mItemName;
	private String mItemList;
	private ViewFlipper mViewFlipper;
	private ItemResponseReceiver mItemReceiver;
	private ItemChangeListener mChangeListener;

	private Fetching mFetchingState = Fetching.IDLE;

	private int mCurrentGlobalPosition = -1;
	private int mNextPosition = -1;
	private int mPreviousPosition = -1;

	private Parcelable mNextItem;
	private Parcelable mPreviousItem;

	private OverScrollUtil() {

	}

	public static OverScrollUtil getInstance() {
		if (null == INSTANCE)
			INSTANCE = new OverScrollUtil();

		return INSTANCE;
	}

	public void attach(Activity activity, String itemName, String itemList, ViewFlipper viewFlipper) {
		if (!(activity instanceof ItemChangeListener))
			throw new IllegalArgumentException("A Activity deve implementar a interface ItemChangeListener");

		if (null != mActivity && null != mItemReceiver) {
			mActivity.unregisterReceiver(mItemReceiver);
		}

		this.mActivity = activity;
		this.mItemName = itemName;
		this.mItemList = itemList;
		this.mViewFlipper = viewFlipper;
		this.mChangeListener = (ItemChangeListener) activity;

		registerResponseReceiver(activity.getString(R.string.app_name));

		if (null == mItemList)
			mFetchingState = Fetching.STANDALONE;
	}

	public void addView(View view) {
		mViewFlipper.addView(view);
	}

	public void addViewAt(View view, int position) {
		this.mViewFlipper.addView(view, position);
	}

	public void setCurrentGlobalPosition(int currentGlobalPosition) {
		this.mCurrentGlobalPosition = currentGlobalPosition;
	}

	public void setAnimationRollUp(Animation in, Animation out) {
		if (null != in)
			in.setAnimationListener(inNullifierListener);

		if (null != out)
			out.setAnimationListener(outNullifierListener);

		this.upIn = in;
		this.upOut = out;
	}

	public void setAnimationRollDown(Animation in, Animation out) {
		if (null != in)
			in.setAnimationListener(inNullifierListener);

		if (null != out)
			out.setAnimationListener(outNullifierListener);

		this.downIn = in;
		this.downOut = out;
	}

	public View getCurrentView() {
		return mViewFlipper.getCurrentView();
	}

	public boolean isFlipping() {
		return null == mViewFlipper ? true : mViewFlipper.isFlipping();
	}

	public void setOverScrollEnabled(boolean overScrollEnabled) {
		PreferencesUtil.setOverScrollEnabled(mActivity, overScrollEnabled);
	}

	public boolean isOverScrollEnabled() {
		return PreferencesUtil.isOverScrollEnabled(mActivity);
	}

	public boolean isOverScrollPossible() {
		return isOverScrollEnabled() && mFetchingState != Fetching.STANDALONE;
	}

	@Override
	public void switchHeader(boolean on) {
		if (on) {
			mChangeListener.overScrolling();

			if (null == mPreviousItem)
				fetchPreviousItem();
		}
	}

	@Override
	public void switchFooter(boolean on) {
		if (on) {
			mChangeListener.overScrolling();

			if (null == mNextItem)
				fetchNextItem();
		}
	}

	@Override
	public void overScrolled(boolean next) {
		if (!isOverScrollEnabled())
			return;

		if (isFlipping() || mFetchingState != Fetching.IDLE)
			return;

		if (next && null == mNextItem)
			return;

		if (!next && null == mPreviousItem)
			return;

		mChangeListener.prepareView(next);

		if (next) {
			mCurrentGlobalPosition = mNextPosition;
			mChangeListener.setupItem(mNextItem);
			rollUp();
		} else {
			mCurrentGlobalPosition = mPreviousPosition;
			mChangeListener.setupItem(mPreviousItem);
			rollDown();
		}

		mNextItem = null;
		mPreviousItem = null;
	}

	private void registerResponseReceiver(String identifier) {
		mItemReceiver = new ItemResponseReceiver();
		mActivity.registerReceiver(mItemReceiver, new IntentFilter(mActivity.getString(R.string.app_name) + "."
				+ ACTION_ITEM_RESPONSE));
	}

	private void fetchItem(int position) {
		if (null == mItemList)
			return;

		Intent broadcast = new Intent(OverScrollUtil.ACTION_ITEM_REQUEST);
		broadcast.putExtra(OverScrollUtil.ITEM_POSITION, position);
		broadcast.putExtra(OverScrollUtil.FLAG_LIST_CALLER, mItemList);

		mActivity.sendBroadcast(broadcast);
	}

	private void fetchNextItem() {
		if (mFetchingState != Fetching.IDLE)
			return;

		mFetchingState = Fetching.NEXT;
		fetchItem(mCurrentGlobalPosition + 1);
	}

	private void fetchPreviousItem() {
		if (mFetchingState != Fetching.IDLE)
			return;

		if (mCurrentGlobalPosition <= 0) {
			mCurrentGlobalPosition = 0;
			mChangeListener.topReached();
			return;
		}

		mFetchingState = Fetching.PREVIOUS;
		fetchItem(mCurrentGlobalPosition - 1);
	}

	public void rollUp() {
		switchFooter(false);
		switchHeader(false);

		mViewFlipper.setInAnimation(upIn);
		mViewFlipper.setOutAnimation(upOut);
		mViewFlipper.showNext();

		while (mViewFlipper.isFlipping())
			;

		if (mViewFlipper.getChildCount() > OverScrollUtil.MAX_NUMBER_OF_ARTICLES) {
			recycleView(mViewFlipper.getChildAt(0));
			mViewFlipper.removeViewAt(0);
		}

		GoogleTrackerUtil.trackEvent(mActivity, "news", "swype_up", getCurrentView().toString());
	}

	public void rollDown() {
		switchFooter(false);
		switchHeader(false);

		mViewFlipper.setOutAnimation(downOut);
		mViewFlipper.setInAnimation(downIn);
		mViewFlipper.showPrevious();

		while (mViewFlipper.isFlipping())
			;

		if (mViewFlipper.getChildCount() > OverScrollUtil.MAX_NUMBER_OF_ARTICLES) {
			recycleView(mViewFlipper.getChildAt(OverScrollUtil.MAX_NUMBER_OF_ARTICLES));
			mViewFlipper.removeViewAt(OverScrollUtil.MAX_NUMBER_OF_ARTICLES);
		}

		GoogleTrackerUtil.trackEvent(mActivity, "news", "swype_up", getCurrentView().toString());
	}

	public void completedViewLoading() {
		int position = mFetchingState == Fetching.NEXT ? mNextPosition : mPreviousPosition;

		if (mFetchingState != Fetching.STANDALONE)
			mFetchingState = Fetching.IDLE;

		if (position != -1) {
			Intent intent = new Intent(OverScrollUtil.ACTION_SELECTION);
			intent.putExtra(OverScrollUtil.ITEM_POSITION, mCurrentGlobalPosition);
			intent.putExtra(OverScrollUtil.FLAG_LIST_CALLER, mItemList);
			mActivity.sendBroadcast(intent);
		}
	}

	public void detach(Context context) {
		if (this.mActivity != context)
			return;

		mItemList = null;

		mChangeListener = null;

		if (null != mViewFlipper) {
			mViewFlipper.removeAllViews();
			mViewFlipper = null;
		}

		if (null != mItemReceiver)
			context.unregisterReceiver(mItemReceiver);

		mItemReceiver = null;
		mNextItem = null;
		mPreviousItem = null;

		mFetchingState = Fetching.IDLE;
	}

	private void recycleView(View view) {
		if (!(view instanceof ViewGroup))
			return;

		ViewGroup vg = (ViewGroup) view;
		for (int i = 0; i < vg.getChildCount(); i++) {
			View v = vg.getChildAt(i);
			if (v != null) {
				if (v instanceof ImageView) {
					ImageView iv = (ImageView) v;
					iv.setImageDrawable(null);
				} else if (v instanceof ViewGroup) {
					recycleView((ViewGroup) v);
				}
			}
		}
	}

	private class ItemResponseReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					mActivity.getString(R.string.app_name) + "." + OverScrollUtil.ACTION_ITEM_RESPONSE)
					&& mItemList.equals(intent.getExtras().getString(OverScrollUtil.FLAG_LIST_CALLER))) {

				Log.w(getClass().getSimpleName(), intent.toString());

				if (intent.getExtras().containsKey(OverScrollUtil.ITEM_POSITION)) {
					if (mFetchingState == Fetching.NEXT)
						mNextPosition = intent.getExtras().getInt(OverScrollUtil.ITEM_POSITION);
					else
						mPreviousPosition = intent.getExtras().getInt(OverScrollUtil.ITEM_POSITION);
				}

				if (null != intent.getExtras()) {
					if (mFetchingState == Fetching.NEXT) {
						mNextItem = intent.getExtras().getParcelable(mItemName);
						mChangeListener.itemReceived(mNextItem, true);

						if (null == mNextItem) {
							mNextPosition = mCurrentGlobalPosition + 1;
							mChangeListener.bottomReached();
						}
					} else if (mFetchingState == Fetching.PREVIOUS) {
						mPreviousItem = intent.getExtras().getParcelable(mItemName);
						mChangeListener.itemReceived(mPreviousItem, false);

						if (null == mPreviousItem) {
							mCurrentGlobalPosition = 0;
							mChangeListener.topReached();
						}
					}

					mFetchingState = Fetching.IDLE;
				} else {
					Log.e(getClass().getSimpleName(), "onReceive > No Extras received");
				}
			}
		}
	}
}