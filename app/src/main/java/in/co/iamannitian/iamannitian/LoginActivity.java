package in.co.iamannitian.iamannitian;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

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
        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        go_to_sign_up = findViewById(R.id.go_to_sign_up);
        click_to_login = findViewById(R.id.click_to_login);
        progressDialog =  new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false); //prevent disappearing

        setUpToolbarMenu();
        click_to_login.setOnClickListener(v -> {
            email.setError(null);
            password.setError(null);

            //==> getting credentials on click the login button
            String user_email = email.getText().toString().trim().replaceAll("\\s+","");
            String user_password = password.getText().toString().trim().replaceAll("\\s+","");
            //===> checking user email
            if (user_email.isEmpty())
            {
                email.requestFocus();
                email.setError("required");
                return;
            }
            //===> checking user password
            if (user_password.isEmpty())
            {
                password.requestFocus();
                password.setError("required");
                return;
            }

            proceedToLogin(user_email, user_password);
          });

        go_to_sign_up.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void proceedToLogin(final String user_email, final String user_password)
    {
        //===> show progress bar first
        progressDialog.setMessage("Authenticating....");
        progressDialog.show();
        //====> disable user interaction when progress dialog appears
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        final String url = "https://app.thenextsem.com/app/login.php";
        //error
        StringRequest sr = new StringRequest(1, url,
                response -> {

                 String response_array[] = response.split(",");
                    if(response_array[0].equals("1") && response_array[1].equals("1"))
                    {
                        progressDialog.dismiss();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId",response_array[2]);
                        editor.putString("userName", response_array[3]);
                        editor.putString("userEmail",response_array[4]);
                        editor.putString("userPhone",response_array[5]);
                        editor.putString("userState", response_array[6]);
                        editor.putString("userCollege",response_array[7]);
                        editor.putString("userDegree",response_array[8]);
                        editor.putString("userBranch",response_array[9]);
                        editor.putString("userStartYear",response_array[10]);
                        editor.putString("userEndYear",response_array[11]);
                        editor.putString("userPicUrl",response_array[12]);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
                    else if(response_array[0].equals("1") && response_array[1].equals("0"))
                    {
                        progressDialog.dismiss();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("userId",response_array[2]);
                        editor.putString("userName", response_array[3]);
                        editor.putString("userEmail",response_array[4]);
                        editor.putString("userPicUrl",response_array[5]);
                        editor.apply();

                        Intent intent = new Intent(getApplicationContext(), CompleteProfile.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        finish();
                    }
                    else if(response_array[0].equals("0") && response_array[1].equals("0"))
                    {
                        progressDialog.dismiss();
                        //===> on dialog dismiss back to interaction mode
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(),
                                response_array[2], Toast.LENGTH_LONG).show();
                    }

                }, error -> {
                    progressDialog.dismiss();
                    //===> on dialog dismiss back to interaction mode
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
        startActivity(new Intent(getApplicationContext(), ForgetPassword.class));
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

    //handling toolbar back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
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
                    startActivity(new Intent(getApplicationContext(), LoginOrSignUpActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}









