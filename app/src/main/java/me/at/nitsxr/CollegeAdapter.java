package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import in.co.iamannitian.iamannitian.CollegeSuggestions;
import in.co.iamannitian.iamannitian.CompleteProfile;
import in.co.iamannitian.iamannitian.R;

import static android.content.Context.MODE_PRIVATE;

public class CollegeAdapter extends ArrayAdapter<CollegeItem>
{
    private List<CollegeItem> collegeItemList;
    private Context mContext;

    public CollegeAdapter(@NonNull Context context, @NonNull List<CollegeItem> collegeList)
    {
        super(context, 0, collegeList);
        mContext = context;
        collegeItemList = new ArrayList<>(collegeList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return collegeFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.college_auto_complete, parent, false);
        TextView collegeName  =  convertView.findViewById(R.id.college_name);
        ImageView collegeLogo = convertView.findViewById(R.id.college_logo);
        LinearLayout clickCollege = convertView.findViewById(R.id.clickCollege);

          CollegeItem collegeItem =  getItem(position);
          if(collegeItem != null)
          {
              collegeName.setText(collegeItem.getCollegeName());
              collegeLogo.setImageResource(collegeItem.getCollegeLogo());
          }

        clickCollege.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("tempData", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tempCollege",collegeItem.getCollegeName().trim());
            editor.apply();
            Intent intent = new Intent(mContext, CompleteProfile.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            // finish the current activity
            ((CollegeSuggestions)mContext).finish();
        });

        return convertView;
    }

    private Filter collegeFilter = new Filter()
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
           FilterResults results = new FilterResults();
           List<CollegeItem> suggestions = new ArrayList<>();
           if(constraint==null || constraint.length() == 0)
           {
               suggestions.addAll(collegeItemList);
           }
           else
           {
               String filterPatter = constraint.toString().toLowerCase().trim();
               for(CollegeItem item : collegeItemList)
               {
                   if(item.getCollegeName().toLowerCase().contains(filterPatter))
                   {
                       suggestions.add(item);
                   }
               }
           }
           results.values = suggestions;
           results.count = suggestions.size();
           return  results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List)results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((CollegeItem) resultValue).getCollegeName();
        }
    };
}
