import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BestFitTest {

    @Test
    void case1() {
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
        final int[] availableBlockSizes = {15, 40, 10, 20};
        MemoryAllocationAlgorithm bf = new BestFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 14),
                new Process(0, 0, 20),
                new Process(0, 0, 3),
                new Process(0, 0, 32),
                new Process(0, 0, 6),
        };

        int address;
        for (Process p : array) {
            address = bf.fitProcess(p, currentlyUsedMemorySlots);
            if (address != -1) {
                // Create a new MemorySlot in the given address and put it in currently used
                int blockStart = 0,blockEnd = availableBlockSizes[0] - 1;
                // Find block in witch the address belongs
                for (int i=1; i<availableBlockSizes.length; i++) {
                    if (address <= blockEnd && address >= blockStart) {
                        break;
                    }
                    blockStart += availableBlockSizes[i-1];
                    blockEnd = blockStart + availableBlockSizes[i] - 1;
                }
                MemorySlot temp = new MemorySlot(address,address+p.getMemoryRequirements()-1,blockStart,blockEnd);
                temp.setProcess(p);
                currentlyUsedMemorySlots.add(temp);
            }
        }

        boolean flag = false;
        int i=0;
        int[] expectedStart = {0, 65, 55, 15, 58};  // Expected slot start addresses
        int[] expectedEnd =  {13, 84, 57, 46, 63};  // Expected slot end addresses
        for (MemorySlot slot : currentlyUsedMemorySlots) {
            if (slot.getStart() != expectedStart[i] || slot.getEnd() != expectedEnd[i]) {
                flag = true;
                break;
            }
            i++;
        }

        assertFalse(flag);
    }

    @Test
    void edgeCaseZero() {
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
        final int[] availableBlockSizes = {15, 40, 10, 20};
        MemoryAllocationAlgorithm bf = new BestFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 15),
                new Process(0, 0, 0),   // Size < 1
                new Process(0, 0, 3),
                new Process(0, 0, -1),  // Size < 1
                new Process(0, 0, -5),  // Size < 1
                new Process(0, 0, 10),
        };

        int address;
        for (Process p : array) {
            address = bf.fitProcess(p, currentlyUsedMemorySlots);
            if (address != -1) {
                // Create a new MemorySlot in the given address and put it in currently used
                int blockStart = 0,blockEnd = availableBlockSizes[0] - 1;
                // Find block in witch the address belongs
                for (int i=1; i<availableBlockSizes.length; i++) {
                    if (address <= blockEnd && address >= blockStart) {
                        break;
                    }
                    blockStart += availableBlockSizes[i-1];
                    blockEnd = blockStart + availableBlockSizes[i] - 1;
                }
                MemorySlot temp = new MemorySlot(address,address+p.getMemoryRequirements()-1,blockStart,blockEnd);
                temp.setProcess(p);
                currentlyUsedMemorySlots.add(temp);
            }
        }

        boolean flag = false;
        int i=0;
        int[] expectedStart = {0, 55, 65};  // Expected slot start addresses
        int[] expectedEnd =  {14, 57, 74};  // Expected slot end addresses
        for (MemorySlot slot : currentlyUsedMemorySlots) {
            if (slot.getStart() != expectedStart[i] || slot.getEnd() != expectedEnd[i]) {
                flag = true;
                break;
            }
            i++;
        }

        assertFalse(flag);
    }

    @Test
    void edgeCaseLarge() {
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
        final int[] availableBlockSizes = {15, 40, 10, 20};
        MemoryAllocationAlgorithm bf = new BestFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 14),
                new Process(0, 0, 20),
                new Process(0, 0, 43),  // Size > Largest available block
                new Process(0, 0, 32),
                new Process(0, 0, 100), // Size > Largest available block
        };

        int address;
        for (Process p : array) {
            address = bf.fitProcess(p, currentlyUsedMemorySlots);
            if (address != -1) {
                // Create a new MemorySlot in the given address and put it in currently used
                int blockStart = 0,blockEnd = availableBlockSizes[0] - 1;
                // Find block in witch the address belongs
                for (int i=1; i<availableBlockSizes.length; i++) {
                    if (address <= blockEnd && address >= blockStart) {
                        break;
                    }
                    blockStart += availableBlockSizes[i-1];
                    blockEnd = blockStart + availableBlockSizes[i] - 1;
                }
                MemorySlot temp = new MemorySlot(address,address+p.getMemoryRequirements()-1,blockStart,blockEnd);
                temp.setProcess(p);
                currentlyUsedMemorySlots.add(temp);
            }
        }

        boolean flag = false;
        int i=0;
        int[] expectedStart = {0, 65, 15};  // Expected slot start addresses
        int[] expectedEnd =  {13, 84, 46};  // Expected slot end addresses
        for (MemorySlot slot : currentlyUsedMemorySlots) {
            if (slot.getStart() != expectedStart[i] || slot.getEnd() != expectedEnd[i]) {
                flag = true;
                break;
            }
            i++;
        }

        assertFalse(flag);
    }

    @Test
    void notEnoughSpace() {
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
        final int[] availableBlockSizes = {15, 40, 10, 20};
        MemoryAllocationAlgorithm bf = new BestFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 15),
                new Process(0, 0, 40),
                new Process(0, 0, 10),
                new Process(0, 0, 20),
                new Process(0, 0, 5),
        };

        int address;
        for (Process p : array) {
            address = bf.fitProcess(p, currentlyUsedMemorySlots);
            if (address != -1) {
                // Create a new MemorySlot in the given address and put it in currently used
                int blockStart = 0,blockEnd = availableBlockSizes[0] - 1;
                // Find block in witch the address belongs
                for (int i=1; i<availableBlockSizes.length; i++) {
                    if (address <= blockEnd && address >= blockStart) {
                        break;
                    }
                    blockStart += availableBlockSizes[i-1];
                    blockEnd = blockStart + availableBlockSizes[i] - 1;
                }
                MemorySlot temp = new MemorySlot(address,address+p.getMemoryRequirements()-1,blockStart,blockEnd);
                temp.setProcess(p);
                currentlyUsedMemorySlots.add(temp);
            }
        }

        boolean flag = false;
        int i=0;
        int[] expectedStart = {0, 15, 55, 65};  // Expected slot start addresses
        int[] expectedEnd =  {14, 54, 64, 84};  // Expected slot end addresses
        for (MemorySlot slot : currentlyUsedMemorySlots) {
            if (slot.getStart() != expectedStart[i] || slot.getEnd() != expectedEnd[i]) {
                flag = true;
                break;
            }
            i++;
        }

        assertFalse(flag);
    }
}