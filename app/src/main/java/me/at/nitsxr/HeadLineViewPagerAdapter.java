package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;

import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_DATE;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_DESCP;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_ID;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_TITLE;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_URL;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_URL_2;

public class HeadLineViewPagerAdapter extends PagerAdapter {

    private List<SlideUtils> sliderImg;

    private Context context;
    private LayoutInflater layoutInflater;

    public HeadLineViewPagerAdapter(List<SlideUtils> sliderImg, Context context)
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

        final SlideUtils utils = sliderImg.get(position);

        TextView textView = itemView.findViewById(R.id.headlines);
        textView.setText(utils.getNewsTitle());

        itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public  void onClick(View view)
            {
                Intent intent =  new Intent(context, OnViewPagerClick.class);
                intent.putExtra(EXTRA_URL, utils.getImageUrl());
                intent.putExtra(EXTRA_NEWS_DESCP,utils.getNewsDescp());
                intent.putExtra(EXTRA_NEWS_TITLE, utils.getNewsTitle());
                intent.putExtra(EXTRA_NEWS_DATE, utils.getNewsDate());
                intent.putExtra(EXTRA_NEWS_ID, utils.getNewsId());
                intent.putExtra(EXTRA_URL_2, utils.getImageUrl2());
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
