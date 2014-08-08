package com.gruponzn.verticaloverscrollflipper.widgets.listeners;

public interface OverScrollListener {
	public void overScrolled(boolean next);

	public void switchHeader(boolean on);

	public void switchFooter(boolean on);
}