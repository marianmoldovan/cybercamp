package com.beeva.beaconsutils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.Collection;
import java.util.List;

import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public abstract class BeaconManager {

    protected static final long SCAN_PERIOD = 5000;
    protected static final long PAUSE_PERIOD = 25000;

    private BeaconListener beaconListener;
    private Context context;

    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    public BeaconManager(Context context) throws BluetoothNotSuportedException, BluetoothLENotSuportedException, BluetoothNotActivatedException {
        if(!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))
            throw new BluetoothNotSuportedException();
        this.context = context;
        bluetoothManager =(BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null)
            throw new BluetoothNotSuportedException();
        if (!bluetoothAdapter.isEnabled())
            throw new BluetoothNotActivatedException();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2 ||
                !context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
            throw new BluetoothLENotSuportedException();
    }

    public static BeaconManager createBeaconManager(Context context) throws BluetoothNotActivatedException, BluetoothLENotSuportedException, BluetoothNotSuportedException {
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return new BeaconManagerLollipop(context);
        else return new BeaconManagerJellyBean(context);
    }

    /**
     * Start periodicall scanning of Beacons
     */
    public abstract void startPeriodicallScan();

    /**
     * Start psingle scan of nearby Beacons. Scanning lasts for 5s.
     */

    public abstract void startSingleScan();

    public abstract void stopScanning();

    public abstract boolean isScanning();

    public abstract List<IBeaconDevice> getAllBeaconSeen();

    public Context getContext() {
        return context;
    }

    public BluetoothManager getBluetoothManager() {
        return bluetoothManager;
    }

    public BluetoothAdapter getBluetoothAdapter() {
        return bluetoothAdapter;
    }

    public BeaconListener getBeaconListener() {
        return beaconListener;
    }

    public void setBeaconListener(BeaconListener beaconListener) {
        this.beaconListener = beaconListener;
    }

}
