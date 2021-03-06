package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {

    private SwitchCompat  notificationSwitch, darkModeSwitch;
    private ImageView notificationLogo, darkModeLogo;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        notificationSwitch = findViewById(R.id.turnNotification);
        notificationLogo = findViewById(R.id.logo2);
        darkModeSwitch = findViewById(R.id.darKMode);
        darkModeLogo = findViewById(R.id.logo1);

        sharedPreferences = getSharedPreferences("appData", MODE_PRIVATE);
        String isNotifyCheck = sharedPreferences.getString("activeNotification","");
        String isDarkCheck = sharedPreferences.getString("activeNotification","");

        if(isNotifyCheck.equals("1"))
            notificationSwitch.setChecked(true);

        if(isDarkCheck.equals("1"))
           darkModeSwitch.setChecked(true);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("activeNotification","1");
                editor.apply();
                notificationLogo.setImageResource(R.drawable.ic_notifications_active);
            }
            else
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("activeNotification","0");
                editor.apply();
                notificationLogo.setImageResource(R.drawable.ic_notifications_off);
            }
        });

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("activeDarkMode","1");
                editor.apply();
                darkModeLogo.setImageResource(R.drawable.ic_brightness_4_black_24dp);
            }
            else
            {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("activeDarkMode","0");
                editor.apply();
                darkModeLogo.setImageResource(R.drawable.ic_brightness_7_black_24dp);
            }
        });

        setUpToolbarMenu();
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.other_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.app_info:
                startActivity(new Intent(getApplicationContext(), AppInfo.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
