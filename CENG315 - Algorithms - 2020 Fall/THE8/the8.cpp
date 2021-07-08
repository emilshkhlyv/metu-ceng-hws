#include "the8.h"

// do not add extra libraries here

#define DEBUG 
#define DEBUG_STDERR(x) do { std::cerr << (x) << endl; } while(0)
#define DEBUG_ARRAY(a, n) do { for (int i = 0; i < n; i++) std::cerr << a[i] << " "; std::cerr << endl; } while(0)

void floyd_warshall(int**& graph, int**& path, int& V){
    for(int k = 0; k < V; ++k) {
        for(int j = 0; j < V; ++j){
            for(int i = 0; i < V; ++i) {
                if(graph[i][j] > graph[i][k] + graph[k][j]){
                    graph[i][j] = graph[i][k] + graph[k][j];
                    path[i][j] = path[i][k];
                }
            }
        }
    }
}

int minCost(int**& graph, int V, int s1, int s2, int d1, int d2, int A, int B){
    int** path = (int**) malloc(sizeof(int*)*V);
    for(int i = 0; i < V; ++i) {
        path[i] = (int*) malloc(sizeof(int) *V);
    }
    floyd_warshall(graph, path, V);
    int result = 10000;
    for(int i = 0; i < V; ++i){
        for(int j = 0; j < V; ++j){
            if(graph[i][j] < 100){
                int cost1 = A*(graph[s1][i] + graph[j][d1]);
                int cost2 = A*(graph[s2][i] + graph[j][d2]);
                result = min(result, cost1+cost2+(graph[i][j]*B));
            }
        }
    }
    for(int i = 0; i < V; ++i){
        free(path[i]);
    }
    free(path);
    return result;
}
