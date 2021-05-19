package me.at.nitsxr;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import in.co.iamannitian.iamannitian.ForgetPassword;
import in.co.iamannitian.iamannitian.R;

public class OtpFragment extends Fragment
{
    private static  final String EMAIL = "";
    private String user_mail = "";
    private Toolbar toolbar;
    private Button proceed;

    public OtpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_otp, container, false);
        proceed = rootView.findViewById(R.id.proceed);
        setUpToolbarMenu(rootView);


       toolbar.setNavigationOnClickListener(v -> {
           startActivity(new Intent(getActivity(), ForgetPassword.class));
           getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
           getActivity().finish();
       });

       proceed.setOnClickListener(v -> {
           openFragment();
       });

        return rootView;
    }

    public static OtpFragment onNewInstance(String email)
    {
        OtpFragment fragment = new OtpFragment();
        Bundle args =  new Bundle();
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        if(getArguments() != null)
        {
           user_mail = getArguments().getString(EMAIL);

        }
    }

    private void setUpToolbarMenu(View rootView)
    {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Back");
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar =  ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(getResources().getColor(R.color.textColor1));
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.textColor1),
                PorterDuff.Mode.SRC_ATOP);
    }

    private void openFragment()
    {
        ResetPasswordFragment fragment = ResetPasswordFragment.onNewInstance2();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragmentContainer, fragment, "RESET_PASSWORD_FRAGMENT").commit();
    }
}
