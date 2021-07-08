#include "the1.h"


// do not add extra libraries here

/*
    arr       : array to be sorted, in order to get points this array should contain be in sorted state before returning
    size      : size of arr
    ascending : true for ascending, false for descending 
    
    you can use ceil function from cmath
    
*/
void merger(int*&arr, int left, int mid, int right, int ascending, int& count){
    int leftside = mid-left+1;
    int rightside = right-mid;
    
    int list1[leftside], list2[rightside];
    
    int i = 0;
    
    for(i = 0; i < leftside; ++i)  list1[i] = arr[i+left];
    for(i = 0; i < rightside; ++i) list2[i] = arr[i+mid+1];
    
    i = 0;
    int j = 0;
    int k = left;
    
    if(!ascending){
        while(i < leftside && j < rightside){
            if(list1[i] > list2[j]){
                arr[k] = list1[i];
                ++i;
            }
            else{
                arr[k] = list2[j];
                ++j;
            }
            ++count;
            ++k;
        }
    }
    else{
        while(i < leftside && j < rightside){
            if(list1[i] < list2[j]){
                arr[k] = list1[i];
                ++i;
                
            }
            else{
                arr[k] = list2[j];
                ++j;
                
            }
            ++count;
            ++k;
        }
    }
    
    while(i < leftside){
        arr[k] = list1[i];
        ++i; ++k;
    }
    while(j < rightside){
        arr[k] = list2[j];
        ++j; ++k;
    }
}

void helper(int*& arr, int left, int right, int ascending, int& count){
    if(right > left){
        int mid = floor((left+right)/2);
        helper(arr, mid+1, right, ascending, count);
        helper(arr, left, mid, ascending, count);
        merger(arr, left, mid, right, ascending, count);
    }
}

int MergeSort (int*& arr, int size, bool ascending){
    int count = 0;
    helper(arr, 0, size-1, ascending, count);
    return count;   
}

void funktion(int*& arr, int start, int end, bool ascending, int& counter){
    if(ascending){
        if(arr[start]>arr[end]){
            int temp = arr[end];
            arr[end] = arr[start];
            arr[start] = temp;
        }
        ++counter;
    }
    else if(!ascending){
        if(arr[start]<arr[end]){
            int temp = arr[end];
            arr[end] = arr[start];
            arr[start] = temp;
        }
        ++counter;
    }
    if((end-start+1) >= 3){
        int leftside = ceil((end-start+1)*2.0/3.0);
        funktion(arr, start, start+leftside-1, ascending, counter);
        funktion(arr, (end-leftside+1), end, ascending, counter);
        funktion(arr, start, start+leftside-1, ascending, counter);
    }
}

int FunkySort (int*& arr, int size, bool ascending)
{
    int counter = 0;
    funktion(arr, 0, size-1, ascending, counter);
    return counter;
}
