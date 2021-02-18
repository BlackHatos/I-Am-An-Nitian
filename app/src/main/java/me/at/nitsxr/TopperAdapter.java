package me.at.nitsxr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import in.co.iamannitian.iamannitian.R;

public class TopperAdapter extends RecyclerView.Adapter<TopperAdapter.MyViewHolder> {

    private ArrayList<TopperGetterSetter> mList;
    private Context mContext;
    private BottomSheetDialog bottomSheetDialog;

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

        holder.clickCard.setOnClickListener(v -> {
            showBottomSheet(name, rank, branch, imageUrl, college, exam);
        });
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
        public RelativeLayout clickCard;

        MyViewHolder(View itemView)
        {
            super(itemView);
            topperImage = itemView.findViewById(R.id.topperImage);
            name = itemView.findViewById(R.id.name);
            rank = itemView.findViewById(R.id.rank);
            exam = itemView.findViewById(R.id.exam);
            branch = itemView.findViewById(R.id.branch);
            clickCard = itemView.findViewById(R.id.clickCard);
        }
    }

    private void showBottomSheet(String name, String rank, String branch,
                                 String imageUrl, String college, String exam)
    {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View bottomSheetView = inflater.inflate(R.layout.story_bottom_sheet, null);
        bottomSheetDialog = new BottomSheetDialog(mContext, R.style.SheetDialog);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.setContentView(bottomSheetView);

        ImageView topperImage = bottomSheetView.findViewById(R.id.topperImage);
        TextView topperName = bottomSheetView.findViewById(R.id.topperName);
        TextView topperRank = bottomSheetView.findViewById(R.id.rank);
        TextView topperBranch = bottomSheetView.findViewById(R.id.branch);
        TextView topperCollege = bottomSheetView.findViewById(R.id.topperCollege);
        Glide.with(mContext)
                .asBitmap()
                .placeholder(R.drawable.usericon)
                .load(imageUrl)
                .into(topperImage);
        topperCollege.setText(college);
        topperName.setText(name);
        topperRank.setText(rank +" | "+exam);
        topperBranch.setText(branch);
        bottomSheetDialog.show();
    }
}
