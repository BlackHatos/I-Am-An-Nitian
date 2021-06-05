package me.at.nitsxr;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static android.content.Context.MODE_PRIVATE;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>
{
    private List<NewsGetterSetter> mList;
    private Context mContext;
    public NotificationAdapter(Context mContext, List<NewsGetterSetter> mList)
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
        NewsGetterSetter getterSetter = mList.get(position);
        String date = getterSetter.getNewsDate();
        String title = getterSetter.getNewsTitle();
        String url = getterSetter.getImageUrl();

        holder.time.setText(date);
        holder.notification.setText(title);

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.usericon)
                .load(url)
                .into(holder.notifyImage);

        holder.top_layout.setOnClickListener(v -> {
            Intent intent =  new Intent(mContext, OnViewPagerClick.class);
            Bundle b = new Bundle();
            b.putSerializable("sampleObject", getterSetter);
            intent.putExtras(b);
            // this is used to check activity type
            intent.putExtra("temp", "1");
            mContext.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder
    {
        public TextView time, notification;
        public ImageView notifyImage;
        public LinearLayout top_layout;
        NotificationViewHolder(View itemView)
        {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            notification = itemView.findViewById(R.id.notification);
            notifyImage = itemView.findViewById(R.id.notifyImage);
            top_layout = itemView.findViewById(R.id.top_layout);
        }
    }

    public void deleteItem(int position)
    {
        String newsId = mList.get(position).getNewsId();
        String userId=  mContext
                .getSharedPreferences("appData", MODE_PRIVATE)
                .getString("userId", "");

        mList.remove(position);
        notifyItemRemoved(position);
        // this line gives the animation
        notifyItemRangeChanged(position, getItemCount());

        final String url = "http://app.iamannitian.com/app/deleted_notification.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if(status.equals("1"))
                        {
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

