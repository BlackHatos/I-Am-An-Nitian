package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import me.at.nitsxr.CollegeAdapter;
import me.at.nitsxr.CollegeItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;


public class CollegeSuggestions extends AppCompatActivity
{
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private List<CollegeItem> collegeItemList;
    private AppCompatAutoCompleteTextView collegeAutoComplete;
    private CollegeAdapter collegeAdapter;
    private TextView custom;
    private LinearLayout other;
    private  View view_x;
    private Intent intent2;
    private int activityType;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_suggestions);
        fillCollege();
        custom = findViewById(R.id.custom); // custom college suggestion text
        other = findViewById(R.id.other); // custom suggestion linear layout
        view_x = findViewById(R.id.viewx); // horizontal line in custom suggestion
        collegeAutoComplete = findViewById(R.id.collegeAutoComplete); // Edit text

        activityType = getIntent().getIntExtra("ACTIVITY_TYPE", 2);

        // Prevent showing keyboard by default
        collegeAutoComplete.clearFocus();
        // Get shared prefs
        sharedPreferences = getSharedPreferences("tempData",MODE_PRIVATE);

        // Move the cursor at the end of the autocomplete text view
        collegeAutoComplete.setSelection(collegeAutoComplete.getText().toString().length());

        // Add onclick listener with the drawable end
        collegeAutoComplete.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; //index of the right drawable

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (collegeAutoComplete.getRight() -
                        collegeAutoComplete.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    collegeAutoComplete.setText("");
                    view_x.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                    return true;
                }
            }
            return false;
        });

        collegeAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(collegeAutoComplete.isPopupShowing())
                {
                    view_x.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                }
                else
                {
                    view_x.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(collegeAutoComplete.isPopupShowing())
                {
                    view_x.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                }
                else
                {
                    view_x.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
               custom.setText(s.toString());
                if(collegeAutoComplete.isPopupShowing())
                {
                    view_x.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                }
                else
                {
                    view_x.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                }
            }
        });

            other.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tempCollege",custom.getText().toString().trim());
            editor.apply();

            if(activityType == 0)
                intent2 = new Intent(CollegeSuggestions.this, CompleteProfile.class);
            else if(activityType == 1)
                intent2 = new Intent(CollegeSuggestions.this, EditProfile.class);

                startActivity(intent2);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
        });

        setUpToolbarMenu();
    }

    public void fillCollege()
    {
        final String url = "https://app.iamannitian.com/app/college_suggestion.php";
        collegeItemList = new ArrayList<>();

        StringRequest sr = new StringRequest(0, url,
                response -> {
                   try
                    {
                        JSONArray jsonArray = new JSONArray(response);
                        for(int i=0; i<jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String collegeName = object.getString("name");
                            String imageUrl = object.getString("url");
                            collegeItemList.add(new CollegeItem(collegeName,imageUrl));
                        }
                    }
                    catch
                    (JSONException e)
                    {
                        e.printStackTrace();
                    }
                    collegeAdapter = new CollegeAdapter(this,collegeItemList, activityType);
                    collegeAutoComplete.setAdapter(collegeAdapter);
                    collegeAdapter.notifyDataSetChanged();

                }, error -> {
            Toast.makeText(this, "failed to save changes", Toast.LENGTH_SHORT).show();
        });

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(sr);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    if(activityType == 0)
                       startActivity(new Intent(getApplicationContext(), CompleteProfile.class));
                    else
                        startActivity(new Intent(getApplicationContext(), EditProfile.class));

                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources()
                .getColor(R.color.textColor1), PorterDuff.Mode.SRC_ATOP);
    }
}
