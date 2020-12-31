#include "HashTable.h"
#include <iostream>
#define HASH_PRIME 413158523

/* ΣΧΕΔΟΝ ΟΛΟΚΛΗΡΩΜΕΝΟ  --  ΔΙΟΡΘΩΘΗΚΕ ΤΟ ΠΡΟΒΛΗΜΑ ΜΕ ΤΙΣ ΣΥΓΚΡΟΥΣΕΙΣ
   ΑΠΟΜΕΝΕΙ TESTING */

HashTable::HashTable()
{
    table = new int[8]; //Αρχικά ο πίνακας έχει 8 θέσεις
    hash_keys = new bool[8]; //Δημιουργείται πίνακας που αποθηκέυει τις θέσεις που χρησιμοποιούνται
    size = 8;
    for (int i=0; i<size; i++) //Αρχικοποιούμε όλα τα κλειδιά σε false
    {
        hash_keys[i] = false;
    }
    numOfElements = 0;
    spaceUsed = 0; //Ο χώρος που, αρχικά, χρησιμοποιείται είναι 0
}

int HashTable::HashFunction1(int element)
{
    return int(32*element + 355) % HASH_PRIME % size; //Όπου είναι: (spaceUsed*element + someint) % largeprime % sizeof(table)
}

int HashTable::HashFunction2(int element)
{
    return int(64*element + 2358) % HASH_PRIME % size;
}

int HashTable::doubleHashing(int element, int i)
{
    return ( HashFunction1(element) + i*HashFunction2(element) ) % size;
}

void HashTable::HashTable_X2() //Η συνάρτηση που διπλασιάζει το μέγεθος του πίνακα
{
    int i, j, tsize, *table2,pos;
    
    //Διπλασιάζω το μέγεθος του πίνακα με τις τιμές
    
    tsize = size; // Αποθηκέυω το μέγεθος του πίνακα με τα στοιχεία σε μια μεταβλητή
    table2 = new int[tsize]; //Δημιουργώ νέο πίνακα-αντίγραφο του πίνακα με τα στοιχεία
    bool *hash_keys2 = new bool[tsize]; // Keep occupied positions
    for (i=0; i<tsize; i++){
        table2[i] = table[i];
        hash_keys2[i] = hash_keys[i];
    }
    // Delete old arrays to prevent memory leak
    delete[] table;
    delete[] hash_keys;
    //Δεσμέυω νέο χώρο (διπλάσιο του τρέχοντος μεγέθους) για τον αρχικό πίνακα και για τον πίνακα με τις θέσεις
    table = new int[tsize*2];
    hash_keys = new bool[tsize*2];
    for (i=0; i<tsize*2; i++){
        hash_keys[i] = false;
    }
    //Αντιγράφω τα στοιχεία του νέου πίνακα στον αρχικό πίνακα, χρησιμοποιώντας doubleHashing
    for (i=0; i<tsize; i++)
    {
        // Check if there was an element in that position
        if(hash_keys2[i]){
            j = 0; //Θέτουμε τον πολλαπλασιαστή της HashFunction2 σε 0
            pos = doubleHashing(table2[i],j);
            while (hash_keys[pos] && pos<size){
                //Αν το doubleHashing δεν βρει κενή θέση, αυξάνουμε το j
                j++;
                pos = doubleHashing(table2[i],j);
            }
            table[pos] = table2[i]; //Όταν βρούμε κενή θέση στον αρχικό πίνακα, αντιγράφουμε το στοιχείο από τον νέο
            hash_keys[pos] = true; //Δηλώνω πως σε αυτή τη θέση υπάρχει στοιχείο στον πίνακα
        }
    }
    size *=2;
    delete[] table2; // Delete the temporary table
    delete[] hash_keys2;
    return;
}

void HashTable::insert(int element) //Εισαγωγή στοιχείου
{
    int j,pos;
    numOfElements++; //Αυξάνω τον αριθμό των στοιχείων στον πίνακα
    spaceUsed = float(numOfElements)/size; //Καθορίζω το ποσοστό χώρου που χρησιμοποιείται για την προσθήκη του νέου στοιχείου
    //Αν ο χώρος είναι μεγαλύτερος του 50%
    if (spaceUsed>0.5){
        HashTable_X2(); //Διπλασιάζει το μέγεθος του πίνακα
    }
    j = 0; //Θέτουμε τον πολλαπλασιαστή της HashFunction2 σε 0
    pos = doubleHashing(element,j);
    while ( hash_keys[pos] ){
        //Αν το doubleHashing δεν βρει κενή θέση, αυξάνουμε το j
        j++;
        pos = doubleHashing(element,j);
    }
    table[pos] = element; //Αφού βρει κενή θέση, τοποθετεί το στοιχείο
    hash_keys[pos] = true; //Τέλος, δηλώνουμε ότι η συγκεκριμένη θέση του πίνακα είναι πλέον κατειλημμένη
}

bool HashTable::search(int element) //Αναζήτηση στοιχείου
{
    int j;
   
    while ( table[doubleHashing(element,j)] != element && doubleHashing(element,j)<size) //Όσο το στοιχείο του πίνακα δεν είναι το στοιχείο που ψάχνουμε, αυξάνει το j
       j++;
    
    if (j<=numOfElements) //Αν το j είναι μικρότερο του αριθμού των στοιχείων στον πίνακα, σημαίνει πως βρήκε το στοιχείο
       return true; //Επιστρέφει τη θέση που βρήκε το στοιχείο
    return false;
}

int HashTable::getElementPos(int element) //Αναζήτηση στοιχείου
{
    int j;
   
    while ( table[doubleHashing(element,j)] != element && doubleHashing(element,j)<size) //Όσο το στοιχείο του πίνακα δεν είναι το στοιχείο που ψάχνουμε, αυξάνει το j
       j++;
    
    if (j<=numOfElements) //Αν το j είναι μικρότερο του αριθμού των στοιχείων στον πίνακα, σημαίνει πως βρήκε το στοιχείο
       return doubleHashing(element,j); //Επιστρέφει τη θέση που βρήκε το στοιχείο
    return -1;
}
                  
bool HashTable::remove(int element)
{
    if ( getElementPos(element) != -1 ) //Αν έχει βρει το στοιχείο στον πίνακα
    {
        hash_keys[getElementPos(element)] = false; //Στον πίνακα με τις θέσεις, δηλώνω ότι δεν υπάρχει στοιχείο στην συγκεκριμένη θέση
        std::cout << "Element deleted" << std::endl;
        return true;
    }
    std::cout << "Could not find element" << std::endl;
    return false;
}
   
        
