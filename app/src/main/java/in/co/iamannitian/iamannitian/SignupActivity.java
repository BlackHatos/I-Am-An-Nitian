package in.co.iamannitian.iamannitian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.HashMap;
import java.util.Map;
import com.google.firebase.iid.FirebaseInstanceId;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity
{
    private EditText email, username, password;
    private Button click_to_sign_up;
    private TextView go_to_login;
    private BottomSheetDialog bottomSheetDialog;
    private String token = "";
    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Getting token of users device from firebase
        getTokenFromFirebase();

        username = findViewById(R.id.username);
        email    = findViewById(R.id.email);
        password = findViewById(R.id.password);
        click_to_sign_up = findViewById(R.id.click_to_sign_up);
        go_to_login = findViewById(R.id.go_to_login);

        progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false); //prevent disappearing

        setUpToolbarMenu();

        click_to_sign_up.setOnClickListener(v -> {
            username.setError(null);
            email.setError(null);
            password.setError(null);

            // Getting credentials on click the sign up button
            String user_name = username.getText().toString().trim();
            String user_email = email.getText().toString().trim().replaceAll("\\s+","");
            String user_password = password.getText().toString().trim().replaceAll("\\s+","");

            // Checking user name
            if(user_name.isEmpty())
            {
                username.requestFocus();
                username.setError("required");
                return;
            }

            // Checking user email
            if(user_email.isEmpty())
            {
                email.requestFocus();
                email.setError("required");
                return;
            }

            // Checking user password
            if(user_password.isEmpty())
            {
                password.requestFocus();
                password.setError("required");
                return;
            }

            // Ok, Proceed to sign up
            proceedToSignup(user_name, user_email , user_password);
        });

        // Go to login activity
        go_to_login.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void proceedToSignup(final String user_name,final String user_email,final String user_password)
    {
        // Show progress bar first
        progressDialog.setMessage("Processing...");
        progressDialog.show();

        // Disable user interaction
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        final String url = "https://app.iamannitian.com/php-mailer/send-otp.php";
        //error
        StringRequest sr = new StringRequest(1, url,
                response -> {

                    try
                    {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.getString("message");

                        if(status.equals("0"))
                        {
                            progressDialog.dismiss();
                            // Enable user interaction
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            // Dismiss progress dialog
                            progressDialog.dismiss();
                            showBottomSheet();
                        }

                    }
                    catch (JSONException e)
                    {
                        progressDialog.dismiss();
                        // Enable user interaction
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }

                }, error -> {
                    progressDialog.dismiss();
                    // Enable user interaction
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map =  new HashMap<>();
                map.put("nameKey", user_name);
                map.put("emailKey", user_email);
                map.put("passwordKey", user_password);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(SignupActivity.this);
        rq.add(sr);
    }

    private void showBottomSheet()
    {
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bootm_sheet_layout,null);
        bottomSheetDialog = new BottomSheetDialog(SignupActivity.this,R.style.SheetDialog);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        final Button send_data  = bottomSheetView.findViewById(R.id.proceed);
        ImageView closeBottomSheet = bottomSheetView.findViewById(R.id.closeBottomSheet);
        final EditText enterOtp = bottomSheetView.findViewById(R.id.enterOtp);

        // Send data with otp to verify if otp exists

        send_data.setOnClickListener(v -> {

            send_data.setText("Processing...");
            // Getting credentials on click the sign up button
            String user_name = username.getText().toString().trim();
            String user_email = email.getText().toString().trim().replaceAll("\\s+","");
            String user_password = password.getText().toString().trim().replaceAll("\\s+","");
            String otp = enterOtp.getText().toString().trim();
            enterOtp.setError(null);

            // Check if otp is empty

            if(otp.isEmpty())
            {
                enterOtp.requestFocus();
                enterOtp.setError("required");
                send_data.setText("Continue");
                return;
            }

            // Proceed to final registration
            finalSignup(user_name, user_email, user_password,otp,token,send_data);
        });

        closeBottomSheet.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });
    }

    void finalSignup(final String user_name, final String user_email,
                     final  String user_password, final String otp, final String token, final Button send_data)
    {
        final String url = "https://app.iamannitian.com/app/app-signup.php";
        StringRequest sr = new StringRequest(1, url,
                response -> {
                    try
                    {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        if(status.equals("0"))
                        {
                            send_data.setText("Continue");
                            Toast.makeText(getApplicationContext(),object.getString("message"),
                                    Toast.LENGTH_LONG).show();
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


                            SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
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

                            Intent intent  = new Intent(SignupActivity.this,CompleteProfile.class);
                            startActivity(intent);
                            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                            finish();
                        }
                    }
                    catch (JSONException e)
                    {
                        send_data.setText("Continue");
                        e.printStackTrace();
                    }

                }, error -> {
                    error.printStackTrace();
                    Toast.makeText(getApplicationContext(),"signup failed", Toast.LENGTH_LONG).show();
                    send_data.setText("Continue");
                }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map =  new HashMap<>();
                map.put("nameKey", user_name);
                map.put("emailKey", user_email);
                map.put("passwordKey", user_password);
                map.put("Token",token);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                map.put("otpKey", otp);
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(SignupActivity.this);
        rq.add(sr);
    }

    // Getting user access token from firebase
    private void getTokenFromFirebase()
    {
          FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener
                  (instanceIdResult -> token = instanceIdResult.getToken());
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            switch (keyCode)
            {
                case KeyEvent.KEYCODE_BACK:
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                    return  true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
