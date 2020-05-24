package com.example.vrglove;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class VrGlove {

    public static BluetoothDevice getDevice() {
        return device;
    }

    private static BluetoothDevice device;
    private static BluetoothGatt gatt;
    private static int gattState;
    private static boolean mIsStateChanged;
    private static List<BluetoothGattService> services;
    private static byte[] gyroReadings; //2102
    private static byte[] accReadings; //2101
    private static byte[] fingersReadings; //2103
    private static View vw;

    public VrGlove(){

    }
    public VrGlove(BluetoothDevice device, View vw){
        this.device = device;
        this.vw = vw;
        this.mIsStateChanged =false;
    }

    public static BluetoothGatt getGatt() {
        return gatt;
    }

    public static void setGatt(BluetoothGatt gatt) {
        VrGlove.gatt = gatt;
    }

    public static int getGattState() {
        return gattState;
    }

    public static void setGattState(int gattState) {
        VrGlove.gattState = gattState;
    }

    public static List<BluetoothGattService> getServices() {
        return services;
    }

    public static void setServices(List<BluetoothGattService> services) {
        VrGlove.services = services;
    }

    public static void setGyroReadings(byte[] gyroReadings) {
        VrGlove.gyroReadings = gyroReadings;
        getGyroReadings();
    }

    public static void setAccReadings(byte[] accReadings) {
        VrGlove.accReadings = accReadings;
        getAccReadings();
    }

    public static void setFingersReadings(byte[] fingersReadings) {
        VrGlove.fingersReadings = fingersReadings;
        getFingersReadings();
    }

    public static void getGyroReadings() {
        TextView x =  vw.findViewById(R.id.textView_gyr_X);
        TextView y =  vw.findViewById(R.id.textView_gyr_Y);
        TextView z =  vw.findViewById(R.id.textView_gyr_Z);

        float f = ByteBuffer.wrap(gyroReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        x.setText(String.format("X:%s",f));
        f = ByteBuffer.wrap(gyroReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        y.setText(String.format("Y:%s",f));
        f = ByteBuffer.wrap(gyroReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        z.setText(String.format("Z:%s",f));
    }

    public static void getAccReadings() {
        TextView x =  vw.findViewById(R.id.textView_acc_X);
        TextView y =  vw.findViewById(R.id.textView_acc_Y);
        TextView z =  vw.findViewById(R.id.textView_acc_Z);

        float f = ByteBuffer.wrap(accReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        x.setText(String.format("X:%s",f));
         f = ByteBuffer.wrap(accReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        y.setText(String.format("Y:%s",f));
         f = ByteBuffer.wrap(accReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        z.setText(String.format("Z:%s",f));

    }

    public static void getFingersReadings() {
        TextView thumb =  vw.findViewById(R.id.textView_thumb_reading);
        TextView index =  vw.findViewById(R.id.textView_index_reading);
        TextView middle =  vw.findViewById(R.id.textView_middle_reading);
        TextView ring =  vw.findViewById(R.id.textView_ring_reading);
        TextView pinky =  vw.findViewById(R.id.textView_pinky_reading);

        float f = ByteBuffer.wrap(fingersReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        thumb.setText(String.format("%s",f));
        f = ByteBuffer.wrap(fingersReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        index.setText(String.format("%s",f));
        f = ByteBuffer.wrap(fingersReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        middle.setText(String.format("%s",f));
        f = ByteBuffer.wrap(fingersReadings,12,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        ring.setText(String.format("%s",f));
        f = ByteBuffer.wrap(fingersReadings,16,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        pinky.setText(String.format("%s",f));
    }

    public static boolean ismIsStateChanged() {
        return mIsStateChanged;
    }

    public static void setmIsStateChanged(boolean mIsStateChanged) {
        VrGlove.mIsStateChanged = mIsStateChanged;
    }
}
