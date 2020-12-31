#include "Graph.h"
#include "List.h"
#include <iostream>
#include <climits>
#define INFINITE INT_MAX

using namespace std;

/* ΣΕ ΕΝΑ ΟΧΙ-ΚΑΙ-ΤΟΣΟ-ΠΡΩΙΜΟ ΣΤΑΔΙΟ (ΟΠΩΣ ΦΑΙΝΕΤΑΙ) */


Graph::Graph()
{
    table = new Vertex*[10];
    vertices = 0;
    capacity = 10;
}

int Graph::deg(int id) //Βαθμός της κορυφής (όρισμα ένα id)
{    
    int c=0; //Αρχικοποίηση του counter
    int pos = findpos(id);
    
    if(pos == -1) //Ελέγχουμε εάν υπάρχει ο κόμβος
        return -1;
    
    c = table[pos]->edgesconnected->getSize(); //Αν υπάρχει, επιστρέφουμε το μέγεθος της λίστας με τις ακμές
    return c;
}

bool Graph::vertexExists(int id) //Συνάρτηση που ψάχνει να βρει αν υπάρχει κόμβος με ένα συγκεκριμένο id
{
    for (int i=0;i<vertices;i++) //Για κάθε κόμβο του πίνακα
    {
        if (table[i]->value == id) //Αν το id του κόμβου είναι ίδιο με το id που ψάχνουμε
            return true;
    }
    return false;
}

bool Graph::edgeExists(int ver1, int ver2) //Συνάρτηση που ψάχνει να βρει αν υπάρχει ακμή μεταξύ δύο κόμβων
{
    int pos1 = findpos(ver1);
    
    if(pos1 == -1)
        return false;
    
    return table[pos1]->edgesconnected->find(ver2); //Ψάχνουμε τη λίστα του πρώτου id για στοιχείο με τιμή το δεύτερο id
}

int Graph::findpos(int id) //Δίνουμε id και επιστρέφει τη θέση του πίνακα
{
    for (int i=0;i<vertices;i++) //Για κάθε κόμβο του πίνακα
    {
        if (table[i]->value == id) //Αν το id του κόμβου είναι ίδιο με το id που ψάχνουμε
            return i;
    }
    return -1; //Αν δεν βρούμε το id
}

void Graph::increaseSize()
{
    // Διπλασιάζουμε το μέγεθος του πίνακα
    Vertex **temp = new Vertex*[2*capacity];
    for(int i=0;i<capacity;i++){
        temp[i] = table[i];
    }
    delete[] table; //Διαγράφουμε τον παλιό πίνακα
    table = temp;
    capacity *= 2; //Η χωρητικότητα έχει διπλασιαστεί
}

int Graph::insertVertex(int element) //Εισαγωγή κορυφής, επιστρέφει τη θέση της κορυφής που εισάχθηκε
{
    if (vertexExists(element))
        return -1;

    auto temp = new Vertex;
    temp->value = element;
    temp->edgesconnected = new List<int>;

    vertices++; //Αυξάνουμε το πλήθος των κόμβων
    //Κάνουμε χειροκίνητη realloc
    if(vertices>capacity){
        increaseSize();
    } 
    table[vertices-1] = temp;
    return vertices-1;
}
    
bool Graph::insertEdge(int ver1, int ver2) //Εισαγωγή ακμής
{
    //Ελέγχουμε εάν υπάρχει ήδη ακμή μεταξύ τους
    if (edgeExists(ver1,ver2))
        return false;
    
    //Ορίζουμε δύο μεταβλητές - τις θέσεις στον πίνακα, των κόμβων με τα δοθέντα id
    int pos1 = findpos(ver1);
    int pos2 = findpos(ver2);
    
    // Αν δεν υπάρχουν οι κόμβοι τους δημιουργούμε
    if(pos1 == -1)
        pos1 = insertVertex(ver1);
    if(pos2 == -1)
        pos2 = insertVertex(ver2);

    //Εισάγουμε στις λίστες των δύο κόμβων τις αντίστοιχες ακμές
    table[pos1]->edgesconnected->insert(ver2);
    table[pos2]->edgesconnected->insert(ver1);
    return true;
}

void Graph::removeEdge(int ver1, int ver2) //Διαγραφή ακμής
{
    //Ορίζουμε δύο μεταβλητές - τις θέσεις στον πίνακα, των κόμβων με τα δοθέντα id
    int pos1 = findpos(ver1);
    int pos2 = findpos(ver2);
    
    //Διαγράφουμε από τις λίστες των δύο κόμβων τις αντίστοιχες ακμές
    table[pos1]->edgesconnected->remove(ver2);
    table[pos2]->edgesconnected->remove(ver1);
}

bool* Graph::DFS(int id)
{
    int i;
    bool* visited = new bool[vertices];
    for (i=0;i<vertices;i++) //Αρχικοποίηση
    {
        visited[i]=false;
    }
    
    RDFS(id, visited);
    return visited; // Returns the table of the nodes visited
}

void Graph::RDFS(int id, bool *visited)
{
    int pos = findpos(id);
    visited[pos] = true;
    Vertex *currentvertex= table[pos];
    for(int i=0;i<currentvertex->edgesconnected->getSize();i++)
    {
        int pos1 = findpos(currentvertex->edgesconnected->get(i));
        if (!visited[pos1]) //Αν δεν έχουμε επισκεφθεί τον κόμβο
        {
            RDFS(table[pos1]->value,visited);
        }
    }
}

// void Graph::RDFS_Compute_Low(int mid)
// {
//     pos = findpos(mid);
//     colour[pos] = true;
//     Low[pos] = discover[pos] = time++;
    
