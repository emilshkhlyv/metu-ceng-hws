### TAKE HOME EXAM 1 – MERGE & FUNKY SORT

```int MergeSort (int*& arr, int size, bool ascending);```

```int FunkySort (int*& arr, int size, bool ascending);```

In this exam, you are asked to sort the given array ```arr``` with two different sorting algorithms namely Merge Sort and Funky Sort ascending or descending depending on the boolean variable ```ascending``` and return the number of number comparisons (comparisons between the values to be sorted only, not your auxiliary comparisons) done in each of the algorithm.

Merge sort should be implemented as exactly in your slides.
```
Funky sorting algorithm (for ascending = true):
If the first element is larger than the last element in $arr$, swap first and the last element.
If there are greater than or equal to 3 elements in the $arr$, then
     1. Funky sort the initial 2/3 of the $arr$ 
     2. Funky sort the final 2/3 of the $arr$
     3. Funky sort the initial 2/3 of the $arr$ again
```
In lines 1-2-3 you should round to nearest greater integer, e.g. 7*2/3 = 5

Note that, you can get extra space for MergeSort but you need to fill the values in ```arr``` variable in a sorted manner before returning and then return the number of comparisons. Funky Sort is done in place.


Constraints
Array sizes are less than 10^5 for MergeSort and 1500 for FunkySort (and > 0)

