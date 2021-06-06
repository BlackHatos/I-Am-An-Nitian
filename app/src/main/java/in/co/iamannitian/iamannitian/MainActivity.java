package in.co.iamannitian.iamannitian;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import me.at.nitsxr.HeadLineViewPagerAdapter;
import me.at.nitsxr.HeaderVolleyRequest;
import me.at.nitsxr.HamburgerDrawable;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
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
    private Handler handler, handler2;

    RequestQueue rq;
    // news data array list
    private List<NewsGetterSetter> sliderImg, mList2;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // setBroadCastReceiver();
        initializeVariables();

        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId())
            {
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
        });

        setUpToolbarMenu();
        setUpDrawerMenu();
        headerUpdate();
        setBroadCastReceiver();
        showBadge();
        sendRequest();
        initTopicVars();
        setTopic();

        runnableHandlers();

        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshScreen.setOnRefreshListener(
                () -> {
                    //RunnableHandlers();
                   // sendRequest();
                    new Handler().postDelayed(() ->
                            refreshScreen.setRefreshing(false),
                            3000);
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
    private void setUpDrawerMenu()
    {
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerArrowDrawable(new HamburgerDrawable(this));
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
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.profile_image);
        CircleImageView circleImageView = menuItem.
                getActionView().findViewById(R.id.profileImage);

        String pic_url = sharedPreferences.getString("userPicUrl", "");

        Glide.with(this)
                .load(pic_url)
                .placeholder(R.drawable.usericon)
                .fitCenter()
                .centerInside()
                .into(circleImageView);

        circleImageView.setOnClickListener(v -> {

                startActivity(new Intent(MainActivity.this, UserProfile.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
        });
        return true;
    }

    //===> Overflow menu item Click listener-
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.app_info:
                startActivity(new Intent(MainActivity.this, AppInfo.class));
                break;
            case R.id.report_bug:
                startActivity(new Intent(MainActivity.this, ReportBug.class));
                break;
            case R.id.contact_us:
                startActivity(new Intent(MainActivity.this, ContactUs.class));
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
        Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

        String str[] = college.split(" ");
        int len = str.length;
        String clg_str = "";
        for(int i=0; i<len-1; i++)
            clg_str += str[i] + " ";


        if(college.equals("null"))
            nit_name.setText("Your College");
        else
        {
            if(len < 4)
               nit_name.setText(college);
            else
                nit_name.setText(clg_str+
                        "\n"+str[len-1]);
        }

        if(name.equals("null"))
            user_name.setText("your Name");
        else
            user_name.setText(name);

        Glide.with(this)
                .load(pic_url)
                .placeholder(R.drawable.usericon)
                .fitCenter()
                .centerInside()
                .into(imageView);
    }

    //====>how notification Badge
    public void showBadge()
    {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        itemView.addView(notificationBadge);
    }

    private void sendRequest()
    {
        // news data for view pager
        sliderImg.clear();
        // news data for headline
        mList2.clear();

        final String url = "https://app.iamannitian.com/app/get-news.php";
        //error
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, response -> {
                    for (int i = 0; i < response.length(); i++)
                    {
                        NewsGetterSetter slideUtils = new NewsGetterSetter();
                        try {
                            JSONObject object = response.getJSONObject(i);
                            slideUtils.setImageUrl(object.getString("url"));
                            slideUtils.setNewsDescp(object.getString("descp"));
                            slideUtils.setNewsTitle(object.getString("title"));
                            slideUtils.setNewsId(object.getString("id"));
                            slideUtils.setNewsDate(object.getString("date"));
                            // reaction of current user on a particular news
                            slideUtils.setStatus(object.getString("status"));
                            // number of likes on a particular news
                            slideUtils.setCount(object.getString("count"));

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }

                        // add objects to the list
                        sliderImg.add(slideUtils);
                        mList2.add(slideUtils);
                    }
                            adapter = new ViewPagerAdapter(sliderImg.subList(0,5), MainActivity.this);

                            //sorting the list in descending order of counts (likes)
                            Collections.sort(mList2, (x, y)->Integer.parseInt(y.getCount()) - Integer.parseInt(x.getCount()));
                            adapter2 = new HeadLineViewPagerAdapter(mList2.subList(0,5),MainActivity.this);

                            viewPager.setAdapter(adapter);
                            viewPager2.setAdapter(adapter2);

                }, error -> error.printStackTrace()){
            @Override
            public Map<String, String> getParams()
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
        snackbar.setAction("Dismiss", view -> snackbar.dismiss());
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

    public void goToCollege(View view)
    {
        startActivity(new Intent(getApplicationContext(),College.class));
    }

    //===> Initializing variables
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

        // headline data array list
        mList2 = new ArrayList<>();
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
                    //sendRequest();
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
        TopicGetterSetter getterSetter;
        String topic_list[] = {"Gate 2021", "Jee Mains", "Jee Advance", "Ese 2021",
                "Engineering", "Education", "Technology", "Placements", "IIT", "NIT"};

        for(int i = 0; i< 10; i++)
        {
            getterSetter = new TopicGetterSetter();
            getterSetter.setTag(topic_list[i]);
            mList3.add(getterSetter);
        }
        topicAdapter = new TopicAdapter(MainActivity.this, mList3);
        topicRecyclerView.setAdapter(topicAdapter);
    }

    // setting up view pagers
    public void runnableHandlers()
    {
        currentPage = 0;
        currentHeadline = 0;
        try{
            handler = new Handler();
            final Runnable update = () -> {
                if (currentPage == 5)
                    currentPage = 0;
                viewPager.setCurrentItem(currentPage++, true);
            };

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(update);
                }
            }, DELAYS_MS, PERIOD_MS);
        }
        catch(Exception ex){ex.printStackTrace();}

        //===> Timer for the headlines
        try{
            final Runnable headline = () -> {
                if (currentHeadline == 5)
                    currentHeadline = 0;
                viewPager2.setCurrentItem(currentHeadline++, true);
            };

            handler2 = new Handler();
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    handler2.post(headline);
                }
            }, 1000, 4000);
        }catch(Exception ex){ ex.printStackTrace();}

    }

    @Override
    public void onResume()
    {
        super.onResume();
        bottomNavigationView.setSelectedItemId(R.id.home);
    }
}
