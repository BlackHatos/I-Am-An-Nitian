package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;

import static android.content.Context.MODE_PRIVATE;

public class GetSaveNewsAdapter extends RecyclerView.Adapter<GetSaveNewsAdapter.MyViewHolder>
{
    private Context mContext;
    private List<NewsGetterSetter> mList;

    public GetSaveNewsAdapter(Context mContext, List<NewsGetterSetter> mList)
    {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public GetSaveNewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.saved_item_scroll, parent, false);
        return  new GetSaveNewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final GetSaveNewsAdapter.MyViewHolder holder, final int position)
    {
        final NewsGetterSetter getterSetter = mList.get(position);
        String newsTitle = getterSetter.getNewsTitle();
        String news_count = getterSetter.getCount();
        String news_date = getterSetter.getNewsDate();
        String news_status = getterSetter.getStatus();
        String image_url = getterSetter.getImageUrl();

        Glide.with(mContext)
                .asBitmap()
                .load(image_url)
                .into(holder.newsImage);

        String arr[] = newsTitle.split(" ");
        String finalTitle = "";
        if(arr.length > 7)
            finalTitle = arr[0] +" "+ arr[1] +" "+ arr[2] +" "
                    + arr[3] +" "+ arr[4] +" "+ arr[5] +" "+ arr[6] +" "+arr[7];
        else
            finalTitle = newsTitle;

        holder.newsDate.setText("18 hours ago");
        holder.newsTitle.setText(finalTitle);
        holder.newsCount.setText(news_count);

        if(getterSetter.getStatus().equals("1"))
            holder.newsHeart.setImageResource(R.drawable.ic_favorite_black_24dp);

        holder.unSaveNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               new NewsDataBase(mContext).deleteRecord(getterSetter.getNewsId());
                mList.remove(position);
                notifyDataSetChanged();
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent =  new Intent(mContext, OnViewPagerClick.class);
                Bundle b = new Bundle();
                b.putSerializable("sampleObject", getterSetter);
                intent.putExtras(b);
                mContext.startActivity(intent);
            }
        });

        holder.newsHeart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                int updated_status = 0;
                int updated_count = 0;

                NewsDataBase newsDataBase = new NewsDataBase(mContext);

                if(getterSetter.getStatus().equals("1"))
                {
                    updated_status = 0;
                    updated_count =  Integer.parseInt(getterSetter.getCount()) - 1;
                    newsDataBase.updateRecord(getterSetter.getNewsId(), "0", updated_count+"");
                    holder.newsCount.setText(updated_count+"");
                    holder.newsHeart.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else
                {
                    updated_status = 1;
                    updated_count  = Integer.parseInt(getterSetter.getCount()) + 1;
                    newsDataBase.updateRecord(getterSetter.getNewsId(), "1", updated_count+"");
                    holder.newsCount.setText(updated_count+"");
                    holder.newsHeart.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                getterSetter.setStatus(updated_status+"");
                getterSetter.setCount(updated_count+"");

                notifyDataSetChanged();

                UserReaction userReaction = new UserReaction(getterSetter,
                        mContext.getSharedPreferences("appData", MODE_PRIVATE)
                                .getString("userId",""), updated_status);
                userReaction.execute();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        ImageView newsImage, newsHeart, unSaveNews;
        TextView newsTitle, newsCount, newsDate;
        CardView cardView;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            newsImage = itemView.findViewById(R.id.newsImage);
            newsHeart = itemView.findViewById(R.id.newsHeart);
            newsCount = itemView.findViewById(R.id.newsCount);
            newsDate = itemView.findViewById(R.id.newsDate);
            unSaveNews = itemView.findViewById(R.id.unSaveNews);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
