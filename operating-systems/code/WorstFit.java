import java.util.ArrayList;

public class WorstFit extends MemoryAllocationAlgorithm {
    
    public WorstFit(int[] availableBlockSizes) {
        super(availableBlockSizes);
    }

    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        boolean fit = false;
        int address = -1;
        /* TODO: you need to add some code here
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. */

        ArrayList<MemorySlot> freeSlots = calculateFreeSlotsInAllBlocks(currentlyUsedMemorySlots);
        MemorySlot maxSlot = new MemorySlot(-1, -2, -1, -2); // Very small slot size
        for (MemorySlot slot : freeSlots) {
            // If a) the slot can store the process and b) the slot's size is larger than the maximum
            if (p.getMemoryRequirements() > 0 && p.getMemoryRequirements() <= slot.getEnd()-slot.getStart()+1 && slot.getEnd()-slot.getStart()+1 > maxSlot.getEnd()-maxSlot.getStart()+1) {
                maxSlot = slot;
            }
        }

        // We check if a maximum slot was actually found
        if (maxSlot.getEnd() != -2) {
            address = maxSlot.getStart();
            fit = true;
        }

        return address;
    }

}
