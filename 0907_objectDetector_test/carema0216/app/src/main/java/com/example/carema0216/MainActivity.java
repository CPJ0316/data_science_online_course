package com.example.carema0216;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.TextView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2{
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    private TextView test;

    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private String TAG = "openCV_Test";
    //    private CameraBridgeViewBase mCVCamera;
    private JavaCameraView mCVCamera;
    private Mat mMat, mRgba, mGray, CannyImg;
    private Button btnTake, btnGallery, btnCannyedge, btnGray;
    private boolean isGrayScale = false;
    private boolean isCannyEdge = false;
    private String folderName = "MyPhotoDir";
    private File file;
    private File folder;
    private CascadeClassifier faceCascade = null;
    private CascadeClassifier smileCascade = null;
    private String input_text;

    private objectDetectorClass objectDetectorClass;
    String fileName = Environment.getExternalStorageDirectory().getPath() + "/sample_picture.jpg";

    BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status){
            switch(status){
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    mCVCamera.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }

    };



    @Override
    protected  void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);


        mCVCamera = (JavaCameraView) findViewById(R.id.camera_view);
        mCVCamera.setVisibility(SurfaceView.VISIBLE);
        mCVCamera.setCvCameraViewListener(this);

        ActivityCompat.requestPermissions(this, new String[] {RECORD_AUDIO}, PackageManager.PERMISSION_GRANTED);
        test = findViewById(R.id.input_test);
        intentRecognizer= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //設定額外的屬性EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS，其值為100。這個屬性表示如果在語音輸入時，靜默持續2秒，則會視為輸入結束。
        //intentRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS , 2000);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 60000);
        //intentRecognizer.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 5000);
        //設定額外的屬性EXTRA_LANGUAGE_MODEL，其值為LANGUAGE_MODEL_FREE_FORM。這個屬性指定語音辨識的語言模型為自由形式，表示可以自由地輸入語音而不限制特定語法。
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //設定額外的屬性EXTRA_LANGUAGE，其值為Locale.getDefault()。這個屬性指定語音辨識的語言環境為預設的系統語言環境，即根據用戶的手機設定來決定語言。
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        Log.d("CREATION","0215--------------------------------------");
        speechRecognizer= SpeechRecognizer.createSpeechRecognizer(this);
        boolean b= SpeechRecognizer.isRecognitionAvailable (this);
        if(b)
        {
            Log.d("CREATION","isRecognitionAvailable_true");
        }
        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle bundle) {
                Log.d("CREATION","onReadyForSpeech");
                //Called when the end pointer is ready for the user to start speaking.
            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d("CREATION","onBeginningOfSpeech");
                //The user has started to speak.
            }

            @Override
            public void onRmsChanged(float v) {
                Log.d("CREATION","onRmsChanged");
            }
            //The sound level in the audio stream has changed. There is no guarantee that this method will be called.

            @Override
            public void onBufferReceived(byte[] bytes) {
                Log.d("CREATION","onBufferReceived");
                //More sound has been received. The purpose of this function is to allow giving feedback to the user regarding the captured audio.
                //There is no guarantee that this method will be called.
            }

            @Override
            public void onEndOfSpeech() {
                Log.d("CREATION","onEndOfSpeech");
                //Called after the user stops speaking.
            }

            @Override
            public void onError(int error) {
                StringBuilder error_str = new StringBuilder();

                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        error_str.append("");
                        break;/*from   w  ww  .j a  v a  2  s .c  om*/
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        error_str.append("");
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        error_str.append("");
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        error_str.append("???");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        error_str.append("");
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        error_str.append("?");
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        error_str.append("");
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        error_str.append("?");
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        error_str.append("");
                        break;
                }
                speechRecognizer.startListening(intentRecognizer);
                error_str.append(":" + error);
                Log.d("CREATION",error_str.toString());
            }

            @Override
            public void onResults(Bundle bundle) {
                //Called when recognition results are ready.
                //Called with the results for the full speech since onReadyForSpeech(android.os.Bundle).
                Log.d("CREATION","string1");
                ArrayList<String> matches=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Log.d("CREATION","string2");
                //String string ="";
                if(matches!=null){
                    input_text = matches.get(0);
                    test.setText(input_text);
                    Log.d("CREATION","string");
                    Log.d("CREATION",input_text);
                }
                else {
                    Log.d("CREATION","Non null");
                }
                if(input_text.contains("拍")||input_text.contains("take"))
                {
                    if(input_text.contains("灰")||input_text.contains("gray"))
                    {
                        isGrayScale = !isGrayScale; // 切換模式
                        isCannyEdge = false;

                        if (isGrayScale) {
                            // 如果切換到灰階模式，重新啟動相機視圖以應用變更
                            mCVCamera.disableView();
                            mCVCamera.enableView();
                        }
                    }
                    if(input_text.contains("輪廓")||input_text.contains("edge"))
                    {
                        isCannyEdge = !isCannyEdge; // 切換模式
                        isGrayScale = false;

                        if (isCannyEdge) {
                            // 如果切換到灰階模式，重新啟動相機視圖以應用變更
                            mCVCamera.disableView();
                            mCVCamera.enableView();
                        }
                    }
                    if (isGrayScale) {
                        mMat = mGray;
                    } else if (isCannyEdge) {
                        mMat = CannyImg;
                    } else {
                        mMat = mRgba;
                    }

                    takePicture(mMat);
                }
                else {
                    if (input_text.contains("灰") || input_text.contains("gray")) {
                        isGrayScale = !isGrayScale; // 切換模式
                        isCannyEdge = false;

                        if (isGrayScale) {
                            // 如果切換到灰階模式，重新啟動相機視圖以應用變更
                            mCVCamera.disableView();
                            mCVCamera.enableView();
                        }
                    }
                    if (input_text.contains("輪廓") || input_text.contains("edge")) {
                        isCannyEdge = !isCannyEdge; // 切換模式
                        isGrayScale = false;

                        if (isCannyEdge) {
                            // 如果切換到灰階模式，重新啟動相機視圖以應用變更
                            mCVCamera.disableView();
                            mCVCamera.enableView();
                        }
                    }
                }
                if(input_text.contains("相簿")||input_text.contains("gallery")||input_text.contains("Gallery"))
                {
                    Intent intent = new Intent(MainActivity.this, CustomGalleryActivity.class);
                    startActivity(intent);
                }
                speechRecognizer.startListening(intentRecognizer);
            }

            @Override
            public void onPartialResults(Bundle bundle) {
                Log.d("CREATION","onPartialResults");
                //Called when partial recognition results are available.
            }

            @Override
            public void onEvent(int i, Bundle bundle) {
                Log.d("CREATION","onEvent");
                //Reserved for adding future events.
            }
        });
        // 人臉檢測
        OpenCVLoader.initDebug();

        //初始人臉檢測
        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_alt);
        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
        File cascadeFile = new File(cascadeDir, "haarcascade_frontalface_alt");
