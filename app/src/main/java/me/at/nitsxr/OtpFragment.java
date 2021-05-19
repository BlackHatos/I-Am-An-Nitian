package me.at.nitsxr;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.co.iamannitian.iamannitian.ForgetPassword;
import in.co.iamannitian.iamannitian.R;

public class OtpFragment extends Fragment
{
    private static  final String EMAIL = "";
    private String user_mail = "";
    private Toolbar toolbar;
    private Button proceed;
    private EditText otp;

    public OtpFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_otp, container, false);
        proceed = rootView.findViewById(R.id.proceed);
        otp = rootView.findViewById(R.id.otp);
        setUpToolbarMenu(rootView);


       toolbar.setNavigationOnClickListener(v -> {
           startActivity(new Intent(getActivity(), ForgetPassword.class));
           getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
           getActivity().finish();
       });

       proceed.setOnClickListener(v -> {
           otp.setError(null);
           user_mail = getArguments().getString(EMAIL);
           String reset_pass_otp = otp.getText().toString().trim();

           if(reset_pass_otp.isEmpty())
           {
               otp.requestFocus();
               otp.setError("required");
               return;
           }

           verifyOtp(reset_pass_otp);
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

    private void openFragment(String mail)
    {
        ResetPasswordFragment fragment = ResetPasswordFragment.onNewInstance2(mail);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right,
                android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.add(R.id.fragmentContainer, fragment, "RESET_PASSWORD_FRAGMENT").commit();
    }

    public void verifyOtp(final String reset_pass_otp)
    {
        final String url = "https://app.iamannitian.com/app/verify-otp.php";

        StringRequest sr = new StringRequest(1, url,
                response -> {

                    try
                    {
                        JSONObject object = new JSONObject(response);

                        String status = object.getString("status");

                        if(status.equals("0"))
                        {
                            Toast.makeText(getActivity(),"invalid otp", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            openFragment(user_mail);
                        }
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "an error occurred1", Toast.LENGTH_SHORT).show();
                    }

                }, error -> {
            error.printStackTrace();
            Toast.makeText(getActivity(), "an error occurred1", Toast.LENGTH_SHORT).show();
        }){
            @Override
            public Map<String, String> getParams() throws AuthFailureError
            {
                Map<String, String> map =  new HashMap<>();
                map.put("otpKey", reset_pass_otp);
                map.put("codeKey", "J6T32A-Pubs7/=H~".trim());
                return map;
            }
        };

        RequestQueue rq = Volley.newRequestQueue(getActivity());
        rq.add(sr);
    }
}
