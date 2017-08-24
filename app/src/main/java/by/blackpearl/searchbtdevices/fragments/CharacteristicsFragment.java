package by.blackpearl.searchbtdevices.fragments;


import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import by.blackpearl.searchbtdevices.DeviceServicesActivity;
import by.blackpearl.searchbtdevices.R;
import by.blackpearl.searchbtdevices.adapters.CharacsAdapter;


public class CharacteristicsFragment extends Fragment
        implements FragmentNotifyInterfaces.IFragmentCharacteristics {

    private final CharacsAdapter.Callback mAdapterCallback;
    private CharacsAdapter mCharacsAdapter;

    public CharacteristicsFragment() {
        mAdapterCallback = new CharacsAdapter.Callback() {
            @Override
            public void applySetValue(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                if (getActivity() instanceof DeviceServicesActivity) {
                    ((DeviceServicesActivity) getActivity())
                            .applyCharacteristicSetValue(bluetoothGattCharacteristic);
                }
            }

            @Override
            public void readCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
                if (getActivity() instanceof DeviceServicesActivity) {
                    ((DeviceServicesActivity) getActivity())
                            .readCharacteristic(bluetoothGattCharacteristic);
                }
            }
        };
        mCharacsAdapter = new CharacsAdapter(mAdapterCallback);
    }

    public static CharacteristicsFragment newInstance() {
        return new CharacteristicsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater
                .inflate(R.layout.fragment_characteristics, container, false);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv_characs);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mCharacsAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void notifyData(List<BluetoothGattCharacteristic> characteristic) {
        mCharacsAdapter.setCharacteristics(characteristic);
    }
}
