package com.example.vrglove;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.SceneView;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.TransformableNode;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static androidx.constraintlayout.widget.Constraints.TAG;


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
    private View vw;
    private SceneView sceneView;
    private ModelRenderable finalHandRenderable;
    private Camera camera;
    private Node coreNode;

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

        generateSceneView();

        return vw;
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
//        camera.setLocalRotation(Quaternion.axisAngle(new Vector3(1,0,0), -10.0f));

        // TODO: enable animation and access bone list
//        finalHandRenderable.getBoneParent();
//        Toast.makeText(getActivity(),bones,Toast.LENGTH_SHORT);

        new Thread (()-> startDataListener()).start();

    }

    private void startDataListener() {
        while(true){
            if(VrGlove.ismIsStateChanged()){
                float[] pos;
                // TODO: Add rotation
//                float[] rot = new float[3];
//                Quaternion[] quat = new Quaternion[3];
                pos = parseAccData(VrGlove.getDataSet().get("Acc"));

                this.coreNode.setLocalPosition(new Vector3(pos[0],pos[1],pos[2]));

//                this.coreNode.setLocalRotation(Quaternion.multiply(Quaternion.multiply(quat[0],quat[1]),quat[2]));
            }
        }
    }

    private float[] parseAccData(Float[] accs) {
        float[] result = new float[3];
        for(int i = 0 ; i < accs.length ; i++){
            result[i] = accs[i].floatValue();
        }

        // TODO: Figure how to parse data and remove this lines
        result[0] = 0f;
        result[1] = 0f;
        result[2] = -0.7f;
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
