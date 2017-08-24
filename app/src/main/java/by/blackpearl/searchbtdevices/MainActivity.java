package by.blackpearl.searchbtdevices;

import android.Manifest;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import by.blackpearl.searchbtdevices.adapters.DevicesAdapter;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    private static final int TIME_SEARCH_DELAY = 20000;
    private static final int REQUEST_ENABLE_BT = 17;
    private BluetoothAdapter mBtAdapter;
    private BluetoothAdapter.LeScanCallback mLeScanCallback;
    private ScanCallback mScanCallback;
    private ArrayList<ScanFilter> mFilterList;
    private ScanSettings mSettings;
    private boolean mIsScanOnProcess = false;
    private DevicesAdapter mDevicesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions();
        }
        BluetoothManager btManager = (BluetoothManager)
                getSystemService(BLUETOOTH_SERVICE);
        mBtAdapter = getBtAdapter(btManager);
        findViewById(R.id.btn_search).setOnClickListener(this);
        RecyclerView rv = (RecyclerView) findViewById(R.id.rv_bt_list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        if (getLastCustomNonConfigurationInstance() instanceof SaverHelper) {
            SaverHelper saver = (SaverHelper) getLastCustomNonConfigurationInstance();
            mDevicesAdapter = saver.savedAdapter;
            mScanCallback = saver.savedScanClbck;
            mLeScanCallback = saver.savedLeScanClbck;
        }
        else{
            mDevicesAdapter = new DevicesAdapter(getDeviceAdapterCallback());
        }
        initialize();
        rv.setAdapter(mDevicesAdapter);
    }

    private void initialize() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this.getApplicationContext(), "BLE Not Supported!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 21) {
            if (mScanCallback == null) {
                mScanCallback = getScanCallback();
            }
            mFilterList = new ArrayList<>();
            mSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
        }
        else {
            if (mLeScanCallback == null) {
                mLeScanCallback = getLeScanCallback();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
            Intent enableBtRequest = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtRequest, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onPause() {
        if (mIsScanOnProcess && !isChangingConfigurations()) {
            stopSearchBtLeDevices();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this.getApplicationContext(), "BT NOT enabled!",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return new SaverHelper(mDevicesAdapter, mScanCallback, mLeScanCallback);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_search:
                startSearchBtLeDevices();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode != 17) {
            return;
        }
        for (int i : grantResults) {
            if (i != PermissionChecker.PERMISSION_GRANTED) {
                Toast.makeText(this.getApplicationContext(), "Permissions denied!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]
                {
                        Manifest.permission.BLUETOOTH,
                        Manifest.permission.BLUETOOTH_ADMIN,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 17);
    }

    private BluetoothAdapter getBtAdapter(BluetoothManager btManager) {
        BluetoothAdapter adapter;
        if (Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.JELLY_BEAN_MR2) {
            adapter = btManager.getAdapter();
        }
        else {
            adapter = BluetoothAdapter.getDefaultAdapter();
        }
        return adapter;
    }

    private DevicesAdapter.Callback getDeviceAdapterCallback() {
        return new DevicesAdapter.Callback() {
            @Override
            public void onDeviceClicked(BluetoothDevice device) {
                Intent activityIntent = new Intent(MainActivity.this,
                        DeviceServicesActivity.class);
                activityIntent.putExtra(DeviceServicesActivity.BT_ADDRESS,
                        device.getAddress());
                startActivity(activityIntent);
            }
        };
    }

    private BluetoothAdapter.LeScanCallback getLeScanCallback() {
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi,
                                 byte[] scanRecord) {
                if (device != null && !mDevicesAdapter.isDeviceContainsInArray(device)) {
                    mDevicesAdapter.addNewDeviceToArray(device, rssi);
                    Toast.makeText(MainActivity.this, "Found: " + device.getName(),
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(MainActivity.this, "Found device: null", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.i("rssi", String.valueOf(rssi));
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ScanCallback getScanCallback() {
        return new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, ScanResult result) {
                super.onScanResult(callbackType, result);
                if (result != null && !mDevicesAdapter
                        .isDeviceContainsInArray(result.getDevice())) {
                    mDevicesAdapter.addNewDeviceToArray(result.getDevice(), result.getRssi());
                    Toast.makeText(MainActivity.this, "Found: " + result.getDevice().getName()
                            , Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
                for (ScanResult sr : results) {
                    Log.i("ScanResult - Results", sr.toString());
                }
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
                Log.e("Scan Failed", "Error Code: " + errorCode);
                Toast.makeText(MainActivity.this, "OnScanFailed: errCode = " + errorCode,
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void startSearchBtLeDevices() {
        if (mIsScanOnProcess) {
            stopSearchBtLeDevices();
        }
        mDevicesAdapter.clearAdapter();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                stopSearchBtLeDevices();
            }
        }, TIME_SEARCH_DELAY);
        if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18) {
            mBtAdapter.startLeScan(mLeScanCallback);
        }
        else if (Build.VERSION.SDK_INT >= 21) {
            mBtAdapter.getBluetoothLeScanner().startScan(mFilterList,
                    mSettings, mScanCallback);
        }
        mIsScanOnProcess = true;
    }

    private void stopSearchBtLeDevices() {
        if (Build.VERSION.SDK_INT < 21 && Build.VERSION.SDK_INT >= 18) {
            mBtAdapter.stopLeScan(mLeScanCallback);
        }
        else if (Build.VERSION.SDK_INT >= 21) {
            mBtAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
        }
        mIsScanOnProcess = false;
    }

    private class SaverHelper {
        private DevicesAdapter savedAdapter;
        private ScanCallback savedScanClbck;
        private BluetoothAdapter.LeScanCallback savedLeScanClbck;

        SaverHelper (DevicesAdapter da, ScanCallback sck,
                     BluetoothAdapter.LeScanCallback lesck) {
            savedAdapter = da;
            savedScanClbck = sck;
            savedLeScanClbck = lesck;
        }
    }
}