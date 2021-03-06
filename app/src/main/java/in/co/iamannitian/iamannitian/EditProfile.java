package in.co.iamannitian.iamannitian;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity
{

    private static final int IMAGE_REQUEST_CODE = 1;
    private Toolbar toolbar;
    private EditText name, email, phone, college, degree, state, branch, start, end;
    private ImageView profilePic, selectImage;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setUpToolbarMenu();
        initVariables();
        setData();

    }

    public void initVariables()
    {
        name = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        phone = findViewById(R.id.user_phone);
        college = findViewById(R.id.user_college);
        state = findViewById(R.id.user_state);
        degree = findViewById(R.id.user_degree);
        branch = findViewById(R.id.user_branch);
        start = findViewById(R.id.start_year);
        end = findViewById(R.id.end_year);
        profilePic = findViewById(R.id.profile_image);
        selectImage = findViewById(R.id.select_image);

        progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false);
    }

    public void setData()
    {
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        String user_name = sharedPreferences.getString("userName", "");
        String user_email = sharedPreferences.getString("userEmail", "");
        String user_phone = sharedPreferences.getString("userPhone", "");
        String imageUrl = sharedPreferences.getString("userPicUrl", "");
        String user_state = sharedPreferences.getString("userState", "");
        String user_college = sharedPreferences.getString("userCollege", "");
        String user_branch = sharedPreferences.getString("userBranch", "");
        String user_degree = sharedPreferences.getString("userDegree", "");
        String start_year = sharedPreferences.getString("userStartYear", "");
        String end_year = sharedPreferences.getString("userEndYear", "");

        name.setText(user_name);
        email.setText(user_email);
        phone.setText(user_phone);
        state.setText(user_state);
        college.setText(user_college);
        degree.setText(user_degree);
        branch.setText(user_branch);
        start.setText(start_year);
        end.setText(end_year);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.usericon)
                .fitCenter()
                .centerInside()
                .into(profilePic);
    }

    public void saveProfile()
    {

        String user_name = name.getText().toString().trim();
        String user_email = email.getText().toString().trim();
        String user_phone = phone.getText().toString().trim();
        String user_state = state.getText().toString().trim();
        String user_college = college.getText().toString().trim();
        String user_branch = branch.getText().toString().trim();
        String user_degree = degree.getText().toString().trim();
        String start_year = start.getText().toString().trim();
        String end_year = end.getText().toString().trim();


        if(user_phone.isEmpty())
        {
            phone.requestFocus();
            phone.setError("required");
            return;
        }

        if(user_state.isEmpty())
        {
            state.requestFocus();
            state.setError("required");
            return;
        }

        if(user_college.isEmpty())
        {
            college.requestFocus();
            college.setError("required");
            return;
        }

        if(user_degree.isEmpty())
        {
            degree.requestFocus();
            degree.setError("required");
            return;
        }

        if(user_branch.isEmpty())
        {
            branch.requestFocus();
            branch.setError("required");
            return;
        }

        if(start_year.isEmpty())
        {
            start.requestFocus();
            start.setError("required");
            return;
        }

        if(end_year.isEmpty())
        {
            end.requestFocus();
            end.setError("required");
            return;
        }

        if(user_name.isEmpty())
        {
            name.requestFocus();
            name.setError("required");
            return;
        }

        if(user_email.isEmpty())
        {
            email.requestFocus();
            email.setError("required");
            return;
        }

        proceedToServer(user_name, user_email, user_phone, user_state, user_college,
                user_degree, user_branch, start_year, end_year);
    }

    private void proceedToServer(String user_name, String user_email, String user_phone,
                                 String user_state, String user_college,
                                 String user_degree, String user_branch,
                                 String start_year, String end_year)
    {
        //===> show progress bar first
        progressDialog.setMessage("Saving...");
        progressDialog.show();
        //===> disable user interaction when progress dialog appears
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        final String url = "https://app.thenextsem.com/app/update_profile.php";
        //error
        StringRequest sr = new StringRequest(1, url,
                response -> {


                    if(response.equals("1"))
                    {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userName",user_name);
                        editor.putString("userEmail",user_email);
                        editor.putString("userPhone",user_phone);
                        editor.putString("userState", user_state);
                        editor.putString("userCollege",user_college);
                        editor.putString("userDegree",user_degree);
                        editor.putString("userBranch",user_branch);
                        editor.putString("userStartYear",start_year);
                        editor.putString("userEndYear",end_year);
                        editor.apply();

                        progressDialog.dismiss();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                    else if(response.equals("0"))
                    {
                        progressDialog.dismiss();
                        //===> on dialog dismiss back to interaction mode
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(),"failed to update profile", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
                    progressDialog.dismiss();
                    //===> on dialog dismiss back to interaction mode
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }){
            @Override
            public Map<String, String> getParams() {
                Map<String, String> map =  new HashMap<>();
                map.put("idKey", sharedPreferences.getString("userId", ""));
                map.put("nameKey", user_name);
                map.put("emailKey", user_email);
                map.put("phoneKey", user_phone);
                map.put("stateKey", user_state);
                map.put("collegeKey", user_college);
                map.put("degreeKey", user_degree);
                map.put("branchKey", user_branch);
                map.put("fromKey", start_year);
                map.put("toKey", end_year);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(sr);
    }

    public void selectImage(View view)
    {
        Intent img = new Intent();
        img.setType("image/*");
        img.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(img,IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data!=null)
        {
            Uri path  = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                profilePic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Edit Profile");
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
        inflater.inflate(R.menu.edit_profile_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.save_button);
       Button saveProfile =  menuItem.
                getActionView().findViewById(R.id.saveButton);
       saveProfile.setOnClickListener(v -> saveProfile());
        return true;
    }
}
