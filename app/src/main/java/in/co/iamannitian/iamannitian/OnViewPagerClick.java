package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import me.at.nitsxr.NewsGetterSetter;
import me.at.nitsxr.UserReaction;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class OnViewPagerClick extends AppCompatActivity
{
    private ImageView newsImage;
    private TextView newsTitle, newDescp, publish_time, reaction_count;
    private Toolbar toolbar;
    private  ImageView reaction_heart;

      @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_view_pager_click);

        newsImage = findViewById(R.id.newImage);
        newsTitle = findViewById(R.id.newsTitle);
        newDescp = findViewById(R.id.newDescp);
        publish_time = findViewById(R.id.publish_time);
        reaction_count = findViewById(R.id.reaction_count);
        reaction_heart = findViewById(R.id.reaction_heart);

            Intent intent = getIntent();
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

        if(getterSetter.getStatus().equals("1"))
            reaction_heart.setImageResource(R.drawable.ic_favorite_black_24dp);
        else
            reaction_heart.setImageResource(R.drawable.ic_favorite_border_black_24dp);

        reaction_heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int updated_status = 0;
                int updated_count = 0;

                if(getterSetter.getStatus().equals("1"))
                {
                    updated_status = 0;
                    updated_count =  Integer.parseInt(getterSetter.getCount()) - 1;
                    reaction_count.setText(updated_count+"");
                    reaction_heart.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
                else
                {
                    updated_status = 1;
                    updated_count  = Integer.parseInt(getterSetter.getCount()) + 1;
                    reaction_count.setText(updated_count+"");
                    reaction_heart.setImageResource(R.drawable.ic_favorite_black_24dp);
                }

                getterSetter.setStatus(updated_status+"");
                getterSetter.setCount(updated_count+"");

                UserReaction userReaction = new UserReaction(getterSetter,
                        getSharedPreferences("appData", MODE_PRIVATE)
                                .getString("userId",""), updated_status);
                userReaction.execute();
            }
        });

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
