package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.at.nitsxr.NotificationAdapter;
import me.at.nitsxr.NotificationGetterSetter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity
{
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private List<NotificationGetterSetter> mList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setUpToolbarMenu();
        setData();
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

    public void setData()
    {
        mList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        NotificationGetterSetter getterSetter;

        for(int i=0; i<10; i++)
        {
            getterSetter = new NotificationGetterSetter();
            getterSetter.setImageUrl("");
            getterSetter.setNotification("This is a test notification " +
                    "please don't mind. Lorem ipsum is a dummy text so please read it");
            getterSetter.setTime("2 hours ago");
            mList.add(getterSetter);
        }
        NotificationAdapter adapter = new NotificationAdapter(NotificationActivity.this, mList);
        recyclerView.setAdapter(adapter);
    }
}
