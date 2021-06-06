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
import androidx.core.content.IntentCompat;

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
    private TextView splashMsg;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Full screen in case of notch
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        setContentView(R.layout.activity_splash_screen);

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");
        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        splashMsg = findViewById(R.id.splash_msg);
        splashMsg.setAnimation(bottom_animation);

        new Handler().postDelayed(() -> {
            if(!userId.equals(""))
                 intent= new Intent(SplashScreen.this, MainActivity.class);
            else
                intent= new Intent(SplashScreen.this, LoginOrSignUpActivity.class);
           // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        }, SPLASH_TIME_OUT);
    }
}
