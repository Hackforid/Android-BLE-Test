package com.smilehacker.ble.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.smilehacker.ble.R;
import com.smilehacker.ble.service.BLEServerService;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class BLEServerActivity extends BaseActivity {

    private final static String TAG = BLEServerActivity.class.getName();
    private final static int REQUEST_ENABLE_BT = 10375;
    @InjectView(R.id.btn_start_server)
    Button mBtnStartServer;
    @InjectView(R.id.btn_stop_server)
    Button mBtnStopServer;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;
    private BluetoothLeAdvertiser mLeAdvertiser;
    private AdvertiseCallback mAdvertiseCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bleserver);
        ButterKnife.inject(this);
        initView();
        init();
        enableBLE();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.bleserver, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        mBtnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAdvertise();
            }
        });

        mBtnStopServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertise();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        mAdvertiseCallback = new AdvertiseCallback() {
            @Override
            public void onStartFailure(int errorCode) {
                super.onStartFailure(errorCode);
                if (errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE) {
                    Log.d(TAG, "Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                }
                else if(errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS){
                    Log.d(TAG, "Failed to start advertising because no advertising instance is available.");
                }
                else if(errorCode == ADVERTISE_FAILED_ALREADY_STARTED){
                    Log.d(TAG, "Failed to start advertising as the advertising is already started.");
                }
                else if(errorCode == ADVERTISE_FAILED_INTERNAL_ERROR){
                    Log.d(TAG, "Operation failed due to an internal error.");
                }
                else if(errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED){
                    Log.d(TAG, "This feature is not supported on this platform.");
                }
                else {
                    Log.d(TAG, "There was unknown error.");
                }
            }

            @Override
            public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                super.onStartSuccess(settingsInEffect);
                Log.i(TAG, "success");
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void enableBLE() {
        // Initializes Bluetooth adapter.
        mBluetoothManager = (BluetoothManager) getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            if (mBluetoothAdapter.isMultipleAdvertisementSupported()) {
                Log.i(TAG, "try get adveryiser");
                mLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
                if (mLeAdvertiser != null) {
                    Log.i(TAG, "get success");
                } else {
                    Log.i(TAG, "get fail");
                }
            } else {
                Log.i(TAG, "not support");
            }
        }
    }

    private void startBLEService() {
        Intent intent = new Intent(this, BLEServerService.class);
        startService(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "request code:" + requestCode + " result Code:" + resultCode);
        if (resultCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                enableBLE();
            } else {
                // TODO 显示错误
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startAdvertise() {
        AdvertiseSettings settings= new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_POWER)
                .setConnectable(true)
                .build();
        AdvertiseData data = new AdvertiseData.Builder()
                .build();

        mLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertise() {
        mLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

}
