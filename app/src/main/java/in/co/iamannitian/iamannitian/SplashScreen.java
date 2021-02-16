/*
* @Project I Am An NITian
* @Date 16 April 2020
*/

package in.co.iamannitian.iamannitian;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity
{
    public static final int SPLASH_TIME_OUT=3000;
    private SharedPreferences sharedPreferences;
    private Animation bottom_animation;
    private TextView splash_msg;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
          super.onCreate(savedInstanceState);
        //======>lets take advantage of the notch
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        final String user_id = sharedPreferences.getString("userId", "");
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        splash_msg = findViewById(R.id.splash_msg);
        splash_msg.setAnimation(bottom_animation);

        new Handler().postDelayed(() -> {
            if(!user_id.equals(""))
                 intent= new Intent(getApplicationContext(), MainActivity.class);
            else
                intent= new Intent(getApplicationContext(), LoginOrSignUpActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
