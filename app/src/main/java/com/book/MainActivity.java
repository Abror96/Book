package com.book;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaCodec;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.book.databinding.ActivityMainBinding;
import com.book.mvp.contracts.MainContract;
import com.book.mvp.interactors.MainInteractorImpl;
import com.book.mvp.presenters.MainPresenterImpl;
import com.book.retrofit.models.AddingVideoResponse;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements MainContract.View {

    private ActivityMainBinding binding;

    private Camera myCamera;
    private MyCameraSurfaceView myCameraSurfaceView;
    private MediaRecorder mediaRecorder;
    private int videoId = -1;
    private MainContract.Presenter presenter;
    private String filePath = "";
    private Camera.Parameters camParam;

//    private String values_keyword=null;
//    private String iso_keyword=null;
    private SurfaceHolder surfaceHolder;
    boolean recording;
    private boolean isServerAnswered = false;
    private int[] qualities = {
            CamcorderProfile.QUALITY_2160P,
            CamcorderProfile.QUALITY_1080P,
            CamcorderProfile.QUALITY_720P,
            CamcorderProfile.QUALITY_480P
    };
//    public static ArrayList<String> iso_values_arr;
    private int duration = 30000;
    private int default_quality = CamcorderProfile.QUALITY_480P;
//    private String default_iso = "auto";

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private int[][] coords = {
            {-596, -748, -296, -448}, // top left
            {440, -748, 740, -448}, // top right
            {-596, 371, -296, 671}, // bottom left
            {440, 371, 740, 671}, // bottom right
            {-126, -242, 174, 58} // center
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setSupportActionBar(binding.toolbar);

        requestPermissions();

        presenter = new MainPresenterImpl(this, new MainInteractorImpl());

        exit();

    }

    private void exit()  {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        String dateInString = "30/06/2019";
        Date t2 = null;
        try {
            t2 = formatter.parse(dateInString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date t1= new Date();
        if (t1.after(t2)) {
            finishAndRemoveTask();
        } else {
            presenter.onVideoCreateCalled();
        }
    }

    private void initCam() {
        recording = false;
        //Get Camera for preview
        myCamera = getCameraInstance();
        if(myCamera == null){
            Toast.makeText(MainActivity.this,
                    "Fail to get Camera",
                    Toast.LENGTH_LONG).show();
        }

        camParam = myCamera.getParameters();
//        getIsoApiKey(camParam.flatten());
//        String supportedIsoValues = camParam.get(values_keyword);
//        iso_values_arr = new ArrayList<>(Arrays.asList(supportedIsoValues.split(",")));



        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);
        FrameLayout myCameraPreview = findViewById(R.id.CameraView);
        myCameraPreview.addView(myCameraSurfaceView);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        //Release Camera before MediaRecorder start
                            releaseCamera();

                            if (!prepareMediaRecorder()) {
                                Toast.makeText(MainActivity.this,
                                        "Fail in prepareMediaRecorder()!\n - Ended -",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            }

                        try {
                            mediaRecorder.start();
                        } catch (IllegalStateException e) {
                            e.printStackTrace();
                        }
                        recording = true;
                    }
                },
                0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                try {
                    if (mediaRecorder != null) {
                        if (isServerAnswered) {
                            Log.d("LOGGERR", "onOptionsItemSelected: " + mediaRecorder + " " + isServerAnswered);
//                            mediaRecorder.stop();  // stop the recording
                            releaseMediaRecorder();
                            releaseCamera();
                            startActivityForResult(new Intent(this, SettingsActivity.class), 3403);
                        }
                    } else {
                        Snackbar.make(binding.mainView, "Проверьте подключение к серверу", Snackbar.LENGTH_LONG).show();
                    }
                } catch (IllegalStateException e) {
                    Snackbar.make(binding.mainView, "Проверьте подключение к серверу", Snackbar.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3403) {
            int focus = -1;
            if (data != null) {
                if (data.getExtras() != null) {
//                    String iso = data.getStringExtra("iso");
                    int quality = data.getExtras().getInt("quality");
                    focus = data.getIntExtra("focus", -1);

                    this.duration = data.getExtras().getInt("duration");
                    default_quality = qualities[quality];
//                    default_iso = iso;

                }
            }
            releaseMediaRecorder();
            releaseCamera();
            initCam();

            if (focus != -1) {
                focusOnTouch(coords[focus][0],coords[focus][1],coords[focus][2],coords[focus][3]);
            }
        }
    }

//    private void getIsoApiKey(String flat) {
//        Log.d("LOGGERR", "getIsoApiKey: " + flat);
//        if(flat.contains("iso-values")) {
//            // most used keywords
//            values_keyword="iso-values";
//            iso_keyword="iso";
//        } else if(flat.contains("iso-mode-values")) {
//            // google galaxy nexus keywords
//            values_keyword="iso-mode-values";
//            iso_keyword="iso";
//        } else if(flat.contains("iso-speed-values")) {
//            // micromax a101 keywords
//            values_keyword="iso-speed-values";
//            iso_keyword="iso-speed";
//        } else if(flat.contains("nv-picture-iso-values")) {
//            // LG dual p990 keywords
//            values_keyword="nv-picture-iso-values";
//            iso_keyword="nv-picture-iso";
//        }
//    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can't use camera without permission", Toast.LENGTH_LONG).show();
                finish();
            } else {
                initCam();
            }
        }
    }


    private Camera getCameraInstance(){
        // TODO Auto-generated method stub
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    private String getFileName_CustomFormat() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH_mm_ss");
        Date now = new Date();
        String strDate = sdfDate.format(now);
        return strDate;
    }


    private boolean prepareMediaRecorder(){
        myCamera = getCameraInstance();
        mediaRecorder = new MediaRecorder();
        myCamera.setDisplayOrientation(90);
        mediaRecorder.setOrientationHint(90);

//        camParam.set(iso_keyword, default_iso);
        myCamera.setParameters(camParam);

        myCamera.unlock();
        mediaRecorder.setCamera(myCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

//        for (int i = 0; i < camParam.getSupportedVideoSizes().size(); i++) {
//            Log.d("LOGGERR", "prepareMediaRecorder: " + camParam.getSupportedVideoSizes().get(i).width + "x" + camParam.getSupportedVideoSizes().get(i).height);
//        }

        mediaRecorder.setProfile(CamcorderProfile.get(default_quality));

        File file_dir = new File(Environment.getExternalStorageDirectory() + "/video_files");
        if (!file_dir.exists()) {
            file_dir.mkdirs();
        }
        File output_file = new File(file_dir, getFileName_CustomFormat() + ".mp4");
        filePath = output_file.getAbsolutePath();
        Log.d("LOGGERR", "prepareMediaRecorder: " + filePath);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setMaxDuration(duration); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(50000000); // Set max file size 50M

        mediaRecorder.setPreviewDisplay(myCameraSurfaceView.getHolder().getSurface());

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }

        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder1, int i, int i1) {
                Log.d("LOGGERR", "новый файл " + filePath);

                mediaRecorder.stop();  // stop the recording
                releaseMediaRecorder();
                releaseCamera();

                File videoFile = new File(filePath);
                RequestBody videoBody = RequestBody.create(MediaType.parse("video/*"), videoFile);
                MultipartBody.Part videoPart = MultipartBody.Part.createFormData("file", videoFile.getName(), videoBody);

                if (videoId != -1) {
                    presenter.onVideoAddCalled(videoPart, videoId);
                }

                if(!prepareMediaRecorder()){
                    Toast.makeText(MainActivity.this,
                            "Fail in prepareMediaRecorder()!\n - Ended -",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                runOnUiThread(new Runnable() {
                    public void run() {
                        mediaRecorder.start();
                    }
                });
            }
        });


        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();  // stop the recording
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        releaseMediaRecorder(); // release the MediaRecorder object
    }

    private void releaseMediaRecorder(){
        if (mediaRecorder != null) {
            mediaRecorder.reset();   // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = new MediaRecorder();

//            myCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera(){
        if (myCamera != null){
            myCamera.release();        // release the camera for other applications
            myCamera = null;
        }
    }


    private void requestPermissions() {

        // check realtime permission if run higher api 23
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, REQUEST_CAMERA_PERMISSION);
            return;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onVideoCreateSuccess(Integer id) {
        Log.d("LOGGERR", "onVideoCreateSuccess: ");
        videoId = id;
        isServerAnswered = true;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initCam();
        }
    }

    private void focusOnTouch(int left, int top, int right, int bottom) {
        if (myCamera != null ) {

            Camera.Parameters parameters = myCamera.getParameters();
            if (parameters.getMaxNumMeteringAreas() > 0){
                Log.i("LOGGERR","fancy !" + left + " " + top + " " + right + " " + bottom);
                Rect rect = new Rect(left, top, right, bottom);

                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
                meteringAreas.add(new Camera.Area(rect, 800));
                parameters.setFocusAreas(meteringAreas);

                myCamera.setParameters(parameters);
                myCamera.autoFocus(mAutoFocusTakePictureCallback);
            }else {
                try {
                    myCamera.autoFocus(mAutoFocusTakePictureCallback);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Camera.AutoFocusCallback mAutoFocusTakePictureCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                // do something...
                Log.i("tap_to_focus","success!");
            } else {
                // do something...
                Log.i("tap_to_focus","fail!");
            }
        }
    };

    @Override
    public void onVideoAddSuccess(AddingVideoResponse.Object videoObject) {
        Log.d("LOGGERR", "video added, id: " + videoObject.getId() + " url: " + videoObject.getPath());
    }

    @Override
    public void showSnackbar(String message) {
        Snackbar.make(binding.mainView, message, Snackbar.LENGTH_LONG).show();
    }

    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;

        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight,
                                   int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }

            Log.d("LOGGERR", "surfaceChanged: ");

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }

            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();

            } catch (Exception e){
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub

        }


    }

}
