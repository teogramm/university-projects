import java.util.ArrayList;

public class BestFit extends MemoryAllocationAlgorithm {
    
    public BestFit(int[] availableBlockSizes) {
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
        MemorySlot minSlot = new MemorySlot(Integer.MIN_VALUE+1, -1, Integer.MIN_VALUE+1, -1); // Very large slot size
        for (MemorySlot slot : freeSlots) {
            // If a) the slot can store the process and b) the slot's size is smaller than the minimum
            if (p.getMemoryRequirements() > 0 && p.getMemoryRequirements() <= slot.getEnd()-slot.getStart()+1 && slot.getEnd()-slot.getStart()+1 < minSlot.getEnd()-minSlot.getStart()+1) {
                minSlot = slot;
            }
        }

        // We check if a minimum slot was actually found
        if (minSlot.getEnd() != -1) {
            address = minSlot.getStart();
            fit = true;
        }

        return address;
    }

}
