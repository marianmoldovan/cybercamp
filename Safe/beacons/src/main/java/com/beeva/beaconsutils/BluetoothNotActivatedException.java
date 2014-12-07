package com.beeva.beaconsutils;

/**
 * Created by marianclaudiu on 5/11/14.
 */
public class BluetoothNotActivatedException extends Exception {
    public BluetoothNotActivatedException() {
        super("Bluetooth not activated on this device");
    }
}
