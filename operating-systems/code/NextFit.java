/*
 * Next Fit is a modification of First Fit which finds a free partion starting where it previously left off
 */

import java.util.ArrayList;

public class NextFit extends MemoryAllocationAlgorithm {

    // Information about last allocation
    int prevBlock = 0;
    int prevAddress = 0;

    public NextFit(int[] availableBlockSizes) {
        super(availableBlockSizes);
    }

    @Override
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        // Search every block starting at the block that was last allocated
        // First we search blocks starting from the last allocated block until
        // the end of the availableBlockSizes array
        for(int currentBlock = prevBlock; currentBlock < availableBlockSizes.length;currentBlock++){
            ArrayList<MemorySlot> availableSlots = calculateFreeSlotsForBlock(currentlyUsedMemorySlots,currentBlock);
            for(MemorySlot free: availableSlots){
                // Check if process fits
                if(p.getMemoryRequirements() <= free.getEnd()-free.getStart() + 1){
                    // If we are on the same block that we started we need to make sure
                    // the slot given starts after the last allocated slot
                    if(currentBlock == prevBlock){
                        if(free.getStart() < prevAddress){
                            continue;
                        }
                    }
                    prevBlock = currentBlock;
                    prevAddress = free.getStart();
                    return free.getStart();
                }
            }
        }
        // Search blocks from the start of the availableBlockSizes until prevBlock
        for(int currentBlock = 0; currentBlock < prevBlock;currentBlock++){
            ArrayList<MemorySlot> availableSlots = calculateFreeSlotsForBlock(currentlyUsedMemorySlots,currentBlock);
            for(MemorySlot free: availableSlots){
                // Check if process fits
                if(p.getMemoryRequirements() <= free.getEnd()-free.getStart() + 1){
                    // currentBlock != prevBlock
                    prevBlock = currentBlock;
                    prevAddress = free.getStart();
                    return free.getStart();
                }
            }
        }
        return -1;
    }
}

