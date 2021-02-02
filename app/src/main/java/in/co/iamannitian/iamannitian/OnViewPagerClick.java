package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_COUNT;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_DATE;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_DESCP;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_ID;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_STATUS;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_NEWS_TITLE;
import static me.at.nitsxr.ViewPagerAdapter.EXTRA_URL;

public class OnViewPagerClick extends AppCompatActivity {

    private ImageView newsImage;
    private TextView newsTitle, newDescp, publish_time, reactionCount;
    private Toolbar toolbar;
    private ImageView userReaction;

      @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_view_pager_click);

        newsImage = findViewById(R.id.newImage);
        newsTitle = findViewById(R.id.newsTitle);
        newDescp = findViewById(R.id.newDescp);
        publish_time = findViewById(R.id.publish_time);
        userReaction = findViewById(R.id.userRection);
        reactionCount = findViewById(R.id.reactionCount);

           Intent intent = getIntent();

             String  imageUrl = intent.getStringExtra(EXTRA_URL);
             String  newsTitlex = intent.getStringExtra(EXTRA_NEWS_TITLE);
             String  newsDescpx = intent.getStringExtra(EXTRA_NEWS_DESCP);
             String  publishTime = intent.getStringExtra(EXTRA_NEWS_DATE);
             String newsID =  intent.getStringExtra(EXTRA_NEWS_ID);
             String newsStatus =  intent.getStringExtra(EXTRA_NEWS_STATUS);
             String newsCount =  intent.getStringExtra(EXTRA_NEWS_COUNT);

        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .centerInside()
                .into(newsImage);

        if(newsStatus.equals("1"))
        {
            userReaction.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }
        else
        {
            userReaction.setImageResource(R.drawable.ic_favorite_black_24dp);
        }

        newsTitle.setText(newsTitlex);
        newDescp.setText(newsDescpx);
        publish_time.setText(publishTime);
        reactionCount.setText(newsCount);

        setUpToolbarMenu();
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1), PorterDuff.Mode.SRC_ATOP);
            actionBar.setIcon(R.drawable.app_logo);
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.app_info:
                startActivity(new Intent(getApplicationContext(), AppInfo.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
