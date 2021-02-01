package com.ewrite.ewrite_sdk.bean;

import android.bluetooth.BluetoothDevice;

import com.ble.ble.LeScanRecord;

public class EwriteLeDevice {
    private final String address;
    private String name;
    private String rxData = "No data";
    private LeScanRecord mRecord;
    private int rssi;
    private boolean oadSupported = false;
    private boolean isConnection = false;
    private BluetoothDevice device;

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public EwriteLeDevice(BluetoothDevice device, String name, String address) {
        this.name = name;
        this.address = address;
        this.device = device;
    }

    public EwriteLeDevice(String name, String address, int rssi, byte[] scanRecord) {
        this.name = name;
        this.address = address;
        this.rssi = rssi;
        this.mRecord = LeScanRecord.parseFromBytes(scanRecord);
    }

    public boolean isOadSupported() {
        return oadSupported;
    }

    public void setOadSupported(boolean oadSupported) {
        this.oadSupported = oadSupported;
    }

    public LeScanRecord getLeScanRecord() {
        return mRecord;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getRxData() {
        return rxData;
    }

    public void setRxData(String rxData) {
        this.rxData = rxData;
    }

    public boolean isConnection() {
        return isConnection;
    }

    public void setConnection(boolean connection) {
        isConnection = connection;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EwriteLeDevice) {
            return ((EwriteLeDevice) o).getAddress().equals(address);
        }
        return false;
    }
}