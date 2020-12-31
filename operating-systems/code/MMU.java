import java.util.ArrayList;

/**
 * Class responsible for managing memory used by processes.
 * Blocks in availableBlockSize are stored in sequential addresses, depending on their position in the
 * availableBlockSizes array.
 * <p>
 * For example if we have 3 blocks: availableBlockSizes = {15,20,11}. Block 0 has addresses from 0 to 14,
 * block 1 from 15 to 34 and block 2 from 35 to 45.
 * <p>
 * Generally, blocks begin from the end of last block + 1 and end at start+blockSize-1
 */
public class MMU {

    private final int[] availableBlockSizes;
    private MemoryAllocationAlgorithm algorithm;
    private ArrayList<MemorySlot> currentlyUsedMemorySlots;
    
    public MMU(int[] availableBlockSizes, MemoryAllocationAlgorithm algorithm) {
        this.availableBlockSizes = availableBlockSizes;
        this.algorithm = algorithm;
        this.currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
    }

    /**
     * Tries to load given process into memory
     * @return Whether the process was put in memory
     */
    public boolean loadProcessIntoRAM(Process p) {
        boolean fit = false;
        /* TODO: you need to add some code here
         * Hint: this should return true if the process was able to fit into memory
         * and false if not */
        // Check for any terminated processes
        ArrayList<MemorySlot> terminatedProcesses = new ArrayList<>();
        for(MemorySlot m: currentlyUsedMemorySlots){
            if(m.getProcess().getPCB().getState() == ProcessState.TERMINATED){
                terminatedProcesses.add(m);
            }
        }
        // Remove MemorySlots of terminated processes
        currentlyUsedMemorySlots.removeAll(terminatedProcesses);

        // Try and fit them in memory using algorithm
        int address = algorithm.fitProcess(p,currentlyUsedMemorySlots);
        // If algorithm manages to fit the process
        if(address != -1){
            // Create a new MemorySlot in the given address and put it in currently used
            int blockStart = 0,blockEnd = availableBlockSizes[0] - 1;
            // Find block in witch the address belongs
            for (int i = 1; i < availableBlockSizes.length; i++) {
                if(address <= blockEnd && address >= blockStart){
                    break;
                }
                blockStart += availableBlockSizes[i-1];
                blockEnd = blockStart + availableBlockSizes[i] - 1;
            }
            MemorySlot temp = new MemorySlot(address,address+p.getMemoryRequirements()-1,blockStart,blockEnd);
            temp.setProcess(p);
            currentlyUsedMemorySlots.add(temp);
            fit = true;
        }
        return fit;
    }
}
