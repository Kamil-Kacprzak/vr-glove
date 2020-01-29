package com.example.vrglove;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTING;
import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_READ;
import static androidx.constraintlayout.widget.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GloveData.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GloveData#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GloveData extends Fragment
    implements View.OnClickListener{

    private OnFragmentInteractionListener mListener;
    private Queue<Runnable> commandQueue;

    private Handler bleHandler = new Handler();
    private boolean commandQueueBusy;
    private View vw;

    public GloveData() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters
     * @return A new instance of fragment GloveData.
     */
    public static GloveData newInstance() {
        GloveData fragment = new GloveData();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vw =  inflater.inflate(R.layout.fragment_main, container, false);

        Switch switchBt = vw.findViewById(R.id.switchBT);
        Button btConnect = vw.findViewById(R.id.buttonConnect);
        Button btDisconnect = vw.findViewById(R.id.buttonDisconnect);
        TextView tvStatus = vw.findViewById(R.id.textView_vrGlove_status);

        if(BluetoothAdapter.getDefaultAdapter().isEnabled()){
            switchBt.setChecked(true);
            tvStatus.setText("Ready to connect");
        }else{
            switchBt.setChecked(false);
            tvStatus.setText("Turn on bluetooth");
        }

        btConnect.setOnClickListener(this);
        btDisconnect.setOnClickListener(this);
        switchBt.setOnClickListener(this);
        return vw;
    }

        @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        Switch switchBT = v.findViewById(R.id.switchBT);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();;
        switch (v.getId()){
            case R.id.switchBT:
                if (switchBT.isChecked()){
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                    }
                }else{
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                    }
                }
                break;
            case R.id.buttonConnect:
                if(mBluetoothAdapter.isEnabled() && VrGlove.getGattState() != 2){
                    final BluetoothManager bluetoothManager =
                            (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                    mBluetoothAdapter = bluetoothManager.getAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        switchBT.setChecked(true);
                    }
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("D0:6B:F2:A7:95:03");
                    new VrGlove(device,vw);
                    int deviceType = device.getType();
                    BluetoothGatt gatt;
                    if(deviceType == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
                        // The peripheral is not cached
                        BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
                        scanner.startScan(scanCallback);
                        if(VrGlove.getDevice() != null){
                            scanner.stopScan(scanCallback);
                            gatt = VrGlove.getDevice().connectGatt(getActivity(),false,bluetoothGattCallback, TRANSPORT_LE);
                            VrGlove.setGatt(gatt);
                        }
                    } else {
                        gatt = VrGlove.getDevice().connectGatt(getActivity(), true, bluetoothGattCallback, TRANSPORT_LE);
                        VrGlove.setGatt(gatt);
                    }

                    if(VrGlove.getGattState() == 2){
                        VrGlove.getGatt().discoverServices();
                       while (VrGlove.getGatt().getServices().size() == 0 || VrGlove.getGatt().getServices().equals(null)){
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    VrGlove.getGatt().discoverServices();
                                }
                            }, 2000);
                       }
                        VrGlove.setServices(VrGlove.getGatt().getServices());
                        BluetoothGattCharacteristic mCharacteristic;
                        for (int i = 0x2101;i<0x2104;i++){
                            mCharacteristic = VrGlove.getServices().get(2).getCharacteristic(convertFromInteger(i));
                            readCharacteristic((mCharacteristic));
                        }
