import java.util.ArrayList;

public class ProcessControlBlock {
    
    private final int pid;
    private ProcessState state;
    // the following two ArrayLists should record when the process starts/stops
    // for statistical purposes
    private ArrayList<Integer> startTimes; // when the process starts running
    private ArrayList<Integer> stopTimes;  // when the process stops running

    /**
     * Static variable used as a global counter for PIDs
     */
    private static int pidTotal= 0;
    
    public ProcessControlBlock() {
        this.state = ProcessState.NEW;
        this.startTimes = new ArrayList<>();
        this.stopTimes = new ArrayList<>();
        /* TODO: you need to add some code here */
        this.pid = pidTotal;
        // Increment global PID counter
        pidTotal += 1;
    }

    public ProcessState getState() {
        return this.state;
    }
    
    public void setState(ProcessState state, int currentClockTime) {
        /* TODO: you need to add some code here */
        if( state == ProcessState.RUNNING){
            startTimes.add(currentClockTime);
        }else if(this.state == ProcessState.RUNNING && (state == ProcessState.TERMINATED || state == ProcessState.READY)){
            // A process must be running to be stopped. If a process is moved from NEW to RUNNING it is not stopped.
            stopTimes.add(currentClockTime);
        }
        this.state = state;
    }
    
    public int getPid() { 
        return this.pid;
    }
    
    public ArrayList<Integer> getStartTimes() {
        return startTimes;
    }
    
    public ArrayList<Integer> getStopTimes() {
        return stopTimes;
    }
    
}
