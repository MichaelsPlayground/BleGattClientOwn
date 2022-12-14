package de.androidcrypto.blegattclientown;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    BluetoothManager bluetoothManager;
    BluetoothAdapter bluetoothAdapter;

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    String macAddressFromScan = ""; // will get filled by Intent from DeviceScanActivity

    /**
     * This block is for requesting permissions up to Android 12+
     *
     */

    private static final int PERMISSIONS_REQUEST_CODE = 191;
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        // inflate option menu
        Toolbar myToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(myToolbar);

         */
        requestBlePermissions(this, PERMISSIONS_REQUEST_CODE);

        // Check if BLE is supported on the device.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this,
                    "BLUETOOTH_LE not supported in this device!",
                    Toast.LENGTH_SHORT).show();
            finish();
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // https://developer.android.com/guide/topics/connectivity/bluetooth/ble-overview

        Button scan = findViewById(R.id.btnMainScanForLeDevices);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivityOwn.class);
                startActivity(intent);
            }
        });

        Button  startLeGattService = findViewById(R.id.btnMainStartLeGattClient);
        startLeGattService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                startActivity(intent);
            }
        });

        // receive the address from DeviceListOwnActivity, if we receive data run the connection part
        Intent incommingIntent = getIntent();
        Bundle extras = incommingIntent.getExtras();
        if (extras != null) {
            macAddressFromScan = extras.getString(EXTRA_DEVICE_ADDRESS); // retrieve the data using keyName
            System.out.println("Main received data: " + macAddressFromScan);
            try {
                if (!macAddressFromScan.equals("")) {
                    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    System.out.println("MAC address received: " + macAddressFromScan + " ... try to connect with...");
                    //appendLog("MAC address received: " + macAddressFromScan + " ... try to connect with...");
                    // Get the BluetoothDevice object
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddressFromScan);
                    //ConnectThread connect = new ConnectThread(device, MY_UUID);
                    //connect.start();


                    // https://developer.android.com/guide/topics/connectivity/bluetooth/connect-gatt-server


                }
            } catch (NullPointerException e) {
                // do nothing, there are just no data
            }
        }
    }

}