package in.co.iamannitian.iamannitian;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ListenNetworkState extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
	{
       LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("android.net.conn.CONNECTIVITY_CHANGE"));
    }
}
