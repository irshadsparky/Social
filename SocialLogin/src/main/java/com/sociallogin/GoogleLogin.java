package com.sociallogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sociallogin.users.SmartGoogleUser;
import com.sociallogin.util.Constants;
import com.sociallogin.util.SmartLoginException;
import com.sociallogin.util.UserUtil;

/**
 * Copyright (c) 2016 Codelight Studios
 * Created by irshad on 26/09/16.
 */

public class GoogleLogin extends SmartLogin {


    @Override
    public void facebook(SmartLoginConfig config) {

    }

    @Override
    public void google(SmartLoginConfig config) {
        GoogleApiClient apiClient = config.getGoogleApiClient();
        Activity activity = config.getActivity();

        if (apiClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(config.getClient_id())
                    .requestEmail()
                    .requestProfile()
                    .build();

            apiClient = new GoogleApiClient.Builder(activity)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }

        ProgressDialog progress = ProgressDialog.show(activity, "", activity.getString(com.sociallogin.R.string.logging_holder), true);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        activity.startActivityForResult(signInIntent, Constants.GOOGLE_LOGIN_REQUEST);
        progress.dismiss();
    }

    @Override
    public void linkdin(SmartLoginConfig config) {

    }


    @Override
    public boolean logout(Context context) {
        try {
            SharedPreferences preferences = context.getSharedPreferences(Constants.USER_PREFS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove(Constants.USER_TYPE);
            editor.remove(Constants.USER_SESSION);
            editor.apply();
            return true;
        } catch (Exception e) {
            Log.e("GoogleLogin", e.getMessage());
            return false;
        }
    }

    @Override
    public void twitter(SmartLoginConfig config) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, SmartLoginConfig config) {
        ProgressDialog progress = ProgressDialog.show(config.getActivity(), "", config.getActivity().getString(com.sociallogin.R.string.getting_data), true);
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//        vollyRequestSimpleLoginClass.setVolleyRequestResponce(config.getCallback());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            SmartGoogleUser googleUser = UserUtil.populateGoogleUser(acct);
            // Save the user
            UserSessionManager.setUserSession(config.getActivity(), googleUser);
            config.getCallback().onGoogleLoginSuccess(googleUser);
           /* vollyRequestSimpleLoginClass.setParams();
            vollyRequestSimpleLoginClass.Start(config.getActivity());*/
            progress.dismiss();
        } else {
//            vollyRequestSimpleLoginClass.getVolleyRequestResponce().onLoginFailure(new SmartLoginException("requestCode-->"+requestCode,LoginType.Google));
            Log.d("GOOGLE SIGN IN", "" + requestCode);
            // Signed out, show unauthenticated UI.
            progress.dismiss();
            config.getCallback().onLoginFailure(new SmartLoginException("Google login failed", LoginType.Google));
        }
    }
}
