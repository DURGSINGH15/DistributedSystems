package com.ds.exp6_RMI;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            // Start the RMI registry on port 500
            LocateRegistry.createRegistry(500);

            // Create an instance(remote object for client) of the ArithmeticServer
            Arithmetic server = new ArithmeticServer();

            //reg.rebind()
            // Bind the remote object (ArithmeticServer) in the RMI registry
            Naming.rebind("rmi://localhost:500/ArithmeticService", server);

            System.out.println("Server is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
