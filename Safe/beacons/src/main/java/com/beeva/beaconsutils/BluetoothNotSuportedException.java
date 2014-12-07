package com.beeva.beaconsutils;

/**
 * Created by marianclaudiu on 5/11/14.
 */
public class BluetoothNotSuportedException extends Exception {
    public BluetoothNotSuportedException() {
        super("Bluetooth not supported on this device");
    }
}
