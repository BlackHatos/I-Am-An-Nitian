package me.at.nitsxr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.R;

public class TopCollegeAdapter extends
        RecyclerView.Adapter<TopCollegeAdapter.TopCollegeViewHolder>
{
    private Context mContext;
    private List<TopCollegeGetterSetter> mList;

    public TopCollegeAdapter(Context mContext, List<TopCollegeGetterSetter> mList)
        {
            this.mContext = mContext;
            this.mList = mList;
        }
    @NonNull
    @Override
    public TopCollegeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.top_colleges_scroll, parent, false);
        return  new TopCollegeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TopCollegeViewHolder holder, final int position)
    {
        final TopCollegeGetterSetter getterSetter = mList.get(position);
        String college_name = getterSetter.getCollege_name();
        holder.collegeName.setText(college_name);

        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.nitsri_photo)
                .load("")
                .into(holder.collegeImage);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class TopCollegeViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView collegeImage;
        private TextView collegeName;
        public TopCollegeViewHolder(View itemView)
        {
            super(itemView);
            collegeImage = itemView.findViewById(R.id.collegeImage);
            collegeName = itemView.findViewById(R.id.collegeName);
        }
    }
}
