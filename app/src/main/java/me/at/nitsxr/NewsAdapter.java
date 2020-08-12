package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;
import in.co.iamannitian.iamannitian.SlideUtils;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>
{
    private Context mContext;
    private ArrayList<NewsGetterSetter> mList;


    public NewsAdapter(Context mContext, ArrayList<NewsGetterSetter> mList)
    {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.news_scroll, parent, false);
        return  new NewsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, final int position) {
           NewsGetterSetter getterSetter = mList.get(position);
           String news_title = getterSetter.getNewsTitle();
           String image_url = getterSetter.getImageUrl();

           holder.newsTitle.setText(news_title);

           Glide.with(mContext)
                .asBitmap()
                .load(image_url)
                .into(holder.newsImageView);

           holder.cardView.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   Toast.makeText(mContext,position+"", Toast.LENGTH_SHORT).show();
               }
           });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder
    {
        public TextView newsTitle;
        public ImageView newsImageView;
        public CardView cardView;
        public NewsViewHolder(View itemView)
        {
            super(itemView);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsImageView = itemView.findViewById(R.id.newsImageView);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }
}
