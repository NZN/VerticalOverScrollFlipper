package com.gruponzn.verticaloverscrollflipper.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.ListView;

import com.gruponzn.verticaloverscrollflipper.OverScrollUtil;
import com.gruponzn.verticaloverscrollflipper.R;

/**
 * Recebe o index do próximo item a ser exibido na rolagem e encaminha para
 * preenchimento da atividade.
 * 
 * @author fernando.drummond
 */
public class ItemRequestReceiver extends BroadcastReceiver {

	private String mItemName;
	private String mName;
	private ListView mList;

	public ItemRequestReceiver(String itemName, String listName, ListView list) {
		this.mItemName = itemName;
		this.mName = listName;
		this.mList = list;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		int position = -1;

		try {
			if (intent.getAction().equals(OverScrollUtil.ACTION_ITEM_REQUEST)
					&& mName.equals(intent.getExtras().getSerializable(OverScrollUtil.FLAG_LIST_CALLER))) {
				position = intent.getIntExtra(OverScrollUtil.ITEM_POSITION, 0);

				Intent broadcast = new Intent(context.getString(R.string.app_name) + "." + OverScrollUtil.ACTION_ITEM_RESPONSE);

				broadcast.putExtra(OverScrollUtil.ITEM_POSITION, position);
				broadcast.putExtra(OverScrollUtil.FLAG_LIST_CALLER, mName);
				broadcast.putExtra(mItemName, (Parcelable) mList.getItemAtPosition(position));

				context.sendBroadcast(broadcast);
			}
		} catch (RuntimeException ep) {
			if (ep instanceof IndexOutOfBoundsException) {
				Intent broadcast = new Intent(context.getString(R.string.app_name) + "." + OverScrollUtil.ACTION_ITEM_RESPONSE);

				broadcast.putExtra(OverScrollUtil.ITEM_POSITION, --position);
				broadcast.putExtra(OverScrollUtil.FLAG_LIST_CALLER, mName);

				context.sendBroadcast(broadcast);
			}
		}
	}
}