package com.example.vrglove;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.example.vrglove.ui.main.SectionsPagerAdapter;

public class MainActivity extends AppCompatActivity
    implements GloveData.OnFragmentInteractionListener,
                 openGL.OnFragmentInteractionListener{

    public Uri mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister broadcast listeners
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        this.mListener = uri;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Switch switchBT= findViewById(R.id.switchBT);
            TextView tvStatus = findViewById(R.id.textView_vrGlove_status);
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        switchBT.setChecked(false);
                        tvStatus.setText("Turn on bluetooth");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        switchBT.setChecked(true);
                        tvStatus.setText("Ready to connect");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        tvStatus.setText("Connecting");
                        break;
                    case BluetoothAdapter.STATE_DISCONNECTING:
                        tvStatus.setText("Disconnecting");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        tvStatus.setText("Turning on");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        tvStatus.setText("Turning off");
                        break;

                }
            }
        }
    };
}