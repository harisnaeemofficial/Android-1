package com.vacuum.app.plex.Fragments.MainFragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.vacuum.app.plex.Fragments.AboutFragment;
import com.vacuum.app.plex.Fragments.EditProfile_Fragment;
import com.vacuum.app.plex.Fragments.RequestFragment;
import com.vacuum.app.plex.Fragments.SettingFragment;
import com.vacuum.app.plex.Fragments.TicketFragment;
import com.vacuum.app.plex.MainActivity;
import com.vacuum.app.plex.Model.User;
import com.vacuum.app.plex.R;
import com.vacuum.app.plex.Utility.ApiClient;
import com.vacuum.app.plex.Utility.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;
import static com.vacuum.app.plex.Fragments.AboutFragment.TAG_ABOUT_FRAGMENT;
import static com.vacuum.app.plex.Fragments.EditProfile_Fragment.EDITPORFILE_FRAGMENT_TAG;
import static com.vacuum.app.plex.Fragments.RequestFragment.TAG_REQUEST_FRAGMENT;
import static com.vacuum.app.plex.Fragments.SettingFragment.TAG_SETTING_FRAGMENT;
import static com.vacuum.app.plex.Fragments.TicketFragment.TAG_TICKET_FRAGMENT;
import static com.vacuum.app.plex.Splash.SplashScreen.MY_PREFS_NAME;

/**
 * Created by Home on 2/19/2018.
 */

