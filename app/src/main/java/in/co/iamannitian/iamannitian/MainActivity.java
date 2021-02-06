package in.co.iamannitian.iamannitian;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import me.at.nitsxr.HeadLineViewPagerAdapter;
import me.at.nitsxr.HeaderVolleyRequest;
import me.at.nitsxr.HumbergerDrawable;
import me.at.nitsxr.NewsDataBase;
import me.at.nitsxr.NewsGetterSetter;
import me.at.nitsxr.TopicAdapter;
import me.at.nitsxr.TopicGetterSetter;
import me.at.nitsxr.ViewPagerAdapter;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private View notificationBadge;
    private ViewPager viewPager, viewPager2;
    private ViewPagerAdapter adapter;
    private HeadLineViewPagerAdapter adapter2;
    private TabLayout tabLayout;
    private SwipeRefreshLayout refreshScreen;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadcastReceiver;
    private Snackbar snackbar;

    private TopicAdapter topicAdapter;
    private ArrayList<TopicGetterSetter> mList3;
    private RecyclerView topicRecyclerView;

    int currentPage = 0;
    int currentHeadline = 0;
    final long DELAYS_MS = 500;
    final long PERIOD_MS = 3000;

    RequestQueue rq;
    private List<NewsGetterSetter> sliderImg;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //===> Registering facebook sdk
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

       //===> initializing youtube related widgets
        initializeVariables();

        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            @Override
            public void run() {
                if (currentPage == 5)
                    currentPage = 0;
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAYS_MS, PERIOD_MS);

        //===> Timer for the headlines
        final Runnable headline = new Runnable() {
            @Override
            public void run() {
                if (currentHeadline == 5)
                    currentHeadline = 0;
                viewPager2.setCurrentItem(currentHeadline++, true);
            }
        };

        final Handler handler2 = new Handler();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler2.post(headline);
            }
        }, 1000, 4000);

        bottomNavigationView.setSelectedItemId(R.id.home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.home:
                        //do nothing
                        break;
                    case R.id.notification:
                        startActivity(new Intent(getApplicationContext(), NotificationActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.news_icon:
                        startActivity(new Intent(getApplicationContext(), NewsActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                }

                return true;
            }
        });

        setUpToolbarMenu();
        setUpDrawerMenu();
        headerUpdate();
        showBadge();
        setBroadCastReceiver();
        initTopicVars();
        setTopic();

        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshScreen.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                //clear the list first before calling the function again
                sliderImg.clear();
                sendRequest();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                            refreshScreen.setRefreshing(false);
                    }
                }, 3000);
            }
        });
    }

    //====> setting up toolbar menu
     private void setUpToolbarMenu()
     {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        actionBar.setIcon(R.drawable.app_logo);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    //=====> Setting up navigation drawer
    private void setUpDrawerMenu() {
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerArrowDrawable(new HumbergerDrawable(this));
        drawerToggle.syncState();
    }

    private void closeDrawer()
    {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    //====> Navigation Item Click Listener
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        closeDrawer();
        switch (menuItem.getItemId())
        {
            case R.id.logout:
                logout();
                break;
            case R.id.settings:
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                break;
            case R.id.saved:
                startActivity(new Intent(getApplicationContext(), SavedItem.class));
                break;
        }
        return true;
    }

    //===> Setting up overflow menu (when toolbar used as action bar)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.profile_image);
        CircleImageView circleImageView = menuItem.
                getActionView().findViewById(R.id.profileImage);

        String pic_url = sharedPreferences.getString("userPicUrl", "");

        Glide.with(this)
                .load(pic_url)
                .placeholder(R.drawable.ic_profilepic)
                .fitCenter()
                .centerInside()
                .into(circleImageView);


        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "clicked", Toast.LENGTH_SHORT).show();
            }
        });

        //setup search in toolbar
      /*  MenuItem item = menu.findItem(R.id.search);
         SearchView searchView = (SearchView) item.getActionView();
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/
        return true;
    }

    //===> Overflow menu item Click listener
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info:
                startActivity(new Intent(MainActivity.this, AppInfo.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //===> Logout function
    void logout()
    {
        //===> logout from facebook sign in
        Profile  profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null)  //===> if user logged in
        {
            LoginManager.getInstance().logOut();
        }

        //===> logout from google sign in
        GoogleSignIn.getClient( this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()).signOut();

        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginOrSignupActivity.class);
        //===> finish all previous activities
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    //====> Updating the header in the navigation view
    public void headerUpdate()
    {
        TextView user_name, nit_name;
        ImageView imageView;
        String name = sharedPreferences.getString("userName", "");
        String college = sharedPreferences.getString("userCollege", "");
        String pic_url = sharedPreferences.getString("userPicUrl", "");

        View headView = navigationView.getHeaderView(0);
        user_name = headView.findViewById(R.id.user_name);
        nit_name = headView.findViewById(R.id.nit_name);
        imageView = headView.findViewById(R.id.profile_pic);

        if(college.isEmpty())
           nit_name.setText("National Institute Of Technology Srinagar");
        else
            nit_name.setText(college);

        if(name.isEmpty())
            user_name.setText("Shubham Maurya");
        else
            user_name.setText(name);

        Glide.with(this)
                .load(pic_url)
                .placeholder(R.drawable.ic_profilepic)
                .fitCenter()
                .centerInside()
                .into(imageView);
    }

    //====>how notification Badge
    public void showBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        itemView.addView(notificationBadge);
    }

    public void sendRequest()
    {
        final String url = "https://app.thenextsem.com/app/get_news.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < 5; i++)
                {
                    NewsGetterSetter slideUtils = new NewsGetterSetter();
                    try {
                        JSONObject object = response.getJSONObject(i);
                        slideUtils.setImageUrl
                                ("https://app.thenextsem.com/news_images/" + object.getString("image1"));
                        slideUtils.setNewsDescp(object.getString("descp"));
                        slideUtils.setNewsTitle(object.getString("title"));
                        slideUtils.setNewsId(object.getString("id"));
                        slideUtils.setNewsDate(object.getString("date"));
                        slideUtils.setImageUrl2(object.getString("image2"));
                        slideUtils.setStatus(object.getString("status"));
                        slideUtils.setCount(object.getString("count"));

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    sliderImg.add(slideUtils);
                }

                adapter = new ViewPagerAdapter(sliderImg, MainActivity.this);
                adapter2 = new HeadLineViewPagerAdapter(sliderImg,MainActivity.this);
                viewPager.setAdapter(adapter);
                viewPager2.setAdapter(adapter2);
            }

        }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                Map<String, String> map =  new HashMap<>();
                map.put("idKey", sharedPreferences.getString("userId",""));
                return map;
            }
        };
        HeaderVolleyRequest.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public void showSnackBar()
    {
         snackbar = Snackbar.make(findViewById(R.id.drawerLayout),
                Html.fromHtml("<font color=#ffffff>No Internet connection</font>"),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));

            snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }


    public void goToNews(View view)
    {
        startActivity(new Intent(getApplicationContext(), NewsActivity.class));
    }

    public void goToStory(View view)
    {
        startActivity(new Intent(getApplicationContext(), SuccessStory.class));
    }

    //===> initializing variables
    private void initializeVariables()
    {
        rq = Volley.newRequestQueue(this);
        sliderImg = new ArrayList<>();
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        viewPager = findViewById(R.id.viewPager);
        viewPager2 = findViewById(R.id.viewPager2);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager2, true);
        refreshScreen = findViewById(R.id.refreshScreen);
    }


    //===> setting up broadcast receiver
    private void setBroadCastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                //======> check internet connection
                ConnectivityManager connectivityManager  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetworkInfo=  connectivityManager.getActiveNetworkInfo();
                if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
                {
                    sendRequest();
                    if(snackbar != null)
                        snackbar.dismiss();
                }
                else
                    showSnackBar();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    //===> unregister broad cast receiver
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }


    private void initTopicVars()
    {
        mList3 = new ArrayList<>();
        topicRecyclerView = findViewById(R.id.topicRecyclerView);
        topicRecyclerView.setHasFixedSize(true);
        topicRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setTopic()
    {
        final String url = "https://app.thenextsem.com/app/get_toppers.php";
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONArray response) {
                for (int i = 0; i < response.length(); i++) {
                    TopicGetterSetter topicGetterSetter = new TopicGetterSetter();
                    try {
                        JSONObject object = response.getJSONObject(i);
                        topicGetterSetter.setTag(object.getString("name"));

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    mList3.add(topicGetterSetter);
                }
                topicAdapter = new TopicAdapter(MainActivity.this, mList3);
                topicRecyclerView.setAdapter(topicAdapter);
            }

        }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        HeaderVolleyRequest.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }
}
