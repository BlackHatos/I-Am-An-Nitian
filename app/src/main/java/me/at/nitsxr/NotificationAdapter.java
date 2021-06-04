package me.at.nitsxr;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import in.co.iamannitian.iamannitian.NotificationActivity;
import in.co.iamannitian.iamannitian.R;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>
{
    private List<NotificationGetterSetter> mList;
    private Context mContext;
    public NotificationAdapter(Context mContext, List<NotificationGetterSetter> mList)
    {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.notification_scroll, parent, false);
        return  new NotificationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position)
    {
        NotificationGetterSetter getterSetter = mList.get(position);
        String date = getterSetter.getTime();
        String title = getterSetter.getTitle();
        String url = getterSetter.getImageUrl();
        String id = getterSetter.getId();

        holder.time.setText(date);
        holder.notification.setText(title);

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.usericon)
                .load(url)
                .into(holder.notifyImage);

        ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull  RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }
        };
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        public TextView time, notification;
        public ImageView notifyImage;
        NotificationViewHolder(View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            notification = itemView.findViewById(R.id.notification);
            notifyImage = itemView.findViewById(R.id.notifyImage);
        }
    }

    public void deleteItem(int position)
    {
        String newsId = mList.get(position).getId();
        String userId=  mContext
                .getSharedPreferences("appData", Context.MODE_PRIVATE)
                .getString("userId", "");

        final String url = "http://app.iamannitian.com/app/deleted_notification.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if(status.equals("1"))
                        {
                            mList.remove(position);
                            notifyItemRemoved(position);
                            // this line gives the animation
                            notifyItemRangeChanged(position, getItemCount());
                            Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                        else
                            {
                                Toast.makeText(mContext, "Deletion failed", Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("idKey",userId);
                map.put("newsIdKey", newsId);
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(mContext);
        rq.add(sr);
    }
}

