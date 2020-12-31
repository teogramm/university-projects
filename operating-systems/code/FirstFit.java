/* @author Maria Papadopoulou

 * First Fit allocates in the first sufficient memory slot
 */
import java.util.ArrayList;

public class FirstFit extends MemoryAllocationAlgorithm {

    public FirstFit(int[] availableBlockSizes) {
        super(availableBlockSizes);
    }

    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        boolean fit = false;
        int address = -1;
        /* TODO: you need to add some code here
         * Hint: this should return the memory address where the process was
         * loaded into if the process fits. In case the process doesn't fit, it
         * should return -1. */

         /* Browsing through every memory block it checks if the process can fit in the given memory slot

             */
       int BlockIndex = 0;
        while (address == -1 && BlockIndex <= availableBlockSizes.length - 1) {
            ArrayList<MemorySlot> space = calculateFreeSlotsForBlock(currentlyUsedMemorySlots, BlockIndex);
            for (MemorySlot ms : space) {
                //checking if the process fits in the memory slot
                if (p.getMemoryRequirements() <= ms.getEnd() - ms.getStart()+1) {
                    fit = true;
                    address = ms.getStart();
                    break;
                }
            }
            //if none is suitable, move to the next memory block
            BlockIndex++;

        }
      
         //the process could not be stored
            if (fit==false){             
                return -1;
            }       
        //return where the process was stored
            else{             
                return address;
                
            }

    }
}

