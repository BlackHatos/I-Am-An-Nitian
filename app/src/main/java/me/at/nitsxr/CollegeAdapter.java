package me.at.nitsxr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import in.co.iamannitian.iamannitian.R;

public class CollegeAdapter extends ArrayAdapter<CollegeItem>
{
    private List<CollegeItem> collegeItemList;

    public CollegeAdapter(@NonNull Context context, @NonNull List<CollegeItem> collegeList)
    {
        super(context, 0, collegeList);
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
          CollegeItem collegeItem =  getItem(position);
          if(collegeItem != null)
          {
              collegeName.setText(collegeItem.getCollegeName());
              collegeLogo.setImageResource(collegeItem.getCollegeLogo());
          }
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
