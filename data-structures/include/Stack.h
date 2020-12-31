#ifndef DOMES_2019_STACK
#define DOMES_2019_STACK
#include "List.h"

template<class T>
class Stack{
public:
    Stack();
    T pop();
    T top();
    void push(T);
    bool empty();
private:
    List<T>* elements;
};

template<class T>
Stack<T>::Stack(){
    elements = new List<T>;
}

template<class T>
void Stack<T>::push(T val){
    elements->insert(val);
}

template<class T>
T Stack<T>::pop(){
    // Get last element
    T temp = elements->getLast();
    elements->deleteLast();
    return temp;
}

template<class T>
bool Stack<T>::empty(){
    return elements->empty();
}

template<class T>
T Stack<T>::top(){
    if(elements->empty())
        return nullptr;
    return elements->getLast();
}
#endif