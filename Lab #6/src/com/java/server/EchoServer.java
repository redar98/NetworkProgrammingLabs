package com.java.server;

import java.io.*;
import java.net.*;

public class EchoServer {
    private static final int port = 1721;
    private static byte[] buff = new byte[256];


    public static void main(String[] args) throws IOException {
        DatagramSocket socket = new DatagramSocket(port);
        System.err.println("Started server on port " + port);

        while(true){
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            socket.receive(packet);

            // Get address & port for sending data back
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buff, buff.length, address, port);

            String received = new String(packet.getData(), 0, packet.getLength());
            socket.send(packet);
            System.out.println("Received: " + received);

            if (received.equals("exit"))
                break;
        }
        socket.close();
    }
}
