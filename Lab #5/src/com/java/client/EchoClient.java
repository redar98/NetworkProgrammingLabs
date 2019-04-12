package com.java.client;

import java.io.*;
import java.net.Socket;

public class EchoClient {
    private static final String host = "localhost";
    private static final int port = 1721;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(host, port);
        System.err.println("Connected to " + host + " on port " + port);

        DataInputStream inputStream = new DataInputStream(socket.getInputStream());
        DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while(true){
            String lineInput = bufferedReader.readLine();

            outputStream.writeUTF(lineInput);

            System.out.println(inputStream.readUTF());

            if (lineInput.equals("exit")) break;
        }

        System.err.println("Closing connection to " + host);
    }
}
