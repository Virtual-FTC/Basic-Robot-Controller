package com.qualcomm.robotcore.hardware;


import android.widget.TextView;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class DcMotorMaster {

    /**
     * DcMotorImpl Objects
     */

    public static DcMotorImpl motorImpl1;
    public static DcMotorImpl motorImpl2;
    public static DcMotorImpl motorImpl3;
    public static DcMotorImpl motorImpl4;
    public static DcMotorImpl motorImpl5;
    public static DcMotorImpl motorImpl6;
    public static DcMotorImpl motorImpl7;
    public static DcMotorImpl motorImpl8;

    public static void setDcMotor1(DcMotorImpl dcMotor) {
        motorImpl1 = dcMotor;
    }

    public static void setDcMotor2(DcMotorImpl dcMotor) {
        motorImpl2 = dcMotor;
    }

    public static void setDcMotor3(DcMotorImpl dcMotor) {
        motorImpl3 = dcMotor;
    }

    public static void setDcMotor4(DcMotorImpl dcMotor) {
        motorImpl4 = dcMotor;
    }

    public static void setDcMotor5(DcMotorImpl dcMotor) {
        motorImpl5 = dcMotor;
    }

    public static void setDcMotor6(DcMotorImpl dcMotor) {
        motorImpl6 = dcMotor;
    }

    public static void setDcMotor7(DcMotorImpl dcMotor) {
        motorImpl7 = dcMotor;
    }

    public static void setDcMotor8(DcMotorImpl dcMotor) {
        motorImpl8 = dcMotor;
    }


    public static Thread UnityUDPSendThread;
    public static Thread UnityUDPReceiveThread;
    static String UnityUdpIpAddress = "35.197.110.179";
    public static int robotNumber = 1;
    static boolean canUseSendSocket;
    static boolean canUseReceiveSocket;

    public static void init() {
        System.out.println("ENTERED INIT OF DC MOTOR MASTER:" + robotNumber);
        canUseSendSocket = true;
        canUseReceiveSocket = true;
        UnityUDPReceiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int port = robotNumber == 1 ? 9051 : robotNumber == 2 ? 9054 : robotNumber == 3 ? 9056 : 9058;
                    System.out.println("RECEIVE PORT: " + port);
                    DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName(UnityUdpIpAddress), port);
                    String message = "hello";
                    socket.send(new DatagramPacket(message.getBytes(), message.length()));
                    while (canUseReceiveSocket) {
                        try {
                            byte[] buffer = new byte[1024];
                            DatagramPacket response = new DatagramPacket(buffer, buffer.length);
                            socket.receive(response);
                            String responseText = new String(buffer, 0, response.getLength());

                            JSONObject jsonObject = new JSONObject(responseText);
                            DcMotorMaster.motorImpl1.encoderPosition = jsonObject.getDouble("motor1");
                            DcMotorMaster.motorImpl2.encoderPosition = jsonObject.getDouble("motor2");
                            DcMotorMaster.motorImpl3.encoderPosition = jsonObject.getDouble("motor3");
                            DcMotorMaster.motorImpl4.encoderPosition = jsonObject.getDouble("motor4");
                            DcMotorMaster.motorImpl5.encoderPosition = jsonObject.getDouble("motor5");
                            DcMotorMaster.motorImpl6.encoderPosition = jsonObject.getDouble("motor6");
                            DcMotorMaster.motorImpl7.encoderPosition = jsonObject.getDouble("motor7");
                            DcMotorMaster.motorImpl8.encoderPosition = jsonObject.getDouble("motor8");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    socket.close();
                    System.out.println("x");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        UnityUDPSendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int port = robotNumber == 1 ? 9050 : robotNumber == 2 ? 9053 : robotNumber == 3 ? 9055 : 9057;
                    System.out.println("SEND PORT: " + port);
                    DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName(UnityUdpIpAddress), port);
                    socket.send(new DatagramPacket("reset".getBytes(), "reset".length()));
                    while (canUseSendSocket) {
                        Thread.sleep(30);
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("motor1", DcMotorMaster.motorImpl1.power);
                            jsonObject.put("motor2", DcMotorMaster.motorImpl2.power);
                            jsonObject.put("motor3", DcMotorMaster.motorImpl3.power);
                            jsonObject.put("motor4", DcMotorMaster.motorImpl4.power);
                            jsonObject.put("motor5", DcMotorMaster.motorImpl5.power);
                            jsonObject.put("motor6", DcMotorMaster.motorImpl6.power);
                            jsonObject.put("motor7", DcMotorMaster.motorImpl7.power);
                            jsonObject.put("motor8", DcMotorMaster.motorImpl8.power);
                            String message = jsonObject.toString();
                            socket.send(new DatagramPacket(message.getBytes(), message.length()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    socket.close();
                    System.out.println("1x");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void start() {
        System.out.println("ENTERED START OF DC MOTOR MASTER");
        UnityUDPReceiveThread.start();
        UnityUDPSendThread.start();
    }

    public static void stop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DcMotorMaster.motorImpl1.power = 0.0;
                    DcMotorMaster.motorImpl2.power = 0.0;
                    DcMotorMaster.motorImpl3.power = 0.0;
                    DcMotorMaster.motorImpl4.power = 0.0;
                    DcMotorMaster.motorImpl5.power = 0.0;
                    DcMotorMaster.motorImpl6.power = 0.0;
                    DcMotorMaster.motorImpl7.power = 0.0;
                    DcMotorMaster.motorImpl8.power = 0.0;
                    Thread.sleep(1500);
                    canUseSendSocket = false;
                    canUseReceiveSocket = false;
                    Thread.sleep(1000);
                    UnityUDPSendThread.interrupt();
                    UnityUDPReceiveThread.interrupt();
                    System.out.println("DONE INTERRUPTING PROGRAM");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }
}
