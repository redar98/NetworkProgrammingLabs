package com.java.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastReceiver extends Thread {
    private DatagramPacket packet;
    private byte[] buff = new byte[256];

    public void run(){
        try {
            MulticastSocket socket = new MulticastSocket(EchoClient.port);
            InetAddress group = InetAddress.getByName(EchoClient.host);
            socket.joinGroup(group);
            System.err.println("MulticastReceiver joined the group at " + group);

            while(true){
                packet = new DatagramPacket(buff, buff.length);
                socket.receive(packet);

                String received = getReceivedMessage();
                System.out.println("Multicast received: " + received);

                if (received.equals("disableMulticast")) break;
            }
            System.err.println("MulticastReceiver stopped by a request that was received!");
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getReceivedMessage(){
        if (packet != null)
            return new String(packet.getData(), 0, packet.getLength());
        else
            return null;
    }
}
