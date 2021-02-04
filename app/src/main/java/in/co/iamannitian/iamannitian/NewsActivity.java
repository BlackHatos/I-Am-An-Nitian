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
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private View notificationBadge;
    private SwipeRefreshLayout refreshScreen;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadcastReceiver;
    private Snackbar snackbar;
    //===>  views
    private RecyclerView  newsRecyclerView;
    private NewsAdapter newsAdapter;
    private ArrayList<NewsGetterSetter> mList;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        FacebookSdk.sdkInitialize(getApplicationContext());

        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        refreshScreen = findViewById(R.id.refreshScreen);
        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        //===> broadcast receiver
        //setBroadCastReceiver();

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
        initRecyclerView();
        showBadge();
        sendRequest();
        setBroadCastReceiver();

        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshScreen.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener()
                {
                    @Override
                    public void onRefresh()
                    {
                        // clear the current list before calling it again
                        mList.clear();
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

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        actionBar.setIcon(R.drawable.app_logo);
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

    /*=======>>>>>>> Show notification Badge <<<<<<<<<=========*/
    public void showBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(2);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        itemView.addView(notificationBadge);
    }


    public void showSnackBar() {
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
//------------------------

    private void initRecyclerView()
    {
        mList = new ArrayList<>();
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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

    public void sendRequest()
    {
        final String url = "https://app.thenextsem.com/app/get_news.php";

        StringRequest sr = new StringRequest(1, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try
                        {
                            JSONArray jsonArray = new JSONArray(response);

                            for(int i=0; i<jsonArray.length(); i++)
                            {
                                NewsGetterSetter newsGetterSetter = new NewsGetterSetter();
                                try {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    newsGetterSetter.setImageUrl
                                            ("https://app.thenextsem.com/news_images/" + object.getString("image1"));
                                    newsGetterSetter.setNewsDescp(object.getString("descp"));
                                    newsGetterSetter.setNewsTitle(object.getString("title"));
                                    newsGetterSetter.setNewsId(object.getString("id"));
                                    newsGetterSetter.setNewsDate(object.getString("date"));
                                    newsGetterSetter.setImageUrl2(object.getString("image2"));
                                    newsGetterSetter.setStatus(object.getString("status"));
                                    newsGetterSetter.setCount(object.getString("count"));

                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                                mList.add(newsGetterSetter);
                            }

                            newsAdapter = new NewsAdapter(NewsActivity.this, mList);
                            newsRecyclerView.setAdapter(newsAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
            }
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
}

