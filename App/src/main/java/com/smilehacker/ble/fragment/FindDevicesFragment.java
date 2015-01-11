package com.smilehacker.ble.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.smilehacker.ble.R;
import com.smilehacker.ble.adapter.BluetoothDeviceListAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by kleist on 13-12-6.
 */
public class FindDevicesFragment extends Fragment {

    private final static String TAG = FindDevicesFragment.class.getName();

    private final static int REQUEST_ENABLE_BT = 10375;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 3000;
    @InjectView(R.id.btn_start)
    Button mBtnStart;
    @InjectView(R.id.btn_stop)
    Button mBtnStop;
    @InjectView(R.id.lv_devices)
    ListView mLvDevices;
    @InjectView(R.id.ll_bottom)
    LinearLayout mLlBottom;

    private boolean mScanning = false;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothManager mBluetoothManager;

    private BluetoothDeviceListAdapter mDeviceListAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkLBESupport();
        mDeviceListAdapter = new BluetoothDeviceListAdapter(getActivity());
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.i(TAG, ">>>Find device: " + device.getName() + " " + device.getAddress());
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_devices, container, false);
        ButterKnife.inject(this, view);
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mLvDevices.setAdapter(mDeviceListAdapter);
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableBLE();
            }
        });

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopScan();
            }
        });
    }


    private void checkLBESupport() {
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), "不支持LBE", Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
    }

    private void enableBLE() {
        // Initializes Bluetooth adapter.
        mBluetoothManager = (BluetoothManager) getActivity().getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            startScan();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "request code:" + requestCode + " result Code:" + resultCode);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                enableBLE();
            } else {
                Toast.makeText(getActivity(), "BLE open fail", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopScan();
    }

    private void startScan() {
        Log.i(TAG, ">>>> start scan");
        mDeviceListAdapter.clearDevices();
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    private void stopScan() {
        Log.i(TAG, ">>>> stop scan");
        mBluetoothAdapter.stopLeScan(mLeScanCallback);
    }


//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
////        final BluetoothDevice device = mLeDeviceList.get((int) id);
////        Intent intent = new Intent(getActivity(), BLEService.class);
////        intent.putExtra(Constants.KEY_BLE_DEVICE, device);
////        getActivity().startService(intent);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}
