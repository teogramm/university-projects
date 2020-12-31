/**
 * @author George Vasiliadis
 * @version 23/12/20
 *
 * SJF is used to implement the homonym scheduler algorithm.
 * SJF can accept all kind of valid process values (even null) but it will only retain the non-null ones.
 * SJF will always return the least CPU-consuming known process, if no other is currently running.
 *
 * Intuition:
 * Scheduler keeps track of the currently running process. If there is no such process, it is allowed to skim through
 * its list of known processes to find and return the next appropriate one.
 */
public class SJF extends Scheduler {

    private Process active; // Denotes the currently running process
    private int lockUntil; // CPU time until which no other processes may enter CPU

    public SJF() {
        super();
        active = null; // At first, no processes are running
        lockUntil = -1; // Any negative value would act as a "locked" flag
    }

    /**
     * Finds and returns the known process with the minimum burst time.
     * It can be called even before adding any actual processed to scheduler. In that case null is properly returned.
     * @return the known process with the minimum burst time. If there is no such process, null is being returned.
     */
    private Process findBurstMin(){
        Process minP = null;

        if (!processes.isEmpty()){
            minP = processes.get(0); // Initialize arbitrarily next process as the first process in list
            int min = minP.getBurstTime();

            for(Process p:processes) {
                if(p.getBurstTime() < min){
                    minP = p;
                    min = p.getBurstTime();
                }
            }
        }
        return minP;
    }

    /**
     * Used to add a process to the scheduler's known-processes list.
     * @param p - process to add to scheduler's known-processes list. Null values are silently ignored.
     */
    public void addProcess(Process p) {
        if(p != null) {
            processes.add(p);
        }
    }

    /**
     * Used to get the least CPU-consuming known process.
     * The least CPU-consuming known process, is defined as the one with the shortest burst time among the ones that are
     * currently known to scheduler.
     * Algorithm works, as expected, non-preemptively.
     * If a process has started running, all other, later-arrived processes may run after this current one has properly
     * finished (even if the later ones have smaller burst times).
     * In order to operate, this function must be able to access a universal CPU.clock (read-only).
     * Client classes should manually handle the removal of the returned process. Otherwise, this method will behave
     * unpredictably.
     * @return the least CPU-consuming process, if no other process is currently running. If there is no such, null
     * value is being returned.
     */
    public Process getNextProcess() {
        Process next = active;

        // If clock has exceeded the CPU time devoted to active process, potential processes may be take its place
        if(CPU.clock >= lockUntil){
            active = findBurstMin();
            // If a process can be run
            if(active != null) {
                lockUntil = CPU.clock + active.getBurstTime();
            }
            next = active;
        }
        return next;
    }
}
