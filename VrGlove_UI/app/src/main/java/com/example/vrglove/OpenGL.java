package com.example.vrglove;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OpenGL.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OpenGL#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OpenGL extends Fragment
        implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    //Render model
    private View vw;
    private SceneView sceneView;
    private ModelRenderable finalHandRenderable;
    private Camera camera;
    private Node coreNode;

    //Animation
    private boolean isCalibrating;
    private float[] accAngles, gyroAngles, modelAngles;

    private static final float dtNanoToSec = 1.0f / 1000000000.0f;
    private long lastTimestamp = 0;
    private static long currentTimestamp;
    private float[] velocity = new float[3],pos = new float[3],
                                rotationVec = new float[3];


    public OpenGL() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment OpenGL.
     */
    public static OpenGL newInstance() {
        OpenGL fragment = new OpenGL();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
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
        vw = inflater.inflate(R.layout.fragment_open_gl, container, false);
        accAngles = new float[3];
        gyroAngles= new float[3];
        modelAngles= new float[3];
        velocity = new float[3];
        pos = new float[3];
        rotationVec = new float[3];

        FloatingActionButton myFab = (FloatingActionButton) vw.findViewById(R.id.fab);
        myFab.setOnClickListener(v -> fabListener());
        isCalibrating = false;
        generateSceneView();

        return vw;
    }

    private void fabListener() {
        isCalibrating = true;

        lastTimestamp = 0;
        velocity = new float[3];
        pos = new float[3];
        rotationVec = new float[3];

        accAngles = new float[3];
        gyroAngles= new float[3];
        modelAngles= new float[3];

        CountDownTimer cd = new CountDownTimer(3000,1000) {
            private Toast cdMsgToast = null;
            @Override
            public void onTick(long l) {
                if(cdMsgToast != null){
                    cdMsgToast.cancel();
                }
                cdMsgToast = Toast.makeText(getActivity(),
                        "Recalibrating glove - restoring original position in "+(l/1000+1)+ " seconds.",
                        Toast.LENGTH_SHORT);
                cdMsgToast.show();
            }

            @Override
            public void onFinish() {
                if(cdMsgToast != null){
                    cdMsgToast.cancel();
                }
                cdMsgToast = Toast.makeText(getActivity(),
                        "Glove recalibrated",
                        Toast.LENGTH_SHORT);
                cdMsgToast.show();

                coreNode.setLocalPosition(new Vector3(0f,0f,-0.7f));

                Quaternion rotation1 = Quaternion.axisAngle(new Vector3(0.0f, 0.0f, 1.0f), 90f);
                Quaternion rotation2 = Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90f);
                coreNode.setLocalRotation(Quaternion.multiply(rotation1, rotation2));

                isCalibrating = false;
            }
        }.start();

    }

    private void generateSceneView() {
        int c = ContextCompat.getColor(getContext(), R.color.WhiteSmoke);
        sceneView = vw.findViewById(R.id.scene_view);
        camera = sceneView.getScene().getCamera();
        sceneView.getScene().addChild(camera);

        MaterialFactory.makeOpaqueWithColor(getContext(), new Color(c))
                .thenAccept(
                        material -> ModelRenderable.builder()
                                .setSource(getContext(),Uri.parse("Final_hand_L.sfb"))
                                .build()
                                .thenAccept(this::onRenderableLoades)
                                .exceptionally(
                                        throwable -> {
                                            Log.e(TAG, "Unable to load Renderable.", throwable);
                                            return null;
                                        }));


        camera.setLocalPosition(new Vector3(0.0f,0.0f,0.0f));

        new Thread (()-> startDataListener()).start();

        // TODO: Animate data from here
//        finalHandRenderable.getBoneParent();
//        Toast.makeText(getActivity(),bones,Toast.LENGTH_SHORT);

    }

    private void startDataListener() {
        while(true){
            if(VrGlove.ismIsStateChanged() && !isCalibrating){
                Quaternion[] quat = new Quaternion[3];

                calculateRotation();
                quat[0] = Quaternion.axisAngle(new Vector3(0.0f,0.0f,-1.0f),modelAngles[0]); // up/down
                quat[1] = Quaternion.axisAngle(new Vector3(1.0f,0.0f,0.0f),modelAngles[1]);
                quat[2] = Quaternion.axisAngle(new Vector3(0.0f,1.0f,0.0f),modelAngles[2]);
                Quaternion resultOrientation = Quaternion.multiply(Quaternion.multiply(quat[1],quat[0]),quat[2]);
                this.coreNode.setLocalRotation(resultOrientation);

                parseAccData();
                this.coreNode.setLocalPosition(new Vector3(0.4f*pos[0],0.4f*pos[2],-0.7f+(0.4f*pos[1])));
                VrGlove.setmIsStateChanged(false);
            }
        }
    }

    private void parseAccData() {
        long currentTime = currentTimestamp;
        Float[] accSet = VrGlove.getDataSet().get("Acc");
        if(lastTimestamp != 0){
            final float dT = (currentTime - lastTimestamp) * dtNanoToSec;
            float[] tmpAngles = modelAngles;
            tmpAngles[0] -= 15.0f;
            tmpAngles[1] -= 20.0f;
            //TODO
            float[] accDataPostRotation = removeRotation(accSet, tmpAngles);
            //removes gravity
            accDataPostRotation[2] -= 0.98f;
            accDataPostRotation = restoreRotation(accDataPostRotation, rotationVec);

            //Double integral to obtain velocity from acceleration, and position from velocity
            for(int i = 0; i < velocity.length; i++){
                velocity[i] += accDataPostRotation[i]*dT;
            }
            for(int i = 0; i < pos.length; i++){
                pos[i] += accDataPostRotation[i]*dT;
            }
        }
        lastTimestamp = currentTime;
    }

    private float[] restoreRotation(float[] accDataPostRotation, float[] rotationV) {
        //TODO: Restore original data by multiplying inverse rotV
        for (int i =0; i< accDataPostRotation.length; i++){
//            accDataPostRotation[i] *= (-1*rotationVec[i]);
        }
        return accDataPostRotation;
    }

    private float[] removeRotation(Float[] accSet, float[] angles) {
        float[] result = new float[3];
        //TODO: Multiply times rotation vector
//        rotationVec = ...angles
        for (int i =0; i< accSet.length; i++){
            result[i] = accSet[i];
//            result[i] *= rotationVec[i];
        }
        return result;
    }

    private void calculateRotation() {
        float[] accInput = new float[3];
        float[] gyroInput = new float[3];
        Float[] accSet = VrGlove.getDataSet().get("Acc");
        Float[] gyroSet = VrGlove.getDataSet().get("Gyro");
        if(accSet != null && gyroSet != null){
            if(accSet.length == gyroSet.length){
                for(int i = 0 ; i < accSet.length ; i++) {
                    accInput[i]  = accSet[i];
                    gyroInput[i] = gyroSet[i];
                }
            }
            calculateAccAngles(accInput);
            calculateGyroAngles(gyroInput);
            modelAngles = complimentaryFilter(accAngles, gyroAngles);
        }
    }

    private void calculateAccAngles(float[] accInput) {
        accAngles[0] = (float) (atan(accInput[1] / sqrt(pow(accInput[0], 2) + pow(accInput[2], 2))) * 180 / PI);
        accAngles[1] = (float) (atan(-1 * accInput[0] / sqrt(pow(accInput[1], 2) + pow(accInput[2], 2))) * 180 / PI);
    }

    private void calculateGyroAngles(float[] gyroInput) {
        for(int i = 0; i< gyroAngles.length; i++){
            gyroAngles[i] += gyroInput[i];
        }
    }

    private float[] complimentaryFilter(float[] accAngles, float[] gyroAngles) {
        float[] result = new float[3];
        //Adjust accelerometer angles to position of the board on the glove
        float[] tmp = accAngles;
        tmp[0] *= -1.0f;
        tmp[1] *= -1.0f;

        tmp[0] += 15.0f;
        tmp[1] += 20.0f;
        //Complimentary filter
        float filterValue = 0.94f;
        result[0] = filterValue * gyroAngles[0] + (1 - filterValue) * tmp[0];
        result[1] = filterValue * gyroAngles[1] + (1 - filterValue) * tmp[1];
        result[2] = gyroAngles[2];
        return result;
    }

    private void onRenderableLoades(ModelRenderable finalHandRenderable) {
        if(finalHandRenderable == null){
            Log.e(TAG, "Renderable is null");
            return;
        }

        finalHandRenderable.setShadowReceiver(false);
        this.finalHandRenderable = finalHandRenderable;

        coreNode = new Node();
        coreNode.setLocalPosition(new Vector3(0f,0f,-0.7f));

        Quaternion rotation1 = Quaternion.axisAngle(new Vector3(0.0f, 0.0f, 1.0f), 90f);
        Quaternion rotation2 = Quaternion.axisAngle(new Vector3(1.0f, 0.0f, 0.0f), 90f);
        coreNode.setLocalRotation(Quaternion.multiply(rotation1, rotation2));

        coreNode.setRenderable(finalHandRenderable);
        sceneView.getScene().addChild(coreNode);
    }

    @Override
    public void onPause() {
        super.onPause();
        sceneView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            sceneView.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    @Override
    public void onClick(View v) {

    }

    public static void setCurrentTimestamp(long timestamp){
        currentTimestamp = timestamp;
    }

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
