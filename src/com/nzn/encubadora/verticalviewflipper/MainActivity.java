package com.nzn.encubadora.verticalviewflipper;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.nzn.encubadora.verticalviewflipper.widget.OverScrollView;
import com.nzn.encubadora.verticalviewflipper.widget.OverScrollView.OverScrollListener;

public class MainActivity extends Activity implements OverScrollListener {

    private ViewFlipper mViewFlipper;
    
    private Animation fadeIn;
    private Animation fadeOut;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);

	mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

	LayoutInflater inflater = LayoutInflater.from(this);

	View flipView = inflater.inflate(R.layout.view_layout, null);
	((OverScrollView) flipView.findViewById(R.id.view_layout)).setOverScrollListener(this);
	mViewFlipper.addView(flipView);

	flipView = inflater.inflate(R.layout.view_layout, null);
	((OverScrollView) flipView.findViewById(R.id.view_layout)).setOverScrollListener(this);
	mViewFlipper.addView(flipView);

	flipView = inflater.inflate(R.layout.view_layout, null);
	((OverScrollView) flipView.findViewById(R.id.view_layout)).setOverScrollListener(this);
	mViewFlipper.addView(flipView);
	
	fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
	fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
    }

    @Override
    public void overScrolled(boolean next) {
	if (next) {
	    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.down_up));
	    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.under_down));
	    mViewFlipper.showNext();
	} else {
	    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, R.anim.up_down));
	    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.over_up));
	    mViewFlipper.showPrevious();
	}
    }
    
    @Override
    public void switchHeaderWarn(boolean on) {
	switchView(mViewFlipper.getCurrentView().findViewById(R.id.header_warn), on);
    }

    @Override
    public void switchHeaderChange(boolean on) {
	switchView(mViewFlipper.getCurrentView().findViewById(R.id.header_change), on);
    }

    @Override
    public void switchFooterWarn(boolean on) {
	switchView(mViewFlipper.getCurrentView().findViewById(R.id.footer_warn), on);
    }

    @Override
    public void switchFooterChange(boolean on) {
	switchView(mViewFlipper.getCurrentView().findViewById(R.id.footer_change), on);
    }

    private void switchView(View view, boolean on) {
	view.startAnimation(on ? fadeIn : fadeOut);
	view.setVisibility(on ? View.VISIBLE : View.INVISIBLE);
    }
}