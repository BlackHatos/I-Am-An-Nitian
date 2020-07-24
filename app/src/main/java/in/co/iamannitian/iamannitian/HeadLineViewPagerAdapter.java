package in.co.iamannitian.iamannitian;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class HeadLineViewPagerAdapter extends PagerAdapter {

    private int image_resources[] =
            {
                    R.drawable.hacked,
                    R.drawable.nittrichy,
                    R.drawable.nitrkl,
                    R.drawable.hostelx,
                    R.drawable.nittrichy
            };

    private List<SlideUtils> headline;

    private Context context;
    private LayoutInflater layoutInflater;

    public HeadLineViewPagerAdapter(List<SlideUtils> headline,Context context)
    {
        this.context = context;
        this.headline = headline;
    }

    @Override
    public int getCount() {
        return image_resources.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {

        return  (view == object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(R.layout.head_lines, container, false);
        TextView textView = itemView.findViewById(R.id.headlines);
        textView.setText(headline.get(position).descp);
        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
