import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class RoundRobinTest {

    @Test
    void addProcessTest() {
        RoundRobin scheduler = new RoundRobin();
        Process array[] = {
                new Process(0, 1,0),
                new Process(0, 2,0),
                new Process(0, 3,0),
                new Process(0, 4,0),
                null,
                new Process(0, 5,0)
        };

        for (int i=0; i<array.length; i++) {
            scheduler.addProcess(array[i]);
        }

        boolean flag = true;
        int next = 1;
        ArrayList<Process> prcs = scheduler.processes;
        for (Process p : prcs) {
            if (p.getBurstTime() == next) {
                next++;
            }
            else {
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    void getNextProcess() {
        RoundRobin scheduler = new RoundRobin(2);
        Process array[] = {
                new Process(0, 4,0),
                new Process(0, 2,0),
                new Process(0, 3,0),
                new Process(0, 5,0),
                new Process(0, 1,0)
        };
        final int[] availableBlockSizes = {100};
        MemoryAllocationAlgorithm bf = new BestFit(availableBlockSizes);
        MMU mmu = new MMU(availableBlockSizes, bf);
        CPU cpu = new CPU(scheduler, mmu, array);

        for (int i=0; i<array.length; i++) {
            scheduler.addProcess(array[i]);
        }

        int burstTimeSum = 0;
        for (Process p : scheduler.processes) {
            burstTimeSum += p.getBurstTime();
        }

        int[] runningFor = new int[5];
        for (int i=0; i<runningFor.length; i++) {
            runningFor[i] = 0;
        }

        cpu.clock = 0;
        String check = "";
        Process np = scheduler.getNextProcess();
        int index;
        while (!scheduler.processes.isEmpty()) {
            check += np.getBurstTime();
            cpu.clock++;
            index = findProcess(array, np);
            runningFor[index]++;
            if (runningFor[index] >= np.getBurstTime()) {
                scheduler.removeProcess(np);
            }
            np = scheduler.getNextProcess();
        }
        assertEquals("442233551443555", check);
    }

    /** Finds the position of a process in an array of processes
     *
     * @param array An array of processes
     * @param p A process
     * @return The position of the process in the array
     */
    int findProcess(Process[] array, Process p) {
        if (p != null) {
            for (int i = 0; i < array.length; i++) {
                if (array[i].getBurstTime() == p.getBurstTime() && array[i].getArrivalTime() == p.getArrivalTime()) {
                    return i;
                }
            }
        }
        return -1;
    }
}