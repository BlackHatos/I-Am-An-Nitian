package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.at.nitsxr.TopCollegeAdapter;
import me.at.nitsxr.TopCollegeGetterSetter;
import me.at.nitsxr.TopicGetterSetter;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class College extends AppCompatActivity {
    private EditText searchCollege;
    private Toolbar toolbar;
    private RecyclerView topCollegeRecyclerView;
    private List<TopCollegeGetterSetter> topCollegeList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college);
        setUpToolbarMenu();
        initVariables();
        setData();
    }

    public void initVariables()
    {
        searchCollege = findViewById(R.id.search_college);
        searchCollege.clearFocus();
        searchCollege.setOnTouchListener((v, event) -> {
            Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
            return false;
        });

        topCollegeList = new ArrayList<>();
        topCollegeRecyclerView =  findViewById(R.id.topCollegeRecyclerView);
        topCollegeRecyclerView.setHasFixedSize(true);
        topCollegeRecyclerView.setLayoutManager(new LinearLayoutManager
                (this, LinearLayoutManager.HORIZONTAL, false));
    }

    public void setData()
    {
        TopCollegeGetterSetter getterSetter;
        for(int i=0;i<8; i++)
        {
            getterSetter = new TopCollegeGetterSetter();
            getterSetter.setUrl("");
            getterSetter.setCollege_name("NIT Srinagar");
            topCollegeList.add(getterSetter);
        }

        TopCollegeAdapter topCollegeAdapter = new TopCollegeAdapter(College.this, topCollegeList);
        topCollegeRecyclerView.setAdapter(topCollegeAdapter);
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Back");
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
}
