#include <iostream>
#include <orchestrator.h>
#include <chrono>

#define TESTED_PUZZLES 100

using namespace std;

int duration(chrono::high_resolution_clock::time_point t1,chrono::high_resolution_clock::time_point t2){
    return chrono::duration_cast<chrono::milliseconds>( t2 - t1 ).count();
}

double average(const int *array,int n){
    int sum = 0;
    for(int i=0;i<n;i++){
        sum += array[i];
    }
    return sum/(double)n;
}

int main() {

    auto o = new Orchestrator(3,3);
    std::cout << o->dfs();

//    // 2 = unsolvable/solvable 3 = 3x3 4x4 5x5 4 = dfs bfs bestfs a*
//    auto counters = new int[2][3][4];
//    for(int i=0;i<2;i++){
//        for(int j=0;j<3;j++){
//            for(int k=0;k<4;k++){
//                counters[i][j][k] = 0;
//            }
//        }
//    }
//
//    for (int i = 3; i <= 3; i++) {
//        int solvable = 0;
//        int unsolvable = 0;
//
//        while (solvable < TESTED_PUZZLES || unsolvable < TESTED_PUZZLES) {
//            auto orchestrator = new Orchestrator(i,i);
//            auto tBefore = chrono::high_resolution_clock::now();
//            auto tResult = orchestrator->dfs();
//            auto tAfter = chrono::high_resolution_clock::now();
//            if(tResult == "no solution found"){
//                if(unsolvable >= TESTED_PUZZLES){
//                    continue;
//                }else{
//                    counters[0][i-3][0] += duration(tBefore,tAfter);
//                    unsolvable++;
//                }
//            }else{
//                if(solvable>=TESTED_PUZZLES){
//                    continue;
//                }else{
//                    counters[1][i-3][0] += duration(tBefore,tAfter);
//                    solvable++;
//                }
//            }
//            tBefore = chrono::high_resolution_clock::now();
//            tResult = orchestrator->bfs();
//            tAfter = chrono::high_resolution_clock::now();
//            if(tResult == "no solution found"){
//                counters[0][i-3][1] += duration(tBefore,tAfter);
//            }else{
//                counters[1][i-3][1] += duration(tBefore,tAfter);
//            }
//            tBefore = chrono::high_resolution_clock::now();
//            tResult = orchestrator->bestFS();
//            tAfter = chrono::high_resolution_clock::now();
//            if(tResult == "no solution found"){
//                counters[0][i-3][2] += duration(tBefore,tAfter);
//            }else{
//                counters[1][i-3][2] += duration(tBefore,tAfter);
//            }
//            tBefore = chrono::high_resolution_clock::now();
//            tResult = orchestrator->aStar();
//            tAfter = chrono::high_resolution_clock::now();
//            if(tResult == "no solution found"){
//                counters[0][i-3][3] += duration(tBefore,tAfter);
//            }else{
//                counters[1][i-3][3] += duration(tBefore,tAfter);
//            }
//            delete orchestrator;
//        }
//    }
//    // Display results
//    for(int i=0;i<2;i++){
//        if(i==0){
//            cout << "Unsolvable ";
//        }else{
//            cout << "Solvable ";
//        }
//        for(int j=3;j<=3;j++){
//            cout << "Board " << j << "x"<<j << "\n";
//            for(int k=0;k<4;k++){
//                // Times are in milliseconds so we divide by 1000 to get seconds
//                cout << "\t";
//                switch(k){
//                    case 0:
//                        cout << "DFS: ";
//                        break;
//                    case 1:
//                        cout << "BFS: ";
//                        break;
//                    case 2:
//                        cout << "BestFS: ";
//                        break;
//                    case 3:
//                        cout << "A*: ";
//                        break;
//                }
//                cout << counters[i][j-3][k]/(double)TESTED_PUZZLES/1000 << "\n";
//            }
//        }
//    }
    return 0;
}
