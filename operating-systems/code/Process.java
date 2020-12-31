import java.util.ArrayList;

public class Process {
    private ProcessControlBlock pcb;
    private int arrivalTime;
    private int burstTime;
    private int memoryRequirements;

    // Cycles that the process spent running
    private int runningTime = 0;

    
    public Process(int arrivalTime, int burstTime, int memoryRequirements) {
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.memoryRequirements = memoryRequirements;
        this.pcb = new ProcessControlBlock();
    }
    
    public ProcessControlBlock getPCB() {
        return this.pcb;
    }
   
    public void run() {
        /* TODO: you need to add some code here
         * Hint: this should run every time a process starts running */
        pcb.setState(ProcessState.RUNNING,CPU.clock);
    }
    
    public void waitInBackground() {
        /* TODO: you need to add some code here
         * Hint: this should run every time a process stops running */
        pcb.setState(ProcessState.READY,CPU.clock);
    }

    public int getBurstTime(){
        return burstTime;
    }

    public int getArrivalTime(){
        return arrivalTime;
    }

    public double getWaitingTime() {
        /* TODO: you need to add some code here
         * and change the return value */
        // Waiting time is turnaround time-running time
        return getTurnAroundTime()-runningTime;
    }
    
    public double getResponseTime() {
        /* TODO: you need to add some code here
         * and change the return value */
        // Response time = first run time-arrival time
        ArrayList<Integer> startTimes = getPCB().getStartTimes();
        if(startTimes.isEmpty()){
            return -1;
        }
        return startTimes.get(0)-arrivalTime;
    }
    
    public double getTurnAroundTime() {
        /* TODO: you need to add some code here
         * and change the return value */
        // Turnaround time = time completed-arrival time
        // Time completed is the last time the process was stopped
        ArrayList<Integer> stopTimes = getPCB().getStopTimes();
        if(stopTimes.isEmpty()){
            // If the process was not stopped, it means it was not processed.
            return -1;
        }
        return stopTimes.get(stopTimes.size() - 1)-arrivalTime;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public void setRunningTime(int runningTime) {
        this.runningTime = runningTime;
    }

    public int getMemoryRequirements() {
        return memoryRequirements;
    }
}
