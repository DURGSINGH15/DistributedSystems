package org.example;

import io.grpc.stub.StreamObserver;
import org.example.ArithmeticServiceProto.ArithmeticRequest;
import org.example.ArithmeticServiceProto.ArithmeticResponse;

public class ArithmeticServiceImpl extends ArithmeticServiceGrpc.ArithmeticServiceImplBase {
    @Override
    public void add(ArithmeticRequest request, StreamObserver<ArithmeticResponse> responseObserver) {
        int result = request.getNum1() + request.getNum2();
        ArithmeticResponse response = ArithmeticResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void subtract(ArithmeticRequest request, StreamObserver<ArithmeticResponse> responseObserver) {
        int result = request.getNum1() - request.getNum2();
        ArithmeticResponse response = ArithmeticResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void multiply(ArithmeticRequest request, StreamObserver<ArithmeticResponse> responseObserver) {
        int result = request.getNum1() * request.getNum2();
        ArithmeticResponse response = ArithmeticResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void divide(ArithmeticRequest request, StreamObserver<ArithmeticResponse> responseObserver) {
        int result;
        if (request.getNum2() == 0) {
            result = (int) Float.POSITIVE_INFINITY; // Handle division by zero
        } else {
            result = (int) request.getNum1() / request.getNum2();
        }
        ArithmeticResponse response = ArithmeticResponse.newBuilder().setResult(result).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
