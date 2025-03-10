package com.ds.EchoClientServer;

import java.io.*;
import java.net.*;

public class ChatServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int portno) throws IOException {
        try {
            serverSocket = new ServerSocket(portno);
            System.out.println("Server started");

            clientSocket = serverSocket.accept();
            System.out.println("Client connected.");

            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            handleClientInteraction();
        } finally {
            stop();
        }
    }

    private void handleClientInteraction() throws IOException {
        String clientMsg;

        while (true) {
            clientMsg = in.readLine();
            if (clientMsg == null || clientMsg.trim().equalsIgnoreCase("bye")) { //end-of-input is "bye" here.
                out.println("Server stopped.");
                break;
            }
            System.out.println("Client: " + clientMsg); //seen on the server side

            out.println("Server: " + clientMsg);
            //written to clientSocket's outputStream which will be red and stored as serverResponse on the client-side.
        }
    }

    public void stop() throws IOException {
        //Java runtime by default closes the resources in reverse order that they were created,here done manually
        //streams connected to a socket should be closed before the socket itself is closed.
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();

        try {
            server.start(4221);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
