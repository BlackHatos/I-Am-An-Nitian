/*
* @Project "I Am An Nitian"
* @Date "16 April 2020"
*/
package in.co.iamannitian.iamannitian;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity
{
    public static final int SPLASH_TIME_OUT=4000;
    private SharedPreferences sharedPreferences;

    //Animation
    private Animation bottom_animation;
    private TextView splash_msg;
    ImageView logo;

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

        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        final String user_id = sharedPreferences.getString("userId", "");
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        splash_msg = findViewById(R.id.splash_msg);
        logo = findViewById(R.id.logo);
        splash_msg.setAnimation(bottom_animation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(!user_id.equals(""))
                {
                    Intent intent= new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent= new Intent(SplashScreen.this, LoginOrSignupActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, SPLASH_TIME_OUT);

    }
}
