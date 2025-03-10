package com.ds.exp4_mutualexclusionalgo;

import java.util.*;
import java.util.concurrent.*;

// Thread class representing each process
class Process extends Thread {
    private final int processId;
    private final SharedData sharedData;
    private final Random rand = new Random();

    public Process(int processId, SharedData sharedData) {
        this.processId = processId;
        this.sharedData = sharedData;
    }

    @Override
    public void run() {
        try {
            // Random delay to simulate varied request times
            Thread.sleep(rand.nextInt(1000));

            // Random timestamp to simulate logical clock
            int timestamp = rand.nextInt(100) + 1;

            // Register this request
            sharedData.registerRequest(processId, timestamp);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Shared data class to manage requests and process them
class SharedData {
    private final int[] timestamps;
    private final List<Integer> requests = new ArrayList<>();
    private final TreeMap<Integer, Integer> timestampMap = new TreeMap<>();
    private final int totalProcesses;

    public SharedData(int totalProcesses) {
        this.totalProcesses = totalProcesses;
        this.timestamps = new int[totalProcesses]; // Initialize timestamps to 0
    }

    // Register a request with timestamp and process ID (thread-safe)
    public synchronized void registerRequest(int processId, int timestamp) {
        timestamps[processId - 1] = timestamp;
        requests.add(processId);
        timestampMap.put(timestamp, processId);

        System.out.println("Process " + processId + " requested at timestamp " + timestamp);
    }

    // Process requests to determine which processes reply or defer
    public synchronized void processRequests() {
        for (int process : requests) {
            System.out.println("\nProcessing request from process: " + process);

            for (int j = 0; j < totalProcesses; j++) {
                if (process != (j + 1)) {
                    if (timestamps[j] > timestamps[process - 1] || timestamps[j] == 0) {
                        System.out.println("Process " + (j + 1) + " Replied");
                    } else {
                        System.out.println("Process " + (j + 1) + " Deferred");
                    }
                }
            }
        }
    }

    // Display the order in which processes enter the critical section
    public synchronized void displayCriticalSectionAccessOrder() {
        System.out.println();
        for (Map.Entry<Integer, Integer> entry : timestampMap.entrySet()) {
            System.out.println("Process " + entry.getValue() + " entered Critical Section");
        }
    }
}

// Main class to initiate the simulation
public class Ricart_Agrawal_algorithm {

    public static void main(String[] args) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the total number of processes: ");
        int ns = scanner.nextInt(); // Total number of processes

        System.out.print("Enter the number of processes that will request access to the critical section: ");
        int ncs = scanner.nextInt(); // Number of requesting processes

        SharedData sharedData = new SharedData(ns);

        // Create threads for each process requesting access
        List<Thread> processThreads = new ArrayList<>();
        for (int i = 1; i <= ncs; i++) {
            Thread processThread = new Process(i, sharedData);
            processThreads.add(processThread);
            processThread.start(); // Start each thread
        }

        // Wait for all threads to finish
        for (Thread thread : processThreads) {
            thread.join();
        }

        // Process the requests and display the results
        sharedData.processRequests();
        sharedData.displayCriticalSectionAccessOrder();

        scanner.close();
    }
}
