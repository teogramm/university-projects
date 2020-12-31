/**
 * @author George Vasiliadis
 * @version 23/12/20
 *
 * FCFS is used to implement the homonym scheduler algorithm.
 * FCFS can accept all kind of valid process values (even null) but it will only retain the non-null ones.
 * FCFS will always return the process that has arrived first among those which are currently known to scheduler.
 *
 * Intuition:
 * Scheduler keeps track of some certain processes at any given time. Those processes are the known-processes.
 * In order to figure out which one of them is the oldest entry, scheduler just sorts them out by arrival-time, picks
 * up the minimum and returns it as the next potential process.
 */
public class FCFS extends Scheduler {

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
     * Used to get the oldest-known process.
     * The oldest-known process, is defined as the one with the earliest arrival time, among the ones that are
     * currently known to scheduler. Thus, the process which arrived first, will be served first. Client classes
     * should manually handle the removal of the returned process. Otherwise, this method will always
     * return the very same process relentlessly.
     * @return the oldest-known process that has arrived to the system. If there is no such, null value is being
     * returned.
     */
    public Process getNextProcess() {
        Process next = null;
        if (!processes.isEmpty()) {
            next = processes.get(0); // Initialize arbitrarily next process as the first process in list
            int min = next.getArrivalTime();

            for (Process p : processes) {
                if (p.getArrivalTime() < min) {
                    next = p;
                    min = p.getArrivalTime();
                }
            }
        }
        return next;
    }
}
