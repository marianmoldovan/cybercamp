package com.beeva.beaconsutils;

/**
 * Created by marianclaudiu on 5/11/14.
 */
public class BluetoothLENotSuportedException extends Exception {
    public BluetoothLENotSuportedException() {
        super("Bluetooth LE Not supported on this device");
    }
}
