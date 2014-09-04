package com.gruponzn.verticaloverscrollflipper.widgets.listeners;

import com.gruponzn.verticaloverscrollflipper.OverScrollUtil;

import android.widget.CheckBox;
import android.widget.CompoundButton;

public class OverScrollCheckBoxListener implements CheckBox.OnCheckedChangeListener {

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		OverScrollUtil.getInstance().setOverScrollEnabled(buttonView.getContext(), isChecked);
	}
}