//                        readCharacteristic(VrGlove.getServices().get(2).getCharacteristic(convertFromInteger(0x2101)));
//                        readCharacteristic(VrGlove.getServices().get(2).getCharacteristic(convertFromInteger(0x2102)));
//                        readCharacteristic(VrGlove.getServices().get(2).getCharacteristic(convertFromInteger(0x2103)));
                    }

                }
                break;
            case R.id.buttonDisconnect:
                if(VrGlove.getGatt() != null && VrGlove.getGattState() == 2 ){
                    VrGlove.getGatt().disconnect();
                }
                break;

        }
    }

    private UUID convertFromInteger(int i) {
        final long MSB = 0x0000000000001000L;
        final long LSB = 0x800000805f9b34fbL;
        long value = i & 0xFFFFFFFF;
        return new UUID (MSB | (value << 32), LSB);
    }

    private boolean readCharacteristic(final BluetoothGattCharacteristic characteristic) {
        if(VrGlove.getGatt() == null) {
            Log.e(TAG, "ERROR: Gatt is 'null', ignoring read request");
            return false;
        }

        // Check if characteristic is valid
        if(characteristic == null) {
            Log.e(TAG, "ERROR: Characteristic is 'null', ignoring read request");
            return false;
        }

        // Check if this characteristic actually has READ property
        if((characteristic.getProperties() & PROPERTY_READ) == 0 ) {
            Log.e(TAG, "ERROR: Characteristic cannot be read");
            return false;
        }

        // Enqueue the read command now that all checks have been passed
        boolean result = commandQueue.add(new Runnable() {
            @Override
            public void run() {
                if(!VrGlove.getGatt().readCharacteristic(characteristic)) {
                    Log.e(TAG, String.format("ERROR: readCharacteristic failed for characteristic: %s", characteristic.getUuid()));
                    completedCommand();
                }
            }
        });

        if(result) {
            nextCommand();
        } else {
            Log.e(TAG, "ERROR: Could not enqueue read characteristic command");
        }
        return result;
    }

    private void nextCommand() {
        // If there is still a command being executed then bail out
        if(commandQueueBusy) {
            return;
        }

        // Check if we still have a valid gatt object
        if (VrGlove.getGatt() == null) {
            commandQueue.clear();
            commandQueueBusy = false;
            return;
        }

        // Execute the next command in the queue
        if (commandQueue.size() > 0) {
            final Runnable bluetoothCommand = commandQueue.peek();
            commandQueueBusy = true;

            bleHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        bluetoothCommand.run();
                    } catch (Exception ex) {
                    }
                }
            });
        }
    }


    private void completedCommand() {
        commandQueueBusy = false;
        commandQueue.poll();
        nextCommand();
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            TextView tvStatus = getActivity().findViewById(R.id.textView_vrGlove_status);
            switch (newState){
                case STATE_CONNECTED:
                    VrGlove.setGattState(newState);
                    tvStatus.setText("Connected");
                    break;
                case STATE_CONNECTING:
                    VrGlove.setGattState(newState);
                    tvStatus.setText("Connecting");
                    break;
                case STATE_DISCONNECTING:
                    VrGlove.setGattState(newState);
                    tvStatus.setText("Disconnecting");
                    break;
                case STATE_DISCONNECTED:
                    VrGlove.getGatt().close();
                    VrGlove.setGattState(newState);
                default:
                    tvStatus.setText("Disconnected");
                    break;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if(status == 129){
                Toast.makeText(getActivity(),"Potential error in catching services",Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);

            // Copy the byte array so we have a threadsafe copy
            final byte[] value = new byte[characteristic.getValue().length];
            System.arraycopy(characteristic.getValue(), 0, value, 0, characteristic.getValue().length );

            // Characteristic has new value so pass it on for processing
            bleHandler.post(new Runnable() {
                @Override
                public void run() {  // VrGlove.onCharacteristicUpdate(BluetoothPeripheral.this, value, characteristic);
                    if(characteristic.getUuid().equals(convertFromInteger(0x2101))){
                        VrGlove.setAccReadings(value);
                    }else if (characteristic.getUuid().equals(convertFromInteger(0x2102))){
                        VrGlove.setGyroReadings(value);
                    }else if(characteristic.getUuid().equals(convertFromInteger(0x2103))){
                        VrGlove.setFingersReadings(value);
                    }else{
                        Toast.makeText(getActivity(),"Unknown characteristic",Toast.LENGTH_SHORT);
                    }
                }
            });

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }
    };

    private final ScanCallback scanCallback = new ScanCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if(device.getAddress().equals("D0:6B:F2:A7:95:03")){
                VrGlove glove = new VrGlove(device, vw);
            }else{
                Toast.makeText(getActivity(),"Not a glove",Toast.LENGTH_SHORT);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {

        }

        @Override
        public void onScanFailed(int errorCode) {
            Toast.makeText(getActivity(),"Scan failed",Toast.LENGTH_SHORT);
        }


    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
