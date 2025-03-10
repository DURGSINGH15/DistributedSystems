package com.ds.EchoClientServer;

import java.io.*;
import java.net.*;

public class ChatClient {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        System.out.println("Connected to the server.");

        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        handleServerInteraction();
    }

    private void handleServerInteraction() throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        String userMsg;
        String serverResponse;

        while (true) {
            // Read user input
            System.out.print("You: ");
            userMsg = userInput.readLine();

            if (userMsg != null && userMsg.trim().equalsIgnoreCase("bye")) {
                out.println("bye");
                break;
            }

            // Send user message to the server via clientSocket
            out.println(userMsg);

            //Read server response
            serverResponse = in.readLine();
            if (serverResponse == null) {
                break;
            }
            System.out.println(serverResponse);
        }

        stop();
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

    public static void main(String[] args) {
        ChatClient client = new ChatClient();
        try {
            client.start("localhost", 4221);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.stop();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
