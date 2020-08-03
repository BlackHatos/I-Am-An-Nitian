package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import static in.co.iamannitian.iamannitian.CollegeSuggestions.NAME_COLLEGE;

public class CompleteProfile extends AppCompatActivity
{
    private AutoCompleteTextView state_auto_complete,user_degree;
    private EditText  start_year, end_year, user_phone,user_branch,college_auto_complete;
    public static final String COLLEGE_NAME = "";
    private SharedPreferences sharedPreferences;

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
        setContentView(R.layout.activity_complete_profile);

        state_auto_complete = findViewById(R.id.state_auto_complete);
        college_auto_complete = findViewById(R.id.college_auto_complete);
        user_phone = findViewById(R.id.user_phone);
        user_degree = findViewById(R.id.user_degree);
        user_branch = findViewById(R.id.user_branch);
        start_year = findViewById(R.id.start_year);
        end_year = findViewById(R.id.end_year);
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        String state_array[] = getResources().getStringArray(R.array.states);
        String degree_array[] = getResources().getStringArray(R.array.degree);

        ArrayAdapter<String> state_adapter = new ArrayAdapter<>(this, R.layout.custom_drop_down_list,
                R.id.custom_drop_down_text_view, state_array);
        state_auto_complete.setAdapter(state_adapter);

        ArrayAdapter<String> degree_adapter = new ArrayAdapter<>(this, R.layout.custom_drop_down_list,
                R.id.custom_drop_down_text_view, degree_array);
        user_degree.setAdapter(degree_adapter);

        //=========> get the college name reverse back from CollegeSuggestion activity
         Intent intent = getIntent();
         String name_college = intent.getStringExtra(NAME_COLLEGE);
         college_auto_complete.setText(name_college);

         //set the the values in the complete profile edit text
         user_phone.setText(sharedPreferences.getString("userPhone",""));
         user_degree.setText(sharedPreferences.getString("userDegree",""));
         user_branch.setText(sharedPreferences.getString("userBranch",""));
         start_year.setText(sharedPreferences.getString("userStartYear",""));
         end_year.setText(sharedPreferences.getString("userEndYear",""));
         state_auto_complete.setText(sharedPreferences.getString("userState",""));
         college_auto_complete.setText(sharedPreferences.getString("userCollege",""));

         //=========> on click the college edit text go to CollegeSuggestion activity
         college_auto_complete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                //=========> creating shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userPhone",user_phone.getText().toString().trim());
                editor.putString("userState", state_auto_complete.getText().toString().trim());
                editor.putString("userCollege",college_auto_complete.getText().toString().trim());
                editor.putString("userDegree",user_degree.getText().toString().trim());
                editor.putString("userBranch",user_branch.getText().toString().trim());
                editor.putString("userStartYear",start_year.getText().toString().trim());
                editor.putString("userEndYear",end_year.getText().toString().trim());
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), CollegeSuggestions.class);
                intent.putExtra(COLLEGE_NAME,college_auto_complete.getText().toString().trim());
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
        });

    }

    public void goToMain(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK); //finish all previous activities
        startActivity(intent);
        finish();
    }

}


