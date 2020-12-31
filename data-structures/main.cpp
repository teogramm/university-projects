#include <iostream>
#include <algorithm>
#include <fstream>
#include <sstream>
#include <chrono>

#include "Minh.h"
#include "MaxHeap.h"
#include "AVLTree.h"
#include "HashTable.h"
#include "Graph.h"
#include "super.h"

using namespace std;
using namespace std::chrono;

long countlines(istream &file)
{
    long count = 0;
    string unused;
    while (getline(file, unused))
        ++count;
    return count;
}

string *getwords(string s)
{
    // Creates an array containing all words of a line
    // Create stream from string
    stringstream stream(s);
    // Get number of words by counting whitespaces
    long wordno;
    wordno = count(s.begin(), s.end(), ' ') + 1;
    auto wordarray = new string[wordno];
    for (int i = 0; i < wordno; i++)
        stream >> wordarray[i];
    return wordarray;
}

unsigned char structuretonumber(string &s)
{
    // Returns a number corresponding to a structure
    // 0 - Minheap
    // 1 - Maxheap
    // 2 - AVLtree
    // 3 - Simple Graph
    // 4 - HashTable
    if (s == "MINHEAP")
    {
        return 0;
    }
    else if (s == "MAXHEAP")
    {
        return 1;
    }
    else if (s == "AVLTREE")
    {
        return 2;
    }
    else if (s == "GRAPH")
    {
        return 3;
    }
    else if (s == "HASHTABLE")
    {
        return 4;
    }
}

void buildFromFile(ifstream &file, DataStructure &structure)
{
    // This functions adds all the numbers from the file to the selected structure
    string temp;
    int x;
    while (getline(file, temp))
    {
        x = stoi(temp);
        structure.insert(x);
    }
}

void buildFromFile(ifstream &file, Graph &graph)
{
    // This functions adds all the numbers from the file to the selected structure
    string temp;
    int x,y; // Οι αριθμοί που θα εισάγουμε
    while (getline(file, temp))
    {
        // Κάθε γραμμή έχει 2 αριθμούς
        auto numbers = getwords(temp);
        x = stoi(numbers[0]);
        y = stoi(numbers[1]);
        graph.insertEdge(x,y);
        delete[] numbers;
    }
}

int main()
{
    int structurecode;
    high_resolution_clock::time_point t1,t2; // Create time points to measure execution times
    auto temp = new string; // String to read the contents of the line
    // Open the files
    ifstream commandfile;
    commandfile.open("commands.txt");
    ofstream outfile;
    outfile.open("output.txt", ios::out | ios::trunc);
    // Create new structures
    auto minheap = new Minh();
    auto maxheap = new MaxHeap();
    auto avl = new AVLTree();
    auto hashtable = new HashTable();
    auto graph = new Graph();
    //Process every line of the file
    while (getline(commandfile, *temp))
    {
        // Get array of the words in the line
        string *linewords = getwords(*temp);
        DataStructure *temp;
        if(sizeof(linewords)>1){
            structurecode = structuretonumber(linewords[1]);
            // Select the appropriate structure
            switch (structurecode)
            {
            case 0:
                temp = minheap;
                break;
            case 1:
                temp = maxheap;
                break;
            case 2:
                temp = avl;
                break;
            // Το graph θέλει ειδική μεταχείριση
            case 4:
                temp = hashtable;
                break;
            }
        }
        // Η δομή των εντολών υπάρχει στην εκφώνηση της άσκησης
        t1= high_resolution_clock::now(); // Time before command is executed
        if (linewords[0] == "BUILD")
        {
            // Open the file containing the data
            ifstream numberfile(linewords[2]);
            if(structurecode != 3){
                buildFromFile(numberfile, *temp);
            }else{
                buildFromFile(numberfile, *graph);
            }
        }
        else if (linewords[0] == "GETSIZE")
        {
            outfile << temp->getSize();
        }
        else if (linewords[0] == "FINDMIN")
        {
            if (structurecode == 0)
            {
                outfile << minheap->getMin();
            }
            else if (structurecode == 2)
            {
                outfile << avl->findmin();
            }
        }
        else if (linewords[0] == "FINDMAX")
        {
            outfile << maxheap->getMax();
        }
        else if (linewords[0] == "SEARCH")
        {
            int x = stoi(linewords[2]);
            bool success;
            if (structurecode == 2)
            {
                success = avl->search(x);
            }
            else if (structurecode == 4)
            {
                success = hashtable->search(x);
            }
            if (success)
            {
                outfile << "SUCCESS";
            }
            else
            {
                outfile << "FAILURE";
            }
        }
        else if (linewords[0] == "COMPUTESHORTESTPATH")
        {
            int start = stoi(linewords[2]);
            int end = stoi(linewords[3]);
            outfile << graph->Dijkstra(start,end);
        }
        else if (linewords[0] == "COMPUTESPANNINGTREE")
        {
            outfile << graph->Prim();
        }
        else if (linewords[0] == "FINDCONNECTEDCOMPONENTS")
        {
            outfile << graph->connectedComponents();
        }
        else if (linewords[0] == "INSERT")
        {
            // Depends if structure is graph or anything else
            int x = stoi(linewords[2]);
            if (structurecode != 3)
            {
                temp->insert(x);
            }
            else
            {
                int y = stoi(linewords[3]);
                graph->insertEdge(x,y);
            }
        }
        else if (linewords[0] == "DELETEMIN")
        {
            minheap->deleteMin();
        }
        else if (linewords[0] == "DELETEMAX")
        {
            maxheap->removeMax();
        }
        else if (linewords[0] == "DELETE")
        {
            int x = stoi(linewords[2]);
            if (structurecode == 2)
            {
                avl->remove(x);
            }
            else
            {
                int y = stoi(linewords[3]);
                graph->removeEdge(x,y);
            }
        }
        // Time when command is finished
        t2 = high_resolution_clock::now();
        // Calculate duration
        auto duration = duration_cast<microseconds>( t2 - t1 ).count();
        outfile << " " << duration << endl;
        delete[] linewords;
    }
    return 0;
}
