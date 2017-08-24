package by.blackpearl.searchbtdevices;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

import by.blackpearl.searchbtdevices.fragments.CharacteristicsFragment;
import by.blackpearl.searchbtdevices.fragments.FragmentNotifyInterfaces;
import by.blackpearl.searchbtdevices.fragments.ServiceNameFragment;

public class DeviceServicesActivity extends AppCompatActivity {

    public static final String BT_ADDRESS = "bt_address";
    private BluetoothDevice mConnDevice;
    private BluetoothGatt mBtGatt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_services);

        String address = getIntent().getExtras().getString(BT_ADDRESS);
        if (address == null) {
            finish();
            Toast.makeText(getApplicationContext(), "BT address is null!", Toast.LENGTH_SHORT)
                    .show();
            return;
        }
        BluetoothManager manager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter btAdapter = manager.getAdapter();
        mConnDevice = btAdapter.getRemoteDevice(address);
        Fragment currentFragment;
        if (getLastCustomNonConfigurationInstance() != null) {
            mBtGatt = (BluetoothGatt) getLastCustomNonConfigurationInstance();
        }
        else {
            currentFragment = ServiceNameFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.container, currentFragment)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBtGatt == null) {
            mBtGatt = mConnDevice.connectGatt(this, false, getCallback());
        }
    }

    @Override
    protected void onPause() {
        if (!isChangingConfigurations()) {
            mBtGatt.disconnect();
            mBtGatt.close();
            mBtGatt = null;
        }
        super.onPause();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mBtGatt;
    }

    private BluetoothGattCallback getCallback() {
        return new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                int newState) {
                switch (newState) {
                    case BluetoothProfile.STATE_CONNECTED:
                        runOnUiThread(getStateConnectRunnable());
                        break;
                    case BluetoothProfile.STATE_DISCONNECTED:
                        runOnUiThread(getStateDisconnectRunnable());
                        DeviceServicesActivity.this.finish();
                        break;
                }
            }

            private Runnable getStateConnectRunnable() {
                return new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "Connected to device", Toast.LENGTH_SHORT)
                                .show();
                        Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                        if (f instanceof FragmentNotifyInterfaces.IFragmentServices) {
                            ((FragmentNotifyInterfaces.IFragmentServices) f).onDeviceConnected();
                        }
                    }
                };
            }

            private Runnable getStateDisconnectRunnable() {
                return new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "Disconnected from device",
                                Toast.LENGTH_SHORT).show();
                    }
                };
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                    if (f instanceof FragmentNotifyInterfaces.IFragmentServices) {
                        runOnUiThread(getServicesRunnable(gatt));
                    }
                } else {
                    Log.i(this.getClass().getSimpleName(), "Status at service not success.");
                }
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt,
                                             BluetoothGattCharacteristic characteristic,
                                             int status) {
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                } else {
                    Log.i(this.getClass().getSimpleName(), "Status at characteristic not success.");
                }
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt,
                                              BluetoothGattCharacteristic characteristic,
                                              int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                if (status == BluetoothGatt.GATT_SUCCESS) {
                    Toast.makeText(DeviceServicesActivity.this.getBaseContext(),
                            "Characteristic was wrote.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(DeviceServicesActivity.this.getBaseContext(),
                            "Characteristic was not wrote.", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    public void readServices() {
        mBtGatt.discoverServices();
    }

    public void readCharacteristics(BluetoothGattService bluetoothGattService) {
        List<BluetoothGattCharacteristic> characters = bluetoothGattService.getCharacteristics();
        Fragment f = CharacteristicsFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, f)
                .addToBackStack("second").commit();
        ((FragmentNotifyInterfaces.IFragmentCharacteristics) f).notifyData(characters);
    }

    public void readCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        mBtGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    public void applyCharacteristicSetValue(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        if (!mBtGatt.writeCharacteristic(bluetoothGattCharacteristic)) {
            Toast.makeText(getBaseContext(), "Set value not applyed.", Toast.LENGTH_SHORT).show();
        }
    }

    public Runnable getServicesRunnable(final BluetoothGatt gatt) {
        return new Runnable() {
            @Override
            public void run() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                ((FragmentNotifyInterfaces.IFragmentServices) f).notifyData(gatt.getServices());
            }
        };
    }
}
