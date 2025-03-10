package com.ds.exp5_bullyAlgo;

import java.util.ArrayList;
import java.util.List;

public class ProcessManager {
    private List<Process> processes = new ArrayList<>(); // List of all processes
    private Process leader = null; // Reference to the current leader

    // Add a process to the system
    public void addProcess(Process process) {
        processes.add(process);
    }

    // Get the list of processes
    public List<Process> getProcesses() {
        return processes;
    }

    // Set the current leader process
    public void setLeader(Process leader) {
        this.leader = leader;
    }

    // Get the current leader process
    public Process getLeader() {
        return leader;
    }

    // Simulate failure of a process with a specific PID
    public void simulateFailure(int pid) {
        for (Process p : processes) {
            if (p.getPid() == pid) {
                p.fail();
                break;
            }
        }
    }

    // Simulate recovery of a process with a specific PID
    public void simulateRecovery(int pid) {
        for (Process p : processes) {
            if (p.getPid() == pid) {
                p.recover();
                break;
            }
        }
    }
}
