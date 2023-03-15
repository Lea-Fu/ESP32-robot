package com.example.app;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.aruco.*;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements AdapterView.OnItemSelectedListener {

    private Button button;
    private Button button3;
    private TextView textView;
    private ImageView imageView;
    private Spinner spinner;
    private String[] spinnerPos = {"Select field to go to", "x:0, y:0 (ID:0)", "x:1, y:0 (ID:1)", "x:2, y:0 (ID:2)", "x:0, y:1 (ID:3)", "x:1, y:1 (ID:4)", "x:2, y:1 (ID:5)", "x:0, y:2 (ID:6)",  "x:1, y:2 (ID:7)", "x:2, y:2 (ID:8)"};

    private static final int TEST_SPEED = 60;
    private static final int TEST_TIME = 500;
    private Controller controller;
    private Communication communication;

    //this has to stay in the Main, to load the OpenCV library correctly
    static {
        if (!OpenCVLoader.initDebug()) {
            System.out.println("Error init OpenCV");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        button = findViewById(R.id.button);
        button3 = findViewById(R.id.button3);
        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        spinner =(Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerPos);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        controller = new Controller();

        communication = new Communication(this, new ICommunicationCallback() {
            @Override
            //callback call
            public void waitForImage(Mat image) {

                controller.waitForImage(image);

                Mat markerIds = new Mat();
                List<Mat> markerCorners = new ArrayList<>();
                Aruco.detectMarkers(image, Aruco.getPredefinedDictionary(Aruco.DICT_4X4_250), markerCorners, markerIds);
                Aruco.drawDetectedMarkers(image, markerCorners, markerIds);

                Bitmap bmp = null;
                Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2RGBA, 4);
                bmp = Bitmap.createBitmap(image.cols(), image.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(image, bmp);
                imageView.setImageBitmap(bmp);
            }
        });

        controller.setCommunication(communication);
        new Thread(controller).start();

        //with this button you can scan BLEdevices near by, stops scanning when it found the Robo
        // with this button you can connect to the Robo
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communication.connect(new ICommunication.OnConnectionCallback() {
                    @Override
                    public void success() {
                        textView.setText("Successfully connected to robot!");
                    }

                    @Override
                    public void failure(String info) {
                        textView.setText(info);
                    }
                });
            }
        });

        //with this button you can send a message to the Robo
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //AlertDialog to type in message
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Give in a message");

                //allow input
                EditText input = new EditText(MainActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                alertDialogBuilder.setView(input);

                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String message;

                        //save input
                        message = input.getText().toString();

                        textView.setText(message);

                        int speed = TEST_SPEED;

                        if (message.length() > 3) {
                            String message2 = message.substring(0, 3);
                            String speed2 = message.substring(3, message.length());
                            message = message2;
                            speed = Integer.parseInt(speed2);
                        }

                        switch (message) {
                            case "00":
                                communication.sendImages();
                                break;
                            case "01":
                                communication.stopSendImages();
                                break;
                            case "111":
                                communication.motorStart(true, true, speed, TEST_TIME);
                                break;
                            case "100":
                                communication.motorStart(false, false, speed, TEST_TIME);
                                break;
                            case "101":
                                communication.motorStart(false, true, speed, TEST_TIME);
                                break;
                            case "110":
                                communication.motorStart(true, false, speed, TEST_TIME);
                                break;
                            case "122":
                                communication.motorStop();
                                break;
                            //"3"
                            default:
                                communication.printOnDisplay(message);
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(getApplicationContext(),spinnerPos[position], Toast.LENGTH_LONG).show();
        switch (position){
            case 1:
                communication.sendImages();
                controller.goToPosition(new Position(0, 0));
                break;
            case 2:
                communication.sendImages();
                controller.goToPosition(new Position(1, 0));
                break;
            case 3:
                communication.sendImages();
                controller.goToPosition(new Position(2, 0));
                break;
            case 4:
                communication.sendImages();
                controller.goToPosition(new Position(0, 1));
                break;
            case 5:
                communication.sendImages();
                controller.goToPosition(new Position(1, 1));
                break;
            case 6:
                communication.sendImages();
                controller.goToPosition(new Position(2, 1));
                break;
            case 7:
                communication.sendImages();
                controller.goToPosition(new Position(0, 2));
                break;
            case 8:
                communication.sendImages();
                controller.goToPosition(new Position(1, 2));
                break;
            case 9:
                communication.sendImages();
                controller.goToPosition(new Position(2, 2));
                break;
            default:
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
