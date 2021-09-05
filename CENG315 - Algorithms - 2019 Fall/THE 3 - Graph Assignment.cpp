#include "the3.h"
#define LOLO long long
#define INF 4040404040404040404LL

using namespace std;

void Bernard_Roy(int n, LOLO**& new_array) {
    for(int i = 0; i < n; ++i)
        for(int j = 0; j < n; ++j)
        {
            if(i == j) new_array[i][j] = 0;
            else if(new_array[i][j] == 0) new_array[i][j] = INF;
        }

    // known as Floyd-Warshall
    for(int k = 0; k < n; ++k)
        for(int j = 0; j < n; ++j)
            for(int i = 0; i < n; ++i)
                new_array[i][j] = min(new_array[i][j], new_array[i][k] + new_array[k][j]);
    
}

void DFSAT(int k, int n, bool*& visited, LOLO**& new_array){
    visited[k] = true;   
    for(int i = 0; i < n; ++i)
        if(new_array[k][i] != INF && visited[i] == false)
            DFSAT(i, n, visited, new_array);
}   

//DFS algorithm
int DFS(int n, LOLO**& new_array){
    bool* visited = new bool[n];
    int count = 0;

    for(int i = 0 ; i < n; ++i)  visited[i] = false;

    for(int k = 0; k < n; ++k)
        if(visited[k] == false)
        {
            DFSAT(k, n, visited, new_array);
            ++count;
        }
    return count;
}

int Important (int n, int**& edgeList, double*& scores){ 

    // declaration of long long** type new array
    LOLO** new_array = new LOLO*[n];
    for(int i = 0; i < n; ++i)
        new_array[i] = new LOLO[n];

    // equalize new_array to edgeList
    for(int i = 0 ; i < n; ++i)
        for(int j = 0; j < n; ++j)
            new_array[i][j] = edgeList[i][j];


    // Making bernard Roy
    Bernard_Roy(n, new_array);

    // count disconnected components
    int count = DFS(n, new_array);

    //find importance of each node in graph
    for(int k = 0; k < n; ++k){
        double score = 0;
        for(int i = 0; i < n; ++i)
            for(int j = 0; j < n; ++j) {
                if(new_array[i][j] != INF && new_array[i][k] != INF && new_array[k][j] != INF) {
                    if(i == j) score += (double(new_array[i][k] + new_array[k][j]));
                    else if(k == i || k == j);       
                    else   score += (double(new_array[i][k] + new_array[k][j])/new_array[i][j]); 
                }
            }
        scores[k] = score;
    }
    return count; 
}