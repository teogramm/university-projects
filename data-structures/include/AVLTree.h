#ifndef DOMES_2019_AVLTREE_H
#define DOMES_2019_AVLTREE_H
#include "super.h"

struct AVLNode{
    AVLNode *leftptr;
    AVLNode *rightptr;
    int value;
    int height;
};

class AVLTree : public DataStructure{
public:
    AVLTree();
    void insert(int);
    int getSize();
    bool search(int);
    void remove(int);
    int findmin();
private:
    long numOfElements; // Number of elements in tree
    AVLNode* root;
    AVLNode* newnode(int);
    AVLNode* rightRotate(AVLNode*);
    AVLNode* leftRotate(AVLNode*);
    AVLNode* recursiveInsert(AVLNode* node, int a);
    int deleteLargestSubtreeElement(AVLNode* root, AVLNode* parent);
};


#endif //DOMES_2019_AVLTREE_H
