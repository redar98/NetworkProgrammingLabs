package com.java;

import com.java.client.EchoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class TestMulticast {

    public static void main(String[] args) throws IOException {
        EchoClient client = new EchoClient();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        while(!client.isSocketClosed()){
            String lineInput = bufferedReader.readLine();
            String answer = client.command(lineInput);

            if (!answer.equals(lineInput))
                System.out.println(answer);
        }
    }
}
