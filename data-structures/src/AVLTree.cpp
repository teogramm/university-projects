#include "AVLTree.h"
#include <cmath>
#include "Stack.h"
#include <cstdlib> // If not included we get an "Ambiguous call to abs error"
#include <algorithm>

using namespace std;

AVLTree::AVLTree() {
    root = nullptr;
}

AVLNode* AVLTree::newnode(int a) {
    AVLNode *temp;
    temp = new AVLNode;
    temp->value = a;
    temp->leftptr = nullptr;
    temp->rightptr = nullptr;
    temp->height = 1;
    return temp;
}

int getHeight(AVLNode* node){
    if(node == nullptr)
        return 0;
    return node->height;
}

int getBalanceFactor(AVLNode* node){
    if(node == nullptr)
        return 0;
    return getHeight(node->leftptr) - getHeight(node->rightptr);
}

// Helper function to be used in insertions
AVLNode* AVLTree::recursiveInsert(AVLNode* node, int a) {
    // We are in position to place the node
    if(node == nullptr){
        return newnode(a);
    }
    if(a < node->value){
        // Need to move further left the tree
        node->leftptr = recursiveInsert(node->leftptr,a);
    }
    else if(a >node->value){
        // Need to move further right down the tree
        node->rightptr = recursiveInsert(node->rightptr,a);
    }
    // Update height
    int leftheight = getHeight(node->leftptr);
    int rightheight = getHeight(node->rightptr);
    node->height = leftheight>rightheight?leftheight:rightheight;
    node->height++;
    // Get balance factor of node
    int balance = getBalanceFactor(node);
    // Handle imbalances
    if(balance > 1 && a < node->leftptr->value){
        // Simple right rotation (the element has been placed on the left subtree)
        return rightRotate(node);
    }
    if(balance < -1 && a> node->rightptr->value){
        // Simple left rotation
        return leftRotate(node);
    }
    if(balance > 1 && a > node->leftptr->value){
        // Left-right rotation
        node->leftptr = leftRotate(node->leftptr);
        return rightRotate(node);
    }
    if(balance < -1 && a< node->rightptr->value){
        // Right-left roation
        node->rightptr = rightRotate(node->rightptr);
        return leftRotate(node);
    }
    // If no balancing is needed return the initial node
    return node;
}

void AVLTree::insert(int a) {
    root = recursiveInsert(root, a);
    numOfElements++;
}

/* Current tree form        Tree we want
        z                   x
       / \                /   \
      x   T1             y     z
     / \                / \   / \
    y  T2              T4 T3 T2 T1
   / \
  T4 T3
*/
AVLNode* AVLTree::rightRotate(AVLNode* z){
    // Create pointers that match the graph above
    AVLNode *x,*T2;
    x = z->leftptr;
    T2 = x->rightptr;
    // Implement the rotation
    z->leftptr = T2;
    x->rightptr = z;
    // Update heights
    z->height = 1 + max(getHeight(z->leftptr),getHeight(z->rightptr));
    x->height = 1 + max(getHeight(x->leftptr),getHeight(x->rightptr));
    // Return the new root
    return x;
}

/* Current tree form        Tree we want
        z                         x
       / \                      /   \
      T1  x                    z     y
         / \                  / \   /  \
        T2  y                T1 T2 T3  T4
           / \
          T3 T4
*/
AVLNode* AVLTree::leftRotate(AVLNode* z){
    // Create pointers that match the graph above
    AVLNode *x = z->rightptr;
    AVLNode *T2 = x->leftptr;
    // Implement the roation
    x->leftptr = z;
    z->rightptr = T2;
    // Update heights
    z->height = 1 + max(getHeight(z->leftptr),getHeight(z->rightptr));
    x->height = 1 + max(getHeight(x->leftptr),getHeight(x->rightptr));
    // Return the new root
    return x;
}

bool AVLTree::search(int x){
    AVLNode *temp = root;
    while(temp != nullptr){
        if(x == temp->value){
            return true;
        }
        if(x < temp->value){
            temp = temp->leftptr;
        }
        else if(x > temp->value){
            temp = temp->rightptr;
        }
    }
    return false;
}

