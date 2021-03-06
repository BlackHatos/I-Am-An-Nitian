package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
        userPhone.setText(phone);
        userState.setText(state);
        userBranch.setText(branch);
        userCollege.setText(college1+"\n"+collegeArray[collegeArray.length-1]);
        userDegree.setText(degree+" ("+start+"-"+end+")");

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
        }
        return super.onOptionsItemSelected(item);
    }

}
