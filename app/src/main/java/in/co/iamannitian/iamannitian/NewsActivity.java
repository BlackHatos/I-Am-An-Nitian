package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import me.at.nitsxr.NewsAdapter;
import me.at.nitsxr.NewsGetterSetter;
import me.at.nitsxr.TagAdapter;
import me.at.nitsxr.TagGetterSetter;

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

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;
    private View notificationBadge;
    private SwitchCompat switchCompat;
    private SwipeRefreshLayout refreshScreen;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadcastReceiver;
    private Snackbar snackbar;

    //recycler views
    private RecyclerView recyclerView, newsRecyclerView;
    private TagAdapter tagAdapter;
    private NewsAdapter newsAdapter;
    private ArrayList<TagGetterSetter> mList;
    private ArrayList<NewsGetterSetter> mList2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*=========>>> Setting Up dark Mode <<<==========*/
        boolean mode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        if (mode)
        {
            setTheme(R.style.DarkTheme);
        }
        else
            {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_news);

        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        refreshScreen = findViewById(R.id.refreshLayout);

        switchCompat = (SwitchCompat) navigationView
                .getMenu()
                .findItem(R.id.dark_mode_switch)
                .getActionView();

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            switchCompat.setChecked(true);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    restartApp();
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    restartApp();
                }
            }
        });

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
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), ChatActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        overridePendingTransition(0, 0);
                        break;
                    case R.id.news_icon:
                       //do nothing
                        break;
                }

                return true;
            }
        });

        setUpToolbarMenu(mode);
        setUpDrawerMenu(mode);
        headerUpdate();
        showBadge();

        //setting up tag recycler view
        initRecyclerView();
        setResourcesForTag();
        setUpToolbarMenu(mode);

        //broadcast receiver to check Internet connectivity
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //======> check internet connection
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    if (snackbar != null)
                        snackbar.dismiss();
                } else {
                    showSnackBar();
                }
            }
        };

        registerReceiver(broadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));

        refreshScreen.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
    private void setUpToolbarMenu(boolean mode) {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        if (mode)
        {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor2));
            actionBar.setIcon(R.drawable.app_logo_dark);
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
            actionBar.setIcon(R.drawable.app_logo);
        }

    }

    /*=======>>>>>>> Setting up navigation drawer <<<<<<<<<=========*/
    private void setUpDrawerMenu(boolean mode) {
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle =
                new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.setDrawerArrowDrawable(new HumbergerDrawable(this, mode));
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /*=======>>>>>>> Overflow menu item Click listener <<<<<<<<<=========*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //=======> this line solves the problem of not opening drawer on clicking drawer icon
        drawerLayout.openDrawer(Gravity.LEFT);

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
        BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(4);
        notificationBadge = LayoutInflater.from(this).inflate(R.layout.notification_badge, menuView, false);
        itemView.addView(notificationBadge);
    }

    /*=======>>>>>>> restart app on clicking the switch <<<<<<<<<=========*/
    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), NewsActivity.class);
        startActivity(i);
        finish();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void initRecyclerView() {
        mList = new ArrayList<>();
        recyclerView = findViewById(R.id.tag_recycler_view);
        recyclerView.setHasFixedSize(true); //recycler view don't change its width and height
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tagAdapter = new TagAdapter(NewsActivity.this, mList);

        mList2 = new ArrayList<>();
        newsRecyclerView = findViewById(R.id.news_recycler_view);
        newsRecyclerView.setHasFixedSize(true);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        newsAdapter = new NewsAdapter(NewsActivity.this, mList2);

        recyclerView.setAdapter(tagAdapter);
        newsRecyclerView.setAdapter(newsAdapter);
    }

    public void setResourcesForTag() {
        String a = "https://app.thenextsem.com/images/Agartala.jpg";
        String b = "https://app.thenextsem.com/images/Bhopal.jpg";
        String c = "https://app.thenextsem.com/images/Calicut.jpg";
        String d = "https://app.thenextsem.com/images/Raipur.jpg";
        String e = "https://app.thenextsem.com/images/Nagpur.jpg";
        String f = "https://app.thenextsem.com/images/Jamshedpur.jpg";
        String g = "https://app.thenextsem.com/images/toppers/rajat_soni.jpg";
        String h = "https://app.thenextsem.com/images/toppers/ankit_singh.jpg";
        String i = "https://app.thenextsem.com/images/toppers/ankur_agarwal.jpg";
        String j = "https://app.thenextsem.com/images/toppers/abhishek_kumar.jpg";

        mList.add(new TagGetterSetter(a, "IITs"));
        mList.add(new TagGetterSetter(b, "NITs"));
        mList.add(new TagGetterSetter(c, "IIITs"));
        mList.add(new TagGetterSetter(d, "GATE"));
        mList.add(new TagGetterSetter(e, "JEE"));
        mList.add(new TagGetterSetter(f, "IES"));
        mList.add(new TagGetterSetter(g, "NEET"));
        mList.add(new TagGetterSetter(h, "DRDO"));
        mList.add(new TagGetterSetter(i, "GRE"));
        mList.add(new TagGetterSetter(j, "SSC"));

        mList2.add(new NewsGetterSetter(a, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(b, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(c, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(d, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(e, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(f, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(g, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(h, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(i, "Lorem Ipsum is a dummy text to show someone"));
        mList2.add(new NewsGetterSetter(j, "Lorem Ipsum is a dummy text to show someone"));
    }
}