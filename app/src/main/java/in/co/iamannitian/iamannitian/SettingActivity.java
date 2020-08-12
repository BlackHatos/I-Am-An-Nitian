package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat switchCompat;
    private ImageView dayNight;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        boolean mode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        if (mode)
        {
            setTheme(R.style.DarkTheme);
        }
        else
        {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        switchCompat = findViewById(R.id.darkSwitch);
        dayNight = findViewById(R.id.logo1);

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            dayNight.setImageResource(R.drawable.ic_copy_night);
            switchCompat.setChecked(true);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    dayNight.setImageResource(R.drawable.ic_copy_night);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    //restartApp();
                }
                else
                    {
                        dayNight.setImageResource(R.drawable.ic_dark_mode);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    //restartApp();
                }
            }
        });
    }

    private void restartApp()
    {
        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
        finish();
    }
}
