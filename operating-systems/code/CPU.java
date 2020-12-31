import java.util.ArrayList;

public class CPU {

    public static int clock = 0; // this should be incremented on every CPU cycle

    // Level of verbosity for outputs
    // 0-no output,1-statistics only,2-display ticks,3-display negative messages
    private static final int VERBOSITY = 2;

    private Scheduler scheduler;
    private MMU mmu;
    private Process[] processes;

    // Process that will arrive next. Points after the index of the last accepted process.
    // We assume that processes are given sorted by arrival time.
    private int currentProcess = 0;

    // Processes that have arrived and are waiting to be put in memory
    private ArrayList<Process> arrivalQueue = new ArrayList<>();

    // Indicates whether a process was completed in the previous cycle,
    // used when deciding if a process does not fit in RAM and should be tossed out
    private boolean processCompleteInCycle = false;

    // Process that is currently running in the processor
    private Process runningProcess;

    private int completedProcesses = 0;

    /**
     *
     * @param processes Array of processes sorted by arrival time
     */
    public CPU(Scheduler scheduler, MMU mmu, Process[] processes) {
        this.scheduler = scheduler;
        this.mmu = mmu;
        this.processes = processes;
    }
    
    public void run() {
        /* TODO: you need to add some code here
         * Hint: you need to run tick() in a loop, until there is nothing else to do... */
        // While not all processes have been completed

        while (completedProcesses < processes.length){

            /* Choose configuration: with or without memory */

            // WITH MEMORY

            // Check if new processes have arrived
            while(currentProcess < processes.length && processes[currentProcess].getArrivalTime() == clock){
                arrivalQueue.add(processes[currentProcess]);
                currentProcess++;
            }
            // Try to put processes from arrival queue into memory
            ArrayList<Process> readyProcesses = new ArrayList<>();
            ArrayList<Process> tooLarge = new ArrayList<>();
            for(Process p:arrivalQueue){
                if(VERBOSITY > 1){
                    System.out.print("Tick " + clock + " (pre): " );
                    System.out.print("Process arrived. PID: " + p.getPCB().getPid() + ". ");
                }

                // Try to fit into memory. If it fits, remove it from the arrival queue, change its
                // state to ready and pass it to the scheduler.
                if(mmu.loadProcessIntoRAM(p)){
                    p.getPCB().setState(ProcessState.READY,clock);
                    scheduler.addProcess(p);
                    readyProcesses.add(p);

                    if(VERBOSITY > 1){
                        System.out.println("Fit in RAM");
                    }
                }else{
                    System.out.print("Not accepted in RAM. ");
                    // If the running process is null but not due to a process being completed
                    // in the previous cycle, this means that the CPU did not execute anything the
                    // previous cycle. But since the arrivalQueue has processes available to be
                    // executed, the only reason the processor did not run anything previously (so memory is empty)
                    // and is now rejecting this process is because it does not fit in memory
                    // Also check if this is the first cycle, so then runningprocess is null anyway
                    if(!processCompleteInCycle && runningProcess == null && clock != 0){
                        tooLarge.add(p);
                        if(VERBOSITY > 1){
                            System.out.print("Tossing out process. Does not fit in memory");
                        }
                        // Increase completed process counter by one because this
                        completedProcesses++;
                    }
                    System.out.println();
                }
            }
            // Remove ready processes from arrival queue
            arrivalQueue.removeAll(readyProcesses);
            // Remove too large processes from the arrival queue
            arrivalQueue.removeAll(tooLarge);

            /*
            // WITHOUT MEMORY
            // As new processes arrive simply add them to the scheduler
            while(currentProcess < processes.length && processes[currentProcess].getArrivalTime() <= clock){
                Process p = processes[currentProcess];
                if(VERBOSITY > 1){
                    System.out.print("Tick " + clock + " (pre): " );
                    System.out.println("Process arrived. PID: " + p.getPCB().getPid());
                }
                scheduler.addProcess(p);
                p.getPCB().setState(ProcessState.READY,clock);
                currentProcess++;
            }
            */

            tick();

            // After the current cycle is complete increment the clock
            clock++;
            // Check if process has finished AFTER incrementing clock because process is completed
            // after its final computation has occurred.
            processCompleteInCycle = false;
            if(runningProcess != null) {
                if (runningProcess.getRunningTime() >= runningProcess.getBurstTime()) {
                    // Process has been completed
                    scheduler.removeProcess(runningProcess);
                    runningProcess.getPCB().setState(ProcessState.TERMINATED, clock);
                    completedProcesses++;
                    if (VERBOSITY > 1) {
                        System.out.println("Tick " + (clock - 1) + " (post): Process completed. PID: " + runningProcess.getPCB().getPid());
                    }
                    runningProcess = null;
                    processCompleteInCycle = true;
                } else if (VERBOSITY > 2) {
                    // Nothing completed and should display negative messages
                    System.out.println("Tick " + (clock - 1) + " (post): Nothing completed.");
                }
            }
        }

        // Display stats
        if(VERBOSITY > 1) {
            System.out.println("\nSTATISTICS");
            for (Process p : processes) {
                //Empty line
                System.out.println();
                System.out.println("PID: " + p.getPCB().getPid());
                System.out.println("Arrival time: " + p.getArrivalTime());
                System.out.println("Turnaround time: " + p.getTurnAroundTime());
                System.out.println("Waiting time: " + p.getWaitingTime());
            }
        }
    }
    
    public void tick() {
        /* TODO: you need to add some code here
         * Hint: this method should run once for every CPU cycle */
        if(VERBOSITY > 1){
            System.out.print("Tick " + clock + ": ");
        }
        Process processToRun = scheduler.getNextProcess();
        if(processToRun != null) {
            // Check if new process is different than process already running. If so, suspend current process.
            if (runningProcess != null && runningProcess.getPCB().getPid() != processToRun.getPCB().getPid()) {
                if (VERBOSITY > 1) {
                    System.out.print("Suspending process " + runningProcess.getPCB().getPid() + ". ");
                }
                runningProcess.waitInBackground();
            }

            // Start running the new process
            if (processToRun.getPCB().getState() == ProcessState.READY) {
                processToRun.run();
            }
            if (VERBOSITY > 1){
                System.out.println("Running process " + processToRun.getPCB().getPid());
            }
            runningProcess = processToRun;
            // Increase running time of process
            runningProcess.setRunningTime(runningProcess.getRunningTime() + 1);
        }else {
            if (VERBOSITY > 1) {
                System.out.println("No process given by scheduler.");
            }
        }
    }
}
