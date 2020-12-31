//
// Created by theodore on 17/03/2020.
//

#include "puzzleBoard.h"
#include "moveTypes.h"
#include <cstdlib>
#include <vector>
#include <random>
#include <algorithm>
#include <thread>
#include <future>
#include <sstream>

using namespace std;

PuzzleBoard::PuzzleBoard(int tRows, int tColumns, const int * initialValues):rows(tRows),columns(tColumns) {
    board = new int*[tRows];
    for(int i=0;i<tRows;i++){
        board[i] = new int[tColumns];
        for(int j=0;j<tColumns;j++){
            board[i][j] = initialValues[i*columns + j];
        }
    }
    parent = nullptr;
    lastMove = none;
}

PuzzleBoard::PuzzleBoard(int x, int y,bool initialize) : rows(x), columns(y) {
    if(initialize){
        board = randomBoard(rows,columns);
        for(int i=0;i<rows;i++){
            for(int j=0;j<columns;j++){
                // The piece with number rows*columns is considered the empty piece
                if(board[i][j] == rows*columns){
                    emptyPiece.first = i;
                    emptyPiece.second = j;
                }
            }
        }
    }else {
        board = new int *[rows];
        for(int i=0;i<rows;i++){
            board[i] = new int[columns];
        }
    }

    // When the board is created from scratch there is no parent
    parent = nullptr;
    lastMove = none;

}

// Creates and populates a 2-dimensional array randomly with incrementing numbers from 1 to rows*columns
int** PuzzleBoard::randomBoard(int rows,int columns){
    // Fist create a vector of the available values
    auto valueVector = new vector<int>();
    for(int i=1;i<=rows*columns;i++){
        valueVector->push_back(i);
    }
    // Create a vector of the available positions
    // and at the same time initialize an empty board
    int** tempBoard = new int*[rows];
    auto posVector = new vector<pair<int,int>>();
    for(int i=0;i<rows;i++){
        for(int j=0;j<columns;j++){
            posVector->push_back(pair<int,int>(i,j));
        }
        tempBoard[i] = new int[columns];
    }
    // Shuffle the two vectors
    random_device rd;
    mt19937 twister(rd());

    shuffle(valueVector->begin(), valueVector->end(), twister);
    shuffle(posVector->begin(), posVector->end(), twister);

    auto vIt = valueVector->begin();
    auto posIt = posVector->begin();

    // Continue until all the values are put on the board
    while(vIt != valueVector->end()){
        int value = *vIt;
        pair pos = *posIt;

        tempBoard[pos.first][pos.second] = value;

        vIt = valueVector->erase(vIt);
        posIt = posVector->erase(posIt);
    }

    delete valueVector;
    delete posVector;

    return tempBoard;
}

PuzzleBoard::PuzzleBoard(const PuzzleBoard& pb){
    this->rows = pb.rows;
    this->columns = pb.columns;

    // Set the base board as the parent
    this->emptyPiece = pb.emptyPiece;
    this->parent = pb.parent;
    this->lastMove = pb.lastMove;

    this->board = new int*[rows];
    for(int i=0;i<rows;i++) {
        this->board[i] = new int[columns];
        for(int j=0;j<columns;j++){
            this->board[i][j] = pb.board[i][j];
        }
    }
}

PuzzleBoard::PuzzleBoard(const PuzzleBoard&& pb) noexcept {
    this->rows = pb.rows;
    this->columns = pb.columns;
    this->emptyPiece = pb.emptyPiece;
    this->lastMove = pb.lastMove;
    this->parent = pb.parent;
    this->board = pb.board;
    for(int i=0;i<pb.rows;i++){
        this->board[i] = pb.board[i];
    }
}

PuzzleBoard::~PuzzleBoard() {
    for(int i=0;i<rows;i++){
        delete[] board[i];
    }
    delete[] board;
}

PuzzleBoard* PuzzleBoard::getParent() const {
    return parent;
}

moveTypes PuzzleBoard::getLastMove() const {
    return lastMove;
}

