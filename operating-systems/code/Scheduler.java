import java.util.ArrayList;
    /* the addProcess() method should add a new process to the list of
     * processes that are candidates for execution. This will probably
     * differ for different schedulers */

public abstract class Scheduler {

    protected ArrayList<Process> processes; // the list of processes to be executed
    
    public Scheduler() {
        this.processes = new ArrayList<>();
    }

    /* the addProcess() method should add a new process to the list of
     * processes that are candidates for execution. This will probably
     * differ for different schedulers */
    public abstract void addProcess(Process p);
    
    /* the removeProcess() method should remove a process from the list
     * of processes that are candidates for execution. Common for all
     * schedulers. */
    public void removeProcess(Process p) {
        processes.remove(p);
    }

    /**
     * Returns the process that should be executed next by the CPU
     * @return null if there are no available processes, Process otherwise
     */
    public abstract Process getNextProcess();
}
