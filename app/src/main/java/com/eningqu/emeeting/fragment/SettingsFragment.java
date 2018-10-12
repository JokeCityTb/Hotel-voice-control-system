package com.eningqu.emeeting.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eningqu.emeeting.R;
import com.eningqu.emeeting.activity.MainActivity;


@SuppressLint("ValidFragment")
public class SettingsFragment extends Fragment implements View.OnClickListener {
    private String mTitle;

    private TextView txtWifi;
    private TextView txtAbout;
    private TextView txtUpdate;
    private TextView txtDevices;

    public static SettingsFragment getInstance(String title) {
        SettingsFragment sf = new SettingsFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pager_settings, null);

        txtWifi = v.findViewById(R.id.txt_wifi);
        txtWifi.setOnClickListener(this);
        txtUpdate = v.findViewById(R.id.txt_update);
        txtUpdate.setOnClickListener(this);
        txtAbout = v.findViewById(R.id.txt_about);
        txtAbout.setOnClickListener(this);
       // txtDevices = v.findViewById(R.id.txt_devices);
        //txtDevices.setText(getIMEI());


        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_wifi:
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
                break;
            case R.id.txt_about:
                about();
                break;
            case R.id.txt_update:
                update();
                break;

        }
    }

    private void about() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View  viewAbout = getLayoutInflater().inflate(R.layout.pager_about,null);
        builder.setView(viewAbout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }

        });

        builder.show();
    }

    private void update() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View  viewAbout = getLayoutInflater().inflate(R.layout.pager_update,null);
        builder.setView(viewAbout);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }

        });

        builder.show();
    }

//    private String getIMEI() {
//        String imei = null;
//        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            imei = "没有开启授权";
//        } else {
//            TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
//            imei = telephonyManager.getDeviceId();
//        }
//        return imei;
//    }

}