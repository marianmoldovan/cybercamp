package com.beeva.beaconsutils;

import java.util.Collection;
import java.util.List;

import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;

public interface BeaconListener {
    void onBeaconScanFinished(List<IBeaconDevice> beacons);
    void noBeaconDevicesFound();
    void onNewBeaconFound(IBeaconDevice beacon);
    void onUpdatedBeaconFound(IBeaconDevice beacon);
}
