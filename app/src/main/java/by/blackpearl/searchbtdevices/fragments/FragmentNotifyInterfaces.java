package by.blackpearl.searchbtdevices.fragments;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by yauheni.
 */

public interface FragmentNotifyInterfaces {

    interface IFragmentServices {
        void onDeviceConnected();
        void notifyData(List<BluetoothGattService> services);
    }

    interface IFragmentCharacteristics {
        void notifyData(List<BluetoothGattCharacteristic> characteristic);
    }
}
