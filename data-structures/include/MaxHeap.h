#ifndef DOMES_2019_MAXHEAP
#define DOMES_2019_MAXHEAP
#include "super.h"
#include <vector>
using namespace std;

class MaxHeap: public DataStructure{
    private:
        vector<int> *elements;
    public:
        MaxHeap();
        void insert(int);
        int getSize(){return elements->size();};
        int getMax(){return elements->at(0);}
        void removeMax();
};

#endif