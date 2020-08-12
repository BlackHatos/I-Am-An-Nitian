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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.material.snackbar.Snackbar;
import org.json.JSONObject;
import java.util.Arrays;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;


public class LoginOrSignupActivity extends AppCompatActivity
{
    private Button login,fb, googleSignInButton;
    private LoginButton fb_login;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;
    private GoogleSignInClient googleSignInClient;
    private NetworkInfo activeNetworkInfo;
    private BroadcastReceiver broadCastReceiver;

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

        FacebookSdk.sdkInitialize(getApplicationContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        //lets take advantage of the notch
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        else
        {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        setContentView(R.layout.activity_login_or_signup);

        //======> Setting  up broad cast receiver
        setBroadCastReceiver();

        login= findViewById(R.id.logsignbtn);
        fb_login = findViewById(R.id.fb_login);
		fb = findViewById(R.id.fb);
		googleSignInButton = findViewById(R.id.signInGoogle);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });

        fb_login.setReadPermissions(Arrays.asList("public_profile","email"));

        callbackManager = CallbackManager.Factory.create();
        fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try
                        {
                                sharedPreferences = getApplicationContext().getSharedPreferences("appData",
                                        MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("userId", object.getString("id"));
                                editor.putString("userName", object.getString("name"));
                                 editor.putString("userEmail", object.getString("email"));
                                editor.apply();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                //======> Go to main activity on successful login
                Intent intent = new Intent(getApplicationContext(), CompleteProfile.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
                fb.setText("Continue with facebook");
                fb.setClickable(true);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();
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
            if (v == fb) {
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
                Uri personPhoto = acct.getPhotoUrl();

                sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("userId", id);
                editor.putString("userName", name);
                editor.putString("userEmail", email);
                editor.apply();

                Intent intent = new Intent(getApplicationContext(), CompleteProfile.class);
                startActivity(intent);
                finish();
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
        snackbar.setActionTextColor(Color.YELLOW);

        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }
}

