package com.ds.exp6_RMI;

import java.rmi.Naming;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            // Lookup the remote object from the RMI registry
            Arithmetic arithmetic = (Arithmetic) Naming.lookup("rmi://localhost:500/ArithmeticService");

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n1. Add");
                System.out.println("2. Subtract");
                System.out.println("3. Multiply");
                System.out.println("4. Divide");
                System.out.print("Choose an operation: ");
                int choice = scanner.nextInt();

                System.out.print("Enter the first number: ");
                int a = scanner.nextInt();

                System.out.print("Enter the second number: ");
                int b = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.println("Result: " + arithmetic.add(a, b));
                        break;
                    case 2:
                        System.out.println("Result: " + arithmetic.subtract(a, b));
                        break;
                    case 3:
                        System.out.println("Result: " + arithmetic.multiply(a, b));
                        break;
                    case 4:
                        try {
                            System.out.println("Result: " + arithmetic.divide(a, b));
                        } catch (ArithmeticException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
