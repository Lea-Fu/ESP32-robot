package com.example.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;
import com.ficat.easyble.BleDevice;
import com.ficat.easyble.BleManager; //https://github.com/Ficat/EasyBle
import com.ficat.easyble.gatt.callback.BleCallback;
import com.ficat.easyble.gatt.callback.BleConnectCallback;
import com.ficat.easyble.gatt.callback.BleMtuCallback;
import com.ficat.easyble.gatt.callback.BleNotifyCallback;
import com.ficat.easyble.gatt.callback.BleWriteByBatchCallback;
import com.ficat.easyble.gatt.callback.BleWriteCallback;
import com.ficat.easyble.scan.BleScanCallback;
import com.ficat.easypermissions.EasyPermissions; //https://github.com/Ficat/EasyPermissions
import org.opencv.core.Mat;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

import static org.opencv.core.CvType.CV_8UC1;

/**
 * Used to send and receive packages from the robot.
 */
public class Communication implements ICommunication {

    private Activity activity;
    private BleManager manager;
    private BleDevice BLEdevice;
    private static final String serviceUuid = "476d099e-1ff4-43c0-9e36-23f4280ec5f7";
    private static final String notifyUuid = "19f220b8-2adf-4c46-867c-c75b208ba652";
    private static final String writeUuid = "19f220b8-2adf-4c46-867c-c75b208ba652";
    private BleManager.ScanOptions scanOptions;
    private BleManager.ConnectOptions connectOptions;
    private ICommunicationCallback callback;


