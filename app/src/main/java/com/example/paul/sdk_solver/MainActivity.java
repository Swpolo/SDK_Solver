package com.example.paul.sdk_solver;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity /*implements CvCameraViewListener2*/ {
    public static Grid sdkGrid;
    SdkView     sdkView;
    SdkSolver   sdkSolver;
    int         solving;

    private CameraBridgeViewBase mOpenCvCameraView;


    int[] keyboardId = {R.id.button0,R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9, R.id.b_solve, R.id.b_reset, R.id.b_load, R.id.b_save} ;

//    private static final String TAG = "POLO";
//    static {
//        if(!OpenCVLoader.initDebug()){
//            Log.d(TAG, "OpenCV not loaded");
//        } else {
//            Log.d(TAG, "OpenCV loaded");
//        }
//    }

//    private void cameraPermission(){
//        int hasCameraPermission = checkSelfPermission(android.Manifest.permission.CAMERA);
//        if (hasCameraPermission != 0){
//            requestPermissions(new String[]{android.Manifest.permission.CAMERA},0);
//        }
//        Log.i("POLO", "Camera permission : " + Integer.toString(hasCameraPermission));
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ActivityMain Creation

        Button b_key;
        int b_key_size = 200;
        for (int i = 0; i < keyboardId.length; i++) {
            b_key = (Button) findViewById(keyboardId[i]);
            b_key.setLayoutParams(new LinearLayout.LayoutParams(b_key_size, LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        sdkGrid = new Grid();
        sdkView = (SdkView) findViewById(R.id.sdkView);
        solving = 0;

        sdkView.setBackgroundColor(Color.WHITE);

        sdkListener();
    }

    public void solve(View v) {
        sdkView.setCellUserSet();
        sdkSolver = new SdkSolver();
        sdkGrid.setGrid(sdkView.getGrid());
        while(sdkSolver.sdkSolve()==0);
        sdkView.setGrid(sdkGrid.getGrid());
        sdkView.invalidate();

//        if (solving == 0) {
//            sdkView.setCellUserSet();
//            sdkSolver = new SdkSolver();
//            sdkGrid.setGrid(sdkView.getGrid());
//            solving = 1;
//        }
//        int solverResult;
//        solverResult = sdkSolver.sdkSolve();
//        sdkView.setGrid(sdkGrid.getGrid());
//        sdkView.invalidate();
//        if (solverResult == 0) {
//        }
//        else {
//            Toast toast = Toast.makeText(getApplicationContext(), "It's over", Toast.LENGTH_SHORT);
//            toast.show();
//        }
    }

    public void reset(View v){
        sdkGrid.reset();
        sdkView.reset();
        solving = 0;
        sdkView.invalidate();
    }

    public void keyboard(View v) {
        Button b = (Button) findViewById(v.getId());
        sdkView.setCell(Integer.parseInt((b.getText().toString())));
        sdkView.invalidate();
    }

    public void save(View v) {
        String fileContent = "";

        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                fileContent = fileContent + sdkView.getCell(col, row);
            }
        }
        Log.d("STATE", fileContent);

        String filename = "lastSDK.txt";

        try {
            FileOutputStream fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(fileContent.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load(View v) {
        String filename = "lastSDK.txt";
        String path = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + filename;
        File file = new File(path);
        int[][] newGrid = new int[9][9];
        if(file.exists()) {
            try {
                FileInputStream fis = openFileInput(filename);
                int col = 0;
                int row = 0;
                while(fis.available() != 0) {
                    newGrid[col][row] = fis.read() - '0';

                    row++;
                    if (row == 9) {
                        row = 0;
                        col++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            sdkView.setGrid(newGrid);
            sdkView.setCellUserSet();
            sdkView.invalidate();
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "No saved SDK", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void changeToCamera(View v){
        sdkGrid.setGrid(sdkView.getGrid());
        setContentView(R.layout.camera);
    }

    public void changeToMain(View v){
        setContentView(R.layout.activity_main);

        sdkView = (SdkView) findViewById(R.id.sdkView);

        sdkView.setGrid(sdkGrid.getGrid());
        sdkView.setCellUserSet();
        sdkView.invalidate();

        sdkListener();
    }

    public void sdkListener(){
        sdkView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float screenX = event.getX();
                float screenY = event.getY();
                sdkView.focusedCell((int) screenX, (int) screenY);
                sdkView.invalidate();

                return true;
            }
        });
    }
}
