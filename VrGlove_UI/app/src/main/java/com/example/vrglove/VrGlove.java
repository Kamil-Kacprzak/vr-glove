package com.example.vrglove;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
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

    private enum DataType{
        GYRO(0),
        ACC(1),
        FINGERS(2);

        private int value = -1;

        private DataType(int value){
            this.value=value;
        }
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
//
//    public static void getGyroReadings() {
//        TextView x =  vw.findViewById(R.id.textView_gyr_X);
//        TextView y =  vw.findViewById(R.id.textView_gyr_Y);
//        TextView z =  vw.findViewById(R.id.textView_gyr_Z);
//
//        float f = ByteBuffer.wrap(gyroReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        x.setText(String.format("X:%s",f));
//        f = ByteBuffer.wrap(gyroReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        y.setText(String.format("Y:%s",f));
//        f = ByteBuffer.wrap(gyroReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        z.setText(String.format("Z:%s",f));
//    }
//
//    public static void getAccReadings() {
//        TextView x =  vw.findViewById(R.id.textView_acc_X);
//        TextView y =  vw.findViewById(R.id.textView_acc_Y);
//        TextView z =  vw.findViewById(R.id.textView_acc_Z);
//
//        float f = ByteBuffer.wrap(accReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        x.setText(String.format("X:%s",f));
//        f = ByteBuffer.wrap(accReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        y.setText(String.format("Y:%s",f));
//        f = ByteBuffer.wrap(accReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        z.setText(String.format("Z:%s",f));
//
//    }
//
//    public static void getFingersReadings() {
//        TextView thumb =  vw.findViewById(R.id.textView_thumb_reading);
//        TextView index =  vw.findViewById(R.id.textView_index_reading);
//        TextView middle =  vw.findViewById(R.id.textView_middle_reading);
//        TextView ring =  vw.findViewById(R.id.textView_ring_reading);
//        TextView pinky =  vw.findViewById(R.id.textView_pinky_reading);
//
//        float f = ByteBuffer.wrap(fingersReadings,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        thumb.setText(String.format("%s",f));
//        f = ByteBuffer.wrap(fingersReadings,4,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        index.setText(String.format("%s",f));
//        f = ByteBuffer.wrap(fingersReadings,8,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        middle.setText(String.format("%s",f));
//        f = ByteBuffer.wrap(fingersReadings,12,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        ring.setText(String.format("%s",f));
//        f = ByteBuffer.wrap(fingersReadings,16,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
//        pinky.setText(String.format("%s",f));
//    }
//

    public static void getGyroReadings() {
        List<TextView> lGyroReadings= new ArrayList<>();

        lGyroReadings.add((TextView) vw.findViewById(R.id.textView_gyr_X));
        lGyroReadings.add((TextView) vw.findViewById(R.id.textView_gyr_Y));
        lGyroReadings.add((TextView) vw.findViewById(R.id.textView_gyr_Z));

        if (!updateFieldsData(lGyroReadings, DataType.ACC)){
            Log.e("VRGloveData ","Unkown call to update data from gyroscope");
        }
    }

    public static void getAccReadings() {
        List<TextView> lAccReadings= new ArrayList<>();

        lAccReadings.add((TextView) vw.findViewById(R.id.textView_acc_X));
        lAccReadings.add((TextView) vw.findViewById(R.id.textView_acc_Y));
        lAccReadings.add((TextView) vw.findViewById(R.id.textView_acc_Z));

        if (!updateFieldsData(lAccReadings, DataType.ACC)){
            Log.e("VRGloveData ","Unkown call to update data from accelerometer");
        }
    }

    public static void getFingersReadings() {
        List<TextView> lFingersViews = new ArrayList<>();

        lFingersViews.add((TextView) vw.findViewById(R.id.textView_thumb_reading));
        lFingersViews.add((TextView) vw.findViewById(R.id.textView_index_reading));
        lFingersViews.add((TextView) vw.findViewById(R.id.textView_middle_reading));
        lFingersViews.add((TextView) vw.findViewById(R.id.textView_ring_reading));
        lFingersViews.add((TextView) vw.findViewById(R.id.textView_pinky_reading));

        if (!updateFieldsData(lFingersViews, DataType.FINGERS)){
            Log.e("VRGloveData ","Unkown call to update data from fingers");
        }
    }

    private static boolean updateFieldsData(List<TextView> kFieldsData, DataType type){
        byte[] data;
        String[] sPrefixes = null;
        int offset = 0;

        switch (type) {
            case GYRO:
                data = gyroReadings;
                sPrefixes = new String[] {"X:","Y:","Z:"};
                break;
            case ACC:
                data = accReadings;
                sPrefixes = new String[]{"X:", "Y:", "Z:"};
            case FINGERS:
                data = fingersReadings;
                break;
            default:
                return false;
        }

        for( int i = 0; i < kFieldsData.size(); i++){
            float  f = ByteBuffer.wrap(data, offset,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            if(sPrefixes != null) {
                kFieldsData.get(i).setText(String.format(sPrefixes[i]+"%s",f));
            }else {
                kFieldsData.get(i).setText(String.format("%s",f));
            }
            offset +=4;
        }
        return true;
    }
}
