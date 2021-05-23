package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import me.at.nitsxr.HeaderVolleyRequest;
import me.at.nitsxr.StoryAdapter;
import me.at.nitsxr.StoryGetterSetter;
import me.at.nitsxr.TopperAdapter;
import me.at.nitsxr.TopperGetterSetter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class SuccessStory extends AppCompatActivity
{
    private Toolbar toolbar;
    private RecyclerView recyclerView, recyclerView2;
    private StoryAdapter storyAdapter;
    private TopperAdapter topperAdapter;
    private ArrayList<TopperGetterSetter> mList;
    private ArrayList<StoryGetterSetter> mList2;
    private SwipeRefreshLayout refreshScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_story);
        refreshScreen = findViewById(R.id.refreshScreen);
        refreshScreen.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
        refreshScreen.setOnRefreshListener(
                () -> {
                    mList.clear();
                    mList2.clear();
                    getStory();
                    getToppers();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshScreen.setRefreshing(false);
                        }
                    }, 3000);
                });

        setUpToolbarMenu();
        initRecyclerView();
        getToppers();
        getStory();
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Success Story");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info:
                startActivity(new Intent(getApplicationContext(), AppInfo.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initRecyclerView()
    {
        mList = new ArrayList<>();
        recyclerView = findViewById(R.id.topper_recycler_view);
        recyclerView.setHasFixedSize(true); //recycler view don't change its width and height
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        mList2 = new ArrayList<>();
        recyclerView2 = findViewById(R.id.story_recycler_view);
        recyclerView2.setHasFixedSize(true);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void getToppers()
    {
        final String url = "https://app.iamannitian.com/app/get-toppers.php";

        //error
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, response -> {

                    for (int i = 0; i < response.length(); i++) {
                        TopperGetterSetter topperGetterSetter = new TopperGetterSetter();
                        try {
                            JSONObject object = response.getJSONObject(i);
                            topperGetterSetter.setImage_link(object.getString("image_link"));
                            topperGetterSetter.setBranch(object.getString("branch"));
                            topperGetterSetter.setId(object.getString("id"));
                            topperGetterSetter.setCollege(object.getString("college"));
                            topperGetterSetter.setName(object.getString("name"));
                            topperGetterSetter.setExam(object.getString("exam"));
                            topperGetterSetter.setRank(object.getString("rank"));

                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                        mList.add(topperGetterSetter);
                    }

                    topperAdapter = new TopperAdapter(SuccessStory.this, mList);
                    recyclerView.setAdapter(topperAdapter);
                }, error -> error.printStackTrace());
        HeaderVolleyRequest.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    public void getStory()
    {
        final String url = "https://app.iamannitian.com/app/get-story.php";

        //error
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONArray>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(JSONArray response) {

                for (int i = 0; i < response.length(); i++) {
                    StoryGetterSetter storyGetterSetter = new StoryGetterSetter();
                    try {
                        JSONObject object = response.getJSONObject(i);
                        storyGetterSetter.setName(object.getString("name"));
                        storyGetterSetter.setId(object.getString("id"));
                        storyGetterSetter.setBranch(object.getString("branch"));
                        storyGetterSetter.setCollege(object.getString("college"));
                        storyGetterSetter.setImageUrl(object.getString("image_link"));
                        storyGetterSetter.setExam(object.getString("exam"));
                        storyGetterSetter.setRank(object.getString("rank"));
                        storyGetterSetter.setStory(object.getString("story"));

                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    mList2.add(storyGetterSetter);
                }

                storyAdapter = new StoryAdapter(SuccessStory.this, mList2);
                recyclerView2.setAdapter(storyAdapter);
            }

        }, error -> error.printStackTrace());

        HeaderVolleyRequest.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

}
