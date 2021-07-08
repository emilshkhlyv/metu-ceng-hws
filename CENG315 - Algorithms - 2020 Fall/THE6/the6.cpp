#include "the6.h"

// do not add extra libraries here


#define DEBUG 
#define DEBUG_STDERR(x) do { std::cerr << (x) << endl; } while(0)
#define DEBUG_ARRAY(a, n) do { for (int i = 0; i < n; i++) std::cerr << a[i] << " "; std::cerr << endl; } while(0)

// for example usage of DEBUG macros check test.cpp

void DFSUtil(int** graph, int& p, int& n, bool*& visited, vector<int>& a){
    visited[p] = true;
    // std::cout << p << " ";
    a.push_back(p);
     for(int i = 0; i < n; ++i){
         if(graph[p][i] == 1){
             if(visited[i] == false){
                 DFSUtil(graph, i, n, visited, a);
             }
         }
     }
}

void fol(int**& graph, int& i, int& n, bool*& visited, vector<int>& list){
    visited[i] = true;

    for(int k = 0; k < n; ++k){
        if(graph[i][k] == 1){
            if(visited[k] == false){
                fol(graph, k, n, visited, list);
            }  
        }
    }
    list.push_back(i);
}

void SCC(vector<vector<int>>& scc, int& n, int**& graph){
    std::vector<int> list;
    bool* visited = (bool*)malloc(sizeof(bool)*n);
    for(int i = 0; i < n; ++i){
        visited[i] = false;
    }
    
    for(int i = 0; i < n; ++i){
        if(!visited[i]){
            fol(graph, i, n, visited, list);
        }
    }
    
    int** Tmatrix = (int**)malloc(sizeof(int*)*n);
    for(int i = 0; i < n; ++i){
        Tmatrix[i] = new int[n];
    }
    
    for(int i = 0 ; i < n; ++i){
        for(int j = 0; j < n; ++j){
            Tmatrix[i][j] = graph[j][i];
        }
    }
    
    for(int i = 0; i < n ;++i){
        visited[i] = false;
    }
    
    while(list.size()){
        int p = list.back();
        list.pop_back();
        if(visited[p] == false){
            vector<int> a;
            DFSUtil(Tmatrix, p, n, visited, a);
            scc.push_back(a);
        }
    }
}

void TopSortUtil(int**& graph, int& p, int& n, bool*& visited, /*vector<int>& vect,*/ vector<int>& topSort){
    visited[p] = true;
    
    for(int i = 0; i < n; ++i){
        if(graph[p][i] == 1){
            if(visited[i] == false){
                TopSortUtil(graph, i, n, visited, /*vect,*/ topSort);
            }
        }
    }
    topSort.push_back(p);
}

void TopSort(vector<int>& topSort, int& n, int**& graph){
    // std::vector<int> vect;
    bool* visited = (bool*)malloc(sizeof(bool)*n);
    for(int i = 0; i < n; ++i){
        visited[i]= false;
    }
    
    for(int i = 0; i < n; ++i){
        if(visited[i] == false){
            TopSortUtil(graph, i, n, visited, /*vect,*/ topSort);
        }
    }
    
    // while(topSort.size()){
    //     // cout << topSort.back() << " ";
    //     topSort.pop_back();
    // }
}

bool isItCycleUtil(int**& graph, int&i, int& n, bool* visited, bool* rec){
    if(!visited[i]){
        visited[i]  = true;
        rec[i]      = true;
        
        for(int k = 0; k < n; ++k){
            if(graph[i][k] == 1){
                if(!visited[k] && isItCycleUtil(graph, k, n, visited, rec)){
                    return true;
                }
                else if(rec[k] == true){
                    return true;
                }
            }
            
        }
    }
    rec[i] = false;
    return false;
}

bool isItCycle(int**& graph, int& n){
    bool* visited   = (bool*)malloc(sizeof(bool)*n);
    bool* rec       = (bool*)malloc(sizeof(bool)*n);
    
    for(int i = 0; i < n; ++i){
        visited[i] = false;
        rec[i] = false;
    }
    
    for(int i = 0; i < n; ++i){
        if(isItCycleUtil(graph, i, n, visited, rec)){
            return true;
        }
    }
    return false;
}


void SCC_TopSort (int**& graph, int n, vector<vector<int>>& scc, vector<int>& topSort)
{
    // std::cout<< isItCycle(graph, n) << std::endl;
    if(isItCycle(graph, n)){
        
        SCC(scc, n, graph);
        
    }
    else{
        TopSort(topSort, n, graph);
    }
}