//        CascadeClassifier faceCascade = null;
        try {
            FileOutputStream os = new FileOutputStream(cascadeFile);
            //創建一個緩衝區，用於從資源中讀取數據並寫入文件
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
            faceCascade = new CascadeClassifier(cascadeFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //初始笑臉檢測
        InputStream smile_is = getResources().openRawResource(R.raw.haarcascade_smile);
        File smileCascadeDir = getDir("cascade", Context.MODE_PRIVATE);
        File smileCascadeFile = new File(smileCascadeDir, "haarcascade_smile_alt");

        try {
            FileOutputStream smile_os = new FileOutputStream(smileCascadeFile);
            //創建一個緩衝區，用於從資源中讀取數據並寫入文件
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = smile_is.read(buffer)) != -1) {
                smile_os.write(buffer, 0, bytesRead);
            }
            smile_is.close();
            smile_os.close();
            smileCascade = new CascadeClassifier(smileCascadeFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        //物體檢測
        try{
            // input size is 300 for this model
            objectDetectorClass=new objectDetectorClass(getAssets(),"MBv1model.tflite","label.txt",300);
            Log.d("MainActivity","Model is successfully loaded");
        }
        catch (IOException e){
            Log.d("MainActivity","Getting some error");
            e.printStackTrace();
        }



//        // 設定相機視圖的寬度和高度為手機螢幕的寬度和高度
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int height = displayMetrics.heightPixels;
//        int width = displayMetrics.widthPixels;
//        mCVCamera.setLayoutParams(new FrameLayout.LayoutParams(width, height));

        btnTake = (Button) findViewById(R.id.photo_btn);
        btnGallery = (Button) findViewById(R.id.gallery_btn);
        btnCannyedge = (Button) findViewById(R.id.cannyedge_btn);
        btnGray = (Button) findViewById(R.id.grayscale_btn);

        btnTake.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){
                if (isGrayScale) {
                    mMat = mGray;
                } else if (isCannyEdge) {
                    mMat = CannyImg;
                } else {
                    mMat = mRgba;
                }

                takePicture(mMat);
            }
        });
        btnGray.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isGrayScale = !isGrayScale; // 切換模式
                isCannyEdge = false;

                if (isGrayScale) {
                    // 如果切換到灰階模式，重新啟動相機視圖以應用變更
                    mCVCamera.disableView();
                    mCVCamera.enableView();
                }
            }
        });
        btnCannyedge.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isCannyEdge = !isCannyEdge; // 切換模式
                isGrayScale = false;

                if (isCannyEdge) {
                    // 如果切換到灰階模式，重新啟動相機視圖以應用變更
                    mCVCamera.disableView();
                    mCVCamera.enableView();
                }
            }
        });
        btnGallery.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CustomGalleryActivity.class);
                startActivity(intent);
                //openGallery();
            }
        });
    }

    public void StartButton(View view){
        Log.d("CREATION","start");
        //test.setText("start");
        speechRecognizer.startListening(intentRecognizer);
    }
    public void EndButton(View view){
        Log.d("CREATION","stop");
        //test.setText("stop");
        //speechRecognizer.stopListening();
    }
    // 在 onRequestPermissionsResult() 方法中處理相機權限請求的結果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                // close the app
                Toast.makeText(MainActivity.this, "Sorry! You can't use this app without granting permission", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private boolean isExternalStorageAvailableForRW() {
        // Check if the external storage is available for read and write ny calling.
        // Environment.getExternalStorageState() method. If the returned state is MEDIA_MOUNTED,
        // then you can read and write files. So, return true in that case, otherwise, false.
        String extStorageState = Environment.getExternalStorageState();
        if (extStorageState.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                return true;
            } else {
                // Permission is revoked
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            // Permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    private void takePicture(Mat mMat) {
        Log.e(TAG, "take picture");
        if (!isExternalStorageAvailableForRW() || isExternalStorageReadOnly()) {
            btnTake.setEnabled(false);
        }
        if (isStoragePermissionGranted()) {
//        ImageReader reader = ImageReader.newInstance((int)mRgba.size().width, (int)mRgba.size().height, ImageFormat.JPEG, 1);
//        List<Surface> outputSurface = new ArrayList<>(2);
//            Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGBA2BGR, 3); // 將RGBA圖像轉換為BGR圖像
            file = null;
            folder = new File(folderName);
            String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IMG_" + timeStamp + ".jpg";
//        file = new File(getExternalFilesDir(folderName), "/" + imageFileName);
//        if (!folder.exists()) {
//            folder.mkdirs();
//
//        }
            File picturesFolder = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES);
            File albumFolder = new File(picturesFolder, "CAMERA_APP");
            if (!albumFolder.exists()) {
                albumFolder.mkdirs();
            }
            file = new File(albumFolder, imageFileName);

            Imgcodecs.imwrite(file.toString(), mMat);
            Log.i(TAG, "Saved：" + file.toString());

            // 發送媒體掃描廣播，使新拍攝的照片立即顯示在相簿中
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));

//            Toast.makeText(MainActivity.this, "Saved: " + fileName, Toast.LENGTH_SHORT).show();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MainActivity.this, "Saved: " + fileName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_VIEW);
        galleryIntent.setType("image/*");
        startActivity(galleryIntent);

    }

    @Override
    public void onCameraViewStarted(int width, int height){
//        // 計算手機屏幕大小
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        int screenWidth = displayMetrics.widthPixels;
//        int screenHeight = displayMetrics.heightPixels;
//        // 設置畫面大小為手機屏幕大小
//        mCVCamera.setScaleY((float) height / (float) width * (float) screenHeight / (float) screenWidth);
        mRgba = new Mat(width, height, CvType.CV_8UC3);
        mGray = new Mat(width, height, CvType.CV_8UC1);
        CannyImg = new Mat(width, height, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped(){
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(@NonNull CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        Mat currentMat = inputFrame.rgba();
        //Mat temp=inputFrame.rgba();

        mRgba = new Mat();
        Imgproc.cvtColor(currentMat, mRgba, Imgproc.COLOR_RGB2BGR);
        if(input_text!=null) {
            currentMat = objectDetectorClass.recognizeImage(currentMat, input_text);
        }
        if (isGrayScale) {
            // 轉換為灰階影像
            Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY);
            currentMat = mGray;
        } else if (isCannyEdge) {
            // canny edge
            Imgproc.Canny(mRgba, CannyImg, 100, 80);
            currentMat = CannyImg;
        }

        if (faceCascade != null && !faceCascade.empty()) {

            // 使用先前載入的人臉檢測器 CascadeClassifier 對輸入圖像執行人臉檢測，將檢測到的人臉位置存儲在 MatOfRect 對象 faceDetections 中
            MatOfRect faceDetections = new MatOfRect();
            faceCascade.detectMultiScale(currentMat, faceDetections);

            // 找到最大面積的矩形
            double maxArea = 0;
            for (Rect rect : faceDetections.toArray()) {
                double area = rect.width * rect.height;
                if (area > maxArea) {
                    maxArea = area;
                }
            }

            // 在檢測到的人臉周圍畫上矩形
            for (Rect rect : faceDetections.toArray()) {
                Scalar rectColor;

                // 計算矩形的面積
                double area = rect.width * rect.height;
                Log.e(TAG, Double.toString(area));

                // 如果矩形的面積小於最大面積的50%，則將矩形顏色設置為紅色，否則為綠色
                if (area < (0.5 * maxArea)) {

                    //模糊
                    Mat faceROI = currentMat.submat(rect);
                    Imgproc.GaussianBlur(faceROI, faceROI, new Size(25, 25), 0);

                    rectColor = new Scalar(255,0 ,0); // 紅
                } else {
                    rectColor = new Scalar(0, 255, 0); // 綠
                }

                faceDetections.release();

                if(area == maxArea){
                    Mat largestFaceROI = currentMat.submat(rect);

                    // 笑臉檢測
                    MatOfRect smileDetections = new MatOfRect();
                    smileCascade.detectMultiScale(largestFaceROI, smileDetections);

                    if (!smileDetections.empty()) {
                        takePicture(currentMat);
                        Log.e(TAG, "takePicture");
                    }

                    smileDetections.release();
                }

                Imgproc.rectangle(currentMat, rect.tl(), rect.br(), rectColor, 3);

                // 在矩形上方繪製面積信息
                String areaText = "Area: " + area;
                Point textPosition = new Point(rect.tl().x, rect.tl().y - 10); // 調整文字位置
                Imgproc.putText(currentMat, areaText, textPosition, 1, 1.7, rectColor, 2);
            }
        } else {
            Log.e(TAG, "Cascade classifier initialization failed.");
        }

        return currentMat;

//        mRgba = new Mat();
//        Imgproc.cvtColor(currentMat, mRgba, Imgproc.COLOR_RGB2BGR);
//
//        if (isGrayScale) {
//            // 轉換為灰階影像
//            Imgproc.cvtColor(mRgba, mGray, Imgproc.COLOR_RGB2GRAY);
//            return mGray;
//        } else if (isCannyEdge) {
//            // canny edge
//            Imgproc.Canny(mRgba, CannyImg, 100, 80);
//            return CannyImg;
//        } else {
//            // 返回原始色彩影像
//            return currentMat;
//        }

    }

    // rotate
    @NonNull
    private Mat rotateMat(@NonNull Mat src, int angle) {
        Mat dst = new Mat();
        int cols = src.cols();
        int rows = src.rows();
        int newCols, newRows;

        // 計算旋轉後的影像尺寸
        if (angle == 90 || angle == 270) {
            newCols = cols;
            newRows = rows;
        } else {
            newCols = rows;
            newRows = cols;
        }

        Point center = new Point(cols / 2, rows / 2);
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, angle, 1.22);

        // 進行旋轉
        Imgproc.warpAffine(src, dst, rotationMatrix, new Size(newCols, newRows));

        return dst;
    }

    @Override
    public void onResume(){
        super.onResume();
        // 檢查相機權限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // 如果沒有相機權限，請求權限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        if(!OpenCVLoader.initDebug()){
            Log.d(TAG, "Opencv library not found!");
        }else{
            Log.d(TAG, "OpenCV library found inside package.");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        mCVCamera.disableView();
    }

    @Override
    public void onDestroy(){
        if(mCVCamera != null){
            mCVCamera.disableView();
        }
        super.onDestroy();
    };

}