package com.java.client;

import java.io.*;
import java.net.*;

public class EchoClient {
    static final String host = "230.0.0.0";
    static final int port = 1721;
    private static DatagramSocket socket;
    private static MulticastReceiver multicastReceiver;


    public EchoClient() throws SocketException {
        socket = new DatagramSocket();
    }

    public String multicastWithResponse(String msg) throws IOException {
        byte[] buff = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, InetAddress.getByName(host), port);

        socket.send(packet);
        packet = new DatagramPacket(buff, buff.length);
        socket.receive(packet);

        return new String(packet.getData(), 0, packet.getLength());
    }

    private boolean multicast(String msg) throws IOException {
        byte[] buff = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buff, buff.length, InetAddress.getByName(host), port);

        socket.send(packet);
        return true;
    }


    public String command(String command) throws IOException {
        if (command.equals("exit")){
            socket.close();
            return "Closed connection!";
        } else if (command.equals("multicast")){
            if (multicastReceiver == null || !multicastReceiver.isAlive()) {
                multicastReceiver = new MulticastReceiver();
                multicastReceiver.start();
            } else
                return "You are already in a multicast group!";
        } else if (command.equals("help")){
            return "help - display available commands\nexit - close the socket\n" +
                    "multicast - join the multicast\ndisableMulticast - stop the multicast receiver on all clients";
        } else {
            multicast(command);
        }

        return command;
    }

    public boolean isSocketClosed(){
        return socket.isClosed();
    }
}
