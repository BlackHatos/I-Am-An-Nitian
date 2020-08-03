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

import static in.co.iamannitian.iamannitian.ViewPagerAdapter.EXTRA_URL;
import static in.co.iamannitian.iamannitian.ViewPagerAdapter.EXTRA_NEWS_TITLE;

public class OnViewPagerClick extends AppCompatActivity {

    private ImageView newsImage;
    private TextView newsTitle;
    private Toolbar toolbar;

      @Override
    protected void onCreate(Bundle savedInstanceState) {

          /*=========>>> Setting Up dark Mode <<<==========*/
          boolean mode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
          if (mode) {
              setTheme(R.style.DarkTheme);
          } else {
              setTheme(R.style.AppTheme);
          }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_view_pager_click);

        newsImage = findViewById(R.id.newImage);
        newsTitle = findViewById(R.id.newsTitle);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_URL);
        String newsTitleX = intent.getStringExtra(EXTRA_NEWS_TITLE);

        Glide.with(this)
                .load(imageUrl)
                .fitCenter()
                .centerInside()
                .into(newsImage);

        newsTitle.setText(newsTitleX);
        setUpToolbarMenu(mode);
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu(boolean mode) {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (mode)
        {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor2));
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor2), PorterDuff.Mode.SRC_ATOP);
            actionBar.setIcon(R.drawable.app_logo_dark);
        }
        else
        {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1), PorterDuff.Mode.SRC_ATOP);
            actionBar.setIcon(R.drawable.app_logo);
        }

    }

    /*=========>>>>>>> Setting up overflow menu (when toolbar used as action bar) <<<<<<<<<=========*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /*=======>>>>>>> Overflow menu item Click listener <<<<<<<<<=========*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(getApplicationContext(), AboutUs.class));
                break;
            case R.id.app_info:
                startActivity(new Intent(getApplicationContext(), AppInfo.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
