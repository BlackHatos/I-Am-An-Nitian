package in.co.iamannitian.iamannitian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.util.HashMap;
import java.util.Map;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SignupActivity extends AppCompatActivity {

    private EditText email, username, password;
    private Button click_to_sign_up;
    private TextView go_to_login;
    private BottomSheetDialog bottomSheetDialog;
    private String token = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            setTheme(R.style.DarkTheme);
        }
        else
        {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_signup);

        //=====> getting token of users device from firebase
        getTokenFromFirebase();

        username = findViewById(R.id.username);
        email    = findViewById(R.id.email);
        password = findViewById(R.id.password);
        click_to_sign_up = findViewById(R.id.click_to_sign_up);
        go_to_login = findViewById(R.id.go_to_login);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false); //prevent disappearing

        click_to_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                username.setError(null);
                email.setError(null);
                password.setError(null);

                //===> getting credentials on click the sign up button
                String user_name = username.getText().toString().trim();
                String user_email = email.getText().toString().trim().replaceAll("\\s+","");
                String user_password = password.getText().toString().trim().replaceAll("\\s+","");

                //===> checking user name
                if(user_name.isEmpty())
                {
                    username.requestFocus();
                    username.setError("required");
                    return;
                }

                //===> checking user email
                if(user_email.isEmpty())
                {
                    email.requestFocus();
                    email.setError("required");
                    return;
                }

                //===> checking user password
                if(user_password.isEmpty())
                {
                    password.requestFocus();
                    password.setError("required");
                    return;
                }

                //===> if every thing is ok then proceed to sign up
                proceedToSignup(user_name, user_email , user_password);
            }

        });

        //===> go to login activity
        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void proceedToSignup(final String user_name,final String user_email,final String user_password)
    {
        //===> show progress bar first
        progressDialog.setMessage("Processing...");
        progressDialog.show();
        //===> disable user interaction when progress dialog appears
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        final String url = "https://app.thenextsem.com/php_mailer/send_otp.php";
        StringRequest sr = new StringRequest(1, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String response_array[] = response.split(",");
                        if(response_array[0].equals("1"))
                        {
                            //===> dismiss the progress dialog when sign up successful
                            progressDialog.dismiss();
                            showBottomSheet();
                        }
                        else if(response_array[0].equals("0"))
                        {
                            progressDialog.dismiss();
                            //===> on dialog dismiss back to interaction mode
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                            Toast.makeText(getApplicationContext(),response_array[1], Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() { //error
            @Override
            public void onErrorResponse(VolleyError error)
            {
                progressDialog.dismiss();
                //===> on dialog dismiss back to interaction mode
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
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
        bottomSheetDialog = new BottomSheetDialog(getApplicationContext(),R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        final Button send_data  = bottomSheetView.findViewById(R.id.proceed);
        ImageView closeBottomSheet = bottomSheetView.findViewById(R.id.closeBottomSheet);
        final EditText enterOtp = bottomSheetView.findViewById(R.id.enterOtp);

        //===> send data with otp to verify an insertion
        send_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                send_data.setText("Processing...");
                //===> getting credentials on click the sign up button
                String user_name = username.getText().toString().trim();
                String user_email = email.getText().toString().trim().replaceAll("\\s+","");
                String user_password = password.getText().toString().trim().replaceAll("\\s+","");
                String otp = enterOtp.getText().toString().trim();
                enterOtp.setError(null);
                if(otp.isEmpty())
                {
                    enterOtp.requestFocus();
                    enterOtp.setError("required");
                    return;
                }

                finalSignup(user_name, user_email, user_password,otp,token,send_data);
            }
        });

        closeBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                send_data.setText("Continue");
            }
        });
    }

    void finalSignup(final String user_name, final String user_email,
                     final  String user_password, final String otp, final String token, final Button send_data)
    {
        final String url = "https://app.thenextsem.com/app/signup.php";
        StringRequest sr = new StringRequest(1, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String response_array[] = response.split(",");

                        if(response_array[0].equals("1"))
                        {
                            SharedPreferences sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId",response_array[1]);
                            editor.putString("userName", response_array[2]);
                            editor.putString("userEmail",response_array[3]);
                            editor.putString("userPhone","");
                            editor.putString("userState", "");
                            editor.putString("userCollege","");
                            editor.putString("userDegree","");
                            editor.putString("userBranch","");
                            editor.putString("userStartYear","");
                            editor.putString("userEndYear","");
                            editor.apply();

                            Intent intent = new Intent(SignupActivity.this, CompleteProfile.class);
                            //===> finish all previous activities
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        else if(response_array[0].equals("0"))
                        {
                            send_data.setText("Continue");
                            Toast.makeText(getApplicationContext(),response_array[1], Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),"Failed to sign up", Toast.LENGTH_LONG).show();
                send_data.setText("Continue");
            }
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

    //====> getting user access token from firebase
    private void getTokenFromFirebase()
    {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>()
        {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task)
            {
                if (task.isSuccessful())
                {
                    token = task.getResult().getToken();
                }
            }
        });
    }

}
