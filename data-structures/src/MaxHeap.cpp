#include "MaxHeap.h"
#include<iostream>
#include <cmath>

MaxHeap::MaxHeap(){
    elements = new vector<int>;
}

void MaxHeap::insert(int v){
    // Add the element to the vector
    elements->push_back(v);
    // Get the position of the last element of the vector(the one we just added)
    int i = elements->size()-1;
    int parent = elements->at(floor(i/2.0));
    while(elements->at(i) > parent){
        // Swap the elements
        int temp = elements->at(i);
        elements->at(i) = elements->at(floor(i/2.0));
        elements->at(floor(i/2.0)) = temp;
        // Move up the tree
        i = floor(i/2.0);
        parent = elements->at(floor(i/2.0));
    }
}

void MaxHeap::removeMax(){
    // Left child is 2*pos+1, right child is 2*pos+2
    int left,right,current,size,temp;
    int i=0;
    size = elements->size();
    if(size==1) return;
    // Move last element to root
    elements->at(0) = elements->at(size-1);
    elements->pop_back();
    size--; // Reduce size
    while(true){
        if(i>size) break;
        current = elements->at(i); // Keep value of current node
        // Keep values of left and right child, if they do not exist put -1 instead
        left = 2*i+1<size?elements->at(2*i+1):-1;
        right = 2*i+2<size?elements->at(2*i+2):-1;
        // If the current node does not have left or right children, break the loop
        if(left==-1 && right == -1) break;
        // If the current node is bigger than both children, then it's in the right position
        if(current>left && current>right) break;
        // Swap the current element with the biggest of its children and move down the tree
        elements->at(i) = left>right?left:right;
        if(left>right){
            elements->at(2*i+1) = current;
            i = 2*i+1;
        }
        else{
            elements->at(2*i+2) = current;
            i = 2*i+2;
        }
    }
}