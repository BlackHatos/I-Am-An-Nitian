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

public class TopperAdapter extends RecyclerView.Adapter<TopperAdapter.MyViewHolder> {

    private ArrayList<TopperGetterSetter> mList;
    private Context mContext;
    public TopperAdapter(Context mContext, ArrayList<TopperGetterSetter> mList)
    {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public TopperAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.topper_scroll, parent, false);
        return  new TopperAdapter.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TopperAdapter.MyViewHolder holder, int position) {
        TopperGetterSetter getterSetter = mList.get(position);

        String name = getterSetter.getName();
        String rank = getterSetter.getRank();
        String branch = getterSetter.getBranch();
        String imageUrl = getterSetter.getImage_link();
        String id = getterSetter.getId();
        String exam = getterSetter.getExam();
        String  college = getterSetter.getCollege();


        holder.name.setText(name);
        holder.rank.setText(rank);
        holder.branch.setText(branch);

        Glide.with(mContext)
                .asBitmap()
                .load(imageUrl)
                .into(holder.topperImage);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView topperImage;
        public TextView name;
        public TextView exam;
        public TextView rank;
        public TextView branch;

        MyViewHolder(View itemView)
        {
            super(itemView);
            topperImage = itemView.findViewById(R.id.topperImage);
            name = itemView.findViewById(R.id.name);
            rank = itemView.findViewById(R.id.rank);
            exam = itemView.findViewById(R.id.exam);
            branch = itemView.findViewById(R.id.branch);
        }
    }
}
