package me.at.nitsxr;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.R;

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
        String time_x = getterSetter.getTime();
        String notify_x = getterSetter.getNotification();
        String url = getterSetter.getImageUrl();

        holder.time.setText(time_x);
        holder.notification.setText(notify_x);

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.nitsri_photo)
                .load(url)
                .into(holder.notifyImage);
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
}
