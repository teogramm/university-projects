#ifndef MINH_H
#define MINH_H
#include "super.h"

class Minh : public DataStructure
{
    public:
        Minh();
        int *array; //Ο πίνακας που θα αποθηκευτούν τα στοιχεία του minheap
        int getSize() {return numOfElements;} //Ο αριθμός των στοιχείων που υπάρχουν στο minheap
        int getMin() {return array[0];} //Επιστρέφει το ελάχιστο
        void insert(int element); //Προσθέτει το στοιχείο στο minheap
        bool deleteMin(); //Αφαιρεί το ελάχιστο στοιχείο από το minheap

    private:
        int numOfElements; //Αριθμός των στοιχείων στο minheap
        int nextSpot; //Η επόμενη θέση προς αποθήκευση στοιχείου
        int min_child(int node); //Επιστρέφει το ελάχιστο μεταξύ των δύο παιδιών ενός κόμβου
        int parent(int node) {return (node-1)/2;} //Επιστρέφει τον γονέα του κόμβου
        void swap(int posA, int posB) {int temp=array[posA]; array[posA]=array[posB]; array[posB]=temp;} //Αντιμεταθέτει τα στοιχεία στις δύο θέσεις
};

#endif // MINH_H
