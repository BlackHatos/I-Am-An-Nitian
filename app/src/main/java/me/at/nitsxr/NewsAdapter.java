package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;

import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_COUNT;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_DATE;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_DESCP;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_ID;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_STATUS;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_TITLE;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_URL;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_URL_2;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>
{
    private Context mContext;
    private ArrayList<NewsGetterSetter> mList;
    private Map<String, String> reactionMap;
    private Map<String, String> countMap;
    private String userId;

    public NewsAdapter(Context mContext, ArrayList<NewsGetterSetter> mList)
    {
        this.mContext = mContext;
        this.mList = mList;
      /*  this.userId = userId;
        this.reactionMap = reactionMap;
        this.countMap = countMap;*/
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.news_scroll, parent, false);
        return  new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewsViewHolder holder, final int position) {
           final NewsGetterSetter getterSetter = mList.get(position);
           String news_title = getterSetter.getNewsTitle();
           String image_url = getterSetter.getImageUrl();
           String status = getterSetter.getStatus();
           final String newsId = getterSetter.getNewsId();

           String newsStatus = "";
           if(reactionMap.containsKey(newsId) && reactionMap.get(newsId).equals("1"))
           {
               holder.userReaction.setImageResource(R.drawable.ic_favorite_black_24dp);
               newsStatus = "1";
           }
           else
           {
               holder.userReaction.setImageResource(R.drawable.ic_favorite_border_black_24dp);
               newsStatus = "0";
           }

     holder.newsTitle.setText(news_title);
           Glide.with(mContext)
                .asBitmap()
                .load(image_url)
                .into(holder.newsImageView);

        final String finalNewsStatus = newsStatus;

        holder.cardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v)
               {
                   Intent intent =  new Intent(mContext,OnViewPagerClick.class);
                   intent.putExtra(EXTRA_URL, getterSetter.getImageUrl());
                   intent.putExtra(EXTRA_NEWS_DESCP,getterSetter.getNewsDescp());
                   intent.putExtra(EXTRA_NEWS_TITLE, getterSetter.getNewsTitle());
                   intent.putExtra(EXTRA_NEWS_DATE, getterSetter.getNewsDate());
                   intent.putExtra(EXTRA_NEWS_ID, newsId);
                   intent.putExtra(EXTRA_URL_2, getterSetter.getImageUrl2());
                   intent.putExtra(EXTRA_NEWS_STATUS, finalNewsStatus);
                   intent.putExtra(EXTRA_NEWS_COUNT, countMap.get(getterSetter.getNewsId()));
                   mContext.startActivity(intent);
               }
           });

           holder.userReaction.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   new UserReaction(getterSetter, holder, userId).execute();
               }
           });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView newsTitle, reactionCount;
        public ImageView newsImageView, userReaction;
        public CardView cardView;

        public NewsViewHolder(View itemView)
        {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsImageView = itemView.findViewById(R.id.newsImageView);
            cardView = itemView.findViewById(R.id.cardView);
            userReaction = itemView.findViewById(R.id.userRection);
            reactionCount = itemView.findViewById(R.id.reactionCount);
        }
    }

}
