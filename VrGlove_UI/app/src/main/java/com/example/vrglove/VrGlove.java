package com.example.vrglove;

import android.bluetooth.BluetoothDevice;

public class VrGlove {

    public static BluetoothDevice getDevice() {
        return device;
    }

    private static BluetoothDevice device;

    public VrGlove(){

    }
    public VrGlove(BluetoothDevice device){
        this.device = device;
    }
}
