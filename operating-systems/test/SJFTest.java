import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SJFTest {

    @Test
    void addProcessTest() {
        SJF scheduler = new SJF();
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
        int nextBurstTime = 1;
        ArrayList<Process> prcs = scheduler.processes;
        for (Process p : prcs) {
            if (p.getBurstTime() == nextBurstTime) {
                nextBurstTime++;
            }
            else {
                flag = false;
            }
        }
        assertTrue(flag);
    }

    @Test
    void getNextProcessTest() {
        SJF scheduler = new SJF();
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

        String check = "";
        Process np = scheduler.getNextProcess();
        cpu.clock = 0;
        while (cpu.clock < burstTimeSum) {
            scheduler.removeProcess(np);
            np = scheduler.getNextProcess();
            check += np.getBurstTime();
            cpu.clock++;
        }
        assertEquals("122333444455555", check);
    }
}