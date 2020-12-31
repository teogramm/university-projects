//
// Created by theodore on 17/03/2020.
//

#ifndef AIPROJ1_ORCHESTRATOR_H
#define AIPROJ1_ORCHESTRATOR_H

#include "puzzleBoard.h"

class Orchestrator {
private:
    int rows,columns;
    PuzzleBoard *puzzle;

public:
    Orchestrator(int tRows,int tColumns);
    Orchestrator(int tRows,int tColumns,int* initValues);
    ~Orchestrator();
    std::string getBoard();

    /**
     * Runs a search for the solution using DFS
     * @return a string representing the sequence of moves that are made, or a no solution found string
     */
    std::string dfs();

    /**
     * Runs a search for the solution using BFS
     * @return a string representing the sequence of moves that are made, or a no solution found string
     */
    std::string bfs();

    /**
     * Runs a search for the solution using BestFS heuristic
     * @return
     */
    std::string bestFS();

    std::string aStar();
};


#endif //AIPROJ1_ORCHESTRATOR_H
