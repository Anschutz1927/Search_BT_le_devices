package by.blackpearl.searchbtdevices.fragments;

import android.bluetooth.BluetoothGattService;
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
import by.blackpearl.searchbtdevices.adapters.ServicesAdapter;

public class ServiceNameFragment extends Fragment
        implements FragmentNotifyInterfaces.IFragmentServices {

    private ServicesAdapter mAdapter;

    public ServiceNameFragment() {
    }

    public static ServiceNameFragment newInstance() {
        return new ServiceNameFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mAdapter = new ServicesAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_service_name, container, false);
        RecyclerView rv = (RecyclerView) v.findViewById(R.id.rv_services);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(mAdapter);
        return v;
    }

    @Override
    public void onDeviceConnected() {
        if (getActivity() instanceof DeviceServicesActivity) {
            ((DeviceServicesActivity) getActivity()).readServices();
        }
    }

    @Override
    public void notifyData(List<BluetoothGattService> services) {
        mAdapter.setServices(services);
    }

}
