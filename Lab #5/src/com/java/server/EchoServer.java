package com.java.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class EchoServer {
    private static final EchoServer instance = new EchoServer();
    private static final int port = 1721;
    private static List<EchoThread> clientThreads = new ArrayList<>();
    private static List<String> savedMessages = new ArrayList<>();


    private EchoServer(){ }

    static EchoServer getInstance(){
        return instance;
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(port);
        System.err.println("Started server on port " + port);

        while(true){
            Socket clientSocket = serverSocket.accept();

            EchoThread clientThread = new EchoThread(clientSocket);
            clientThread.start();
        }
    }

    int getConnections(){
        synchronized (this) {
            return clientThreads.size();
        }
    }

    void addClientThread(EchoThread clientThread){
        synchronized (this) {
            clientThreads.add(clientThread);
        }
    }

    void removeClientThread(EchoThread clientThread){
        synchronized (this) {
            clientThreads.remove(clientThread);
        }
    }

    void addMessage(String message){
        synchronized (this) {
            savedMessages.add(message);
        }
    }

    String getMessages(){
        synchronized (this){
            String message = "";
            for(String s: savedMessages){
                message += s;
                if (savedMessages.indexOf(s) < savedMessages.size() - 1) message += "\n";
            }
            return message;
        }
    }
}
