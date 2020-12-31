//
// Created by theodore on 17/03/2020.
//

#include <memory>
#include <stack>
#include <unordered_set>
#include <queue>
#include "orchestrator.h"
#include "puzzleBoard.h"
#include "moveTypes.h"

Orchestrator::Orchestrator(int tRows,int tColumns):rows(tRows),columns(tColumns){
    puzzle = new PuzzleBoard(rows,columns,true);
}


Orchestrator::Orchestrator(int tRows, int tColumns, int *initValues):rows(tRows),columns(tColumns) {
    puzzle = new PuzzleBoard(tRows,tColumns,initValues);
}


Orchestrator::~Orchestrator() {
    delete puzzle;
}

std::string Orchestrator::getBoard() {
    return puzzle->showBoard();
}

std::string buildSolution(PuzzleBoard* board){
    auto currentBoard = board;
    auto path = std::string();
    while(currentBoard!= nullptr){
        auto lastMoveStr = moveTypesToSting(currentBoard->getLastMove()).append( " ");
        // Use reverse to avoid costly string operations
        std::reverse(lastMoveStr.begin(),lastMoveStr.end());
        path = path.append(lastMoveStr);
        currentBoard = currentBoard->getParent();
    }
    std::reverse(path.begin(),path.end());
    return path;
}


std::string Orchestrator::dfs(){
    // We first create a stack(for DFS)
    auto searchHead = new std::stack<PuzzleBoard*>();
    searchHead->push(puzzle);
    auto closedSet = new std::unordered_set<PuzzleBoard*,BoardHash,BoardEquality>();
    while(!searchHead->empty()){
        // Remove the top state of the stack
        auto currentState = searchHead->top();
        searchHead->pop();
        // If state has been examined before do not do anything
        if(closedSet->find(currentState) != closedSet->end()){
            delete currentState;
            continue;
        }
        // Current state is solution
        if(currentState->totalManhattanDistance() == 0){
            auto solutionPath = buildSolution(currentState);
            // Cleanup all the states
            for (auto element : *closedSet) {
                if(element != puzzle){
                    delete element;
                }
            }
            delete closedSet;
            while(!searchHead->empty()){
                delete searchHead->top();
                searchHead->pop();
            }
            delete searchHead;
            delete currentState;
            return solutionPath;
        }
        // Apply all possible moves
        auto upBoard = currentState->move(up);
        auto downBoard = currentState->move(down);
        auto leftBoard = currentState->move(left);
        auto rightBoard = currentState->move(right);
        if(upBoard != nullptr){
            searchHead->push(upBoard);
        }
        if(leftBoard != nullptr){
            searchHead->push(leftBoard);
        }
        if(rightBoard != nullptr){
            searchHead->push(rightBoard);
        }
        if(downBoard != nullptr){
            searchHead->push(downBoard);
        }
        // Put the current state in the closed set,as it has been examined
        closedSet->insert(currentState);
    }
    for (auto element : *closedSet) {
        if(element != puzzle){
            delete element;
        }
    }
    delete closedSet;
    delete searchHead;
    return "no solution found";
}

std::string Orchestrator::bfs(){
    // Same code as dfs but instead we use a queue
    auto searchHead = new std::queue<PuzzleBoard*>();
    searchHead->push(puzzle);
    auto closedSet = new std::unordered_set<PuzzleBoard*,BoardHash,BoardEquality>();
    while(!searchHead->empty()){
        // Remove the top state of the stack
        auto currentState = searchHead->front();
        searchHead->pop();
        // If state has been examined before do not do anything
        if(closedSet->find(currentState) != closedSet->end()){
            delete currentState;
            continue;
        }
        // Current state is solution
        if(currentState->totalManhattanDistance() == 0){
            auto solutionPath = buildSolution(currentState);
            // Cleanup all the states
            for (auto element : *closedSet) {
                if(element != puzzle){
                    delete element;
                }
            }
            delete closedSet;
            while(!searchHead->empty()){
                delete searchHead->front();
                searchHead->pop();
            }
            delete searchHead;
            delete currentState;
            return solutionPath;
        }
        // Apply all possible moves
        auto upBoard = currentState->move(up);
        auto downBoard = currentState->move(down);
        auto leftBoard = currentState->move(left);
        auto rightBoard = currentState->move(right);
        if(upBoard != nullptr){
            searchHead->push(upBoard);
        }
        if(leftBoard != nullptr){
            searchHead->push(leftBoard);
        }
        if(rightBoard != nullptr){
            searchHead->push(rightBoard);
        }
        if(downBoard != nullptr){
            searchHead->push(downBoard);
        }
        // Put the current state in the closed set,as it has been examined
        closedSet->insert(currentState);
    }
    for (auto element : *closedSet) {
        if(element != puzzle){
            delete element;
        }
    }
    delete searchHead;
    delete closedSet;
    return "no solution found";
}

