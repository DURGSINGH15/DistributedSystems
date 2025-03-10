package com.ds.multiclientchat;

import java.io.*;
import java.net.*;

public class ChatClient implements Runnable {

    static Socket clientSocket = null;
    static PrintStream output = null;
    static BufferedReader input = null;
    static BufferedReader userInput = null;
    static boolean flag = false;

    public static void main(String[] args) {
        int portNumber = 4221;
        String host = "localhost";
        //1_clientTry
        try {
            // Create client socket to connect to the server
            clientSocket = new Socket(host, portNumber);

            // Create input stream to read from client console
            userInput = new BufferedReader(new InputStreamReader(System.in));

            // Create output stream to send data to the server
            output = new PrintStream(clientSocket.getOutputStream());

            // Create input stream to receive data from the server
            input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        } catch (UnknownHostException e) {
            System.err.println("Unknown host: " + host);
        } catch (IOException e) {
            System.err.println("Exception for Input/Output");
        }

        if (clientSocket != null) { // When a client is connected
            //2_clientTry
            try {
                // Start client's thread, for reading messages from server(from other clients)
                new Thread(new ChatClient()).start();
                //1w
                while (!flag) {
                    // main thread reads user input and sends it to server
                    // Read input from the console and send it on the output stream
                    output.println(userInput.readLine());
                }

                // Close resources
                output.close();
                input.close();
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("IOException: " + e);
            }
        }
    }

    @Override
    public void run() {
        String msg;
        //3_clientTry
        try {
            // Reading messages from the server
            while ((msg = input.readLine()) != null) {
                System.out.println(msg); // Printing it on self console
            }
            flag = true; // Indicates closing the session
        } catch (IOException e) {
            System.err.println("IOException: " + e);
        }
    }
}
