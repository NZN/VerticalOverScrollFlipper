package com.gruponzn.verticaloverscrollflipper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;

import com.gruponzn.verticaloverscrollflipper.OverScrollUtil;

/**
 * Recebe o index do item exibido ap??s o t??rmino da anima????o de rolagem e
 * altera a posi????o na lista de itens.
 * 
 * @author fernando.drummond
 */
public class ItemSelectionReceiver extends BroadcastReceiver {

	private String mName;
	private ListView mList;

	public ItemSelectionReceiver(String listName, ListView list) {
		this.mName = listName;
		this.mList = list;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			if (intent.getAction().equals(OverScrollUtil.ACTION_SELECTION) && null != intent.getExtras()
					&& mName.equals(intent.getExtras().getSerializable(OverScrollUtil.FLAG_LIST_CALLER))) {
				int position = intent.getIntExtra(OverScrollUtil.ITEM_POSITION, 0);
				mList.smoothScrollToPosition(position);
				mList.setSelection(position);
			}
		} catch (RuntimeException ep) {
		}
	}
}