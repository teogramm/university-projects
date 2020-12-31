import java.util.ArrayList;

public abstract class MemoryAllocationAlgorithm {

    protected final int[] availableBlockSizes;

    public MemoryAllocationAlgorithm(int[] availableBlockSizes) {
        this.availableBlockSizes = availableBlockSizes;
    }

    /**
     * Tries to fit given process into memory
     * @param currentlyUsedMemorySlots Memory slots in use. Should be used only for determining free space.
     *                                 Do not write to it.
     * @return -1 if process cannot fit into memory, memory address of process otherwise
     */
    public abstract int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots);

    /**
     * Calculates all free memory regions in the given block.
     * @param blockIndex Index of the block in the availableBlockSizes array.
     * @return ArrayList of available memory slots.
     */
    protected ArrayList<MemorySlot> calculateFreeSlotsForBlock(ArrayList<MemorySlot> currentlyUsedMemorySlots, int blockIndex) {
        ArrayList<MemorySlot> availableSlots = new ArrayList<>();

        // Calculate block start and end address
        int blockStartAddr = 0;
        for (int i = 0; i < blockIndex; i++) {
            blockStartAddr += availableBlockSizes[i];
        }
        int blockEndAddr = blockStartAddr + availableBlockSizes[blockIndex] - 1;

        // Initially we assume the whole block is free
        availableSlots.add(new MemorySlot(blockStartAddr, blockEndAddr, blockStartAddr, blockEndAddr));

        /*
        The following algorithm starts by assuming the whole block is free. It then starts examining the currently
        used memory slots. Every time it finds an occupied region inside the given memory block, it "splits" the free
        space.
         */

        // Check each occupied slot
        for (MemorySlot m : currentlyUsedMemorySlots) {
            // If it is in the current block
            if (m.getStart() >= blockStartAddr && m.getEnd() <= blockEndAddr) {
                // Use a new arraylist to avoid modifying availableSlots array inside loop
                ArrayList<MemorySlot> slotsToAdd = new ArrayList<>();
                ArrayList<MemorySlot> slotsToRemove = new ArrayList<>();
                // Find free space slot in which the occupied region belongs
                for (MemorySlot freeSpaceSlot : availableSlots) {
                    if (m.getEnd() <= freeSpaceSlot.getEnd() && m.getStart() >= freeSpaceSlot.getStart()) {
                        // Split freeSpaceSlot in two slots: one from freeSpaceSlot start to used slot start
                        // and one from used slot end to freeSpaceSlot end
                        // If the used region is on the start or end limits of the block we do not create a
                        // new free slot before or after the occupied region respectively.
                        if (m.getStart() - freeSpaceSlot.getStart() > 0) {
                            slotsToAdd.add(new MemorySlot(freeSpaceSlot.getStart(), m.getStart() - 1, blockStartAddr, blockEndAddr));
                        }
                        if (freeSpaceSlot.getEnd() - m.getEnd() > 0) {
                            slotsToAdd.add(new MemorySlot(m.getEnd() + 1, freeSpaceSlot.getEnd(), blockStartAddr, blockEndAddr));
                        }
                        slotsToRemove.add(freeSpaceSlot);
                    }
                }
                availableSlots.removeAll(slotsToRemove);
                availableSlots.addAll(slotsToAdd);
            }
        }
        return availableSlots;
    }

    /**
     * @return ArrayList with free memory slots across all blocks
     */
    protected ArrayList<MemorySlot> calculateFreeSlotsInAllBlocks(ArrayList<MemorySlot> currentlyUsedMemorySlots){
        ArrayList<MemorySlot> freeSlots = new ArrayList<>();
        for(int i=0;i< availableBlockSizes.length;i++){
            freeSlots.addAll(calculateFreeSlotsForBlock(currentlyUsedMemorySlots,i));
        }
        return freeSlots;
    }
}
