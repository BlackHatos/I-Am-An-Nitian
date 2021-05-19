package me.at.nitsxr;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import in.co.iamannitian.iamannitian.ForgetPassword;
import in.co.iamannitian.iamannitian.R;

public class ResetPasswordFragment extends Fragment
{
    private Toolbar toolbar;
    public ResetPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_reset_password, container, false);

        setUpToolbarMenu(rootView);
       /* toolbar.setNavigationOnClickListener(v -> {
            openFragment();
        });*/

        return rootView;
    }


    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
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

    public static ResetPasswordFragment onNewInstance2()
    {
        ResetPasswordFragment fragment = new ResetPasswordFragment();
        return fragment;
    }

    private void openFragment()
    {
        OtpFragment fragment = OtpFragment.onNewInstance("null");
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragmentContainer, fragment, "OTP_FRAGMENT").commit();
    }
}
