package by.blackpearl.searchbtdevices.adapters;

import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.blackpearl.searchbtdevices.DeviceServicesActivity;
import by.blackpearl.searchbtdevices.R;

/**
 * Created by yauheni.
 */

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.Holder> {


    private List<BluetoothGattService> mServices;

    public ServicesAdapter() {
        mServices = new ArrayList<>();
    }

    public void setServices(List<BluetoothGattService> services) {
        int size = mServices.size();
        mServices.clear();
        notifyItemRangeRemoved(0, size);
        mServices = services;
        notifyItemRangeInserted(0, mServices.size());
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_device_service, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.mServiceNameTv.setText(mServices.get(position).getUuid().toString());
        Context ctx = holder.itemView.getContext();
        holder.itemView.setOnClickListener(getOnClickListener(ctx, position));
    }

    @Override
    public int getItemCount() {
        return mServices.size();
    }

    private View.OnClickListener getOnClickListener(final Context ctx,
                                                    final int position) {
        if (ctx instanceof DeviceServicesActivity) {
            return new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((DeviceServicesActivity) ctx).readCharacteristics(mServices.get(position));
                }
            };
        }
        else {
            return null;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final TextView mServiceNameTv;

        Holder(View itemView) {
            super(itemView);
            mServiceNameTv = (TextView) itemView.findViewById(R.id.tv_service_name);
        }
    }
}
