package com.ds.exp4_mutualexclusionalgo;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;

public class TokenRing {
    private final int numProcesses;
    private final int[] ring;
    private int token;  // Token holder
    private final Object lock = new Object();
    private int messagesSent = 0;
    private final List<Double> responseTimes = new ArrayList<>();
    private final List<Queue<Integer>> queues = new ArrayList<>();
    private final List<ProcessThread> processes = new ArrayList<>();
    private PrintWriter logger;  // Logger for writing to file

    public TokenRing(int numProcesses) {
        if (numProcesses != 3) {
            throw new IllegalArgumentException("This implementation is specifically for 3 processes.");
        }

        this.numProcesses = numProcesses;
        this.ring = new int[numProcesses];
        for (int i = 0; i < numProcesses; i++) {
            ring[i] = i;
            queues.add(new LinkedBlockingQueue<>());
        }
        this.token = 0;  // Initially, process 0 has the token.

        // Initialize the logger
        try {
            // Opening the file in overwrite mode (the second argument is false)
            logger = new PrintWriter(new FileWriter("tokenlog.txt", false), true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize threads for each process
        for (int i = 0; i < numProcesses; i++) {
            processes.add(new ProcessThread(i));
        }
    }

    public void start() {
        // Start threads
        for (ProcessThread process : processes) {
            process.start();
        }

        // Join threads with a timeout to make the implementation quicker
        for (ProcessThread process : processes) {
            try {
                process.join(10000); // Adjust timeout as needed
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Display performance metrics
        printPerformanceMetrics();

        // Close the logger
        if (logger != null) {
            logger.close();
        }
    }

    private void requestCriticalSection(int id) {
        synchronized (lock) {
            logger.println("Process " + id + " requesting critical section");
            messagesSent++;
        }
    }

    private void enterCriticalSection(int id) {
        synchronized (lock) {
            if (token == id) {
                logger.println("Process " + id + " entering critical section");
                messagesSent++;
                // Simulate critical section work
                try {
                    Thread.sleep(100); // Simulate work in the critical section
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void exitCriticalSection(int id) {
        synchronized (lock) {
            if (token == id) {
                logger.println("Process " + id + " exiting critical section");
                token = (id + 1) % numProcesses;  // Pass token to the next process in the ring
                logger.println("Token passed to process " + token);
                messagesSent++;
            }
        }
    }

    private void printPerformanceMetrics() {
        double avgResponseTime = responseTimes.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        logger.printf("\nPerformance Metrics:\nMessages Sent: %d\nAverage Response Time: %.4f seconds\n",
                messagesSent, avgResponseTime);
    }

    private class ProcessThread extends Thread {
        private final int id;
        private final Random random = new Random();

        public ProcessThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
            int iterations = 2; // Number of times each process will try to enter the critical section
            for (int i = 0; i < iterations; i++) {
                // Simulate processing before requesting critical section
                try {
                    Thread.sleep((long) (random.nextDouble() * 400 + 100)); // Sleep time between requests (0.1 to 0.5 seconds)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Request access to the critical section
                requestCriticalSection(id);

                // Measure time to enter the critical section
                long startTime = System.nanoTime();
                enterCriticalSection(id);
                long endTime = System.nanoTime();

                // Record response time in seconds
                responseTimes.add((endTime - startTime) / 1_000_000_000.0);

                // Exit critical section and pass the token to the next process
                exitCriticalSection(id);
            }
        }
    }

    public static void main(String[] args) {
        int numProcesses = 3;
        TokenRing tokenRing = new TokenRing(numProcesses);
        tokenRing.start(); //main thread
    }
}