    /**
     * Constructor
     * @param activity The current Activity from Android
     * @param callback Callback for the communication
     */
    Communication(Activity activity, ICommunicationCallback callback) {

        this.activity = activity;

        this.callback = callback;

        //check if the device supports BLE
        BleManager.supportBle(activity.getApplication());

        //is Bluetooth turned on?
        BleManager.isBluetoothOn();

        //scan/connection options is not necessary, if you don't set,
        //it will use default config
        scanOptions = BleManager.ScanOptions
                .newInstance()
                .scanPeriod(1000)
                .scanDeviceName(null);

        connectOptions = BleManager.ConnectOptions
                .newInstance()
                .connectTimeout(12000);

        manager = BleManager
                .getInstance()
                .setScanOptions(scanOptions)//it is not necessary
                .setConnectionOptions(connectOptions)//like scan options
                .setLog(true, "TAG")
                .init(activity.getApplication());//Context is needed here,do not use Activity,which can cause Activity leak


        if (!BleManager.isBluetoothOn()) {
            BleManager.toggleBluetooth(true);
        }
        //for most devices whose version is over Android6,scanning may need GPS permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !isGpsOn()) {
            Toast.makeText(activity, "GPS has to be turned on", Toast.LENGTH_LONG).show();
            return;
        }
        EasyPermissions
                .with(activity)
                .request(Manifest.permission.ACCESS_FINE_LOCATION)
                .autoRetryWhenUserRefuse(true, null);
    }

    /**
     * checks if the gps turned on
     * @return gps on
     */
    private boolean isGpsOn() {
        LocationManager locationManager
                = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * connect robot via BLE to phone
     * @param connectionCallback Callback to check the connection status
     */
    @Override
    public void connect(OnConnectionCallback connectionCallback) {

        manager.startScan(scanOptions, new BleScanCallback() {
            /**
             * scan devices
             * @param device current devices near by
             * @param rssi unused
             * @param scanRecord unused
             */
            @Override
            public void onLeScan(BleDevice device, int rssi, byte[] scanRecord) {
                String name = device.name;
                System.out.println(name);
                String address = device.address;
                System.out.println(address);
                String text = name + " " + address + "\n";
                System.out.println(text);

                if (name.equals("Robot00003ca9741c5210")) {
                    BLEdevice = device;
                    manager.stopScan();

                    BleConnectCallback bleConnectCallback = new BleConnectCallback() {
                        @Override
                        public void onStart(boolean startConnectSuccess, String info, BleDevice device) {
                            if (startConnectSuccess) {
                                //start to connect successfully
                                System.out.println("Connected");
                            } else {
                                //fail to start connection, see details from 'info'
                                String failReason = info;
                                connectionCallback.failure(info);
                            }
                        }
                        @Override
                        public void onFailure(int failCode, String info, BleDevice device) {
                            connectionCallback.failure(info);
                            if (failCode == BleConnectCallback.FAIL_CONNECT_TIMEOUT) {
                                //connection timeout
                            } else {
                                //connection fail due to other reasons
                            }

                        }

                        /**
                         * connected robot to phone
                         * @param device current BLE device
                         */
                        @Override
                        public void onConnected(BleDevice device) {
                            manager.setMtu(device, 512, new BleMtuCallback() {
                                @Override
                                public void onMtuChanged(int mtu, BleDevice device) {
                                    System.out.println("MTU is now " + mtu);
                                    connectionCallback.success();
                                }
                                @Override
                                public void onFailure(int failCode, String info, BleDevice device) {
                                    System.out.println(info);
                                    connectionCallback.failure(info);
                                }
                            });

                            manager.notify(BLEdevice, serviceUuid, notifyUuid, new BleNotifyCallback() {

                                private static final int PACKET_SIZE = 500;
                                private static final int SEQUENCE_HEADER_SIZE = 4;

                                private int size = 0;
                                private int sequenceNumber = -1;
                                private ByteBuffer imData = null;
                                private int counter = 0;
                                private int imWidth = 0;
                                private int imHeight = 0;

                                /**
                                 * get the incoming packages from the robot
                                 * @param data package from the robot
                                 * @param device the robot's BLE device
                                 */
                                @Override
                                public void onCharacteristicChanged(byte[] data, BleDevice device) {

                                    if (data.length > SEQUENCE_HEADER_SIZE) {
                                        //better UDP now, more like TCP, packages can now get lost without an exception
                                        byte[] seqNumb = Arrays.copyOfRange(data, 0, SEQUENCE_HEADER_SIZE);
                                        ByteBuffer seqBuf = ByteBuffer.wrap(seqNumb).order(ByteOrder.LITTLE_ENDIAN);
                                        int seqNum = seqBuf.getInt();
                                        byte[] packet = Arrays.copyOfRange(data, SEQUENCE_HEADER_SIZE, data.length);

                                        if (seqNum == 0) {
                                            sequenceNumber = 0;
                                            //put all packages together (1 package is 500Bytes + 4Byte sequence_header (should be 19 packages, because the image is 9.216KB) -> the last one might be smaller)
                                            //ESP32 is litte endian and Java VM is big endian, so the ByteOrder has to be changed
                                            ByteBuffer header = ByteBuffer.wrap(packet).order(ByteOrder.LITTLE_ENDIAN);
                                            size = header.getInt();
                                            imWidth = header.getInt();
                                            imHeight = header.getInt();
                                            imData = ByteBuffer.allocateDirect(PACKET_SIZE * size);
                                            counter = 0;
                                        } else if (seqNum == sequenceNumber + 1) {
                                            sequenceNumber++;
                                            //System.out.println("data.len: " + data.length + ", offset:" + counter * PACKET_SIZE + ", seqNum: " + sequenceNumber);
                                            imData.put(packet);
                                            if (counter == size - 1) {
                                                //Last packet

                                                //Create the Mat from data

                                                Mat image = new Mat(imHeight, imWidth, CV_8UC1, imData); //8 bit unsigned, 1 channel

                                                //Call the communication callback
                                                callback.waitForImage(image);

                                                //Delete the buffer and reset counter, size, width, height and sequenceNumber
                                                imData = null;
                                                counter = 0;
                                                size = 0;
                                                imWidth = 0;
                                                imHeight = 0;
                                                sequenceNumber = -1;
                                            } else {
                                                counter++;
                                            }
                                        } else {
                                            //reset everything
                                            sequenceNumber = -1;
                                            imData = null;
                                            counter = 0;
                                            size = 0;
                                            imWidth = 0;
                                            imHeight = 0;
                                        }
                                    }
                                }

                                @Override
                                public void onNotifySuccess(String notifySuccessUuid, BleDevice device) {

                                }

                                @Override
                                public void onFailure(int failCode, String info, BleDevice device) {
                                    switch (failCode) {
                                        case BleCallback.FAIL_DISCONNECTED://connection has disconnected
                                            break;
                                        case BleCallback.FAIL_OTHER://other reason
                                            break;
                                        default:
                                            break;
                                    }

                                }
                            });
                        }

                        @Override
                        public void onDisconnected(String info, int status, BleDevice device) {

                        }
                    };

                    if (BLEdevice != null) {
                        manager.connect(BLEdevice.address, connectOptions, bleConnectCallback);
                    }
                }
            }

            @Override
            public void onStart(boolean startScanSuccess, String info) {
                if (startScanSuccess) {
                    //start scan successfully
                    System.out.println(info);
                } else {
                    //fail to start scan, you can see details from 'info'
                    String failReason = info;
                    System.out.println(failReason);
                    connectionCallback.failure(info);
                }
            }

            @Override
            public void onFinish() {

            }
        });

    }

    /**
     * start motors
     * @param forward1 move motor1 forward
     * @param forward2 move motor2 forward
     * @param speed the speed of the motors
     */
    @Override
    public void motorStart(boolean forward1, boolean forward2, int speed) {
        String message = "1";

        if (forward1) {
            message += "1";
        } else {
            message += "0";
        }

        if (forward2) {
            message += "1";
        } else {
            message += "0";
        }

        byte[] data = message.getBytes();
        byte[] data2 = Arrays.copyOf(data, data.length + 1);
        data2[data.length] = (byte) speed;

        sendData(data2);
    }


    /**
     * start motors
     * overloaded method
     * @param forward1 move motor1 forward
     * @param forward2 move motor2 forward
     * @param speed the speed of the motors
     * @param time the duration the motors should be turned on
     */
    @Override
    public void motorStart(boolean forward1, boolean forward2, int speed, int time) {
        String message = "1";

        if (forward1) {
            message += "1";
        } else {
            message += "0";
        }

        if (forward2) {
            message += "1";
        } else {
            message += "0";
        }

        byte[] data = message.getBytes();
        byte[] data2 = Arrays.copyOf(data, data.length + 3);
        data2[data.length] = (byte) speed;
        data2[data.length + 1] = (byte) time;
        data2[data.length + 2] = (byte) (time >> 8); //shift to the right, because of little endian

        sendData(data2);
    }

    /**
     * stop motors
     */
    @Override
    public void motorStop() {

        String message = "122";

        byte[] data = message.getBytes();

        sendData(data);
    }

    /**
     * print on display
     * @param message the message to print
     */
    @Override
    public void printOnDisplay(String message) {

        message = "3" + message;

        byte[] data = message.getBytes();

        sendData(data);
    }

    /**
     * Start sending images from robot to phone
     */
    @Override
    public void sendImages() {

        String message = "00";

        byte[] data = message.getBytes();

        sendData(data);
    }

    /**
     * stop sending images from robot to phone
     */
    @Override
    public void stopSendImages() {

        String message = "01";

        byte[] data = message.getBytes();

        sendData(data);
    }

    /**
     * send arbitrary data to the robot
     * @param data the data to send
     */
    private synchronized void sendData(byte[] data) {
        if (data.length > 20) {
            manager.writeByBatch(BLEdevice, serviceUuid, writeUuid, data, 20, new BleWriteByBatchCallback() {
                @Override
                public void writeByBatchSuccess(byte[] data, BleDevice device) {

                }

                @Override
                public void onFailure(int failCode, String info, BleDevice device) {

                }
            });
        } else {
            manager.write(BLEdevice, serviceUuid, writeUuid, data, new BleWriteCallback() {
                @Override
                public void onWriteSuccess(byte[] data, BleDevice device) {
                    System.out.println("Success");
                }

                @Override
                public void onFailure(int failCode, String info, BleDevice device) {
                    System.out.println("Fail"); //package gets lost, happens when motor is on because of inference
                }

            });
        }
        notifyAll();
    }
}
