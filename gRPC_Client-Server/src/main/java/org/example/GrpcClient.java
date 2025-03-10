package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Scanner;
import org.example.ArithmeticServiceProto.ArithmeticRequest;
import org.example.ArithmeticServiceProto.ArithmeticResponse;

public class GrpcClient {
    public static void main(String[] args) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        ArithmeticServiceGrpc.ArithmeticServiceBlockingStub stub = ArithmeticServiceGrpc.newBlockingStub(channel);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter first number (or type 'exit' to quit): ");
            String input = scanner.next();
            if (input.equalsIgnoreCase("exit")) {
                break;
            }
            int num1 = Integer.parseInt(input);

            System.out.println("Enter second number: ");
            int num2 = scanner.nextInt();

            System.out.println("Choose an operation: 1-Add, 2-Subtract, 3-Multiply, 4-Divide");
            int choice = scanner.nextInt();

            ArithmeticRequest request = ArithmeticRequest.newBuilder()
                    .setNum1(num1)
                    .setNum2(num2)
                    .build();

            ArithmeticResponse response;
            switch (choice) {
                case 1:
                    response = stub.add(request);
                    break;
                case 2:
                    response = stub.subtract(request);
                    break;
                case 3:
                    response = stub.multiply(request);
                    break;
                case 4:
                    response = stub.divide(request);
                    break;
                default:
                    System.out.println("Invalid choice.");
                    continue;
            }

            System.out.println("Result: " + response.getResult());
        }

        channel.shutdown();
        System.out.println("Client exited.");
    }
}
