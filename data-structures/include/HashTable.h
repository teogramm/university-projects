#ifndef HASHTABLE_H
#define HASHTABLE_H
#include "super.h"

class HashTable : public DataStructure
{
    public:
        HashTable();
        int *table;
        void insert(int element); //Εισαγωγή
        bool search(int element); //Αναζήτηση
        bool remove(int element); //Διαγραφή
        int getSize() {return numOfElements;};
    private:
        int numOfElements;
        bool *hash_keys; //Η συνάρτηση που αποθηκεύει ποιες θέσεις του πίνακα χρησιμοποιούνται
        double spaceUsed;
        int size; // The size of the array
        int HashFunction1(int element); //Η πρώτη Hash Function
        int HashFunction2(int element); //Η δεύτερη Hash Function
        int doubleHashing(int element, int i); //Πραγματοποιεί double hashing
        void HashTable_X2(); //Διπλασιάζει το μέγεθος του πίνακα
        int getElementPos(int);
};

#endif // HASHTABLE_H
