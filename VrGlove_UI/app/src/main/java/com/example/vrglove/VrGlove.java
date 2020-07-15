package com.example.vrglove;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.view.View;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;

public class VrGlove {

    public static BluetoothDevice getDevice() {
        return device;
    }

    private static BluetoothDevice device;
    private static BluetoothGatt gatt;
    private static int gattState;
    private volatile static boolean mIsStateChanged;
    private volatile static boolean mIsFingersReadings;
    private static List<BluetoothGattService> services;
    private static byte[] gyroReadings; //2102
    private static byte[] accReadings; //2101
    private static byte[] fingersReadings; //2103
    private static View vw;


    private volatile static HashMap<String,Float[]> dataSet = new HashMap<>();

     VrGlove(BluetoothDevice device, View vw){
         VrGlove.device = device;
         VrGlove.vw = vw;
        VrGlove.mIsStateChanged =false;
         VrGlove.mIsFingersReadings = false;
    }

     static BluetoothGatt getGatt() {
        return gatt;
    }

     static void setGatt(BluetoothGatt gatt) {
        VrGlove.gatt = gatt;
    }

     static int getGattState() {
        return gattState;
    }

     static void setGattState(int gattState) {
        VrGlove.gattState = gattState;
    }

    static List<BluetoothGattService> getServices() {
        return services;
    }

     static void setServices(List<BluetoothGattService> services) {
        VrGlove.services = services;
    }

     static void setGyroReadings(byte[] gyroReadings) {
        VrGlove.gyroReadings = gyroReadings;
        getGyroReadings();
    }

     static void setAccReadings(byte[] accReadings) {
        VrGlove.accReadings = accReadings;
        getAccReadings();
    }

     static void setFingersReadings(byte[] fingersReadings) {
        VrGlove.fingersReadings = fingersReadings;
        getFingersReadings();
    }

    private static void getGyroReadings() {
        TextView x =  vw.findViewById(R.id.textView_gyr_X);
        TextView y =  vw.findViewById(R.id.textView_gyr_Y);
        TextView z =  vw.findViewById(R.id.textView_gyr_Z);

        Float[] data = new Float[3];

        float f = ByteBuffer.wrap(gyroReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[0] = f;
        x.setText(String.format("X:%.2f",f));
        f = ByteBuffer.wrap(gyroReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[1] = f;
        y.setText(String.format("Y:%.2f",f));
        f = ByteBuffer.wrap(gyroReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        z.setText(String.format("Z:%.2f",f));
        data[2] = f;

        dataSet.put("Gyro",data);
        setmIsStateChanged(true);
    }

    private static void getAccReadings() {
        ModelRenderer.setCurrentTimestamp(System.nanoTime());
        TextView x =  vw.findViewById(R.id.textView_acc_X);
        TextView y =  vw.findViewById(R.id.textView_acc_Y);
        TextView z =  vw.findViewById(R.id.textView_acc_Z);

        Float[] data = new Float[3];


        float f = ByteBuffer.wrap(accReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[0] = f;
        x.setText(String.format("X:%.2f",f));
         f = ByteBuffer.wrap(accReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[1] = f;
         y.setText(String.format("Y:%.2f",f));
         f = ByteBuffer.wrap(accReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[2] = f;
         z.setText(String.format("Z:%.2f",f));

        dataSet.put("Acc",data);
        setmIsStateChanged(true);
    }

    private static void getFingersReadings() {
        TextView thumb =  vw.findViewById(R.id.textView_thumb_reading);
        TextView index =  vw.findViewById(R.id.textView_index_reading);
        TextView middle =  vw.findViewById(R.id.textView_middle_reading);
        TextView ring =  vw.findViewById(R.id.textView_ring_reading);
        TextView pinky =  vw.findViewById(R.id.textView_pinky_reading);

        Float[] data = new Float[5];

        float f = ByteBuffer.wrap(fingersReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        thumb.setText(String.format("%.0f",f));
        data[0] = f;
        f = ByteBuffer.wrap(fingersReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        index.setText(String.format("%.0f",f));
        data[1] = f;
        f = ByteBuffer.wrap(fingersReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        middle.setText(String.format("%.0f",f));
        data[2] = f;
        f = ByteBuffer.wrap(fingersReadings,12,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[3] = f;
        ring.setText(String.format("%.0f",f));
        f = ByteBuffer.wrap(fingersReadings,16,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        data[4] = f;
        pinky.setText(String.format("%.0f",f));

        //Thumb,Index,Middle,Ring,Pinky
        dataSet.put("Fingers",data);
        setmIsFingersReadings(true);
    }

    public static boolean ismIsStateChanged() {
        return mIsStateChanged;
    }

    public static void setmIsStateChanged(boolean mIsStateChanged) {
        VrGlove.mIsStateChanged = mIsStateChanged;
    }

    public static boolean ismIsFingersReadings() {
        return mIsFingersReadings;
    }

    public static void setmIsFingersReadings(boolean mIsFingersReadings) {
        VrGlove.mIsFingersReadings = mIsFingersReadings;
    }

    public static HashMap<String, Float[]> getDataSet() {
        return dataSet;
    }
}
