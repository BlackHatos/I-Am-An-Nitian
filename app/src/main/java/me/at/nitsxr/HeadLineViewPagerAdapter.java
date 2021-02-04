package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;

public class HeadLineViewPagerAdapter extends PagerAdapter {

    private List<NewsGetterSetter> sliderImg;

    private Context context;
    private LayoutInflater layoutInflater;

    public HeadLineViewPagerAdapter(List<NewsGetterSetter> sliderImg, Context context)
    {
        this.context = context;
        this.sliderImg = sliderImg;
    }

    @Override
    public int getCount() {
        return sliderImg.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return  (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.head_lines, container, false);

        final NewsGetterSetter utils = sliderImg.get(position);

        TextView textView = itemView.findViewById(R.id.headlines);
        textView.setText(utils.getNewsTitle());

        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view)
            {
                Intent intent =  new Intent(context, OnViewPagerClick.class);
                Bundle b = new Bundle();
                b.putSerializable("sampleObject", utils);
                intent.putExtras(b);
                context.startActivity(intent);
            }
        });

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
