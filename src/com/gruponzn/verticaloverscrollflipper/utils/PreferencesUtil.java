package com.gruponzn.verticaloverscrollflipper.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesUtil {

	private static final String OVERSCROLL_ENABLED = "V2.a72840b6e418972170e207863b7e15f9";

	public static void setOverScrollEnabled(Context context, boolean enabled) {
		getSharedPreferences(context, OVERSCROLL_ENABLED).edit().putBoolean(OVERSCROLL_ENABLED, enabled).commit();
	}

	public static boolean isOverScrollEnabled(Context context) {
		return getSharedPreferences(context, OVERSCROLL_ENABLED).getBoolean(OVERSCROLL_ENABLED, true);
	}

	private static SharedPreferences getSharedPreferences(Context context, String key) {
		String packageName = context.getPackageName();
		if (packageName == null || packageName.length() == 0)
			packageName = "com.gruponzn.verticaloverscrollflipper";

		String preferencesKey = packageName + "." + key;
		return context.getSharedPreferences(preferencesKey, Context.MODE_PRIVATE);
	}
}