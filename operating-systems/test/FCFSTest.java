import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FCFSTest {
    @Test
    void  simpleUsage(){
        FCFS scheduler = new FCFS();
        Process array[] = {
                new Process(0, 0, 0),
                new Process(1, 0,0),
                new Process(2, 0,0),
                new Process(3, 0,0),
                new Process(4, 0,0),
                new Process(5, 0,0)
        };

        for(Process p:array){
            scheduler.addProcess(p);
        }

        for(Process p:array){
            scheduler.removeProcess(p);
        }
    }

    @Test
    void nullValues(){
        FCFS scheduler = new FCFS();
        assertNull(scheduler.getNextProcess());
        scheduler.addProcess(null);
        assertNull(scheduler.getNextProcess());
        scheduler.removeProcess(null);
        assertNull(scheduler.getNextProcess());
    }

    @Test
    void logicInOrder(){
        FCFS scheduler = new FCFS();
        String correct = "0445";
        Process array[] = {
                new Process(0, 5, 0),
                new Process(4, 5, 0),
                new Process(4, 3, 0),
                new Process(5, 10, 0)
        };

        for(Process p:array){
            scheduler.addProcess(p);
        }

        Process p;
        String check = "";
        for(int i=0; i<array.length; i++){
            p = scheduler.getNextProcess();
            scheduler.removeProcess(p);
            check += p.getArrivalTime();
        }

        assertEquals(check, correct);
    }

    @Test
    void logicOutOfOrder(){
        FCFS scheduler = new FCFS();
        String correct = "061025";
        Process array[] = {
                new Process(10, 5, 0),
                new Process(6, 5, 0),
                new Process(25, 3, 0),
                new Process(0, 10, 0)
        };

        for(Process p:array){
            scheduler.addProcess(p);
        }

        Process p;
        String check = "";
        for(int i=0; i<array.length; i++){
            p = scheduler.getNextProcess();
            scheduler.removeProcess(p);
            check += p.getArrivalTime();
        }

        assertEquals(check, correct);
    }
}