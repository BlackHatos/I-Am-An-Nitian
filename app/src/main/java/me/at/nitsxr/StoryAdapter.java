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
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
       StoryGetterSetter getterSetter = mList.get(position);
       String topper_name = getterSetter.getName();
       String topper_rank = getterSetter.getRank();
       String topper_college = getterSetter.getBranch();
       String imageUrl = getterSetter.getImageUrl();

       holder.name.setText(topper_name);
       holder.rank.setText("AIR-"+topper_rank);
       holder.branch.setText(topper_college);

        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(holder.storyImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class StoryViewHolder extends RecyclerView.ViewHolder
      {
          public ImageView storyImage;
          public TextView name;
          public TextView rank;
          public TextView branch;

          StoryViewHolder(View itemView)
          {
              super(itemView);
              storyImage = itemView.findViewById(R.id.storyImage);
              name = itemView.findViewById(R.id.name);
              rank = itemView.findViewById(R.id.rank);
              branch = itemView.findViewById(R.id.branch);
          }
      }
}
