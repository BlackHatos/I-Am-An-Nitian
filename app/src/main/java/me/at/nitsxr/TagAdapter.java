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

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.TagViewHolder>
{
    private Context mContext;
    private ArrayList<TagGetterSetter> mList;

    public TagAdapter(Context mContext, ArrayList<TagGetterSetter> mList)
    {
          this.mContext = mContext;
          this.mList = mList;
    }

    @NonNull
    @Override
    public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.tag_scroll, parent, false);
        return  new TagViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
        TagGetterSetter getterSetter = mList.get(position);
        String tag_text = getterSetter.getTagText();
        String tag_image = getterSetter.getImage();

        holder.tagText.setText(tag_text);
        Glide.with(mContext)
       .asBitmap()
       .load(tag_image)
       .into(holder.tagPhoto);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
    class TagViewHolder extends RecyclerView.ViewHolder
    {
        public TextView tagText;
        public ImageView tagPhoto;

        public TagViewHolder(View itemView)
        {
            super(itemView);
            tagText = itemView.findViewById(R.id.tagText);
            tagPhoto = itemView.findViewById(R.id.tag);
        }
    }
}
