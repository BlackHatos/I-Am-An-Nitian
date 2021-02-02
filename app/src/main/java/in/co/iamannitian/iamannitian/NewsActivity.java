package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import me.at.nitsxr.HeadLineViewPagerAdapter;
import me.at.nitsxr.HeaderVolleyRequest;
import me.at.nitsxr.HumbergerDrawable;
import me.at.nitsxr.NewsAdapter;
import me.at.nitsxr.NewsGetterSetter;
import me.at.nitsxr.SlideUtils;
import me.at.nitsxr.ViewPagerAdapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private View notificationBadge;
    private SwipeRefreshLayout refreshScreen;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadcastReceiver;
    private Snackbar snackbar;
    //===>  views
    private RecyclerView recyclerView, newsRecyclerView;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsGetterSetter> mList;
    private Map<String, String> reactionMap;
    private Map<String, String> countMap;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        FacebookSdk.sdkInitialize(getApplicationContext());

        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        refreshScreen = findViewById(R.id.refreshScreen);
        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        //===> broadcast receiver
        setBroadCastReceiver();

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
                        //
                        break;
                }

                return true;
            }
        });

        setUpToolbarMenu();
        setUpDrawerMenu();
        initRecyclerView();
        headerUpdate();
        showBadge();

        //===> broadcast receiver
        setBroadCastReceiver();
        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshScreen.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener()
                {
                    @Override
                    public void onRefresh()
                    {
                        sendRequest();
                        // new RequestYoutubeAPI().execute();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshScreen.setRefreshing(false);
                            }
                        }, 3000);
                    }
                });
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        actionBar.setIcon(R.drawable.app_logo);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    /*=======>>>>>>> Setting up navigation drawer <<<<<<<<<=========*/
    private void setUpDrawerMenu() {
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerArrowDrawable(new HumbergerDrawable(this));
        drawerToggle.syncState();
    }

    private void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    /*=======>>>>>>> Navigation Item Click Listener <<<<<<<<<========*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        closeDrawer();
        switch (menuItem.getItemId()) {
            case R.id.logout:
                logout();
            case R.id.settings:
                break;
        }
        return true;
    }

    /*=========>>>>>>> Setting up overflow menu (when toolbar used as action bar) <<<<<<<<<=========*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //=======> this line solves the problem of not opening drawer on clicking drawer icon
        //drawerLayout.openDrawer(Gravity.LEFT);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
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

    /*=======>>>>>>> Logout function <<<<<<<<<=========*/
    void logout()
    {
        //logout from facebook sign in
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null)  //if user logged in
        {
            LoginManager.getInstance().logOut();
        }

        //logout from google sign in
        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()).signOut();

        sharedPreferences.edit().clear().apply();
        Intent intent = new Intent(getApplicationContext(), LoginOrSignupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //finish all previous activities
        startActivity(intent);
    }

    /*=======>>>>>>> Updating the header in the navigation view <<<<<<<<<=========*/
    public void headerUpdate()
    {
        TextView user_name, nit_name;
        String name = sharedPreferences.getString("userName", "");
        String college = sharedPreferences.getString("userCollege", "");
        View headView = navigationView.getHeaderView(0);
        user_name = headView.findViewById(R.id.user_name);
        nit_name = headView.findViewById(R.id.nit_name);
        user_name.setText(name.trim());
        nit_name.setText(college);
    }

    /*=======>>>>>>> Show notification Badge <<<<<<<<<=========*/
    public void showBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        itemView.addView(notificationBadge);
    }


    public void showSnackBar() {
        snackbar = Snackbar.make(findViewById(R.id.drawerLayout),
                Html.fromHtml("<font color=#ffffff>No Internet connection. Turn on WiFi or mobile data</font>"),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(Color.YELLOW);

        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
//------------------------

    private void initRecyclerView() {
        mList = new ArrayList<>();
        recyclerView.setHasFixedSize(true); //recycler view don't change its width and height
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //initialize map
        reactionMap = new HashMap<>();
        countMap = new HashMap<>();

        newsRecyclerView = findViewById(R.id.news_recycler_view);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
       // newsAdapter = new NewsAdapter(NewsActivity.this, mList, reactionMap, countMap,"1");
        newsRecyclerView.setAdapter(newsAdapter);
    }


    //===> setting up broadcast receiver
    private void setBroadCastReceiver()
    {
        broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent) {
                //===> check internet connection
                ConnectivityManager connectivityManager  = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetworkInfo=  connectivityManager.getActiveNetworkInfo();
                if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
                {
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

    public void sendRequest() {
        final String url = "https://app.thenextsem.com/app/get_news.php";

       /*     JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                   try {
                            JSONArray newsArray = response.getJSONArray("news");
                            JSONArray reactionArray = response.getJSONArray("reaction");
                            JSONArray newsCount = response.getJSONArray("newsCount");

                            Log.d("respos==>",newsCount.toString());

                            for (int i = 0; i < newsArray.length(); i++)
                            {
                                NewsGetterSetter getterSetter = new NewsGetterSetter();
                                JSONObject object = newsArray.getJSONObject(i);
                                getterSetter.setImageUrl
                                        ("https://app.thenextsem.com/news_images/" + object.getString("image1"));
                                getterSetter.setNewsDescp(object.getString("descp"));
                                getterSetter.setNewsTitle(object.getString("title"));
                                getterSetter.setNewsDate(object.getString("date"));
                                getterSetter.setNewsId(object.getString("id"));
                                getterSetter.setImageUrl2(object.getString("image2"));

                                mList2.add(getterSetter);
                            }

                           //setting up reaction map

                            for(int i=0; i<reactionArray.length(); i++)
                            {
                                JSONObject object = reactionArray.getJSONObject(i);
                                reactionMap.put(object.getString("news_id"),object.getString("status"));
                            }

                          for(int i=0; i<newsCount.length(); i++)
                          {
                              JSONObject object = newsCount.getJSONObject(i);
                              countMap.put(object.getString("news_id"), object.getString("count"));
                          }

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }*/

                      /*  newsAdapter = new NewsAdapter(NewsActivity.this, mList2, reactionMap,countMap,
                                sharedPreferences.getString("userId",""));
                        newsRecyclerView.setAdapter(newsAdapter);

                    }
                }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        })
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("userId","45");
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(NewsActivity.this);
        rq.add(jsonObjectRequest);
    }*/
    }
}