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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_DISCONNECTING;
import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;


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
        View vw =  inflater.inflate(R.layout.fragment_main, container, false);

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
        Switch switchBT = v.findViewById(R.id.switchBT);;
        switch (v.getId()){
            case R.id.switchBT:
                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
                if(true){
                    // tODO: enum for connection states
                    BluetoothAdapter bluetoothAdapter;
                    final BluetoothManager bluetoothManager =
                            (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                    bluetoothAdapter = bluetoothManager.getAdapter();
                    if (!bluetoothAdapter.isEnabled()) {
                        switchBT.setChecked(true);
                    }
                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice("D0:6B:F2:A7:95:03");
                    VrGlove vrGlove = new VrGlove(device);
                    int deviceType = device.getType();
                    BluetoothGatt gatt;
                    if(deviceType == BluetoothDevice.DEVICE_TYPE_UNKNOWN) {
                        // The peripheral is not cached
                        BluetoothLeScanner scanner = bluetoothAdapter.getBluetoothLeScanner();
                        scanner.startScan(scanCallback);
                        if(VrGlove.getDevice() != null){
                            scanner.stopScan(scanCallback);
                            gatt = VrGlove.getDevice().connectGatt(getActivity(),false,bluetoothGattCallback, TRANSPORT_LE);
                        }
                    } else {
                        gatt = VrGlove.getDevice().connectGatt(getActivity(), true, bluetoothGattCallback, TRANSPORT_LE);
                    }


//TODO: Bluetooth conneciton
                }
                break;
            case R.id.buttonDisconnect:
                break;

        }
    }

    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            TextView tvStatus = getActivity().findViewById(R.id.textView_vrGlove_status);
            switch (newState){
                case STATE_CONNECTED:
                    tvStatus.setText("Connected");
                    break;
                case STATE_CONNECTING:
                    tvStatus.setText("Connecting");
                    break;
                case STATE_DISCONNECTING:
                    tvStatus.setText("Disconnecting");
                    break;
                case STATE_DISCONNECTED:
                default:
                    tvStatus.setText("Disconnected");
                    break;
            }
        }
//TODO: update vrglove class from here
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
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
                VrGlove glove = new VrGlove(device);
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
