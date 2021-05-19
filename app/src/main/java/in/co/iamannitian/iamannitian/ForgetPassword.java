package in.co.iamannitian.iamannitian;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import me.at.nitsxr.OtpFragment;

public class ForgetPassword extends AppCompatActivity
{
    private Toolbar toolbar;
    private Button proceed;
    private EditText email;
    RelativeLayout fragmentContainer;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        proceed = findViewById(R.id.proceed);
        email = findViewById(R.id.email);
        fragmentContainer = findViewById(R.id.fragmentContainer);

        proceed.setOnClickListener(v -> {
            String mail = email.getText().toString().trim();
            openFragment(mail);
        });
        setUpToolbarMenu();
    }

    private void openFragment(String mail)
    {
        OtpFragment fragment = OtpFragment.onNewInstance(mail);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragmentContainer, fragment, "OTP_FRAGMENT").commit();
    }

    private void setUpToolbarMenu()
    {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Back");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }
}
