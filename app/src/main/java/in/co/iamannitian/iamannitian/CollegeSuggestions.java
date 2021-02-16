package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import me.at.nitsxr.CollegeAdapter;
import me.at.nitsxr.CollegeItem;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static in.co.iamannitian.iamannitian.CompleteProfile.COLLEGE_NAME;

public class CollegeSuggestions extends AppCompatActivity
{
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private List<CollegeItem> collegeItemList;
    private AppCompatAutoCompleteTextView collegeAutoComplete;
    private CollegeAdapter collegeAdapter;
    private  String college_name  = "";
    private TextView custom;
    private LinearLayout other;
    private  View viewx;
    public static final String NAME_COLLEGE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_suggestions);
        fillCollege();
        custom = findViewById(R.id.custom);
        other = findViewById(R.id.other);
        viewx = findViewById(R.id.viewx);
        collegeAutoComplete = findViewById(R.id.collegeAutoComplete);
        // prevent showing keyboard by default
        collegeAutoComplete.clearFocus();
        sharedPreferences = getSharedPreferences("appData",MODE_PRIVATE);
        collegeAdapter = new CollegeAdapter(this,collegeItemList);
        collegeAutoComplete.setAdapter(collegeAdapter);

        //=========> get the college name from CompleteProfile activity
        Intent intent = getIntent();
        college_name = intent.getStringExtra(COLLEGE_NAME);
        collegeAutoComplete.setText(college_name);
        //========> move the cursor at the end of the autocomplete text view
        collegeAutoComplete.setSelection(collegeAutoComplete.getText().toString().length());

        //========> add onclick listener with the drawable end
        collegeAutoComplete.setOnTouchListener((v, event) -> {
            final int DRAWABLE_RIGHT = 2; //index of the right drawable

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (collegeAutoComplete.getRight() -
                        collegeAutoComplete.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    collegeAutoComplete.setText("");
                    viewx.setVisibility(View.GONE);
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
                    viewx.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                }
                else
                {
                    viewx.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(collegeAutoComplete.isPopupShowing())
                {
                    viewx.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                }
                else
                {
                    viewx.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
               custom.setText(s.toString());
                if(collegeAutoComplete.isPopupShowing())
                {
                    viewx.setVisibility(View.GONE);
                    other.setVisibility(View.GONE);
                }
                else
                {
                    viewx.setVisibility(View.VISIBLE);
                    other.setVisibility(View.VISIBLE);
                }
            }
        });

           other.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("userCollege",custom.getText().toString().trim());
            editor.apply();
            Intent intent_x = new Intent(getApplicationContext(), CompleteProfile.class);
            intent_x.putExtra(NAME_COLLEGE,custom.getText().toString().trim());
            startActivity(intent_x);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        setUpToolbarMenu();
    }

        /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.getNavigationIcon().setColorFilter(getResources()
                    .getColor(R.color.textColor1), PorterDuff.Mode.SRC_ATOP);
    }

    private void fillCollege()
    {
        collegeItemList = new ArrayList<>();
        collegeItemList.add(new CollegeItem("National Institute Of Technology Srinagar", R.drawable.srinagar));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Trichy", R.drawable.trichy));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Rourkela", R.drawable.rourkela));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Sikkim", R.drawable.sikkim));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Silchar", R.drawable.silchar));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Nagpur", R.drawable.nagpur));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Nagaland", R.drawable.nagaland));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Agartala", R.drawable.agartala));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Andhra Pradesh", R.drawable.andhra));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Jamshedpur", R.drawable.jamshedpur));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Jalandhar", R.drawable.jalandhar));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Surat", R.drawable.surat));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Suratkal", R.drawable.suratkal));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Allahabad", R.drawable.allahabad));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Calicut", R.drawable.calicut));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Durgapur", R.drawable.durgapur));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Warangal", R.drawable.warangal));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Puducherry", R.drawable.pudu));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Meghalaya", R.drawable.meghalay));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Manipur", R.drawable.manipur));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Mizoram", R.drawable.mizoram));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Bhopal", R.drawable.bhopal));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Kurukshetra", R.drawable.kurukshetra));
        collegeItemList.add(new CollegeItem("National Institute Of Technology Jaipur", R.drawable.jaipur));
    }

    //handing hardware back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    startActivity(new Intent(getApplicationContext(), CompleteProfile.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
