package com.ds.exp5_bullyAlgo;

public class BullyElection {
    public static void main(String[] args) {
        ProcessManager manager = new ProcessManager();

        // Create and start the processes
        for (int i = 1; i <= 5; i++) {
            Process process = new Process(i, manager);
            manager.addProcess(process);
            new Thread(process).start();
        }

        // Simulate initial election
        manager.getProcesses().get(manager.getProcesses().size() - 1).startElection();

        try {
            // Wait for the initial election to complete
            Thread.sleep(5000);

            // Simulate leader failure
            System.out.println("Simulating leader failure...");
            manager.simulateFailure(5);

            // Wait for new election to complete
            Thread.sleep(5000);

            // Simulate recovery of the previous leader
            System.out.println("Simulating recovery of previous leader...");
            manager.simulateRecovery(5);

            // Allow some time for the recovery election to complete
            Thread.sleep(5000);

            // Stop all processes after the simulation is complete
            Process.stopAllProcesses();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
