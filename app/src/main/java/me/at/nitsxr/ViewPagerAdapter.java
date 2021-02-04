package me.at.nitsxr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import in.co.iamannitian.iamannitian.OnViewPagerClick;
import in.co.iamannitian.iamannitian.R;

public class ViewPagerAdapter extends PagerAdapter
{


  private List<NewsGetterSetter> sliderImg;
  private ImageLoader imageLoader;

  private Context context;
  private LayoutInflater layoutInflater;

  public ViewPagerAdapter(List<NewsGetterSetter> sliderImg, Context context)
  {
    this.context = context;
    this.sliderImg = sliderImg;
  }

  @Override
  public int getCount() {
    return  sliderImg.size();
  }

  @Override
  public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

    return  (view == object);
  }

  @NonNull
  @Override
  public Object instantiateItem(@NonNull ViewGroup container, final int position)
  {
    layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View itemView = layoutInflater.inflate(R.layout.swipe_layout, container, false);

    final NewsGetterSetter utils = sliderImg.get(position);

    final ImageView imageView = itemView.findViewById(R.id.imageView);


    // ===> This piece of code explains how to load image using volley

     imageLoader = HeaderVolleyRequest.getInstance(context).getImageLoader();
     imageLoader.get(utils.getImageUrl(), ImageLoader.getImageListener
     (imageView,R.color.cardColor,android.R.drawable.ic_dialog_alert));

    final TextView textView = itemView.findViewById(R.id.image_count);
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

    ViewPager vp = (ViewPager) container;
    vp.addView(itemView, 0);
    return itemView;
  }

  @Override
  public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
    container.removeView((View) object);
  }
}
