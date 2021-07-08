#include "the2.h"

// do not add extra libraries here


/*
    arr       : array to be sorted, in order to get points this array should contain be in sorted state before returning
    size      : size of arr
    ascending : true for ascending, false for descending 
    
    you can use ceil function from cmath
    
*/
int partition(int*& list, int low, int high, bool ascending, int& counter){
    int pivot = list[high];
    int i = low -1;
    int j = low;
    int temp = -1;
    for(; j < high; ++j){
        if(ascending && list[j] < pivot){
            //swap
            temp = list[j];
            list[j] = list[++i];
            list[i] = temp;
        }
        else if(!ascending && list[j] > pivot){
            temp = list[j];
            list[j] = list[++i];
            list[i] = temp;
        }
        ++counter;
    }
    list[high] = list[++i];
    list[i] = pivot;
    return i;
}

void qshelper(int*& list, int low, int high, bool ascending, int& counter){
    if(high > low){
        int mid = partition(list, low, high, ascending, counter);
        qshelper(list, low, mid-1, ascending, counter);
        qshelper(list, mid+1, high, ascending, counter);
    }
}

int QuickSort (int*& arr, int size, bool ascending)
{
    int counter = 0;
    qshelper(arr, 0, size-1, ascending, counter);
    return counter;
}


void qs3helper(int*& list, int low, int high, bool ascending, int& counter){
    int i = low, beginner = low, laster = high;
    if(low<high){
        while(i<laster){
            if(ascending && list[i]<list[high]){
                int temp = list[i];
                list[i]  = list[beginner];
                list[beginner] = temp;
                i++; beginner++;
                ++counter;
            }
            else if(!ascending && list[i]>list[high]){
                int temp = list[i];
                list[i]  = list[beginner];
                list[beginner] = temp;
                i++; beginner++;
                ++counter;
            }                
            else if(list[i] == list[high]){
                --laster;
                int temp = list[i];
                list[i]  = list[laster];
                list[laster] = temp;
                counter += 2;
            }
            else{
                i++;
                counter+=2;
            }
        }
        int mid = std::min(laster-beginner, high-laster+1);
        for(int i = 0; i < mid; ++i){
            int temp = list[beginner+i];
            list[beginner+i]  = list[high-mid+1+i];
            list[high-mid+1+i] = temp;
        }
        qs3helper(list, low, beginner-1,  ascending, counter);
        qs3helper(list, high-laster+beginner+1, high, ascending, counter);
    }
}


int QuickSort3 (int*& list, int size, bool ascending){
    int counter = 0;
    qs3helper(list, 0, size-1, ascending, counter);
    return counter;
}
