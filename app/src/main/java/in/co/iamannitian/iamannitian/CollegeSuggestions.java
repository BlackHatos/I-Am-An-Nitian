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
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import static in.co.iamannitian.iamannitian.CompleteProfile.COLLEGE_NAME;

public class CollegeSuggestions extends AppCompatActivity {

    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    private List<CollegeItem> collegeItemList;
    private AppCompatAutoCompleteTextView collegeAutoComplete;
    private CollegeAdapter collegeAdapter;
    public static final String NAME_COLLEGE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_college_suggestions);
        fillCollege();
        collegeAutoComplete = findViewById(R.id.collegeautoComplete);
        sharedPreferences = getSharedPreferences("appData",MODE_PRIVATE);
        collegeAdapter = new CollegeAdapter(this,collegeItemList);
        collegeAutoComplete.setAdapter(collegeAdapter);

        //=========> get the college name from CompleteProfile activity
        Intent intent = getIntent();
        String college_name = intent.getStringExtra(COLLEGE_NAME);
        collegeAutoComplete.setText(college_name);
        //========> move the cursor at the end of the autocomplete text view
        collegeAutoComplete.setSelection(collegeAutoComplete.getText().toString().length());

        //========> add onclick listener with the drawable end
        collegeAutoComplete.setOnTouchListener(new View.OnTouchListener()
                                               {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0; //index of the left drawable
                final int DRAWABLE_RIGHT = 2; //index of the right drawable
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (collegeAutoComplete.getRight() -
                            collegeAutoComplete.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        collegeAutoComplete.setText("");
                        return true;
                    }
                }
                    return false;
                }
        });


        setUpToolbarMenu();
    }

    /*=======>>>>>>> Setting up toolbar menu <<<<<<<<<=========*/
    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
       actionBar.setDisplayHomeAsUpEnabled(true);

       //on press back button
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {

               //=========> creating shared preferences
               SharedPreferences.Editor editor = sharedPreferences.edit();
               editor.putString("userCollege",collegeAutoComplete.getText().toString().trim());
               editor.apply();

               Intent intent = new Intent(getApplicationContext(),CompleteProfile.class);
               intent.putExtra(NAME_COLLEGE,collegeAutoComplete.getText().toString().trim());
               startActivity(intent);
               overridePendingTransition(0, 0);
           }
       });

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
