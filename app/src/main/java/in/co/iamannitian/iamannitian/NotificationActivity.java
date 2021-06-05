package in.co.iamannitian.iamannitian;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import me.at.nitsxr.NewsGetterSetter;
import me.at.nitsxr.NotificationAdapter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<NewsGetterSetter> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setUpToolbarMenu();
        initRecyclerView();
        getNotification();
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Notifications");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.clear:
                Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void initRecyclerView()
    {
        mList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
    }

    public void getNotification()
    {
        final String url = "http://app.iamannitian.com/app/get_notification.php";
       // mList.clear();
        StringRequest sr = new StringRequest(1, url,
                response -> {
                    try
                    {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            NewsGetterSetter newsGetterSetter = new NewsGetterSetter();
                            try
                            {
                                JSONObject object = jsonArray.getJSONObject(i);

                                newsGetterSetter.setImageUrl(object.getString("url"));
                                newsGetterSetter.setNewsDescp(object.getString("descp"));
                                newsGetterSetter.setNewsTitle(object.getString("title"));
                                newsGetterSetter.setNewsId(object.getString("id"));
                                newsGetterSetter.setNewsDate(object.getString("date"));
                                // user's reaction for each article
                                newsGetterSetter.setStatus(object.getString("status"));
                                // total reactions on each article
                                newsGetterSetter.setCount(object.getString("count"));

                            }catch (JSONException ex){
                                ex.printStackTrace();
                            }

                            mList.add(newsGetterSetter);
                        }

                        NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, mList);
                        recyclerView.setAdapter(adapter);

                        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                            @Override
                            public boolean onMove(@NonNull  RecyclerView recyclerView, @NonNull  RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                                return false;
                            }

                            @Override
                            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                                adapter.deleteItem(viewHolder.getAdapterPosition());
                            }

                            @Override
                            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull
                                    RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
                                                    boolean isCurrentlyActive) {

                                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                        .addBackgroundColor(ContextCompat.getColor(NotificationActivity.this, R.color.deleteButtonColor))
                                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                                        .create()
                                        .decorate();

                                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                            }
                        }).attachToRecyclerView(recyclerView);

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

        RequestQueue rq = Volley.newRequestQueue(NotificationActivity.this);
        rq.add(sr);
    }

}
