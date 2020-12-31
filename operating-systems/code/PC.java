
public class PC {

    public static void main(String[] args) {
        /* TODO: You may change this method to perform any tests you like */
        
        final Process[] processes = {
                // Process parameters are: arrivalTime, burstTime, memoryRequirements (kB)
                new Process(0, 2, 10),
                new Process(1, 2, 10),
                new Process(2, 2, 10),
                new Process(2, 3, 40),
                new Process(2, 3, 10)
        };
        final int[] availableBlockSizes = {40, 50, 40, 40,70}; // sizes in kB
        MemoryAllocationAlgorithm algorithm = new NextFit(availableBlockSizes);
        MMU mmu = new MMU(availableBlockSizes, algorithm);
        Scheduler scheduler = new SJF();
        CPU cpu = new CPU(scheduler, mmu, processes);
        cpu.run();
    }

}
