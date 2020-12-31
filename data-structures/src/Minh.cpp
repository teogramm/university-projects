#include <iostream>
#include <stdlib.h>
#include "Minh.h"

Minh::Minh()
{
    array = (int*) malloc (sizeof(int)); //Δέσμευση χώρου
    numOfElements=0; //Αρχικοποίηση των στοιχείων του minheap σε 0
    nextSpot=0;
}

int Minh::min_child(int node)
{
    int min;

    min=2*node+1;
    if (array[min+1]<array[min])
        min++;
    return min;
}

void Minh::insert(int element)
{
    int i;

    numOfElements++; //Αύξηση του αριθμού των στοιχείων στο minheap
    array = (int*) realloc (array,numOfElements*sizeof(int)); //Δεσμεύει επιπλέον χώρο για το νέο στοιχείο

    array[nextSpot]=element; //Προσθήκη του νέου στοιχείου στον πίνακα
    i=nextSpot;
    while (array[parent(i)]>element) //Όσο το νέο στοιχείο είναι μικρότερο από τον γονέα του, ανεβαίνει
    {
        swap(parent(i), i);
        i=parent(i);
    }
    nextSpot++;
}

bool Minh::deleteMin()
{
    int i,temp;

    if (!numOfElements) //Αν δεν υπάρχουν στοιχεία στο minheap, επιστρέφει false
        return false;

    swap(0,nextSpot-1); //Αντιμεταθέτει το ελάχιστο με το τελευταίο στοιχείο του minheap
    numOfElements--;
    nextSpot--;
    array = (int*) realloc (array,numOfElements*sizeof(int)); //Μικραίνει τον δεσμευμένο χώρο

    i=0;
    while (array[i]>array[min_child(i)] && min_child(i)<numOfElements) //Όσο το στοιχείο είναι μεγαλύτερο από το ελάχιστο παι
    {
        temp=min_child(i);
        swap(i,min_child(i)); //Αντιμεταθέτει το στοιχείο με το ελάχιστο παιδί του
        i=temp;
    }
    return true;
}