public class ProfileFragment extends Fragment implements View.OnClickListener, RewardedVideoAdListener {
    LinearLayout layout_settings,layout2_payment,layout_about,layout_share,layout_request,layout_redeem,layout1_editprofile;
    Button more_points;
    Context mContext;
    TextView points_textview,title;
    RewardedVideoAd mRewardedVideoAd;
    SharedPreferences prefs;
    String ADMOB_PLEX_REWARDED_1,user_id;
    Dialog dialog;
    static int points_value;
    ProgressBar progresssbar_redeem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_fragment, container, false);
        layout_settings = view.findViewById(R.id.layout_settings);
        layout1_editprofile = view.findViewById(R.id.layout1_editprofile);
        layout2_payment = view.findViewById(R.id.layout2_payment);
        layout_about = view.findViewById(R.id.layout_about);
        layout_share = view.findViewById(R.id.layout_share);
        layout_request = view.findViewById(R.id.layout_request);
        layout_redeem = view.findViewById(R.id.layout_redeem);

        more_points = view.findViewById(R.id.more_points);
        points_textview = view.findViewById(R.id.points);
        title = view.findViewById(R.id.title);

        mContext = this.getActivity();



        prefs = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        ADMOB_PLEX_REWARDED_1 = prefs.getString("ADMOB_PLEX_REWARDED_1",null);
        user_id = prefs.getString("id",null);
        title.setText(prefs.getString("full_name","John Doe"));

        layout_settings.setOnClickListener(this);
        more_points.setOnClickListener(this);
        layout1_editprofile.setOnClickListener(this);
        layout2_payment.setOnClickListener(this);
        layout_about.setOnClickListener(this);
        layout_share.setOnClickListener(this);
        layout_request.setOnClickListener(this);
        layout_redeem.setOnClickListener(this);


        // Use an activity context to get the rewarded video instance.
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(mContext);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();
        update_points_value();
        get_points();
        return view;
    }

    private void update_points_value() {
        points_value = prefs.getInt("points",0);
        if (points_value <= 10)
        {
            points_textview.setText("$"+String.valueOf(points_value));
            points_textview.setTextColor(Color.RED);
        }else
        {
            points_textview.setText("$"+String.valueOf(points_value));
            points_textview.setTextColor(Color.WHITE);
        }
    }

    private void get_points() {
        String BASE_URL = "https://mohamedebrahim.000webhostapp.com/";

        ApiInterface apiService = ApiClient.getClient(mContext,BASE_URL).create(ApiInterface.class);
        Call<User> call_details = apiService.get_points(user_id);
        call_details.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User m = response.body();
                SharedPreferences.Editor editor = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt("points",m.getPoints());
                editor.apply();
                update_points_value();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_settings:
                switchfragment(new SettingFragment(),TAG_SETTING_FRAGMENT);
                break;
            case R.id.more_points:

                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                }else {
                    Toast.makeText(mContext, "Not loaded yet!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layout1_editprofile:
                switchfragment(new EditProfile_Fragment(),EDITPORFILE_FRAGMENT_TAG);
                break;
            case R.id.layout2_payment:
                switchfragment(new TicketFragment(),TAG_TICKET_FRAGMENT);
                //Toast.makeText(mContext, "Go Premium!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.layout_about:
                switchfragment(new AboutFragment(),TAG_ABOUT_FRAGMENT);
                break;
            case R.id.layout_share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "If you guys want a free (Movie/TVseries) app, Give Plex a try. https://github.com/mohamedebrahim96/PlexMedia/raw/master/version/release/app-release.apk";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "If you guys want a free (Movie/TVseries) app, Give Plex a try.");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;
            case R.id.layout_request:
                switchfragment(new RequestFragment(),TAG_REQUEST_FRAGMENT);
                break;
            case R.id.layout_redeem:
                alert_dialog();
                break;

            default:
        }
    }



    private void switchfragment(Fragment fragment,String TAG) {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, TAG);
        fragmentTransaction.addToBackStack(MainActivity.CURRENT_TAG);
        fragmentTransaction.commit();
    }
    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(ADMOB_PLEX_REWARDED_1,//ca-app-pub-3940256099942544/5224354917
                new AdRequest.Builder().build());
    }
    @Override
    public void onRewardedVideoAdLoaded() {
       // mRewardedVideoAd.show();
    }
    @Override
    public void onRewardedVideoAdOpened() {
    }
    @Override
    public void onRewardedVideoStarted() {

    }
    @Override
    public void onRewardedVideoAdClosed() { loadRewardedVideoAd(); }

    private void update_points(int points) {
        String BASE_URL = "https://mohamedebrahim.000webhostapp.com/";

        ApiInterface apiService = ApiClient.getClient(mContext,BASE_URL).create(ApiInterface.class);
        Log.e("TAG :points",String.valueOf(points));
        Call<User> call_details = apiService.points(user_id,String.valueOf(points));
        call_details.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User m = response.body();
                SharedPreferences.Editor editor = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putInt("points",m.getPoints());
                editor.apply();
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }
    @Override
    public void onRewarded(RewardItem rewardItem) {
        update_points(prefs.getInt("points",0)+50); }
    @Override
    public void onRewardedVideoAdLeftApplication() {   }
    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {    }
    @Override
    public void onResume() {        mRewardedVideoAd.resume(mContext);
        super.onResume(); }
    @Override
    public void onPause() {        mRewardedVideoAd.pause(mContext);
        super.onPause(); }
    @Override
    public void onDestroy() {        mRewardedVideoAd.destroy(mContext);
        super.onDestroy(); }

    private void alert_dialog() {
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.redeem_layout);
        dialog.setTitle("Test Capcha");
        final EditText captcha_edit_text= dialog.findViewById(R.id.captcha_edit_text);
        final Button redeem_Button =  dialog.findViewById(R.id.redeem_Button);
        final TextView error_redeem= dialog.findViewById(R.id.error_redeem);
        final TextView balance= dialog.findViewById(R.id.balance);
        final LinearLayout redeem_edit_layout= dialog.findViewById(R.id.redeem_edit_layout);
        progresssbar_redeem= dialog.findViewById(R.id.progresssbar_redeem);

        captcha_edit_text.requestFocus();
        captcha_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 0) {
                    redeem_Button.setEnabled(false);
                    redeem_Button.setAlpha(0.2f);
                }else {
                    redeem_Button.setEnabled(true);
                    redeem_Button.setAlpha(1);
                }
            }
        });
        redeem_Button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                progresssbar_redeem.setVisibility(View.VISIBLE);
                Redeem(captcha_edit_text.getText().toString(),error_redeem,
                        captcha_edit_text,
                        redeem_edit_layout,
                        balance,
                        redeem_Button);
                }
        });
        dialog.show();
    }

    private void Redeem(String redeem, final TextView error_redeem,
                        final EditText captcha_edit_text,
                        final LinearLayout redeem_edit_layout,
                        final TextView balance,
                        final Button redeem_Button) {
        String BASE_URL = "https://mohamedebrahim.000webhostapp.com/";
        ApiInterface apiService = ApiClient.getClient(mContext,BASE_URL).create(ApiInterface.class);
        Call<User> call_details = apiService.redeem(user_id,redeem);
        call_details.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    User m = response.body();
                    SharedPreferences.Editor editor = mContext.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putInt("points", m.getPoints());
                    editor.apply();
                    progresssbar_redeem.setVisibility(View.GONE);
                    InputMethodManager inputMethodManager =(InputMethodManager)getContext()
                            .getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(captcha_edit_text.getWindowToken(), 0);
                    redeem_edit_layout.setVisibility(View.GONE);
                    balance.setVisibility(View.VISIBLE);
                    balance.setText("$"+(m.getPoints()-points_value)+".00 is added. \n Your new balance is $"+String.valueOf(m.getPoints())+".00");
                    redeem_Button.setText("Confirm");
                    redeem_Button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) { dialog.dismiss();
                        }
                    });
                    update_points_value();
                }catch (Exception e){  }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e("TAG", t.toString());
                progresssbar_redeem.setVisibility(View.GONE);
                error_redeem.setVisibility(View.VISIBLE);
            }
        });

    }
}