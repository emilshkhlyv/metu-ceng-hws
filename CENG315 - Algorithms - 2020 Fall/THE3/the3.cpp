#include "the3.h"

// do not add extra libraries here


/*
    arr       : array to be sorted, in order to get points this array should contain be in sorted state before returning
    size      : size of arr
    ascending : true for ascending, false for descending 
    
    you can use ceil function from cmath
    
*/

void countingSort(long arr[], bool ascending, int n, int l, long exp, int& counter){
    long size = 1;
    for(int i = 0; i < l; ++i) size *= 10;
    long out[n];
    int* list = new int[size];
    for(int i = 0; i < size; i++){
        list[i] = 0;
    }
    
    
    for(int i = 0; i < n; i++){
        list[(arr[i]/exp)%size]++;
        ++counter;
    }
    
    for(int i = 1; i < size; i++){
        list[i] += list[i-1];
        ++counter;
    }
    
    for(int i = n-1; i >= 0; i--){
        out[list[(arr[i]/exp)%size]-1] = arr[i];
        list[(arr[i]/exp)%size]--;
        ++counter;
    }
    
    for(int i = 0; i < n; i++){
        arr[i] = out[i];
        ++counter;
    }
    
    delete[] list;
}



int RadixSort(long arr[], bool ascending, int n, int l ){
    int counter = 0;
    long max = pow(10, 12);
    for(long exp = 1; max>exp; exp *= pow(10, l)){
        countingSort(arr, ascending, n, l, exp, counter);
    }

    int i = 0, j = n-1;
    if (!ascending){
        while(i<j) {
            std::swap(arr[i ++],arr[j --]);
        }
    }
    return counter;
    
}
