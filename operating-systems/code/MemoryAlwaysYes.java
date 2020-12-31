import java.util.ArrayList;

public class MemoryAlwaysYes extends MemoryAllocationAlgorithm{

    public MemoryAlwaysYes(int[] availableBlockSizes) {
        super(availableBlockSizes);
    }

    @Override
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        ArrayList<MemorySlot> freeSlots = calculateFreeSlotsInAllBlocks(currentlyUsedMemorySlots);
        for(MemorySlot m: freeSlots){
            if(m.getEnd()-m.getStart()+1 >= p.getMemoryRequirements()){
                return m.getStart();
            }
        }
        return -1;
    }
}
