package com.codepath.snapmap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.login.widget.LoginButton;

public class LoginFragment extends Fragment {

    LoginButton loginButton;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);

        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);

       return view;
   }

}
