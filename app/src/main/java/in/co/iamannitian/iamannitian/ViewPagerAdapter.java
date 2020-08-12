package in.co.iamannitian.iamannitian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.toolbox.ImageLoader;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends PagerAdapter {

  public static final String EXTRA_URL = "imageUrl";
  public static final String EXTRA_NEWS_TITLE = "newsTitle";

  private List<SlideUtils> sliderImg;
  private ImageLoader imageLoader;

  private Context context;
  private LayoutInflater layoutInflater;

  public ViewPagerAdapter(List<SlideUtils> sliderImg,Context context)
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
  public Object instantiateItem(@NonNull ViewGroup container, final int position) {
    layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View itemView = layoutInflater.inflate(R.layout.swipe_layout, container, false);

    SlideUtils utils = sliderImg.get(position);

    final ImageView imageView = itemView.findViewById(R.id.imageView);

   /*
    * ===> This piece of code explains how to load image using volley
    *
    * imageLoader = HeaderVolleyRequest.getInstance(context).getImageLoader();
    * imageLoader.get(utils.getSlideImageUrl(), ImageLoader.getImageListener
    * (imageView,R.color.viewPagerDefaultColor,android.R.drawable.ic_dialog_alert));
    *
    * */

    Glide.with(context)
            .asBitmap()
            .load(utils.getSlideImageUrl())
            .skipMemoryCache(true)
            .into(imageView);

    final TextView textView = itemView.findViewById(R.id.image_count);
    textView.setText(sliderImg.get(position).descp);

    itemView.setOnClickListener(new View.OnClickListener(){
        @Override
        public  void onClick(View view)
        {
          Intent intent =  new Intent(context, OnViewPagerClick.class);
          SlideUtils clickedItem =  sliderImg.get(position);
          intent.putExtra(EXTRA_URL, clickedItem.getSlideImageUrl());
          intent.putExtra(EXTRA_NEWS_TITLE, clickedItem.getDescp());
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
