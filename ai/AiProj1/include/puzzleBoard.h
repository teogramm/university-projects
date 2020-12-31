//
// Created by theodore on 17/03/2020.
//

#ifndef AIPROJ1_PUZZLEBOARD_H
#define AIPROJ1_PUZZLEBOARD_H

#include <utility>
#include <string>
#include <boost/container_hash/hash.hpp>
#include "moveTypes.h"

/**
 * Represents the given puzzle and contains helper methods.
 */
class PuzzleBoard {
public:
    PuzzleBoard(int,int,bool);
    PuzzleBoard(int,int,const int*);
    PuzzleBoard(const PuzzleBoard&pb);
    PuzzleBoard(const PuzzleBoard&&pb) noexcept ;
    ~PuzzleBoard();
    bool operator==(const PuzzleBoard & obj) const ;
    bool operator!=(const PuzzleBoard & obj) const;

    [[nodiscard]] PuzzleBoard* getParent() const;
    [[nodiscard]] moveTypes getLastMove() const;

    /**
     * Attempts to move the empty piece towards the specified direction
     * @param m a varibale of type moveTypes that indicates the direction to move the empty piece
     * @return a new PuzzleBoard object with the desired move applied, nullptr if move is not possible
     */
    PuzzleBoard* move(moveTypes m);
    [[nodiscard]] int totalManhattanDistance() const;
    std::string showBoard();

    // Allow hash function to access the internal fields
    friend struct BoardHash;
    friend struct BoardEquality;

private:

    int rows,columns;
    // Pointer to the empty piece
    std::pair<int,int> emptyPiece;
    // Points to the parent board before this one, useful when performing a search and
    // we find the solution
    PuzzleBoard* parent;
    // The move that was made to get to this state
    moveTypes  lastMove;
    // 2-dimensional array of pointers
    int** board;

    static int** randomBoard(int rows,int columns);
    static int pieceManhattanDistance(int,int,int);
    [[nodiscard]] int rangeManhattanDistance(int start,int end) const;
};

// Define the hash for a PuzzleBoard object
// two PuzzleBoard objects should have equal hash when the same
// numbers are in the same positions
struct BoardHash{
    size_t operator()(PuzzleBoard* const& pb) const noexcept {
        size_t hash = 0;
        for(int i=0;i<pb->rows;i++){
            for(int j=0;j<pb->columns;j++){
                boost::hash_combine(hash,i);
                boost::hash_combine(hash,j);
                boost::hash_combine(hash,pb->board[i][j]);
            }
        }
        return hash;
    }
};

struct BoardEquality{
    bool operator()(const PuzzleBoard* p1,const PuzzleBoard* p2) const {
        if(p1->rows != p2->rows || p1->columns != p2->columns){
            return false;
        }
        for(int i=0;i<p1->rows;i++){
            for(int j=0;j<p1->columns;j++){
                if(p1->board[i][j] != p2->board[i][j]){
                    return false;
                }
            }
        }
        return true;
    }
};

/**
 * Custom comperator so that priority queue is a minheap
 */
struct BoardReverseComperator{
    bool operator()(const std::pair<PuzzleBoard*,int>& p1,const std::pair<PuzzleBoard*,int>&  p2) const{
        return p1.second > p2.second;
    }
};
#endif //AIPROJ1_PUZZLEBOARD_H
