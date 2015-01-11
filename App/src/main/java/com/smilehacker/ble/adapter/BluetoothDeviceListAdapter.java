package com.smilehacker.ble.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.smilehacker.ble.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kleist on 13-12-6.
 */
public class BluetoothDeviceListAdapter extends BaseAdapter {

    private List<BluetoothDevice> mDeviceList;
    private Context mContext;

    public BluetoothDeviceListAdapter(Context context) {
        mContext = context;
        mDeviceList = new ArrayList<>();
    }

    public void addDevice(BluetoothDevice device) {
        if (!mDeviceList.contains(device)) {
            mDeviceList.add(device);
            notifyDataSetChanged();
        }
    }

    public void clearDevices() {
        mDeviceList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDeviceList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDeviceList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup container) {

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_ble_device, null);
            viewHolder = new ViewHolder();
            viewHolder.tvDeviceName = (TextView) convertView.findViewById(R.id.tv_device_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDevice device = mDeviceList.get(position);
        String deviceName = device.getName();
        viewHolder.tvDeviceName.setText(deviceName);

        return convertView;
    }

    private static class ViewHolder {
        public TextView tvDeviceName;
    }
}