int AVLTree::getSize(){
    return numOfElements;
}

void AVLTree::remove(int key) {
    auto stack = new Stack<AVLNode*>;
    // Check if tree is empty
    if(numOfElements == 0){
        return;
    }
    AVLNode *temp = root;
    while(temp != nullptr && temp->value != key){
        stack->push(temp);
        if(key < temp->value){
            temp = temp->leftptr;
        }
        else if(key > temp->value){
            temp = temp->rightptr;
        }
    }
    // If element was not found, do nothing
    if(temp == nullptr){
        delete stack;
        return;
    }
    // Get the element's parent
    auto parent = stack->top();
    stack -> pop();
    // Node has no children
    if(temp->leftptr == nullptr && temp->rightptr == nullptr){
        // Update the parent node
        if(parent->leftptr == temp){
            parent->leftptr = nullptr;
        }
        else if(parent->rightptr == temp){
            parent->rightptr = nullptr;
        }
        delete temp;
    }
    // Node has only 1 child
    else if(temp->leftptr == nullptr || temp->rightptr == nullptr){
        // Keep the child
        AVLNode *child = temp->leftptr == nullptr?temp->rightptr:temp->leftptr;
        // Update the parent node
        if(parent->leftptr == temp){
            parent->leftptr = child;
        }
        else if(parent->rightptr == temp){
            parent->rightptr = child;
        }
        delete temp;
    }
    // Node has 2 children
    else{
        // Delete the largest element of the left subtree and get its value
        int replacement = deleteLargestSubtreeElement(temp->leftptr, temp);
        temp->value = replacement;
    }
    // Update parent height
    parent->height = 1 + max(getHeight(parent->leftptr),getHeight(parent->rightptr));
    while(!stack->empty()){
        auto node = stack->top();
        stack->pop();
        if(!stack->empty()) {
            parent = stack->top();
        }
        else{
            parent = nullptr;
        }
        temp = node;
        int balance = getBalanceFactor(node);
        // Handle imbalances
        if(balance > 1 && getBalanceFactor(node->leftptr)>=0){
            // Simple right rotation
            temp = rightRotate(node);
        }
        if(balance > 1 && getBalanceFactor(node->leftptr)<0){
            // Left right rotation
            node->leftptr = leftRotate(node->leftptr);
            temp = rightRotate(node);
        }
        if(balance < -1 && getBalanceFactor(node->leftptr)<=0){
            // Simple left rotation
            temp = leftRotate(node);
        }
        if(balance < -1 && getBalanceFactor(node->leftptr)>0){
            // Right-left rotation
            node->rightptr = rightRotate(node->rightptr);
            temp = leftRotate(node);
        }
        // Update parent node
        // Check if parent is root
        if(parent == nullptr){
            root = temp;
        }
        else{
            if(parent->leftptr == node){
                parent->leftptr = temp;
            }
            else if(parent->rightptr == node){
                parent->rightptr = temp;
            }
        }
    }
    numOfElements--;
    delete stack;
}

int AVLTree::deleteLargestSubtreeElement(AVLNode* root, AVLNode* parent){
    // Deletes the largest element of the subtree and returns its value
    AVLNode* temp = root;
    AVLNode* tparent = parent;
    while(temp->rightptr != nullptr){
        tparent = temp;
        temp = temp->rightptr;
    }
    int value = temp->value;
    // Keep the child (if any)(node will always have at most 1 child)
    AVLNode *child = temp->leftptr == nullptr?temp->rightptr:temp->leftptr;
    // Update the parent node
    tparent->leftptr = child;
    tparent->height = 1 + max(getHeight(tparent->leftptr),getHeight(tparent->rightptr));
    return value;
}

int AVLTree::findmin(){
    AVLNode* temp = root;
    if(temp==nullptr) return -1;
    while(temp->leftptr != nullptr){
        temp = temp->leftptr;
    }
    return temp->value;
}