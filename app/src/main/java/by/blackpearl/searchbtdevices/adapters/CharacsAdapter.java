package by.blackpearl.searchbtdevices.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.blackpearl.searchbtdevices.R;

/**
 * Created by yauheni.
 */

public class CharacsAdapter extends RecyclerView.Adapter<CharacsAdapter.Holder> {

    private static final byte[] DEFAULT_VALUE = {(byte) 0, (byte) 1};
    private final Callback mCallback;
    private ArrayList<BluetoothGattCharacteristic> mCharacteristics;

    public CharacsAdapter(Callback callback) {
        mCallback = callback;
        mCharacteristics = new ArrayList<>();
    }

    public void setCharacteristics(List<BluetoothGattCharacteristic> characteristic) {
        int size = mCharacteristics.size();
        mCharacteristics.clear();
        notifyItemRangeRemoved(0, size);
        mCharacteristics.addAll(characteristic);
        notifyItemRangeInserted(0, mCharacteristics.size());
    }

    @Override
    public CharacsAdapter.Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_list_sevice_characs, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final int pos = position;
        holder.mCharacsUuidTv.setText(mCharacteristics
                .get(position).getUuid().toString());
        mCallback.readCharacteristic(mCharacteristics.get(0));
        holder.mBtnSendIb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCharacteristics.get(pos).setValue(DEFAULT_VALUE)) {
                    Toast.makeText(holder.itemView.getContext(),
                            "Value not setted.", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCallback.applySetValue(mCharacteristics.get(pos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCharacteristics.size();
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final TextView mCharacsUuidTv;
        private final ImageButton mBtnSendIb;

        Holder(View itemView) {
            super(itemView);
            mCharacsUuidTv = (TextView) itemView
                    .findViewById(R.id.tv_characteristic);
            mBtnSendIb = (ImageButton) itemView.findViewById(R.id.ib_send_byte);
        }
    }
    public interface Callback {
        void applySetValue(BluetoothGattCharacteristic bluetoothGattCharacteristic);

        void readCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic);
    }
}
