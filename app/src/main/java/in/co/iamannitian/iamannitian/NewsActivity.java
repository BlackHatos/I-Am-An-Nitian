package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import me.at.nitsxr.NewsAdapter;
import me.at.nitsxr.NewsGetterSetter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.at.nitsxr.TopicAdapter.TOPIC;

public class NewsActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private View notificationBadge;
    private SwipeRefreshLayout refreshScreen;
    private TextView welcome_text, topic_text, topic_tab;
    private CardView cardView;
    private RecyclerView  newsRecyclerView;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsGetterSetter> mList;
    private String userName = "";
    private String topic;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        welcome_text = findViewById(R.id.welcome_text);
        topic_text = findViewById(R.id.topic_text);
        cardView = findViewById(R.id.card1);
        topic_tab = findViewById(R.id.topic_tab);

        // getting topic from main activity
        Intent intent = getIntent();
        topic = intent.getStringExtra(TOPIC);

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        userName = sharedPreferences
                .getString("userName", "")
                .split(" ")[0];

        refreshScreen = findViewById(R.id.refreshScreen);
        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));

        bottomNavigationView.setSelectedItemId(R.id.news_icon);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.news_icon:
                        break;
                }
                return true;
            }
        });

        setUpToolbarMenu();
        initRecyclerView();
        showBadge();
        getData();

        if(topic != null)
        {
            cardView.setVisibility(View.VISIBLE);
            topic_tab.setText(topic);
        }
        else
        {
            welcome_text.setText("Welcome, "+userName);
            topic_text.setText("Here are your today's updates");
            welcome_text.setVisibility(View.VISIBLE);
            topic_text.setVisibility(View.VISIBLE);
        }


        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshScreen.setOnRefreshListener(
                () -> {
                    getData();
                    new Handler().postDelayed(() -> refreshScreen.setRefreshing(false), 3000);
                });
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        actionBar.setIcon(R.drawable.app_logo);
        toolbar.getNavigationIcon().setColorFilter(getResources()
                        .getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    /*=========>>>>>>> Setting up overflow menu (when toolbar used as action bar) <<<<<<<<<=========*/
    @SuppressLint("WrongConstant")
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        //setting up the search view in toolbar
        MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) item.getActionView();

        //changing the size and style of the text in search view
        EditText searchEdit  = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEdit.setTextSize(17.0f);

        Typeface typeface = ResourcesCompat.getFont(getApplicationContext(),R.font.work_sans);
        searchEdit.setTypeface(typeface);

        // remove horizontal underline in search view
         View v = searchView.findViewById(androidx.appcompat.R.id.search_plate);
         v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                filter(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s)
            {
                if(s.equals(""))
                {
                    topic_text.setVisibility(View.VISIBLE);
                    welcome_text.setText("Welcome, "+userName);
                    newsAdapter.filterList(mList);
                }

                return false;
            }
        });

         // restore the news data after closing the search view
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {

                topic_text.setVisibility(View.VISIBLE);
                welcome_text.setText("Welcome, "+userName);
                newsAdapter.filterList(mList);
                return true;
            }
        });

        return true;
    }

    /*=======>>>>>>> Overflow menu item Click listener <<<<<<<<<=========*/
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

    /*=======>>>>>>> Show notification Badge <<<<<<<<<=========*/
    public void showBadge()
    {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        itemView.addView(notificationBadge);
    }

    private void initRecyclerView()
    {
        mList = new ArrayList<>();
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void getData()
    {
        mList.clear();
        final String url = "https://app.iamannitian.com/app/get-news.php";
        //error
        StringRequest sr = new StringRequest(1, url,
                response -> {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            NewsGetterSetter newsGetterSetter = new NewsGetterSetter();
                            try {
                                JSONObject object = jsonArray.getJSONObject(i);
                                    newsGetterSetter.setImageUrl
                                            ("https://app.iamannitian.com/news-images/" + object.getString("image1"));
                                    newsGetterSetter.setNewsDescp(object.getString("descp"));
                                    newsGetterSetter.setNewsTitle(object.getString("title"));
                                    newsGetterSetter.setNewsId(object.getString("id"));
                                    newsGetterSetter.setNewsDate(object.getString("date"));
                                    newsGetterSetter.setImageUrl2(object.getString("image2"));
                                    newsGetterSetter.setStatus(object.getString("status"));
                                    newsGetterSetter.setCount(object.getString("count"));
                            }catch (JSONException ex){
                                ex.printStackTrace();
                            }

                            if(topic != null)
                            {
                                if(newsGetterSetter.getNewsTitle().toLowerCase().contains(topic.toLowerCase()))
                                    mList.add(newsGetterSetter);
                            }
                            else
                                mList.add(newsGetterSetter);
                        }

                        newsAdapter = new NewsAdapter(NewsActivity.this, mList);
                        newsRecyclerView.setAdapter(newsAdapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            error.printStackTrace();
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                String id = sharedPreferences.getString("userId", "");
                map.put("idKey", id);
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(NewsActivity.this);
        rq.add(sr);
    }


    // filtering the news based on the keyword entered in search bar
    private void filter(String text)
    {
        List<NewsGetterSetter> filteredList = new ArrayList<>();
        boolean flag  = false;

        if(mList.size() != 0)
        {
            for (NewsGetterSetter item : mList) //here mList is filtered list
            {
                   if (item.getNewsTitle().toLowerCase().contains(text.toLowerCase())) {
                    {
                        filteredList.add(item);
                        flag = true;
                    }
                }
            }

            if(!flag)
            {
                topic_text.setVisibility(View.GONE);
                welcome_text.setText("No results found");
            }
            else
            {
                topic_text.setVisibility(View.VISIBLE);
                welcome_text.setText("Welcome, "+userName);
            }

            newsAdapter.filterList(filteredList);
        }
    }
}
