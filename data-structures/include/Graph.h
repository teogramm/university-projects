#ifndef GRAPH_H
#define GRAPH_H
#include "List.h"

/* ΑΠΟΜΕΝΟΥΝ ΣΥΝΑΡΤΗΣΕΙΣ DFS, PRIM ΚΛΠ. */

struct Vertex
{
    List<int>* edgesconnected; // Οι κόμβοι με τους οποίους γειτονεύει ο κόμβος
    int value;
};

class Graph
{
    public:
        Graph();
        int deg(int id); //Ο βαθμός της κορυφής
        int numOfVertices() {return vertices;} //Επιστρέφει τον αριθμό των κορυφών
        int numOfEdges() {return edges;} //Επιστρέφει τον αριθμό των ακμών
        int findpos(int id);//Δίνουμε id και επιστρέφει τη θέση του πίνακα
        bool vertexExists(int id); //Επιστρέφει αν υπάρχει ήδη κόμβος με αυτό το id
        bool edgeExists(int ver1, int ver2); //Επιστρέφει αν υπάρχει ήδη ακμή μεταξύ των κορυφών
        int insertVertex(int element); //Εισαγωγή κορυφής
        bool insertEdge(int ver1, int ver2); //Εισαγωγή ακμής
        void removeEdge(int ver1, int ver2); //Διαγραφή ακμής
        bool* DFS(int id);
        void RDFS(int id,bool*);
//      void RDFS_Compute_Low(int mid);
        int pickNode(); //Dijkstra: επιλέγει κόμβο που πληροί οριμένα κριτήρια
        int Dijkstra(int, int); //Ο αλγόριθμος του Dijkstra
        int connectedComponents();
        int Prim();

    private:
        Vertex **table; //Ο πίνακας που σε κάθε θέση του έχει έναν κόμβο (pointer σε struct Vertex)
        int vertices; //Κορυφές
        int edges; //Ακμές
        int capacity; //Το μέγεθος του πίνακα με τις κορυφές
        void increaseSize(); //Αυξάνει το μέγεθος του πίνακα με τις κορυφές κατα 10
        int *S; //Dijkstra: με ποια σειρά επισκεφθήκαμε τους κόμβους
        int *distance; //Dijkstra: η απόσταση κάθε κόμβου από έναν συγκεκριμένο
};

#endif // GRAPH_H
