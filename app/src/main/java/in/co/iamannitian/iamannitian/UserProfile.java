package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;

public class UserProfile extends AppCompatActivity
{
   private Toolbar toolbar;
   private ImageView profileImage;
   private TextView userName, userEmail, userPhone,
           userState, userCollege, userBranch, userDegree;
   private SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        userName = findViewById(R.id.user_name);
        userEmail = findViewById(R.id.user_email);
        userPhone = findViewById(R.id.phone_number);
        userState = findViewById(R.id.state_name);
        userCollege = findViewById(R.id.college_name);
        userBranch = findViewById(R.id.branch_name);
        userDegree = findViewById(R.id.degree_name);
        profileImage = findViewById(R.id.profile_image);

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        String name = sharedPreferences.getString("userName", "");
        String email = sharedPreferences.getString("userEmail", "");
        String phone = sharedPreferences.getString("userPhone", "");
        String imageUrl = sharedPreferences.getString("userPicUrl", "");
        String state = sharedPreferences.getString("userState", "");
        String college = sharedPreferences.getString("userCollege", "");
        String branch = sharedPreferences.getString("userBranch", "");
        String degree = sharedPreferences.getString("userDegree", "");
        String start = sharedPreferences.getString("userStartYear", "");
        String end = sharedPreferences.getString("userEndYear", "");

        String collegeArray [] = college.split(" ");

        String college1  = "";
        for(int i = 0; i<collegeArray.length-1; i++)
            college1 +=  collegeArray[i] +" ";

        userName.setText(name);
        userEmail.setText(email);

        // Phone
        if(phone.equals("null"))
        {
            userPhone.setText("+91-XXXXXXXXXX");
        }
        else
        {
            userPhone.setText(phone);
        }

        // State
        if(state.equals("null"))
        {
            userState.setText("Your State");
        }
        else
        {
            userState.setText(state);
        }

        // College
        if(college.equals("null"))
        {
           userCollege.setText("Your College");
        }
        else
        {
            if(collegeArray.length <= 4)
                userCollege.setText(college);
            else
              userCollege.setText(college1+"\n"+collegeArray[collegeArray.length-1]);
        }

        // Degree
        if(degree.equals("null") && start.equals("null") && end.equals("null"))
        {
            userDegree.setText("Your Degree"+ " (20XX-20XX)");
        }
        else if(!degree.equals("null") && start.equals("null") && end.equals("null"))
        {
            userDegree.setText(degree+" (20XX-20XX)");
        }
        else if(degree.equals("null") && !start.equals("null") && !end.equals("null"))
        {
            userDegree.setText("Your Degree ("+ start +"-"+ end +")");
        }
        else
        {
            userDegree.setText(degree+" ("+ start +"-"+ end +")");
        }

        // Branch
        if(branch.equals("null"))
        {
            userBranch.setText("Your Branch");
        }
        else
        {
            userBranch.setText(branch);
        }


        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.usericon)
                .fitCenter()
                .centerInside()
                .into(profileImage);

        setUpToolbarMenu();
    }

    public void editProfile(View view)
    {
        startActivity(new Intent(UserProfile.this, EditProfile.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("My Profile");
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
            case android.R.id.home:
                Intent intent = new Intent(UserProfile.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Handing hardware back button
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    startActivity(new Intent(UserProfile.this, MainActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
