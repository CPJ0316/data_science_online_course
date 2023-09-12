//package com.example.carema0216;
//
//import android.content.Context;
//import android.hardware.Camera;
//import android.nfc.Tag;
//import android.util.AttributeSet;
//import android.util.Log;
//
//import org.opencv.android.JavaCameraView;
//
//import java.io.FileOutputStream;
//import java.util.jar.Attributes;
//
//public class CameraView extends JavaCameraView implements Camera.PictureCallback {
//
//    private static final String TAG = "OpenCV";
//    private String mPictureFileName;
//
//    public CameraView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public void takePicture(final String fileName) {
//        Log.i(TAG, "Taking pictures");
//        this.mPictureFileName = fileName;
//        mCamera.setPreviewCallback(null);
//
//        // PictureCallback is implemented by the current class
//        mCamera.takePicture(null, null, this);
//    }
//    @Override
//    public void onPictureTaken(byte[] data, Camera camera) {
//        Log.i(TAG, "Saving a bitmap to file");
//        mCamera.startPreview();
//        mCamera.setPreviewCallback(this);
//
//        // Write the image in a file (in ipeg format)
//        try {
//            FileOutputStream fos = new FileOutputStream(mPictureFileName);
//
//            fos.write(data);
//
//        }
//    }
//}