std::string Orchestrator::bestFS(){
    using namespace std;
    // Store a each board with its rating so we do not have to calculate it constantly
    // We only calculate it once when we put it in the queue.
    // The closed should be unaffected by this
    auto minHeap = new priority_queue<pair<PuzzleBoard*,int>,vector<pair<PuzzleBoard*,int>>,BoardReverseComperator>();
    auto closedSet = new unordered_set<PuzzleBoard*,BoardHash,BoardEquality>();
    minHeap->push(pair<PuzzleBoard*,int>(puzzle,puzzle->totalManhattanDistance()));
    while(!minHeap->empty()){
        auto currentPair = minHeap->top();
        auto currentBoard = currentPair.first;
        minHeap->pop();
        if(closedSet->find(currentBoard) != closedSet->end()){
            delete currentBoard;
            continue;
        }
        if(currentPair.second == 0){
            // Return the solution
            auto solutionPath = buildSolution(currentBoard);
            // Cleanup
            for (auto element : *closedSet) {
                if(element != puzzle){
                    delete element;
                }
            }
            delete closedSet;
            while(!minHeap->empty()){
                delete minHeap->top().first;
                minHeap->pop();
            }
            delete currentBoard;
            delete minHeap;
            return solutionPath;
        }
        // Apply all possible moves
        PuzzleBoard* upBoard = currentBoard->move(up);
        PuzzleBoard* downBoard = currentBoard->move(down);
        PuzzleBoard* leftBoard = currentBoard->move(::moveTypes::left);
        PuzzleBoard* rightBoard = currentBoard->move(::moveTypes ::right);
        if(upBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(upBoard,upBoard->totalManhattanDistance()));
        }
        if(leftBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(leftBoard,leftBoard->totalManhattanDistance()));
        }
        if(rightBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(rightBoard,rightBoard->totalManhattanDistance()));
        }
        if(downBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(downBoard,downBoard->totalManhattanDistance()));
        }
        closedSet->insert(currentBoard);
    }
    for (auto element : *closedSet) {
        if(element != puzzle){
            delete element;
        }
    }
    delete closedSet;
    delete minHeap;
    return "no solution found";
}

std::string Orchestrator::aStar(){
    using namespace std;
    // Store a each board with its rating so we do not have to calculate it constantly
    // We only calculate it once when we put it in the queue.
    // The closed should be unaffected by this
    auto minHeap = new priority_queue<pair<PuzzleBoard*,int>,vector<pair<PuzzleBoard*,int>>,BoardReverseComperator>();
    auto closedSet = new unordered_set<PuzzleBoard*,BoardHash,BoardEquality>();
    minHeap->push(pair<PuzzleBoard*,int>(puzzle,puzzle->totalManhattanDistance()));
    while(!minHeap->empty()){
        auto currentPair = minHeap->top();
        auto currentBoard = currentPair.first;
        minHeap->pop();
        if(closedSet->find(currentBoard) != closedSet->end()){
            delete currentBoard;
            continue;
        }
        // The depth of the current element
        auto depth = currentPair.second - currentBoard->totalManhattanDistance();
        if(currentPair.second - depth == 0){
            // Return the solution
            auto solutionPath = buildSolution(currentBoard);
            // Cleanup
            for (auto element : *closedSet) {
                if(element != puzzle){
                    delete element;
                }
            }
            delete closedSet;
            while(!minHeap->empty()){
                delete minHeap->top().first;
                minHeap->pop();
            }
            delete currentBoard;
            delete minHeap;
            return solutionPath;
        }

        // Apply all possible moves
        PuzzleBoard* upBoard = currentBoard->move(up);
        PuzzleBoard* downBoard = currentBoard->move(down);
        PuzzleBoard* leftBoard = currentBoard->move(::moveTypes::left);
        PuzzleBoard* rightBoard = currentBoard->move(::moveTypes ::right);
        if(upBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(upBoard,upBoard->totalManhattanDistance() + depth + 1));
        }
        if(leftBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(leftBoard,leftBoard->totalManhattanDistance() + depth + 1));
        }
        if(rightBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(rightBoard,rightBoard->totalManhattanDistance() + depth + 1));
        }
        if(downBoard != nullptr){
            minHeap->push(pair<PuzzleBoard*,int>(downBoard,downBoard->totalManhattanDistance() + depth + 1));
        }
        closedSet->insert(currentBoard);
    }
    for (auto element : *closedSet) {
        if(element != puzzle){
            delete element;
        }
    }
    delete closedSet;
    delete minHeap;
    return "no solution found";
}