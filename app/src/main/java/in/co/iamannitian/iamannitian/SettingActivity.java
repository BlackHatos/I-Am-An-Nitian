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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        String isNotify = sharedPreferences.getString("isNotify","");

        if(isNotify.equals("1"))
            notificationSwitch.setChecked(true);
        else
            notificationSwitch.setChecked(false);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(isChecked)
            {

                editor.putString("isNotify","1");
                notificationLogo.setImageResource(R.drawable.ic_notifications_active);
                changeState("1");
            }
            else
            {
                editor.putString("isNotify","0");
                notificationLogo.setImageResource(R.drawable.ic_notifications_off);
                changeState("0");
            }

            editor.apply();
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

    public void changeState(final String state)
    {
        final String url = "https://app.iamannitian.com/app/change-notification-state.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {
                   // do nothing
                }, error -> {
                    error.printStackTrace();
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("idKey", sharedPreferences.getString("userId", ""));
                map.put("stateKey", state);
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(SettingActivity.this);
        rq.add(sr);
    }
}
