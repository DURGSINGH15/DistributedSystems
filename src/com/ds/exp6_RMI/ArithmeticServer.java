package com.ds.exp6_RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ArithmeticServer extends UnicastRemoteObject implements Arithmetic {

    // Constructor
    public ArithmeticServer() throws RemoteException {
        super();
    }
    @Override
    public int add(int a, int b) throws RemoteException {
        return a + b;
    }
    @Override
    public int subtract(int a, int b) throws RemoteException {
        return a - b;
    }
    @Override
    public int multiply(int a, int b) throws RemoteException {
        return a * b;
    }
    @Override
    public float divide(int a, int b) throws RemoteException {
        if (b == 0) {
            throw new ArithmeticException("Division by zero is not allowed");
        }
        return (float) a / b;
    }
}
