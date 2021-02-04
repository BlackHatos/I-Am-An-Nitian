package me.at.nitsxr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.MainActivity;
import in.co.iamannitian.iamannitian.R;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.MyViewHolder> {

    private ArrayList<TopicGetterSetter> mList;
    private Context mContext;
    public TopicAdapter(Context mContext, ArrayList<TopicGetterSetter> mList)
    {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public TopicAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.topic_scroll, parent, false);
        return  new TopicAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TopicAdapter.MyViewHolder holder, int position) {
        TopicGetterSetter getterSetter = mList.get(position);
        String imageUrl = getterSetter.getImageUrl();
        String tag = getterSetter.getTag();

        holder.tag.setText(tag);

        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(holder.topicImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView topicImage;
        public TextView tag;

        MyViewHolder(View itemView)
        {
            super(itemView);
            topicImage = itemView.findViewById(R.id.topicImage);
            tag = itemView.findViewById(R.id.tag);
        }
    }
}
