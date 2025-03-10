package com.ds.multiclientchat;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class ChatServer implements Runnable {
    // Server socket created to get client connected
    private ServerSocket serverSocket = null;
    private Socket clientSocket = null; // Client socket created to listen to client

    // This chat server can accept up to 10 clients' connections
    private clientThread[] threads = new clientThread[10];//Messages from one client are broadcasted to others using the threads array.
    private HashMap<String,clientThread> clientMap = new HashMap<String, clientThread>();
    //For private messages, clientMap is used to identify the recipient.

    public void run() {
        int portNumber = 4221; // The port number on which the server listens
        System.out.println("Server Started...");
        System.out.println("[ Press Ctrl+C to terminate ]");
        //1
        try {
            // Server socket is created to get connected with clients
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Exception for Input/Output: " + e);
        }
        //w1
        while (true) {
            //2
            try {
                // Listening for client on clientSocket
                clientSocket = serverSocket.accept();

                for (int i = 0; i < 10; i++) {
                    if (threads[i] == null) { // If a thread is not started, initially
                        // Created an array of threads for each new client
                        (threads[i] = new clientThread(clientSocket, threads)).start();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Exception for Input/Output: " + e);
            }
        }
    }
    class clientThread extends Thread {
        BufferedReader input = null; // Input stream
        PrintStream output = null;   // Output stream
        Socket clientSocket = null;  // Client socket
        clientThread[] threads;

        public clientThread(Socket clientSocket, clientThread[] threads) {
            this.clientSocket = clientSocket; // Getting the socket of the current client for communication
            this.threads = threads;           // Getting the threads array for communication with other clients
        }


        public void run() {
            String msg;
            String userName; // Knowing the name of the current user
            //3cT
            try {
                // Created input stream to listen to the client
                input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                // Created output stream to send messages to all the listening clients
                output = new PrintStream(clientSocket.getOutputStream());


                output.println("What is your Name? Enter it:");
                userName = input.readLine();
                clientMap.put(userName, this);

                output.println(userName + " Welcome to the chat room.");
                output.println("To leave the chat room, type $$.");


                // Notify all other clients that a new user has joined
                for (int i = 0; i < 10; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].output.println("A new user arrived in the chat room: " + userName);
                    }
                }
                //w2
                // Keep reading messages from the client
                while (true) {
                    msg = input.readLine(); // Actually reading the input stream

                    if (msg.startsWith("$$")) // If the message indicates termination
                        break; // Then break

                    else if (msg.startsWith("sendto:")) {
                        String target  = msg.split(" ")[0].substring(7);
                        String message = msg.substring(7+target.length()+1);
                        sendMessageToClient(target, userName +"(privately):  " +message);
                    }

                    // Broadcast the message to all other clients
                    else{
                        for (int i = 0; i < 10; i++) {
                            if (threads[i] != null) {
                                threads[i].output.println("<" + userName + "> " + msg);
                            }
                        }
                    }
                }

                // Notify all other clients that the user is leaving
                for (int i = 0; i < 10; i++) {
                    if (threads[i] != null && threads[i] != this) {
                        threads[i].output.println("A user is leaving the chat room: " + userName + "...");
                    }
                }

                output.println(userName + " left the chat room.");
                output.println("[ Press Control+C to return to the prompt... ]");

                // Mark the current thread as null ,so it can be reused
                for (int i = 0; i < 10; i++) {
                    if (threads[i] == this) {
                        threads[i] = null;
                    }
                }
                // Close resources
                input.close();
                output.close();
                clientSocket.close();

            } catch (IOException e) {
                System.out.println("Exception for Input/Output: " + e);
            }
        }


        private void sendMessageToClient(String targetClientKey, String message) {
            clientThread targetClient;
            synchronized (clientMap) {
                targetClient = clientMap.get(targetClientKey);
            }

            if (targetClient != null) {
                targetClient.sendMessage(message);
            } else {
                sendMessage("User " + targetClientKey + " not found.");
            }
        }
        public void sendMessage(String message){
            output.println(message);
        }

    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.run();
    }
}
