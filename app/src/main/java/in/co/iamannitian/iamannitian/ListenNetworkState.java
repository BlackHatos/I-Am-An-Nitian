package in.co.iamannitian.iamannitian;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ListenNetworkState extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
	{
      ///do something
    }

	public boolean checkConnection(Context context)
	{
		ConnectivityManager connectivityManager = (ConnectivityManager)context
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
               if(networkInfo != null && networkInfo.isConnected())
				   return true;
			   else
				   return false;
	}
}
