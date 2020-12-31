import java.util.ArrayList;

/**
 * Dummy algorithm used to test the calculateFreeSlotsForBlock function
 */
public class DummyAlgorithm extends MemoryAllocationAlgorithm{
    public DummyAlgorithm(int[] availableBlockSizes) {
        super(availableBlockSizes);
    }

    @Override
    public int fitProcess(Process p, ArrayList<MemorySlot> currentlyUsedMemorySlots) {
        return 0;
    }
}
