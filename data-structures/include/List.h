#ifndef DOMES_2019_LIST
#define DOMES_2019_LIST

template<class T>
struct ListNode{
    ListNode* prev;
    ListNode* next;
    T value;
};

template<class T>
class List{
    public:
        List();
        ~List();
        void insert(T);
        bool remove(T); // Remove given element(if it exists)
        void removepos(unsigned); //Remove element at given position
        bool find(T);
        T& get(unsigned); // Get element at position
        bool empty();
        T operator[](unsigned);
        int getSize(){return numofelements;};
        void deleteLast();
        T getLast();
    private:
        ListNode<T>* root;
        ListNode<T>* tail;
        int numofelements;
};

template<class T>
List<T>::List(){
    root = nullptr;
    numofelements = 0;
}

template<class T>
List<T>::~List(){
    auto temp = root;
    while(temp != nullptr){
        auto next = temp->next;
        delete temp;
    }
}

template<typename T>
T List<T>::operator[](unsigned index){
    return this->get(index);
}

template<class T>
void List<T>::insert(T val){
    auto temp = new ListNode<T>;
    temp->value = val;
    temp->next = nullptr;
    // Check whether list has elements
    if(root == nullptr){
        temp->prev = nullptr;
        root = temp;
    }
    else{
        temp->prev = tail;
        tail->next = temp;
    }
    tail = temp;
    numofelements++;
}

template<class T>
bool List<T>::remove(T val){
    ListNode<T>* temp = root;
    while(temp != nullptr){
        if(temp->value == val) break;
        temp = temp->next;
    }
    if(temp == nullptr){
        return false;
    }
    // Update previous node
    if(temp->prev != nullptr) {
        temp->prev->next = temp->next;
    }
    // Update next node
    if(temp->next != nullptr) {
        temp->next->prev = temp->prev;
    }
    delete temp;
    numofelements--;
    return true;
}

template<class T>
void List<T>::removepos(unsigned pos){
    if(pos>numofelements-1) return;
    ListNode<T>* temp = root;
    for(int i=0;i<pos;i++){
        temp = temp->next;
    }
    // temp is the node to be deleted
    // Update previous node
    if(temp->prev != nullptr) {
        temp->prev->next = temp->next;
    }
    else{
        root = temp->next;
    }
    // Update next node
    if(temp->next != nullptr) {
        temp->next->prev = temp->prev;
    }else{
        tail = temp->prev;
    }
    numofelements--;
}

template<class T>
bool List<T>::find(T val){
    ListNode<T>* temp = root;
    while(temp != nullptr){
        if(temp->value == val) return true;
        temp = temp->next;
    }
    return false;
}

template<class T>
T& List<T>::get(unsigned pos){
    if(pos>numofelements-1) throw;
    ListNode<T>* temp = root;
    for(int i=0;i<pos;i++){
        temp = temp->next;
    }
    return temp->value;
}

template <class T>
void List<T>::deleteLast() {
    auto temp = tail;
    tail = tail->prev;
    delete temp;
    numofelements--;
}

template <class T>
T List<T>::getLast() {
    return tail->value;
}

template<class T>
bool List<T>::empty(){
    return numofelements==0;
}

#endif
