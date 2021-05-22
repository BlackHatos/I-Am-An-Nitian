package in.co.iamannitian.iamannitian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity
{
    private EditText email, password;
    private TextView go_to_sign_up;
    private Button click_to_login;
    private  ProgressDialog progressDialog;
    private SharedPreferences sharedPreferences;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Shared Preferences
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        go_to_sign_up = findViewById(R.id.go_to_sign_up);
        click_to_login = findViewById(R.id.click_to_login);
        progressDialog =  new ProgressDialog(this, R.style.ProgressDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false); //prevent disappearing

        setUpToolbarMenu();
        click_to_login.setOnClickListener(v -> {
            email.setError(null);
            password.setError(null);

            // Getting credentials on click the login button

            String user_email = email.getText().toString().trim().replaceAll("\\s+","");
            String user_password = password.getText().toString().trim().replaceAll("\\s+","");

            // Checking user email

            if (user_email.isEmpty())
            {
                email.requestFocus();
                email.setError("required");
                return;
            }

            // Checking user password

            if (user_password.isEmpty())
            {
                password.requestFocus();
                password.setError("required");
                return;
            }

            proceedToLogin(user_email, user_password);
          });

        go_to_sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
        
    }

    private void proceedToLogin(final String user_email, final String user_password)
    {
        // Show progress bar first
        progressDialog.setMessage("Authenticating....");
        progressDialog.show();

        // Disable user interaction
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        final String url = "https://app.iamannitian.com/app/app-login.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {
                    progressDialog.dismiss();

                    try
                    {
                        JSONObject object = new JSONObject(response);

                        String status = object.getString("status");

                        if(status.equals("0"))
                        {
                            String message = object.getString("message");
                            // Enable user interaction
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            String id = object.getString("id");
                            String name = object.getString("name");
                            String email = object.getString("email");
                            String phone = object.getString("phone");
                            String state = object.getString("state");
                            String college = object.getString("college");
                            String degree = object.getString("degree");
                            String branch = object.getString("branch");
                            String start = object.getString("start_year");
                            String end = object.getString("end_year");
                            String pic_url = object.getString("pic_url");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId", id);
                            editor.putString("userName", name);
                            editor.putString("userEmail", email);
                            editor.putString("userPicUrl", pic_url);
                            editor.putString("userPhone", phone);
                            editor.putString("userState", state);
                            editor.putString("userCollege",college);
                            editor.putString("userDegree", degree);
                            editor.putString("userBranch", branch);
                            editor.putString("userStartYear", start);
                            editor.putString("userEndYear", end);
                            editor.apply();


                            // Check if profile is incomplete

                            if(phone.equals("null") || state.equals("null") || college.equals("null")
                                    || degree.equals("null") || branch.equals("null") || start.equals("null")
                                    || end.equals("null")
                            )
                            {
                                Intent intent  = new Intent(LoginActivity.this,CompleteProfile.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }
                            else  // If profile is complete
                            {
                                Intent intent  = new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                finish();
                            }
                        }
                    }
                    catch (JSONException e)
                    {
                        // Enable user interaction
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
                    progressDialog.dismiss();

                    // Enable user interaction
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
                }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("emailKey", user_email);
                map.put("passwordKey", user_password);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(LoginActivity.this);
        rq.add(sr);
    }

    public void forgotPassword(View view)
    {
        startActivity(new Intent(LoginActivity.this, ForgetPassword.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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

    // Handling toolbar back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(LoginActivity.this, LoginOrSignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
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
                    startActivity(new Intent(LoginActivity.this, LoginOrSignUpActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}