//     ListNode<int>* temp;
    
//     temp = table[pos]->edgesconnected->root; //Δημιουργούμε pointer στη ρίζα της λίστας
//     while (temp != nullptr) //Όσο υπάρχουν στοιχεία στη λίστα
//     {
//         pos1 = findpos(temp->value);
//         temp = temp->next;
//         if (colour[pos1] == false) //Αν δεν έχουμε επισκεφθεί τον κόμβο
//         {
//             pred[pos1] = pos;
//             RDFS_Compute_Low(temp->value);
//             Low[pos] = min(Low[pos],Low[pos1]);
//         }
//         else
//         {
//             if (pos1 != pred[pos])
//                 Low[pos] = min(Low[pos],discover[pos1]);
//         }
//         temp = temp->next;
//     }
// }

int Graph::pickNode() //Επιστρέφει ποιον κόμβο θα ελέγξουμε στον αλγόριθμο του Dijkstra
{
    for (int i=0;i<vertices;i++)
    {
        if (distance[i] != INFINITE && S[i] == 0)
            return i;
    }
}

int Graph::Dijkstra(int start, int target)
{
    S = new int[vertices];
    distance = new int[vertices]; //Η απόσταση κάθε κόμβου από τον δοθέν κόμβο
    
    int pos = findpos(start);
    int i, tpos, time; //time -- Πότε συναντήσαμε μια κορυφή
    
    for (i=0;i<vertices;i++) //Αρχικοποίηση
    {
        S[i] = 0; //Δεν έχουμε συναντήσει κανέναν κόμβο
        distance[i] = INFINITE; //Όλες οι αποστάσεις, από τον κόμβο που είμαστε, είναι άπειρες
    }
    
    distance[pos] = 0; //Η απόσταση από τον κόμβο που είμαστε στον κόμβο pos, είναι 0 (αφού κόμβος που είμαστε == pos)
    
    time = 1; //Θέτουμε τον χρόνο σε 1
    
    //Βρίσκουμε την απόσταση όλων των κόμβων από τον pos
    for (i=0;i<vertices-1;i++)
    {
        int v = pickNode(); //Διαλέγουμε κόμβο που δεν έχουμε ελέγξει και δεν έχει άπειρη απόσταση από τον κόμβο που ελέγχουμε
        S[v] = time; //Δηλώνουμε ότι συναντήσαμε τον κόμβο v, την χρονική στιγμή time
        Vertex *currentv = table[v];
        //Ενημερώνουμε την απόσταση των, συνδεδεμένων με τον v, κόμβων
        for(int j=0;j<currentv->edgesconnected->getSize();j++)//Μέχρι να τελειώσουν οι κόμβοι της λίστας
        {
            tpos = findpos(currentv->edgesconnected->get(j)); //Η λίστα περιέχει id, εμείς θέλουμε θέση στον πίνακα
            
            //Αν 1) δεν έχουμε συναντήσει τον κόμβο, και 2) η απόστασή του κόμβου είναι μεγαλύτερη από την απόσταση του v + 1
            if ((S[tpos] == 0)  && (distance[v]+1 < distance[tpos]))
                distance[tpos] = distance[v]+1; //Η απόσταση γίνεται ίση με την απόσταση του v + 1
        }
        time++;
    }
    
    delete[] S;
    int tempd = distance[findpos(target)]; //Κρατάμε την απόσταση από τον κόμβο start στον κόμβο target
    delete[] distance;
    return tempd;
}

int Graph::connectedComponents() {
    int cc = 0; // Number of connected components
    int i;
    int novisited = 0;
    int nextDFS = 0;
    bool *visited = new bool[vertices];
    for(i=0;i<vertices;i++){
        visited[i] = false;
    }
    while(novisited!=vertices){
        bool *newvisited = DFS(table[nextDFS]->value);
        for(i=0;i<vertices;i++){
            visited[i] = newvisited[i]?newvisited[i]:visited[i]; // Only change visited state if we visit a new node
            if(!newvisited[i]){
                nextDFS = i;
            }
            if(newvisited[i]){
                novisited++;
            }
        }
        delete[] newvisited;
        cc++;
    }
    return cc;
}

int Graph::Prim() //Minimum Spanning Tree
{
    int *key = new int[vertices]; //Καταγράφει το τρέχον κόστος σύνδεσης της κάθε κορυφής
    bool *colour = new bool[vertices]; //Καταγράφει αν η κορυφή είναι στο δέντρο
    int *pred = new int[vertices];

    int i, j, tpos, sum;

    for (i=0;i<vertices;i++) //Αρχικοποίηση
    {
        key[i] = INFINITE;
        colour[i] = false;
        pred[i] = -1;
    }
    key[0] = 0;
    pred[0] = -1;

    for (i=0;i<vertices;i++) //Για κάθε κόμβο
    {
        Vertex *currentv = table[i];
        for (j=0;j<currentv->edgesconnected->getSize();j++) //Για τους γειτονικούς του κόμβους
        {
            tpos = findpos(currentv->edgesconnected->get(j)); //Η θέση του γειτονικού που ελέγχουμε

            if (!colour[tpos] && key[tpos] == INFINITE){ //Αν δεν έχουμε ελέγξει την κορυφή, και το κόστος της δεν είναι άπειρο
                key[tpos] = 1;
                    pred[tpos] = i;
            }
        }
        colour[i] = true; //Έχουμε ελέγξει την κορυφή
    }
    
    sum = 0;
    for (i=0;i<vertices;i++)
    {
        sum += key[i];
    }
    return sum;
}
