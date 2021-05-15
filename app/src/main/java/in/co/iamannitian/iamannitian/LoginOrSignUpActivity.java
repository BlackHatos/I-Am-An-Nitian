package in.co.iamannitian.iamannitian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;


public class LoginOrSignUpActivity extends AppCompatActivity
{
    private Button appLoginButton, fbLoginButton;
    public Button signInGoogle;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadCastReceiver;
	private String token = "";
	private static final int RC_SIGN_IN = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_signup);

        appLoginButton = findViewById(R.id.logsignbtn);
        fbLoginButton = findViewById(R.id.fb);

        // Google sign in

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        signInGoogle = findViewById(R.id.signInGoogle);

        signInGoogle.setOnClickListener(v -> {
            // Disable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            signInGoogle.setText("Signing you in...");
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });


        // App log in or sign up

        appLoginButton.setOnClickListener(v -> {
            Intent intent= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });


        // Facebook Log In

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        (object, response) -> {
                            try{
                                String id = object.getString("id");
                                String name = object.getString("name");
                                String email = object.getString("email");
                                String pic_url = "https://graph.facebook.com/"+id+"/"+"picture?type=large";
                                sendRequest(name, email, pic_url, "facebook");

                            } catch (Exception e)
                            {
                                // Enable user intercation
                                fbLoginButton.setText("Continue with facebook");
                                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                e.printStackTrace();
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel()
            {
                // Enable user interaction
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                fbLoginButton.setText("Continue with facebook");
                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                // Enable user interaction
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                fbLoginButton.setText("Continue with facebook");
                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Facebook signing-in callback manager
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Google signing-in stuff
        if (requestCode == RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    //Handling google sign-in
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try
        {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if(account != null)
            {
                String email = account.getEmail();
                String name = account.getDisplayName();
                String id = account.getId();
                String personPhoto = account.getPhotoUrl().toString();
                sendRequest(name, email, personPhoto, "google");
            }
            else
            {
                // Enable user interaction
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                signInGoogle.setText("Continue with Google");
                Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
            }

    } catch (ApiException e)
        {
            // Enable user interaction
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            signInGoogle.setText("Continue with Google");
            Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRequest(final String user_name, final String user_email, final String user_pic_url,
                             final String source)
    {
        final String url = "https://app.iamannitian.com/app/fb-google-signup.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, response -> {
            try
            {
                JSONObject object = new JSONObject(response);

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

                sharedPreferences = this.getSharedPreferences("appData", MODE_PRIVATE);
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

                // check if profile is incomplete

                if(phone.equals("null") || state.equals("null") || college.equals("null")
                || degree.equals("null") || branch.equals("null") || start.equals("null")
                || end.equals("null")
                )
                {
                    Intent intent  = new Intent(LoginOrSignUpActivity.this,CompleteProfile.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
                else  // if profile is complete
                {
                    Intent intent  = new Intent(LoginOrSignUpActivity.this,MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                    finish();
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }, error -> error.printStackTrace()){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("emailKey", user_email);
                map.put("nameKey", user_name);
                map.put("urlKey", user_pic_url);
                map.put("sourceKey", source);
                map.put("Token", token);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(request);
    }


    // OnClick Facebook Button

    public void onClick(View v)
    {
        fbLoginButton.setText("Signing you in...");
            // Disable user interaction
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        LoginManager.getInstance().logInWithReadPermissions(this,
                Arrays.asList("public_profile","email"));
    }
}

