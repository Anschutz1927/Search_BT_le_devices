package by.blackpearl.searchbtdevices.adapters;

import android.bluetooth.BluetoothDevice;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import by.blackpearl.searchbtdevices.R;

/**
 * Created by yauheni.
 */

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.Holder> {

    private ArrayList<BluetoothDevice> mBtDevices;
    private SparseIntArray mBtDevicesSignal;
    private Callback mCallback;

    public DevicesAdapter(Callback callback) {
        mBtDevices = new ArrayList<>();
        mBtDevicesSignal = new SparseIntArray();
        this.mCallback = callback;
    }

    public void addNewDeviceToArray(BluetoothDevice device, int rssi) {
        mBtDevices.add(device);
        int position = mBtDevices.indexOf(device);
        mBtDevicesSignal.put(position, rssi);
        notifyItemRangeChanged(0, mBtDevices.size());
    }

    public boolean isDeviceContainsInArray(BluetoothDevice device) {
        for (BluetoothDevice btd : mBtDevices) {
            if (btd.getAddress().equals(device.getAddress())) {
                return true;
            }
        }
        return false;
    }

    public void clearAdapter() {
        int size = mBtDevices.size();
        mBtDevices.clear();
        mBtDevicesSignal.clear();
        notifyItemRangeRemoved(0, size);
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_device_info, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        final BluetoothDevice device = mBtDevices.get(position);
        if (device.getName() != null && !device.getName().equals("")) {
            holder.mDeviceName.setText(device.getName());
        }
        else {
            holder.mDeviceName.setText("No name");
        }
        holder.mDeviceMac.setText(device.getAddress());
        holder.mDeviceSignalLevel
                .setText(mBtDevicesSignal.get(position) + " dB");
        holder.mItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onDeviceClicked(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBtDevices.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private View mItemView;
        private TextView mDeviceName;
        private TextView mDeviceMac;
        private TextView mDeviceSignalLevel;

        Holder(View itemView) {
            super(itemView);
            mDeviceName = (TextView) itemView
                    .findViewById(R.id.tv_device_name);
            mDeviceMac = (TextView) itemView
                    .findViewById(R.id.tv_device_mac);
            mDeviceSignalLevel = (TextView) itemView
                    .findViewById(R.id.tv_device_signal_level);
            mItemView = itemView;
        }
    }

    public interface Callback {
        void onDeviceClicked(BluetoothDevice device);
    }
}