PuzzleBoard* PuzzleBoard::move(moveTypes m) {
    auto emptyPieceLocation = emptyPiece;
    auto newLocation = emptyPieceLocation;
    // Fist check if move is valid
    switch(m){
        case up:
            newLocation.first -= 1;
            if (newLocation.first< 0){
                return nullptr;
            } else{
                break;
            }
        case down:
            newLocation.first += 1;
            if (newLocation.first >= rows){
                return nullptr;
            }else{
                break;
            }
        case ::moveTypes::left:
            newLocation.second -= 1;
            if (newLocation.second  < 0){
                return nullptr;
            }else{
                break;
            }
        case ::moveTypes::right:
            newLocation.second += 1;
            if(newLocation.second >= columns){
                return nullptr;
            }
            else{
                break;
            }
        case none:
            return nullptr;
    }
    // Valgrind indicates this is leaking memory but can't see why
    // TODO: Maybe create a clone function instead of using the copy constructor
    auto newPBoard = new PuzzleBoard(*this);
    for(int i=0;i<rows;i++){
        for(int j=0;j<columns;j++){
            newPBoard->board[i][j] = this->board[i][j];
        }
    }
    newPBoard->parent = this;
    newPBoard->lastMove = m;
    // Do the swap
    // Update the values on the Piece objects
    newPBoard->board[emptyPieceLocation.first][emptyPieceLocation.second] = newPBoard->board[newLocation.first][newLocation.second];
    newPBoard->board[newLocation.first][newLocation.second] = columns * rows;
    newPBoard->emptyPiece = newLocation;

    return newPBoard;
}

/**
 * Calculates the distance between where the square is and where the square
 * should be
 */
int PuzzleBoard::pieceManhattanDistance(int row, int col, int val) {
    // Square with value 1 should be at position 0

    val -= 1;
    int correctPositionRow = val / 3;
    int correctPositionCol = val % 3;

    return abs(correctPositionRow - row) + abs(correctPositionCol - col);
}

/**
 * Get the manhattan distance for a range of the board. Used for parallelization.
 * Calculations INCLUDE the end piece!
 */
int PuzzleBoard::rangeManhattanDistance(const int start,const int end) const{
    int tempDistance = 0;
    for(int i=start;i<=end;i++){
        int row = i/rows;
        int col = i%columns;
        tempDistance += pieceManhattanDistance(row,col,board[row][col]);
    }
    return tempDistance;
}

int PuzzleBoard::totalManhattanDistance()const{
    if(rows*columns>400) {
        int distance = 0;
        // Get the number of threads on this computer
        unsigned concurentThreadsSupported = std::thread::hardware_concurrency();
        if (concurentThreadsSupported == 0) {
            concurentThreadsSupported = 1;
        }
        auto threads = new std::future<int>[concurentThreadsSupported];
        auto results = new int[concurentThreadsSupported];
        int boardSize = columns * rows;
        for (int i = 0; i < concurentThreadsSupported; i++) {
            if (i != concurentThreadsSupported - 1) {
                // If the thread examines any part other than the last, end one element early(as that element will be
                // processed by the next thread
                unsigned int from = i * (boardSize / concurentThreadsSupported);
                unsigned int to = ((i + 1) * ((boardSize / concurentThreadsSupported)) - 1);
                threads[i] = std::async(&PuzzleBoard::rangeManhattanDistance, this, from, to);
            } else {
                // On the last thread we add the remaining items that have not been allocated
                unsigned int from = i * (boardSize / concurentThreadsSupported);
                unsigned int to =
                        ((i + 1) * (boardSize / concurentThreadsSupported) - 1) + boardSize % concurentThreadsSupported;
                threads[i] = std::async(&PuzzleBoard::rangeManhattanDistance, this, from, to);
            }
        }
        for (int i = 0; i < concurentThreadsSupported; i++) {
            results[i] = threads[i].get();
        }
        distance = accumulate(results, results + concurentThreadsSupported, 0);
        delete[] results;
        delete[] threads;
        return distance;
    }else {
        int distance = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                distance += pieceManhattanDistance(i, j, board[i][j]);
            }
        }
        return distance;
    }
}

string PuzzleBoard::showBoard(){
    stringstream out = stringstream();
    for(int i=0;i<rows;i++){
        for(int j=0;j<columns;j++){
            out << board[i][j] << " ";
        }
        out << "\n";
    }
    return out.str();
}

bool PuzzleBoard::operator==(const PuzzleBoard & obj) const{
    // Check if the boards have different dimensions
    if(this->rows != obj.rows || this->columns != obj.columns){
        return false;
    }
    // Then check the board contents
    for(int i=0;i<rows;i++){
        for(int j=0;j<columns;j++){
            if(this->board[i][j] != obj.board[i][j]){
                return false;
            }
        }
    }
    return true;
}

bool PuzzleBoard::operator!=(const PuzzleBoard &obj) const {
    return !(*this == obj);
}
