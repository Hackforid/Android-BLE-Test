package com.smilehacker.ble.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.smilehacker.ble.R;
import com.smilehacker.ble.activity.BLEServerActivity;
import com.smilehacker.ble.activity.FindDevicesActivity;

/**
 * Created by kleist on 13-12-5.
 */
public class MainFragment extends Fragment {

    private Button mBtnFindDevices;
    private Button mBtnStartServer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        findView(rootView);
        initView();
        return rootView;
    }

    private void findView(View view) {
        mBtnFindDevices = (Button) view.findViewById(R.id.btn_find_devices);
        mBtnStartServer = (Button) view.findViewById(R.id.btn_start_server);
    }

    private void initView() {
        mBtnFindDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), FindDevicesActivity.class);
                startActivity(intent);
            }
        });

        mBtnStartServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BLEServerActivity.class);
                startActivity(intent);
            }
        });
    }
}
