package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import me.at.nitsxr.NewsDataBase;
import me.at.nitsxr.NewsGetterSetter;
import me.at.nitsxr.StoryGetterSetter;
import me.at.nitsxr.UserReaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class OnViewPagerClick extends AppCompatActivity
{
    private ImageView newsImage;
    private TextView newsTitle, newDescp, publish_time, reaction_count;
    private Toolbar toolbar;
    private  ImageView reaction_heart, saveNews, shareNews;
    private LinearLayout linearLayout;
    private SharedPreferences sharedPreferences;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_view_pager_click);

        linearLayout = findViewById(R.id.bottomBar);
        newsImage = findViewById(R.id.newImage);
        newsTitle = findViewById(R.id.newsTitle);
        newDescp = findViewById(R.id.newDescp);
        publish_time = findViewById(R.id.publish_time);
        reaction_count = findViewById(R.id.reaction_count);
        reaction_heart = findViewById(R.id.reaction_heart);
        saveNews = findViewById(R.id.saveNews);
        shareNews = findViewById(R.id.shareNews);
        view = findViewById(R.id.view);

        sharedPreferences = getSharedPreferences("tempData", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent intent = getIntent();
        String temp =  intent.getStringExtra("temp");

        // check activity type
        if(temp.equals("1"))
        {
            setUpToolbarMenu("News");
            linearLayout.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
            publish_time.setVisibility(View.VISIBLE);
            final NewsGetterSetter getterSetter = (NewsGetterSetter) intent.getSerializableExtra("sampleObject");
            Glide.with(this)
                    .load(getterSetter.getImageUrl())
                    .fitCenter()
                    .centerInside()
                    .into(newsImage);

            newsTitle.setText(getterSetter.getNewsTitle());
            newDescp.setText(getterSetter.getNewsDescp());
            publish_time.setText(getterSetter.getNewsDate());
            reaction_count.setText(getterSetter.getCount());

            // check user reaction
            if (getterSetter.getStatus().equals("1"))
                reaction_heart.setImageResource(R.drawable.ic_favorite_black_24dp);
            else
                reaction_heart.setImageResource(R.drawable.ic_favorite_border_black_24dp);

            // update user reaction
            reaction_heart.setOnClickListener(v -> {
                int updated_status = 0;
                int updated_count = 0;

                editor.putString("isReactionClicked", "1");
                editor.apply();

                if (getterSetter.getStatus().equals("1")) {
                    updated_status = 0;
                    updated_count = Integer.parseInt(getterSetter.getCount()) - 1;
                    reaction_count.setText(updated_count + "");
                    reaction_heart.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                } else {
                    updated_status = 1;
                    updated_count = Integer.parseInt(getterSetter.getCount()) + 1;
                    reaction_count.setText(updated_count + "");
                    reaction_heart.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                getterSetter.setStatus(updated_status + "");
                getterSetter.setCount(updated_count + "");

                // reflect changes in the database
                UserReaction userReaction = new UserReaction(getterSetter,
                        getSharedPreferences("appData", MODE_PRIVATE)
                                .getString("userId", ""), updated_status);
                userReaction.execute();
            });


            shareNews.setOnClickListener(v -> {
                Intent intent1 = new Intent();
                intent1.setAction(Intent.ACTION_SEND);
                intent1.putExtra(Intent.EXTRA_TEXT, getterSetter.getNewsTitle() + "\n" + "" +
                        "Download the app : https://iamannitian.co.in");
                intent1.setType("text/plain");
                Intent shareIntent = Intent.createChooser(intent1, "Share via");
                startActivity(shareIntent);
            });

            final NewsDataBase newsDataBase = new NewsDataBase(OnViewPagerClick.this);

            if (newsDataBase.isPresent(getterSetter.getNewsId()))
                saveNews.setImageResource(R.drawable.save_article);

            saveNews.setOnClickListener(v -> {
                editor.putString("isReactionClicked", "1");
                editor.apply();

                if (newsDataBase.isPresent(getterSetter.getNewsId())) {
                    newsDataBase.deleteRecord(getterSetter.getNewsId());
                    saveNews.setImageResource(R.drawable.save_border);
                    Toast.makeText(getApplicationContext(), "Unsaved", Toast.LENGTH_SHORT).show();
                } else {
                    saveNews.setImageResource(R.drawable.save_article);
                    newsDataBase.addOne(getterSetter);
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
        {
            setUpToolbarMenu("Story");
            // setMargin(relativeLayoutMargin, 10,0,10,30);
            final StoryGetterSetter getterSetter = (StoryGetterSetter) intent.getSerializableExtra("sampleObject");
            Glide.with(this)
                    .load(getterSetter.getImageUrl())
                    .fitCenter()
                    .centerInside()
                    .into(newsImage);

            newsTitle.setText(getterSetter.getName()+" | "+getterSetter.getExam()+" | "
                    +getterSetter.getRank()+" | "+ getterSetter.getCollege());

            newDescp.setText(getterSetter.getStory());
        }
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu(String toolBarText) {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(toolBarText);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setIcon(R.drawable.app_logo);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources()
                        .getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.app_info) {
            startActivity(new Intent(getApplicationContext(), AppInfo.class));
        } else if (sharedPreferences.getString("isFromNotification", "0")
                .equals("1")
                && (item.getItemId() == android.R.id.home))
        {
            Intent intent = new Intent(OnViewPagerClick.this, NewsActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Handing hardware back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(sharedPreferences.getString("isFromNotification", "0").equals("1")
                    && keyCode == KeyEvent.KEYCODE_BACK)
            {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
