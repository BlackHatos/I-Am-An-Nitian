package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class AboutUs extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /*=========>>> Setting Up dark Mode <<<==========*/
        boolean mode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
        if (mode) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        setUpToolbarMenu(mode);
    }

    public void setUpToolbarMenu(boolean mode)
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("I Am An Nitian");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (mode) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor2));
            actionBar.setIcon(R.drawable.app_logo_dark);
        } else {
            toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
            actionBar.setIcon(R.drawable.app_logo);
        }
        actionBar.setDisplayShowHomeEnabled(true);
    }


    public void goToFb(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/iamannitian"));
        startActivity(intent);
    }

    public void goToInsta(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/i_am_an_nitian/"));
        startActivity(intent);
    }

    public void goToYoutube(View v)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.youtube.com/iamannitian"));
        startActivity(intent);
    }
}
