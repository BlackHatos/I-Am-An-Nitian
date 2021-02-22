package in.co.iamannitian.iamannitian;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ContactUs extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        setUpToolbarMenu();
    }

    private void setUpToolbarMenu() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Contact Us");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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

    public void callMe(View view)
    {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:8130512823"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    10);
            return;
        }
        else
        {
            try {
                startActivity(callIntent);
            }
            catch (android.content.ActivityNotFoundException ex)
            {
                Toast.makeText(getApplicationContext(), "Activity not found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void mailMe(View view)
    {
       Intent intent = new Intent(android.content.Intent.ACTION_SEND);
       intent.setType("message/rfc822");
       intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mauryask1732@gmail.com"});
       startActivity(Intent.createChooser(intent, "Choose an email client"));
    }
}
