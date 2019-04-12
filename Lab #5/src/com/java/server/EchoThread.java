package com.java.server;

import javax.xml.crypto.Data;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Calendar;

public class EchoThread extends Thread {
    private Socket socket;

    EchoThread(Socket clientSocket) {
        this.socket = clientSocket;
    }

    public void run() {
        try (DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream())
        ){
            System.err.println(this + ": Accepted connection on " + socket.getRemoteSocketAddress());
            EchoServer.getInstance().addClientThread(this);

            for(String receivedLine = ""; !receivedLine.equals("exit"); ){
                receivedLine = inputStream.readUTF();
                System.out.println(this + "@Client: " + receivedLine);

                interpretCommand(outputStream, receivedLine);
            }

            System.err.println(this + ": Closing connection for " + socket.getRemoteSocketAddress());
            EchoServer.getInstance().removeClientThread(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void interpretCommand(DataOutputStream outputStream, String command) throws IOException {
        command = command.trim();
        String feedback = command + "\n";
        String[] commandPart = command.toLowerCase().split(" ");

        switch (commandPart[0]){
            case "help":
                feedback += "help - display commands\nexit - close the connection\nconnections - display active connections\n" +
                        "factorial <int> - calculate factorial of a number\ntime - display the server time\nsave <String> - saves a message in the server\n" +
                        "messages - gets all messages saved in the server";
                break;
            case "connections":
                feedback += "Total active connections to the server: " + EchoServer.getInstance().getConnections();
                break;
            case "factorial":
                if (commandPart.length < 2) {
                    feedback += "Usage: factorial <integer number>";
                    break;
                }
                try {
                    final int number = Integer.parseInt(commandPart[1]);
                    feedback += "Factorial of " + number + " is " + factorialOf(number);
                } catch (Exception e){
                    feedback += "Invalid arguments!";
                }
                break;
            case "time":
                feedback += "Current server time: " + Calendar.getInstance().getTime();
                break;
            case "save":
                if (commandPart.length < 2){
                    feedback += "Usage: save <String message>";
                    break;
                }
                EchoServer.getInstance().addMessage(command.substring(command.indexOf(' ') + 1));
                feedback += "Your message is saved in server!";
                break;
            case "messages":
                feedback += EchoServer.getInstance().getMessages();
                break;
        }
        outputStream.writeUTF(feedback.equals(command + "\n")? feedback: feedback + "\n");
    }

    private static BigInteger factorialOf(int number) {
        BigInteger factorialSum = BigInteger.ONE;

        for(int i = 2; i < number; i++)
            factorialSum = factorialSum.multiply(BigInteger.valueOf(i));

        return factorialSum;
    }

    @Override
    public String toString(){
        return "EST-" + Thread.currentThread().getId();
    }
}
