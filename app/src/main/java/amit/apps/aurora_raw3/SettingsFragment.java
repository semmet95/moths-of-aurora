package amit.apps.aurora_raw3;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import amit.apps.aurora_raw3.datainitializers.SettingsHolder;
import jp.wasabeef.blurry.Blurry;

public class SettingsFragment extends Fragment {
    private Dialog customDialog;

    public SettingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View layout = inflater.inflate(R.layout.fragment_settings, container, false);
        Switch fanotif = layout.findViewById(R.id.fanotifications);
        Switch innotif = layout.findViewById(R.id.innotifications);
        Switch twnotif = layout.findViewById(R.id.twnotifications);
        Switch vinotif = layout.findViewById(R.id.vinotifications);
        Switch tinotif = layout.findViewById(R.id.tinotifications);
        Button contactus = layout.findViewById(R.id.contactus);
        Button shareButton = layout.findViewById(R.id.sharewithfriends);
        Button rateapp = layout.findViewById(R.id.rateapp);
        Button aboutaurora = layout.findViewById(R.id.aboutaurora);
        Button aboutapp = layout.findViewById(R.id.aboutapp);
        Button privacypolicy = layout.findViewById(R.id.privacypolicy);

        fanotif.setChecked(SettingsHolder.getfanotif());
        innotif.setChecked(SettingsHolder.getinnotif());
        twnotif.setChecked(SettingsHolder.gettwnotif());
        vinotif.setChecked(SettingsHolder.getvinotif());
        tinotif.setChecked(SettingsHolder.gettinotif());

        fanotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsHolder.setfanotif(isChecked);
            }
        });
        innotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsHolder.setinnotif(isChecked);
            }
        });
        twnotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsHolder.settwnotif(isChecked);
            }
        });
        vinotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsHolder.setvinotif(isChecked);
            }
        });
        tinotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingsHolder.settinotif(isChecked);
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent=new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"mothsofaurora@gmail.com"});
                startActivity(emailIntent);
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String msg="Moths of Aurora\n";
                intent.putExtra(Intent.EXTRA_TEXT, msg+"http://play.google.com/store/apps/details?id="+ SettingsFragment.this.getActivity().getPackageName());
                SettingsFragment.this.getActivity().startActivity(intent);
            }
        });

        aboutapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog= new Dialog(SettingsFragment.this.getContext(), R.style.blurtheme) {
                    public boolean dispatchTouchEvent(@NonNull MotionEvent event)
                    {
                        customDialog.dismiss();
                        return false;
                    }
                };
                customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Blurry.delete((ViewGroup) layout.getRootView());
                    }
                });
                customDialog.setContentView(R.layout.about_dialog);
                Window window = customDialog.getWindow();
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                window.setGravity(Gravity.CENTER);
                Blurry.with(SettingsFragment.this.getContext()).radius(10).sampling(1).onto((ViewGroup) layout.getRootView());
                customDialog.show();
            }
        });

        rateapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + SettingsFragment.this.getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + SettingsFragment.this.getContext().getPackageName())));
                }
            }
        });

        aboutaurora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog= new Dialog(SettingsFragment.this.getContext());
                customDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        Blurry.delete((ViewGroup) layout.getRootView());
                    }
                });
                customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customDialog.setContentView(R.layout.aboutaurora_dialog);
                customDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                Blurry.with(SettingsFragment.this.getContext()).radius(10).sampling(1).onto((ViewGroup) layout.getRootView());
                customDialog.show();
            }
        });

        privacypolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://singh-95.github.io/github-pages-with-jekyll/privacy-policy.htm");
                Intent goToPrivacyPolicy = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(goToPrivacyPolicy);
            }
        });

        android.support.v7.app.ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Settings");
        }
        return layout;
    }
}
