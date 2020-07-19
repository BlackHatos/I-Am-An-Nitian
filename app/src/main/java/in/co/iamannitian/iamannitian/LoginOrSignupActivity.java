package in.co.iamannitian.iamannitian;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import org.json.JSONObject;
import java.util.Arrays;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class LoginOrSignupActivity extends AppCompatActivity {

    private Button login,fb;
    private LoginButton fb_login;
    private CallbackManager callbackManager;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*=========>>> Setting Up dark Mode <<<==========*/
        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
        {
            setTheme(R.style.DarkTheme);
        }
        else
        {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login_or_signup);

        FacebookSdk.sdkInitialize(getApplicationContext());

        login= findViewById(R.id.logsignbtn);
        fb_login = findViewById(R.id.fb_login);
		fb = findViewById(R.id.fb);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(LoginOrSignupActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        fb_login.setReadPermissions(Arrays.asList("public_profile","email"));

        callbackManager = CallbackManager.Factory.create();
        fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try
                        {
                            String  name = object.getString("name");
                            String  email = object.getString("email");
                            String  id = object.getString("id");
                            String imageUrl = "https://graph.facebook.com/"+id+"";

                            sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userId",id);
                            editor.putString("userName", name);
                            editor.putString("userEmail",email);
                            editor.apply();

                            startActivity(new Intent(LoginOrSignupActivity.this, MainActivity.class));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(LoginOrSignupActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                fb.setText("Continue with facebook");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginOrSignupActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                fb.setText("Continue with facebook");
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void onClick(View v)
    {
        fb.setText("Logging you in....");
        if (v == fb)
        {
            fb_login.performClick();
        }
    }

    public void loadUserProfile(AccessToken accessToken)
    {
    }
}


/*=========================== shared preferences saving user data started ============================*/
  /*                */
/*=========================== shared preferences saving user data finished ============================*/

           /* String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    String gender = object.getString("gender");
                    String imageUrl = "https://graph.facebook.com"+ id +"/picture?return_ssl_resources=1";
                    */
