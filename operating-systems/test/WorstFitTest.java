import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class WorstFitTest {

    @Test
    void case1() {
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
        final int[] availableBlockSizes = {15, 40, 10, 20};
        MemoryAllocationAlgorithm wf = new WorstFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 14),
                new Process(0, 0, 20),
                new Process(0, 0, 3),
                new Process(0, 0, 32),  // Won't fit since the block with size 40 is taken by the first process
                new Process(0, 0, 6),
        };

        int address;
        for (Process p : array) {
            address = wf.fitProcess(p, currentlyUsedMemorySlots);
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
        int[] expectedStart = {15, 29, 65, 68};  // Expected slot start addresses
        int[] expectedEnd =  {28, 48, 67, 73};  // Expected slot end addresses
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
    void case2() {
        ArrayList<MemorySlot> currentlyUsedMemorySlots = new ArrayList<MemorySlot>();
        final int[] availableBlockSizes = {15, 40, 10, 20};
        MemoryAllocationAlgorithm wf = new WorstFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 2),
                new Process(0, 0, 1),
                new Process(0, 0, 3),
                new Process(0, 0, 32),  // Won't fit since the block with size 40 is taken by the first process
                new Process(0, 0, 6),
        };

        int address;
        for (Process p : array) {
            address = wf.fitProcess(p, currentlyUsedMemorySlots);
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
        int[] expectedStart = {15, 17, 18, 21, 65};  // Expected slot start addresses
        int[] expectedEnd =  {16, 17, 20, 52, 70};  // Expected slot end addresses
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
        MemoryAllocationAlgorithm wf = new WorstFit(availableBlockSizes);
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
            address = wf.fitProcess(p, currentlyUsedMemorySlots);
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
        int[] expectedStart = {15, 30, 33};  // Expected slot start addresses
        int[] expectedEnd =  {29, 32, 42};  // Expected slot end addresses
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
        MemoryAllocationAlgorithm wf = new WorstFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 14),
                new Process(0, 0, 20),
                new Process(0, 0, 43),  // Size > Largest available block
                new Process(0, 0, 32),
                new Process(0, 0, 100), // Size > Largest available block
        };

        int address;
        for (Process p : array) {
            address = wf.fitProcess(p, currentlyUsedMemorySlots);
            if (address != -1) {
                // Create a new MemorySlot in the given address and put it in currently used
                int blockStart = 0, blockEnd = availableBlockSizes[0] - 1;
                // Find block in witch the address belongs
                for (int i = 1; i < availableBlockSizes.length; i++) {
                    if (address <= blockEnd && address >= blockStart) {
                        break;
                    }
                    blockStart += availableBlockSizes[i - 1];
                    blockEnd = blockStart + availableBlockSizes[i] - 1;
                }
                MemorySlot temp = new MemorySlot(address, address + p.getMemoryRequirements() - 1, blockStart, blockEnd);
                temp.setProcess(p);
                currentlyUsedMemorySlots.add(temp);
            }
        }

        boolean flag = false;
        int i = 0;
        int[] expectedStart = {15, 29};  // Expected slot start addresses
        int[] expectedEnd = {28, 48};  // Expected slot end addresses
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
        MemoryAllocationAlgorithm wf = new WorstFit(availableBlockSizes);
        Process array[] = {
                new Process(0, 0, 35),
                new Process(0, 0, 13),
                new Process(0, 0, 7),
                new Process(0, 0, 20),
                new Process(0, 0, 10),
                new Process(0, 0, 8),
                new Process(0, 0, 7),
                new Process(0, 0, 5),
        };

        int address;
        for (Process p : array) {
            address = wf.fitProcess(p, currentlyUsedMemorySlots);
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
        int[] expectedStart = {15, 65, 0, 55, 7, 78, 50};  // Expected slot start addresses
        int[] expectedEnd =  {49, 77, 6, 64, 14, 84, 54};  // Expected slot end addresses
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