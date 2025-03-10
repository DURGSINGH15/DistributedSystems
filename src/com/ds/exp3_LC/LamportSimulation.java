package com.ds.exp3_LC;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.Random;
//packages used for logging events into lamport_simulation.log file
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

// Class representing events between processes
class Event {
    public int type; // 0 = local, 1 = send, 2 = receive
    public long senderId, receiverId;
    public int localTime;
    public String content;

    public Event(int type, long senderId, long receiverId, int localTime, String content) {
        this.type = type;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.localTime = localTime;
        this.content = content;
    }
}

class LamportClock extends Thread {
    private static long idCounter = 0;
    private static synchronized long getNextId() {
        return ++idCounter; //it increments idCounter first(threadId starts from 1) and then returns it.
    }
    private long threadId; // Unique ID for the thread

    private BlockingQueue<Event> buffer;
    private Random rand = new Random();
    private int time = 0; // Logical clock

    private List<String> eventLog;

    public LamportClock(BlockingQueue<Event> buffer) {
        this.buffer = buffer;
        this.threadId = getNextId();
        this.setName("LamportClock-" + threadId); // Name for easier identification
        this.eventLog = new ArrayList<>();
    }
    private void logEvent(String event) {
        System.out.println(event);
        eventLog.add(event);
    }

    private void localEvent() {
        time++;
        String logMessage = "Thread " + getName() + " local event. Time: " + time;
        logEvent(logMessage);
    }
    private void sendEvent(long receiverId) throws InterruptedException {

        time++; //first the time would increment and then send the event.
        String content = "Message from " + getName() + " to LamportClock-" + receiverId;
        Event event = new Event(1, threadId, receiverId, time, content);
        buffer.put(event); //inserting event inside the shared buffer

        String logMessage = "Thread " + getName() + " sent message to LamportClock-" + receiverId + ". Time: " + time;
        logEvent(logMessage);
    }

    private void receiveEvent(Event event) {
        time = Math.max(time, event.localTime) + 1;
        String logMessage = "Thread " + getName() + " received message from LamportClock-" + event.senderId + ". Updated time: " + time;
        logEvent(logMessage);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 2; i++) { // Limit the number of iterations
                if (rand.nextBoolean()) {
                    // Simulate an internal event
                    localEvent();
                } else {
                    // Simulate sending a message
                    long receiverId = rand.nextInt(3) + 1; //  3 threads(0,1,2) intialised => receiverId(1,2,3)
                    if (receiverId != threadId) { //to ensure threads can't consume the message that they sent
                        sendEvent(receiverId);
                    }
                }

                // checks if a message is available in queue without getting stuck indefinitely( timeout of 500ms when polling)
                Event receivedEvent = buffer.poll(500, TimeUnit.MILLISECONDS);
                if (receivedEvent != null) {
                    String logMessage = "Thread " + getName() + " (ID: " + threadId + ") polling event. Receiver ID: " + receivedEvent.receiverId;
                    logEvent(logMessage);
                    if (receivedEvent.receiverId == threadId) { //if the message was meant for the thread checking it then receiveEvent
                        receiveEvent(receivedEvent);
                    } else {
                        logMessage = "Thread " + getName() + " ignoring event for receiver ID " + receivedEvent.receiverId;
                        logEvent(logMessage);
                        buffer.put(receivedEvent); // Put the event back into the queue if it's not meant for this thread

                    }
                }
                // Sleep for a random time to ensure non-determinism but keeping sleep time less to avoid delays
                Thread.sleep(rand.nextInt(100));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public List<String> getEventLog() {
        return eventLog;
    }

    public long getThreadId() {
        return threadId;
    }
}

public class LamportSimulation {
    private static final String LOG_FILE = "lamport_simulation.log";


    public static void main(String[] args) {
        BlockingQueue<Event> sharedBuffer = new LinkedBlockingQueue<>(10); // Bounded buffer

        // Create 3 threads for Lamport clocks
        LamportClock process1 = new LamportClock(sharedBuffer);
        LamportClock process2 = new LamportClock(sharedBuffer);
        LamportClock process3 = new LamportClock(sharedBuffer);

        //start() method invokes the run() method of Thread class,
        //once we call start() method on instances of Thread class(or its subclass(class extending it)), JVM creates new threads for each of these LamportClock objects
        //these new threads are called child threads.
        process1.start();
        process2.start();
        process3.start();
        //each thread will start running concurrently in the background, that is run() method is executed in parallel with other threads.

        try {
            // The main thread waits (blocks) until all threads(process1, process2, and process3) have completed their tasks.
            process1.join();
            process2.join();
            process3.join();

            writeLogsToFile(process1, process2, process3);

            System.out.println("Simulation complete. Log file: " + LOG_FILE);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        System.out.println("Simulation complete.");
    }

    private static void writeLogsToFile(LamportClock... processes) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE))) {
            for (LamportClock process : processes) {
                writer.println("Process " + process.getThreadId() + " (" + process.getName() + ") Event Log:");
                for (String event : process.getEventLog()) {
                    writer.println("  " + event);
                }
                writer.println();
            }
        }
    }
}

