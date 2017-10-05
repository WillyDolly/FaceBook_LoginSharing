package com.popland.pop.facebook_loginsharing;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.LikeView;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
CallbackManager callbackManager;
AccessTokenTracker accessTokenTracker;
AccessToken accessToken;
   ShareDialog shareDialog;
    ProfileTracker profileTracker;
    Profile profile;
    TextView pictureUriTV;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //1. register Facebook app on developers.facebook.com/apps
        //2. add SDK Facebook
        //- repositories, dependencies
        //- FB app Id -> strings.xml
        //- metadata, CustomTabActivity(FacebookActivity) -> Manifest
        //- print out KeyHash:  tHBg4UC6Opyeb0j1n/zNpP+9AyI=(obtain debug & release)
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo("com.popland.pop.facebook_loginsharing",
//                    PackageManager.GET_SIGNATURES);
//            for(Signature signature:info.signatures){
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash: ", Base64.encodeToString(md.digest(),Base64.DEFAULT));
//            }
//        }catch(PackageManager.NameNotFoundException exception){
//
//        }catch(NoSuchAlgorithmException exc){
//
//        }
        FacebookSdk.sdkInitialize(getApplicationContext());// Login status & Profile saved to FB SDK
        callbackManager = CallbackManager.Factory.create();
        //3.App Events
        //3.1 LogIn
        // solution 1:
        LoginButton loginButton = (LoginButton)findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this,"Cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        loginButton.setReadPermissions(Arrays.asList("user_friends","public_profile","email"));
         // solution 2:
//        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//                        @Override
//            public void onSuccess(LoginResult loginResult) {
//                Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });
//        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList("email"));
        //3.2 Check LogIn status
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken != null) {
                    //Save and Query accessToken
                    accessToken = currentAccessToken;
                    Toast.makeText(MainActivity.this, accessToken.getToken(), Toast.LENGTH_LONG).show();
                }
            }
        };
        //3.3 Obtain profile during Login
        pictureUriTV = (TextView)findViewById(R.id.ProfilePictureUri);
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                if(currentProfile!= null) {
                    profile = currentProfile;
                    pictureUriTV.setText(profile.getName() + profile.getId() +
                            "\n" + profile.getProfilePictureUri(200, 200).toString());
                }
            }
        };
        //3.4 Share content: Link, image, video, multimedia -> FB's timeline
         ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("SPEED NUMBER. Remember 18 digits in 7s???")
                .setQuote("No, you can't."+"\n 543564575678678869247167"+"\n Time: 23.232"+"\n Correct: 2")
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.popland.pop.speednumber"))//link to GPlay show no details
               // .setImageUrl(Uri.parse("https://drive.google.com/file/d/0B4VOIcTq_prmVTJuQk5hSnJZT1E/view?usp=sharing"))
                .build();
        //Solution 1:
         ShareButton shareButton = (ShareButton)findViewById(R.id.fb_share_button);
        shareButton.setShareContent(content);
        //Solution 2: CustomShareButton -> ShareDialog
//        shareButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                shareDialog = new ShareDialog(MainActivity.this);
//                    if (ShareDialog.canShow(ShareLinkContent.class)) {
//                        shareDialog.show(content);
//                    }
//            }
//        });
        //3.5 Like: not working yet
        LikeView likeView = (LikeView)findViewById(R.id.fb_like_view);
        likeView.setObjectIdAndType(
                "https://www.facebook.com/photo.php?fbid=1721576191467431&set=a.1385921338366253.1073741831.100008451996101&type=3&theater",
                LikeView.ObjectType.PAGE
        );


    }
    //After Login/ Share, callback to MainActivity return outcome
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
}
