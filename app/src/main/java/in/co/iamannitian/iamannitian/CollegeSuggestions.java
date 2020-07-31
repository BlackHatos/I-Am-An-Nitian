package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import me.at.nitsxr.CollegeAdapter;
import me.at.nitsxr.CollegeItem;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class CollegeSuggestions extends AppCompatActivity {

    private Toolbar toolbar;
    private List<CollegeItem> collegeItemList;
    private AutoCompleteTextView collegeAutoComplete;
    private CollegeAdapter collegeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*=========>>> Setting Up dark Mode <<<==========*/
        boolean mode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        if (mode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_suggestions);
        fillCollege();
        collegeAutoComplete = findViewById(R.id.collegeautoComplete);
        collegeAdapter = new CollegeAdapter(this,collegeItemList);
        collegeAutoComplete.setAdapter(collegeAdapter);

        setUpToolbarMenu(mode);
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu(boolean mode) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true);
        if (mode)
            toolbar.getNavigationIcon().setColorFilter(getResources()
                    .getColor(R.color.textColor2), PorterDuff.Mode.SRC_ATOP);
        else
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
}
