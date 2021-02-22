package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder>
{
    private ArrayList<StoryGetterSetter> mList;
    private Context mContext;
      public StoryAdapter(Context mContext, ArrayList<StoryGetterSetter> mList)
      {
          this.mContext = mContext;
          this.mList = mList;
      }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.story_scroll, parent, false);
        return  new StoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position)
    {
       StoryGetterSetter getterSetter = mList.get(position);
       String name = getterSetter.getName();
       String rank = getterSetter.getRank();
       String branch = getterSetter.getBranch();
       String imageUrl = getterSetter.getImageUrl();
       String college = getterSetter.getCollege();
       String exam = getterSetter.getExam();
       String id = getterSetter.getId();

       //holder.storyTitle.setText(name+" | "+exam+" | "+rank+" | "+college+" | "+branch);
         Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(holder.storyImage);

        holder.clickStory.setOnClickListener(v -> {
            Intent intent =  new Intent(mContext, OnViewPagerClick.class);
            Bundle b = new Bundle();
            b.putSerializable("sampleObject", getterSetter);
            intent.putExtras(b);
            intent.putExtra("temp", "2");
            mContext.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder
      {
          public ImageView storyImage;
          public TextView storyTitle;
          public CardView clickStory;

          StoryViewHolder(View itemView)
          {
              super(itemView);
              storyImage = itemView.findViewById(R.id.storyImage);
              storyTitle = itemView.findViewById(R.id.storyTitle);
              clickStory = itemView.findViewById(R.id.cardView);
          }
      }
}
