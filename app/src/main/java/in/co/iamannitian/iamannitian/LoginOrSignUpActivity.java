package in.co.iamannitian.iamannitian;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginOrSignUpActivity extends AppCompatActivity
{
    private Button login,fb, googleSignInButton;
    private LoginButton fb_login;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadCastReceiver;
	private String token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        getTokenFromFirebase();
        setContentView(R.layout.activity_login_or_signup);
        setBroadCastReceiver();

        sharedPreferences = getApplicationContext().getSharedPreferences("appData",
                MODE_PRIVATE);

        login= findViewById(R.id.logsignbtn);
        fb_login = findViewById(R.id.fb_login);
		fb = findViewById(R.id.fb);
		googleSignInButton = findViewById(R.id.signInGoogle);

        login.setOnClickListener(v -> {
            Intent intent= new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        fb_login.setReadPermissions(Arrays.asList("public_profile","email"));
        callbackManager = CallbackManager.Factory.create();
        fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
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
                                sendRequest(name, email, pic_url, "social");
                            } catch (Exception e)
                            {
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
                Toast.makeText(getApplicationContext(), "failed to login", Toast.LENGTH_SHORT).show();
                fb.setText("Continue with facebook");
                fb.setClickable(true);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "failed to login", Toast.LENGTH_SHORT).show();
                fb.setText("Continue with facebook");
                fb.setClickable(true);
            }
        });
    }


    //=====> facebook button click listener
    public void onClick(View v)
    {
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            fb.setText("Logging you in....");
            fb.setClickable(false);
            googleSignInButton.setClickable(false);
            if (v == fb)
            {
                fb_login.performClick();
            }
        }
        else
            showSnackBar();
    }

    //=====> Google sign in button  click listener
    public void signIn(View v)
    {
        if(activeNetworkInfo != null && activeNetworkInfo.isConnected())
        {
            googleSignInButton.setText("Logging you in....");
            googleSignIn();
        }
        else
            showSnackBar();
    }

    private  void googleSignIn()
    {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, 5);
    }

    //=====> Google & Facebook onActivityResult function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 5)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount acct = result.getSignInAccount();

            if(acct != null)
            {
                String email = acct.getEmail();
                String name = acct.getDisplayName();
                String id = acct.getId();
                String personPhoto = acct.getPhotoUrl().toString();
                sendRequest(name, email, personPhoto, "social");
            }
        }
    }

    //==========> Setting up broadcast receiver from internet connection
    public void setBroadCastReceiver()
    {
        broadCastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                //======> check internet connection
                ConnectivityManager connectivityManager  = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);
                activeNetworkInfo=  connectivityManager.getActiveNetworkInfo();
            }
        };

        registerReceiver(broadCastReceiver,
                new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
    }

    //====> unregister broadcast receiver onDestroy
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadCastReceiver);
    }

    //=======> Show this snack bar on changing the network state
    public void showSnackBar()
    {
        final Snackbar snackbar = Snackbar.make(findViewById(R.id.myRelativeLayout),
                Html.fromHtml("<font color=#ffffff>No Internet connection. Turn on WiFi or mobile data</font>"),
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));

        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    private void sendRequest(final String name, final String email, final String pic_url, final String source)
    {
        final String url = "https://app.thenextsem.com/app/fb_google_signup.php";
        //error
        StringRequest request = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                String respo[] = response.split(",");
                if(respo[0].equals("1") && respo[1].equals("1"))
                {
					sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("userId", respo[2]);
					editor.putString("userName", respo[3]);
					editor.putString("userEmail", respo[4]);
					editor.putString("userPicUrl", respo[5]);
					editor.putString("userPhone", respo[6]);
					editor.putString("userState", respo[7]);
					editor.putString("userCollege", respo[8]);
					editor.putString("userDegree", respo[9]);
					editor.putString("userBranch", respo[10]);
					editor.putString("userStartYear", respo[11]);
					editor.putString("userEndYear", respo[12]);
					editor.apply();

					Intent intent  = new Intent(getApplicationContext(),MainActivity.class);
					startActivity(intent);
					finish();
                }
                else if((respo[0].equals("1") && respo[1].equals("0")))
                {
                    sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putString("userId", respo[2]);
					editor.putString("userName", respo[3]);
					editor.putString("userEmail", respo[4]);
					editor.putString("userPicUrl", respo[5]);
					editor.apply();  
					
					startActivity(new Intent(getApplicationContext(), CompleteProfile.class));
					finish();
                }
                else if( (respo[0].equals("0") && respo[1].equals("1")))
                {
                    sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", respo[2]);
                    editor.putString("userName", respo[3]);
                    editor.putString("userEmail", respo[4]);
                    editor.putString("userPicUrl", respo[5]);
                    editor.putString("activeNotification","1");
                    editor.apply();

                    Intent intent = new Intent(getApplicationContext(), CompleteProfile.class);
                    startActivity(intent);
                    finish();
                }
				else
                {
					 Toast.makeText(getApplicationContext(), "Unable to sign up", Toast.LENGTH_SHORT).show();
                     logout();
				}
            }

        }, error -> error.printStackTrace())
        {
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("emailKey", email);
                map.put("nameKey", name);
                map.put("urlKey", pic_url);
                map.put("sourceKey", source);
                map.put("Token", token);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getApplicationContext());
        rq.add(request);
    }

	 //====> getting user access token from firebase
    private void getTokenFromFirebase()
    {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                token = task.getResult().getToken();
            }
        });
    }

    private void logout()
    {
        //===> logout from facebook sign in
        Profile profile = Profile.getCurrentProfile().getCurrentProfile();
        if (profile != null)  //===> if user logged in
        {
            LoginManager.getInstance().logOut();
        }

        //===> logout from google sign in
        GoogleSignIn.getClient( this,new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()).signOut();

        sharedPreferences.edit().clear().apply();

        startActivity(new Intent(getApplicationContext(), CompleteProfile.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        overridePendingTransition(0,0);
    }
}

