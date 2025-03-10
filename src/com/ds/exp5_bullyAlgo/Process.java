package com.ds.exp5_bullyAlgo;
import java.util.Random;
public class Process implements Runnable {
    private final int pid;  // Unique Process ID
    private final ProcessManager manager;  // Reference to the ProcessManager
    private boolean isAlive = true;  // Flag to indicate if this process is alive
    protected boolean isLeader = false;  // Flag to indicate if this process is the leader
    private static boolean stopSimulation = false; // Flag to stop the simulation

    public Process(int pid, ProcessManager manager) {
        this.pid = pid;
        this.manager = manager;
    }
    public int getPid() {
        return pid;
    }
    public boolean isAlive() {
        return isAlive;
    }
    // action of failing done in Process class by simulated by ProcessManager
    public void fail() {
        isAlive = false;
        System.out.println("Process " + pid + " has failed.");
    }

    // Simulate the process recovering and possibly triggering an election
    public void recover() {
        isAlive = true;
        System.out.println("Process " + pid + " has recovered.");
        if (manager.getLeader() == null || pid > manager.getLeader().getPid()) {
            startElection();
        }
    }
    // Start an election process
    public void startElection() {
        System.out.println("Process " + pid + " starts an election.");
        for (Process p : manager.getProcesses()) {
            // Send election message to all higher PID processes
            if (p.getPid() > pid && p.isAlive()) {
                System.out.println("Process " + pid + " sends election message to Process " + p.getPid());
                p.respondElection(this);
                return; // Higher PID process takes over the election
            }
        }
        // If no higher PID process is alive, this process becomes the leader
        becomeLeader();
    }

    // Respond to an election message from another process which have definitely lower process id
    public void respondElection(Process initiator) { //initiator process knows the processes having greater process id than itself
        //hence the process responding to initiator has to start election to confirm there is no process with greater process id alive
        if (isAlive) {
            System.out.println("Process " + pid + " responds to election from Process " + initiator.getPid());
            startElection(); // This process starts its own election
        }
    }

    // This process becomes the new leader
    public void becomeLeader() {
        isLeader = true;
        manager.setLeader(this); // Notify the manager that this process is now the leader
        System.out.println("Process " + pid + " is elected as the new leader.");
    }

    // Stop the simulation by setting the flag to true
    public static void stopAllProcesses() {
        stopSimulation = true;
    }

    // Main execution logic for each process
    @Override
    public void run() {
        Random rand = new Random();
        while (isAlive && !stopSimulation) {
            try {
                Thread.sleep(rand.nextInt(5000) + 1000); // Random delay between actions
                if (isLeader) {
                    // If this process is the leader, announce that it's alive
                    System.out.println("Process " + pid + " (Leader) is alive.");
                } else if (manager.getLeader() == null || !manager.getLeader().isAlive()) {
                    // If there's no leader or the current leader has failed, start a new election
                    startElection();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
