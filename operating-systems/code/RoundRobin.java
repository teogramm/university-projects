/**
 * @author George Vasiliadis
 * @version 27/12/20
 *
 * RoundRobin is used to implement the homonym scheduler algorithm.
 * RoundRobin can accept all kind of valid process values (even null) but it will only retain the non-null ones.
 * RoundRobin will always return the next known RoundRobin (RR) process, if no other is currently running.
 *
 * The next RR process is defined as:
 * - For an empty processes' list, it is null.
 * - For the last process in processes' list, it is the very first process in that list
 * - For any process in processes' list, it is the one on its right.
 *
 * Intuition:
 * Scheduler keeps track of the currently running process. If it is time for a new process to take control, the
 * scheduler finds which should come next and updates the active process. In order for such a change to happen any of
 * the following conditions should be true:
 * - There is no active process
 * - The previously running process has just finished
 * - The current time-slice has ended
 */
public class RoundRobin extends Scheduler {
    private int quantum;
    private Process active;
    private int lockUntil;

    public RoundRobin() {
        this.quantum = 1; // default quantum
        active = null;
        lockUntil = -1;
    }

    public RoundRobin(int quantum) {
        this();
        this.quantum = quantum;
    }

    @Override
    public void addProcess(Process p) {
        if(p != null){
            processes.add(p);
        }
    }

    @Override
    public void removeProcess(Process p){
        super.removeProcess(p);
        // When a process is being removed the state of the internal list is being altered.
        // The only use of this function is to remove a process that has just finished (AKA the active process).
        // Thus, the active process should point to nothing.
        active = null;
    }

    /**
     * Essential method used to check whether it is time for a change of active process.
     * If there is no process running (e.g. the last one has just finished) it might be time for change.
     * If the CPU clock has overtaken the time-lock it might be time for change as well.
     * @return true if the currently running process should be changed.
     */
    private boolean isTimeForChange(){
        return active == null || CPU.clock >= lockUntil;
    }

    /**
     * Essential method used to retrieve the next RR process.
     * The next RR process is defined as the process which comes right after the one that is currently being used in a
     * circular manner.
     *
     * Round Robin is implemented over a List that is used as a Queue. The next item is always the first item in that
     * list. Any last item is being placed at the very end of that list. Whenever an item is being accessed it is
     * being removed from the first place and appended to the end of it. Every other item is shifted clockwise to the
     * left.
     * @return the next RR process in known processes. If there are no processes null is being returned.
     */
    private Process circularNext(){
        Process next = null;
        if(!processes.isEmpty()) {

            // If there is some active process, take it from the start of the processes list and place it at the end.
            if(active != null) {
                Process temp = processes.remove(0);
                processes.add(temp);
            }

            // Return the leftmost item of the processes list.
            next = processes.get(0);
        }
        return next;
    }

    /**
     * Used to get the next RR process in a circular way. The next RR process, is defined as the one that
     * comes right after the currently active one in a linked list.
     *
     * Client classes should manually handle the removal of the returned process. Otherwise, this method will behave
     * unpredictably.
     * @return the next RR process. If there is no such, null value is being returned.
     */
    @Override
    public Process getNextProcess() {
        Process next = active;
        if(isTimeForChange()){
            next = active = circularNext();

            // Update the time-lock only if there is some pending process.
            // This might not be the case when method is called for the first time. The time-lock shouldn't be
            // constrained with no reason.
            if(next != null) {
                lockUntil = CPU.clock + quantum;
            }
        }
        return next;
    }
}