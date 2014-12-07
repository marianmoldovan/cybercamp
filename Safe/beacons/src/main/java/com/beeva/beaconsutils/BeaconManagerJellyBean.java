package com.beeva.beaconsutils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;
import uk.co.alt236.bluetoothlelib.util.IBeaconUtils;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BeaconManagerJellyBean extends BeaconManager{

    private BeaconScanCallback scanCallback;
    private BeaconScanStopRunnable beaconScanStopRunnable;
    private BeaconScanStartRunnable beaconScanStartRunnable;
    private AtomicBoolean scanning;
    private Handler handler;

    private Map<String, IBeaconDevice> temporalBeaconMap;
    private Map<String, IBeaconDevice> beaconMap;

    /**
     *
     * Constructor for Beacon Manager
     *
     * @param context
     * @throws com.beeva.beaconsutils.BluetoothNotActivatedException
     * @throws com.beeva.beaconsutils.BluetoothNotSuportedException
     * @throws com.beeva.beaconsutils.BluetoothLENotSuportedException
     */
    public BeaconManagerJellyBean(Context context) throws BluetoothNotSuportedException, BluetoothLENotSuportedException, BluetoothNotActivatedException {
        super(context);
        beaconMap = new HashMap<String, IBeaconDevice>();
        scanCallback = new BeaconScanCallback();
        beaconScanStopRunnable = new BeaconScanStopRunnable();
        beaconScanStartRunnable = new BeaconScanStartRunnable();
        handler = new Handler();
        scanning = new AtomicBoolean(false);
    }

    class BeaconScanCallback implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
            if(IBeaconUtils.isThisAnIBeacon(deviceLe)){
                if(!temporalBeaconMap.containsKey(device.getAddress())) {
                    IBeaconDevice iBeaconDevice = new IBeaconDevice(deviceLe);
                    if("ddf86afc-6404-11e4-b116-123b93f75cba".equals(iBeaconDevice.getUUID())) {
                        temporalBeaconMap.put(device.getAddress(), iBeaconDevice);
                        if (getBeaconListener() != null)
                            getBeaconListener().onNewBeaconFound(iBeaconDevice);
                    }
                }
                else{
                    IBeaconDevice iBeaconDevice = temporalBeaconMap.get(device.getAddress());
                    iBeaconDevice.updateRssiReading(deviceLe.getTimestamp(), deviceLe.getRssi());
                    if(getBeaconListener() != null) getBeaconListener().onUpdatedBeaconFound(iBeaconDevice);
                }
            }
        }

    }

    class BeaconScanStopRunnable implements Runnable {

        @Override
        public void run() {
            getBluetoothAdapter().stopLeScan(scanCallback);
            scanning.set(false);
            handler.postDelayed(beaconScanStartRunnable, PAUSE_PERIOD);

            if(getBeaconListener() != null){
                if(temporalBeaconMap.size() > 0 ) getBeaconListener().onBeaconScanFinished(new ArrayList<IBeaconDevice>(temporalBeaconMap.values()));
                else getBeaconListener().noBeaconDevicesFound();
            }
            beaconMap.putAll(temporalBeaconMap);
        }

    }

    class BeaconScanStartRunnable implements Runnable {

        @Override
        public void run() {
            proceedScanning();
            handler.postDelayed(beaconScanStopRunnable, SCAN_PERIOD);
        }
    }

    /**
     * Start periodicall scanning of Beacons
     */
    public void startPeriodicallScan() {
        proceedScanning();
        handler.postDelayed(beaconScanStopRunnable, SCAN_PERIOD);
    }

    private void proceedScanning() {
        temporalBeaconMap = new HashMap<String, IBeaconDevice>();
        scanning.set(true);
        getBluetoothAdapter().startLeScan(scanCallback);
    }

    /**
     * Start psingle scan of nearby Beacons. Scanning lasts for 5s.
     */

    public void startSingleScan() {
        temporalBeaconMap = new HashMap<String, IBeaconDevice>();
        scanning.set(true);
        getBluetoothAdapter().startLeScan(scanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getBluetoothAdapter().stopLeScan(scanCallback);
                scanning.set(false);
                if(getBeaconListener() != null){
                    if(temporalBeaconMap.size() > 0 ) getBeaconListener().onBeaconScanFinished(new ArrayList<IBeaconDevice>(temporalBeaconMap.values()));
                    else getBeaconListener().noBeaconDevicesFound();
                }
            }
        }, SCAN_PERIOD * 2);
    }

    public void stopScanning() {
        if(scanning.get()) {
            getBluetoothAdapter().stopLeScan(scanCallback);
        }
        handler.removeCallbacks(beaconScanStartRunnable);
        handler.removeCallbacks(beaconScanStopRunnable);
        scanning.set(false);
    }

    public boolean isScanning(){
        return scanning.get();
    }


    public List<IBeaconDevice> getAllBeaconSeen() {
        return new ArrayList<IBeaconDevice>(beaconMap.values());
    }
}
