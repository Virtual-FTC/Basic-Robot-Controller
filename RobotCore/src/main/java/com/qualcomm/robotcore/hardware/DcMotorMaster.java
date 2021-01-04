package com.qualcomm.robotcore.hardware;


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
//    public static boolean canRunUDPThreads;
//    private static DatagramSocket RXsocket;
//    private static DatagramSocket TXsocket;
//    private static int TX_RXCount = 0;

    public static void start() {
        System.out.println("ENTERED START OF DC MOTOR MASTER");
//        canRunUDPThreads = true;
//        if (TX_RXCount == 0) {
//            TX_RXCount++;
        UnityUDPReceiveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int port = 9051;
                    DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName(UnityUdpIpAddress), port);
                    String message = "hello";
                    socket.send(new DatagramPacket(message.getBytes(), message.length()));
                    while (true) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        UnityUDPSendThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int port = 9050;
                    DatagramSocket socket = new DatagramSocket();
                    socket.connect(InetAddress.getByName(UnityUdpIpAddress), port);
                    while (true) {
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
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        UnityUDPReceiveThread.start();
        UnityUDPSendThread.start();
    }
}
