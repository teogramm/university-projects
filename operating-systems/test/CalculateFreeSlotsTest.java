import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class CalculateFreeSlotsTest {

    @Test
    void empty_block(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15});
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();

        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,0);
        Assertions.assertEquals(emptyMemorySlots.size(),1);

        MemorySlot emptySlot = emptyMemorySlots.get(0);
        Assertions.assertEquals(emptySlot.getStart(),0);
        Assertions.assertEquals(emptySlot.getEnd(),14);
    }

    @Test
    void full_block(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15});

        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();
        currentlyUsedMemorySlots.add(new MemorySlot(0,14,0,14));

        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,0);

        Assertions.assertEquals(emptyMemorySlots.size(),0);
    }

    @Test
    void single_block_ordered(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15});

        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();
        currentlyUsedMemorySlots.add(new MemorySlot(0,2,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(4,11,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(14,14,0,14));

        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,0);

        Assertions.assertEquals(emptyMemorySlots.size(),2);
    }

    @Test
    void single_block_unordered(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15});

        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();
        currentlyUsedMemorySlots.add(new MemorySlot(14,14,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(0,2,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(4,11,0,14));

        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,0);

        Assertions.assertEquals(emptyMemorySlots.size(),2);
    }

    @Test
    void multi_block_empty(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15,55});
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();

        // Add memory slots in other block
        currentlyUsedMemorySlots.add(new MemorySlot(14,14,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(0,2,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(4,11,0,14));

        // Calculate for block with size 55
        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,1);
        Assertions.assertEquals(emptyMemorySlots.size(),1);

        MemorySlot emptySlot = emptyMemorySlots.get(0);
        Assertions.assertEquals(emptySlot.getStart(),15);
        Assertions.assertEquals(emptySlot.getEnd(),69);
    }

    @Test
    void multi_block_full(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15,55});

        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();
        currentlyUsedMemorySlots.add(new MemorySlot(15,69,15,69));

        // Add memory slots in other block
        currentlyUsedMemorySlots.add(new MemorySlot(14,14,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(0,2,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(4,11,0,14));

        // Calculate for block with size 55
        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,1);
        Assertions.assertEquals(emptyMemorySlots.size(),0);
    }

    @Test
    void multi_block_unordered(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15,55});

        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();
        // Add some used slots in current block
        currentlyUsedMemorySlots.add(new MemorySlot(18,20,15,69));

        // Add some used slots in other block
        currentlyUsedMemorySlots.add(new MemorySlot(14,14,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(0,2,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(4,11,0,14));

        // Add some used slots in current block
        currentlyUsedMemorySlots.add(new MemorySlot(50, 55,15,69));

        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsForBlock(currentlyUsedMemorySlots,1);

        Assertions.assertEquals(emptyMemorySlots.size(),3);

        int emptySpaceSum = 0;
        for(MemorySlot empty:emptyMemorySlots){
            emptySpaceSum += empty.getEnd()-empty.getStart()+1;
        }
        Assertions.assertEquals(emptySpaceSum,55-9);
    }

    @Test
    void multi_block_all(){
        MemoryAllocationAlgorithm dummy = new DummyAlgorithm(new int[]{15,55});

        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<>();
        // Add some used slots in current block
        currentlyUsedMemorySlots.add(new MemorySlot(18,20,15,69));

        // Add some used slots in other block
        currentlyUsedMemorySlots.add(new MemorySlot(14,14,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(0,2,0,14));
        currentlyUsedMemorySlots.add(new MemorySlot(4,11,0,14));

        // Add some used slots in current block
        currentlyUsedMemorySlots.add(new MemorySlot(50, 55,15,69));

        ArrayList<MemorySlot> emptyMemorySlots = dummy.calculateFreeSlotsInAllBlocks(currentlyUsedMemorySlots);

        Assertions.assertEquals(emptyMemorySlots.size(),5);

        int emptySpaceSum = 0;
        for(MemorySlot empty:emptyMemorySlots){
            emptySpaceSum += empty.getEnd()-empty.getStart()+1;
        }
        Assertions.assertEquals(emptySpaceSum,70-3-1-3-8-6);
    }
}
