package com.gipsyz.safe.dto;

import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;

/**
 * Created by batman on 06/12/2014.
 */
public class SimpleBeacon {

    private String id;
    private String uuid;
    private int major, minor;

    public SimpleBeacon(IBeaconDevice device){
        this.uuid = device.getUUID();
        this.major = device.getMajor();
        this.minor = device.getMinor();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
